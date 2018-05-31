package compiler;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by marin on 8/6/2017.
 */
class SymbolTable {
    private Stack <Node> ST;
    private Scope scope;
    private HashMap <String, Bucket> peek;


    public SymbolTable() {
        setST();
        setScope();
        setPeek();
    }

    public void setST() {
        this.ST = new Stack<Node>();
    }

    public void setScope() {
        this.scope = new Scope();
    }

    public Scope getScope() {
        return scope;
    }


    public void enter (){
        this.scope.enter();
    }

    public boolean exit() {
        if (getScope().empty()){
            getScope().exit();
            return true;
        }

        Node n;
        int endID;
        int startID = ST.peek().getNodeID();

        if (ST.peek().getNodeID() < 2) {
            while (!ST.isEmpty()) {
                n = ST.pop();
                peek.get(n.getSymbolName()).delete(n);
            }
            this.scope.exit();
            return true;
        }

        endID = scope.getScope().get(getScope().getCurrent()-1).getNodeID();
        while (startID != endID) {
            n = ST.pop();
            peek.get(n.getSymbolName()).delete(n);
            startID = ST.peek().getNodeID();
        }

        this.scope.exit();
        return true;
    }

    public void setPeek() {
        this.peek = new HashMap<String, Bucket>();
    }

    public Node getHash(String name, String selection) {
        if (peek.isEmpty() || peek.get(name)  == null) return null;

        return peek.get(name).getLastEntry(selection);
    }

    public void insert(Node n) {
        ST.push(n);
        scope.setEndNode(n);

        Bucket b = new Bucket();
        b.insert(n.getSymbolName(), n);
        peek.put(n.getSymbolName(), b);
    }

    public boolean lookup(Node newbie,String selection) {
        Node n = getHash(newbie.getSymbolName(), selection);
        if (n == null) return false;

        int startID;

        if (getScope().getCurrent() > 1 ) {
            startID = getScope().getScope().get(getScope().getCurrent() - 1 ).getNodeID() + 1;

            if (newbie.getNodeID() >= startID) {
                if (n.getSymbol() instanceof Variable || n.getSymbol() instanceof ArrayVar) return true;

                return n.getSymbol().compare((Function) newbie.getSymbol());
            } else {
                return false;
            }
        }
        else {
            if (n.getSymbol() instanceof Variable || n.getSymbol() instanceof ArrayVar) return true;

            return n.getSymbol().compare((Function) newbie.getSymbol());
        }
    }


}

class Scope {
    private static int current = 0;
    private HashMap <Integer, Node> scope;

    public Scope() {
        setScope();
    }

    public void setScope() {
        this.scope = new HashMap<Integer, Node>();
    }
    public HashMap<Integer, Node> getScope() {
        return scope;
    }

    public boolean  setEndNode( Node n) {
        if (n == null) return false;

        this.scope.put(getCurrent(), n);

        return true;
    }

    public boolean empty() {
        if (this.scope.isEmpty() || this.scope.get(getCurrent()) == null) {
            return true;
        }
        return false;
    }

    public void setCurrent(int current) {
        Scope.current = current;
    }

    public int getCurrent() {
        return current;
    }

    public void enter(){
        setCurrent(getCurrent() + 1);
        scope.put(getCurrent(), null);
    }

    public void exit() {
        scope.remove(getCurrent());
        setCurrent(getCurrent() - 1);
    }

    public void print() {

        for (Map.Entry <Integer, Node> entry:
             scope.entrySet()) {

            if (entry.getValue() == null) {
                System.out.println("null");
                return;
            }

            System.out.println(entry.getKey() + " --> " + entry.getValue().getSymbolName());
        }
    }
}

class Bucket {
    private Stack <Node> varStack;
    private Stack <Node> funcStack;

    public Bucket() {
        setVarStack();
        setFuncStack();
    }

    public Node getLastEntry(String selection) {
        if (selection.equals("func")) {
            if (funcStack.isEmpty()) return null;
            return funcStack.peek();
        }

        if (selection.equals("var")) {
            if (varStack.isEmpty()) return null;
            return varStack.peek();
        }
        return null;
    }

    public void setVarStack() {
        this.varStack = new Stack<Node>();
    }
    public void setFuncStack() {
        this.funcStack = new Stack<Node>();
    }

    public Stack<Node> getVarStack() {
        return varStack;
    }
    public Stack<Node> getFuncStack() {
        return funcStack;
    }

    public boolean insert(String name, Node node) {

        if (node.getSymbol() instanceof Variable) {
            varStack.push(node);
            return true;
        }
        else if (node.getSymbol() instanceof  Function) {
            funcStack.push(node);
            return true;
        }
        else {
            return false;
        }
    }
    public boolean delete(Node n) {

        if (n.getSymbol() instanceof Function && !funcStack.empty()) {
            funcStack.pop();
            return true;
        }
        if (n.getSymbol() instanceof Variable && !varStack.empty()) {
            varStack.pop();
            return true;
        }

        return false;
    }

    public void print() {

        System.out.println("Variables in Bucket: ");
        for (int i = 0; i < varStack.size(); i++) {
            System.out.println("->" + varStack.get(i).getSymbolName());
        }

        System.out.println("Functions in Bucket: ");
        for (int i = 0; i < funcStack.size(); i++) {
            System.out.println("->" + funcStack.get(i).getSymbolName());
        }
    }
}
