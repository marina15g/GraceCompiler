package compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marin on 7/6/2017.
 */
public class Node {
    private Symbol symbol;
    private Node previousDef;
    private int nodeID;
    private static int IDGenerator = 0;

    public Node(String name,String type,boolean reference, String locality, List <String> dims){
        this.setNodeID();
        this.setArrayVar(name, type, reference, locality, dims);
        this.setPreviousDef(null);
    }
    public Node(String name,String type,boolean reference, String locality){
        this.setNodeID();
        this.setVar(name, type, reference, locality);
        this.setPreviousDef(null);
    }
    public Node(String name,String type, List <Variable> params){
        this.setNodeID();
        this.setFunc(name,type,params);
    }

    public void setNodeID(){
        this.nodeID=IDGenerator;
        IDGenerator++;
    }

    public void setVar(String name,String type,boolean reference, String locality){
        this.symbol = new Variable(name,type,reference,locality);
    }

    public void setArrayVar(String name,String type,boolean reference, String locality, List <String> dims){
        this.symbol = new ArrayVar(name,type,reference,locality);
        this.setDimensions(dims);
    }
    public void setDimensions(List <String> dims){
        for(int i=0;i<dims.size();i++){
            this.symbol.addDimension(dims.get(i));
        }
    }

    public void setFunc(String name,String type,List <Variable> params){
        this.symbol=new Function(name,type);
        this.setParameters(params);
    }
    public void setParameters(List <Variable> params){
        if(params==null){
            return;
        }
        for (int i = 0; i <params.size() ; i++) {
            this.symbol.insertParam(params.get(i));
        }
    }

    public void setPreviousDef(Node previousDef) {
        this.previousDef = previousDef;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public int getNodeID() {
        return nodeID;
    }

    public String getSymbolName(){
        return this.symbol.getName();
    }

    public Node getPreviousDef() {
        return previousDef;
    }

    public void print(){
        System.out.println("\n\n________________________");
        System.out.println("Node ID : " + this.nodeID);
        this.symbol.print();
        if(this.previousDef!=null) {
            System.out.println("-> Previous Node:");
            this.previousDef.print();
        }else{
            System.out.println("->No previous Node Definition");
        }
        System.out.println("________________________");

    }
}

abstract class Symbol {
    private String name;
    private String type;


    public Symbol(String name,  String type){
        this.setName(name);
        this.setType(type);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract void addDimension(String idx);
    public abstract void insertParam(Variable v);

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean compare(Function F){return false;}

    public void print(){
        System.out.println("Name: " + this.getName());
        System.out.println("Type: " + this.getType());
    }
}

class Function extends Symbol {
    private List <Variable> params;

    public Function(String name, String type){
        super(name, type);
        this.params = new ArrayList<Variable>();
    }

    @Override
    public String getName() {
        return super.getName();
    }
    public  void addDimension(String idx){}


    @Override
    public String getType() {
        return super.getType();
    }

    public List<Variable> getParams() {
        return params;
    }

    public void insertParam(Variable var){
        this.params.add(var);
    }



    public boolean compare(Function F){
        if(!super.getName().equals(F.getName())){
            return false;
        }
        if(!super.getType().equals(F.getType())){
            return false;
        }
        if(this.params.size() != F.getParams().size()){
            return false;
        }

        for (int i = 0; i < this.params.size(); i++) {
            if (!this.params.get(i).getType().equals(F.getParams().get(i).getType())) return false;
        }

        return true;
    }

    public void print(){
        super.print();

        for (int i = 0; i < params.size(); i++) {
            params.get(i).print();
        }
        System.out.println();
    }

}

class Variable extends Symbol{
    private boolean reference;
    private String locality;


    public Variable(String name, String type, boolean reference, String locality){
        super(name, type);
        this.setReference(reference);
        this.setLocality(locality);
    }

    public void setName(String name){
        super.setName(name);
    }
    public void setType(String type){
        super.setType(type);
    }
    public void setReference(boolean reference){
        this.reference = reference;
    }
    public void setLocality(String locality){
        this.locality = locality;
    }

    public String getName(){
        return super.getName();
    }
    public String getType(){
        return super.getType();
    }
    public boolean getReference(){
        return this.reference;
    }
    public String getLocality(){
        return this.locality;
    }
    public  void addDimension(String idx){}
    public void insertParam(Variable var){}

    public void print(){
        super.print();

        System.out.println("reference: " + this.getReference());
        System.out.println("locality: " + this.getLocality());
    }
}

class ArrayVar extends Variable{
    private List<Integer> size;


    public ArrayVar(String name, String type, boolean reference, String locality){
        super(name, type, reference, locality);
        this.setSize();
    }

    public void setSize() {
        this.size = new ArrayList<Integer>();
    }

    public List<Integer> getSize() {
        return this.size;
    }

    public int getLength(){
        return this.size.size();
    }
    @Override
    public void addDimension(String idx){
        this.size.add(Integer.parseInt(idx));
    }

    public void printDimension(){
        System.out.print("dimensions: ");
        for (int i = 0; i < this.getLength(); i++){
            System.out.print(size.get(i) + " ");
        }
        System.out.println("\ntotal: " + this.getLength());
    }

    @Override
    public void print() {
        super.print();
        this.printDimension();
    }
}
