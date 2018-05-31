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
                if(n.getSymbol() instanceof Function){
                    if(!((Function) n.getSymbol()).isDefined()){
                        return false;
                    }
                }
                peek.get(n.getSymbolName()).delete(n);
            }
            this.scope.exit();
            return true;
        }

        endID = scope.getScope().get(getScope().getCurrent()-1).getNodeID();
        while (startID != endID) {
            n = ST.pop();
            if(n.getSymbol() instanceof Function){
                if(!((Function) n.getSymbol()).isDefined()){
                    return false;
                }
            }
            peek.get(n.getSymbolName()).delete(n);
            startID = ST.peek().getNodeID();
        }

        this.scope.exit();
        return true;
    }

    public void setPeek() {
        this.peek = new HashMap<String, Bucket>();
    }

    /*Returns last defined Symbol with given parameter name from func/var stack according to selection ("func"/"var") */
    public Node getHash(String name, String selection) {
        if (peek.isEmpty() || peek.get(name)  == null) return null;

        return peek.get(name).getLastEntry(selection);
    }
    public Node getFunction(Node f){
        if(f==null){return null;}
        Node ret=getHash(f.getSymbolName(),"func");
        while (ret!=null){
            if (ret.getSymbol().compare((Function) f.getSymbol())){
                return ret;
            }
            ret=ret.getPreviousDef();
        }
        return null;
    }

    public void insert(Node n) {
        ST.push(n);
        scope.setEndNode(n);
        Bucket b=this.peek.get(n.getSymbolName());
        if(b==null) {
           b = new Bucket();
        }
        b.insert(n.getSymbolName(), n);
        peek.put(n.getSymbolName(), b);
    }


    public boolean lookup(Node newbie) {
        Node func = getHash(newbie.getSymbolName(), "func");
        Node var = getHash(newbie.getSymbolName(), "var");

        if (func == null && var == null) return false;

        if (var != null) {
            if (getScope().getScope().size() < 2) {
                return true;
            } else {
                int startID = getScope().getScope().get(getScope().getCurrent() - 1).getNodeID() + 1;

                if (var.getNodeID() >= startID) {
                    return true;
                }
            }
        }

        if (func != null) {
            if (newbie.getSymbol() instanceof Function){
                if (getScope().getScope().size() < 2) {
                    while(func!=null) {
                        if (func.getSymbol().compare((Function) newbie.getSymbol()) == true) {
                            return true;
                        }
                        func=func.getPreviousDef();
                    }
                    return false;
                } else {
                    int startID = getScope().getScope().get(getScope().getCurrent() - 1).getNodeID() + 1;
                    Node f=func;
                    while(func!=null) {
                        if (func.getNodeID() >= startID) {
                            if (func.getSymbol().compare((Function) newbie.getSymbol()) == true) {
                                return true;
                            }
                        }
                        func=func.getPreviousDef();
                    }
                    return false;
                }
            }
            //instance of variable
            if (getScope().getScope().size() < 2) {
                return true;
            } else {
                int startID = getScope().getScope().get(getScope().getCurrent() - 1).getNodeID() + 1;

                if (func.getNodeID() >= startID) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
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
            if(!varStack.isEmpty()) {
                node.setPreviousDef(varStack.peek());
            }
            varStack.push(node);
            return true;
        }
        else if (node.getSymbol() instanceof  Function) {
            if (!funcStack.isEmpty()) {
                node.setPreviousDef(funcStack.peek());
            }
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
