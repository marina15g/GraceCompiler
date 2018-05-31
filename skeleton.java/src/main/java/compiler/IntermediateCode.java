package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by merti on 25-Jun-17.
 */
public class IntermediateCode {
    private HashMap <Integer, String[]> Keeper;
    private List <String> TRUE;
    private List <String> FALSE;
    private List <String> NEXT;
    private List <tempVar> tempVars;
    private static int label = 0;
    private ArrayList <Integer[]> unitList;

    public IntermediateCode(){
        setKeeper();
        setTempVars();  // empty list for temp vars
        setUnitList();
    }


    public void setKeeper() {
        this.Keeper = new HashMap<Integer, String[]>();
    }

    

    public void setUnitList() {
        this.unitList = new ArrayList<Integer[]>();
    }
    
    public void unit() {
        Integer[] newUnit = new Integer[2];
        newUnit[0] = this.label;
        if (this.unitList.isEmpty()) {
            this.unitList.add(newUnit);
        }
        
        unitList.get(unitList.size() -1 )[0] = this.label;
    }
    
    public void endu() {
        Integer[] newUnit = new Integer[2];
        newUnit[1] = this.label;
        if (this.unitList.isEmpty()) {
            this.unitList.add(newUnit);
        }

        unitList.get(unitList.size() -1 )[1] = this.label;
    }

    public void setTempVars() {
        this.tempVars = new ArrayList<tempVar>();
    }

    public void GENQUAD (String op, String x, String y, String z){
        //System.out.println("Label" + this.label + " : " + op + ", " + x + ", " + y + ", " + z);
        this.label++;
        String[] Quad = new String[5];

        Quad[0] = this.label + "";
        Quad[1] = op;
        Quad[2] = x;
        Quad[3] = y;
        Quad[4] = z;
        this.Keeper.put(this.label, Quad);

    }


    public String NEXTQUAD() {
        return (this.label + 1) + "";
    }

    public tempVar NEWTEMP(String type) {
        tempVar newTemp = new tempVar(type);
        this.tempVars.add(newTemp);
        return newTemp;
    }

    public List<String[]> EMPTYLIST() {
        return new ArrayList<String[]>();
    }

    public List<String> MAKELIST(String label) {
        List <String> list = new ArrayList<String>();
        list.add(label);

        return list;
    }

    public List <String> MERGE(List<String>... Lists) {
        List <String> mergeList = new ArrayList<String>();

        for (List<String> item:Lists) {
            mergeList.addAll(item);
        }

        return mergeList;
    }

    public void BACKPATCH(List<String> list, String z) {
        for (String item: list) {
            this.Keeper.get(Integer.parseInt(item))[4] = z;
        }

    }



    public void print(List <String> list) {
        if (list.isEmpty()) {
            System.out.println("Empty");
            return;
        }

        for (String item: list) {
            System.out.println(item);
        }
    }

    public void printVars(List <tempVar> tmp) {
        if (tmp.isEmpty()) {
            System.out.println("Empty Varlist");
            return;
        }

        System.out.println("TempVars:");

        for (tempVar v: tmp) {
            v.print();
        }
    }

    public void printUnits() {
        if (unitList.isEmpty()) {
            System.out.println("No units");
            return;
        }

        for (int i = 0; i < unitList.size(); i++) {
            System.out.println("Unit " + unitList.size() + ")");
            System.out.println("Start: " + unitList.get(i)[0] + ", End: " + unitList.get(i)[1]);
        }
    }

    public void print(String[] str) {
        System.out.print(str[0] + ": ");
        for (int i = 1; i < str.length-1; i++) {
            System.out.print(str[i] + ", ");
        }
        System.out.println(str[str.length-1]);
    }

    public void print() {
        System.out.println("Total Quads:");
        for (HashMap.Entry<Integer, String[]> entry: Keeper.entrySet()) {
            print( entry.getValue());
        }


       // System.out.println("<------------------->");
       // printVars(tempVars);

       // printUnits();
    }
}

class tempVar {
    private static int id = 0;
    private String name;
    private String type;
    private String value;
    private List <String> TRUE;
    private List <String> FALSE;

    public tempVar() {
        setName();
    }

    public tempVar(String type) {
        setName();
        setType(type);
    }

    public tempVar (String type, String value) {
        setType(type);
        setValue(value);
    }


    public void setName() {
        this.name = "$" + this.id + "";
        this.id++;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTRUE(List<String> TRUE) {
        this.TRUE = TRUE;
    }

    public void setFALSE(List<String> FALSE) {
        this.FALSE = FALSE;
    }

    public List<String> getTRUE() {
        return TRUE;
    }

    public List<String> getFALSE() {
        return FALSE;
    }

    public String getName() {
        return name;
    }

    public void print() {
        System.out.println("varName: " + this.name + ", varType: " + this.type + ", VarValue:" + this.value);
    }
}