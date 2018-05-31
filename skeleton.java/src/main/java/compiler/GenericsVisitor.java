package compiler;

import com.sun.org.apache.bcel.internal.generic.NEW;
import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;

import java.io.IOException;
import java.util.*;

import static jdk.nashorn.internal.objects.Global.__noSuchProperty__;
import static jdk.nashorn.internal.objects.Global.print;
import static jdk.nashorn.internal.runtime.ScriptObject.spillAllocationLength;

public class GenericsVisitor extends DepthFirstAdapter {
    private int index = 0;
    private SymbolTable S;
    private IntermediateCode IC;
    private List <Variable> varList;
    private List <Variable> paramList;
    private List <Integer> dimList;
    private Stack <String>  returnStack;
    private boolean retFlag;
    private boolean declarationFlag;
    private int line ;
    private List <String[]> argType;
    private List <String> quadList;
    private List <tempVar> condList;
    private List <String> labelList;


    public void errorFound(String s){
        System.out.println("Semantic error: "+ s);
        System.exit(-1);
    }
    public GenericsVisitor(){
        S=new SymbolTable();
        IC=new IntermediateCode();
        quadList=new ArrayList<String>();
        condList=new ArrayList<tempVar>();
        labelList=new ArrayList<String>();
    }

    public void InsertLibrary(){
        List <Variable> varlist=new ArrayList<Variable>();
        varlist.add(new Variable("n","int",true,"local"));
        Node puti=new Node("puti","nothing",varlist);
        S.insert(puti);

        varlist=new ArrayList<Variable>();
        varlist.add(new Variable("c","char",true,"local"));
        Node putc=new Node("putc","nothing",varlist);
        varlist=new ArrayList<Variable>();
        List <Integer> d=new ArrayList<Integer>();
        d.add(0);
        varlist.add(new ArrayVar("s","char",true,"local",d));
        S.insert(putc);

        Node geti=new Node("geti","int",null);
        S.insert(geti);

        Node getc=new Node("getc","char",null);
        S.insert(getc);

        varlist=new ArrayList<Variable>();
        varlist.add(new Variable("n","int",false,"local"));
        varlist.add(new ArrayVar("s","char",true,"local",d));
        Node gets=new Node("gets","nothing",varlist);
        S.insert(gets);

        varlist=new ArrayList<Variable>();
        varlist.add(new Variable("n","int",false,"local"));
        Node abs=new Node("abs","int",varlist);
        S.insert(abs);

        Node chr=new Node("chr","char",varlist);
        S.insert(chr);

        varlist = new ArrayList<Variable>();
        varlist.add(new Variable("c", "char", false, "true"));
        Node ord = new Node("ord", "int", varlist);
        S.insert(ord);

        varlist = new ArrayList<Variable>();
        varlist.add(new ArrayVar("s","char",true, "local", d));
        Node strlen = new Node("strlen", "int", varlist);
        S.insert(strlen);

        varlist = new ArrayList<Variable>();
        varlist.add(new ArrayVar("s1","char",true, "local", d));
        varlist.add(new ArrayVar("s2","char",true, "local", d));
        Node strcmp = new Node("strcmp", "int", varlist);
        S.insert(strcmp);

        varlist = new ArrayList<Variable>();
        varlist.add(new ArrayVar("trg","char",true, "local", d));
        varlist.add(new ArrayVar("src","char",true, "local", d));
        Node strcpy = new Node("strcmp", "nothing", varlist);
        S.insert(strcpy);

        varlist = new ArrayList<Variable>();
        varlist.add(new ArrayVar("trg","char",true, "local", d));
        varlist.add(new ArrayVar("src","char",true, "local", d));
        Node strcat = new Node("strcat", "nothing", varlist);
        S.insert(strcat);

    }

    public boolean addVar(Variable v){
        for (Variable x: varList) {
            if (x.getName().equals(v.getName())) {
                return false;
            }
        }
        varList.add(v);
        return true;
    }
    //EXPR
    public void inAIdentifierExpr(AIdentifierExpr node){
        line=node.getId().getLine();
    }
    public  void outAIdentifierExpr(AIdentifierExpr node){
        String [] entry= new String[2];
        Node e=S.getHash(node.getId().getText().trim(),"var");
        if(e==null){
            errorFound("["+node.getId().getLine()+","+node.getId().getPos()+"] : Identifier "+node.getId().getText()+" is undeclared");
        }else{
            entry[0]=e.getSymbol().getType();
            if(e.getSymbol() instanceof ArrayVar){
                entry[1]=((ArrayVar) e.getSymbol()).getSize().size()+"";
            }else{
                entry[1]=0+"";
            }
            argType.add(entry);
        }
        quadList.add(node.getId().getText().trim());
    }


    public void inAFIdExpr(AFIdExpr node){
        line=node.getId().getLine();

    }
    public void outAFIdExpr(AFIdExpr node){          }


    public void inAStringExpr(AStringExpr node){
        line=node.getStringLiteral().getLine();

    }
    public void outAStringExpr(AStringExpr node){
        String [] entry = new String[2];
        entry[0]="char";
        entry[1]="1";
        argType.add(entry);
        quadList.add(node.getStringLiteral().getText().trim());
    }


    public void inAArrayExpr(AArrayExpr node){
    }
    public void outAArrayExpr(AArrayExpr node){
      /*  Node n = S.getHash(node.getArrayId().toString().trim(),"var");
        System.out.println("---->"+n.getSymbolName());*/
      String[]  right=argType.remove(argType.size()-1);
      String[] left =argType.remove(argType.size()-1);
      if(!right[0].equals("int") || !right[1].equals("0")){
          errorFound("[" +((AIdentifierExpr)node.getArrayId()).getId().getLine()+","+((AIdentifierExpr)node.getArrayId()).getId().getPos()+"] : Invalid access in array "+((AIdentifierExpr)node.getArrayId()).getId().getText().trim());
      }
      String[] entry= new String[2];
      entry[0]=left[0];
      if(Integer.parseInt(left[1]) -1<0){
          String[] tokens = node.getArrayId().toString().split(" ");
          errorFound(" Invalid access in array "+ tokens[0]);
      }
      entry[1]=(Integer.parseInt(left[1]) -1) +"";
      argType.add(entry);

      if(node.getArrayId() instanceof AIdentifierExpr || node.getArrayId() instanceof AStringExpr){
          String idx=quadList.remove(quadList.size()-1);
          String id=quadList.remove(quadList.size()-1);
          String q=IC.NEWTEMP(entry[0]).getName();
          IC.GENQUAD("array",id,idx,q);
          quadList.add(q);
      }else{
          String idx=quadList.remove(quadList.size()-1);
          String id=quadList.remove(quadList.size()-1);
          String q=IC.NEWTEMP(entry[0]).getName();
          IC.GENQUAD("array",id,idx,q);
          quadList.add(q);
      }

    }


    public void inAConstIntExpr(AConstIntExpr node){
        line=node.getConstInteger().getLine();

    }
    public void outAConstIntExpr(AConstIntExpr node){
        String [] entry = new String[2];
        entry[0]="int";
        entry[1]="0";
        argType.add(entry);
        quadList.add(node.getConstInteger().getText().trim());
    }


    public void inAConstCharExpr(AConstCharExpr node){
        line=node.getConstChar().getLine();

    }
    public void outAConstCharExpr(AConstCharExpr node){
        String [] entry = new String[2];
        entry[0]="char";
        entry[1]="0";
        argType.add(entry);
        quadList.add(node.getConstChar().getText().trim());
    }


    public void inASignedExpr(ASignedExpr node){
    }
    public void outASignedExpr(ASignedExpr node){
        String holder=quadList.remove(quadList.size()-1);
        if(node.getSign().toString().trim().equals("+")) {
            quadList.add(holder);
        }else{
            String entry=IC.NEWTEMP("int").getName();
            IC.GENQUAD("*",holder,"-1",entry);
            quadList.add(entry);
        }
    }


    public void inATimesExpr(ATimesExpr node){
    }
    public void outATimesExpr(ATimesExpr node){
        String [] right= argType.remove(argType.size()-1);

        String []  left= argType.remove(argType.size()-1);

        if (left[0].equals("int") && left[1].equals("0")&& right[0].equals("int") && right[1].equals("0")){
            String [] entry =new String[2];
            entry[0]="int";
            entry[1]="0";
            argType.add(entry);
        }else {
            errorFound("["+line+",-]: Operands must be of type int");
        }
        String RQ=quadList.remove(quadList.size()-1);
        String LQ=quadList.remove(quadList.size()-1);

        String entry=IC.NEWTEMP("int").getName();
        IC.GENQUAD("*",LQ,RQ,entry);
        quadList.add(entry);

    }


    public void inADivExpr(ADivExpr node){
    }
    public void outADivExpr(ADivExpr node){
        String [] right= argType.remove(argType.size()-1);

        String []  left= argType.remove(argType.size()-1);

        if (left[0].equals("int") && left[1].equals("0")&& right[0].equals("int") && right[1].equals("0")){
            String [] entry =new String[2];
            entry[0]="int";
            entry[1]="0";
            argType.add(entry);
        }else {
            errorFound("["+line+",-]: Operands must be of type int");
        }
        String RQ=quadList.remove(quadList.size()-1);
        String LQ=quadList.remove(quadList.size()-1);

        String entry=IC.NEWTEMP("int").getName();
        IC.GENQUAD("/",LQ,RQ,entry);
        quadList.add(entry);

    }


    public void inAModExpr(AModExpr node){
    }
    public void outAModExpr(AModExpr node){
        String [] right= argType.remove(argType.size()-1);

        String []  left= argType.remove(argType.size()-1);

        if (left[0].equals("int") && left[1].equals("0")&& right[0].equals("int") && right[1].equals("0")){
            String [] entry =new String[2];
            entry[0]="int";
            entry[1]="0";
            argType.add(entry);
        }else {
            errorFound("["+line+",-]: Operands must be of type int");
        }
        String RQ=quadList.remove(quadList.size()-1);
        String LQ=quadList.remove(quadList.size()-1);

        String entry=IC.NEWTEMP("int").getName();
        IC.GENQUAD("%",LQ,RQ,entry);
        quadList.add(entry);

    }


    public void inAPlusExpr(APlusExpr node){
    }
    public void outAPlusExpr(APlusExpr node){
        String [] right= argType.remove(argType.size()-1);

        String []  left= argType.remove(argType.size()-1);

        if (left[0].equals("int") && left[1].equals("0")&& right[0].equals("int") && right[1].equals("0")){
            String [] entry =new String[2];
            entry[0]="int";
            entry[1]="0";
            argType.add(entry);
        }else {
            errorFound("["+line+",-]: Operands must be of type int");
        }
        String RQ=quadList.remove(quadList.size()-1);
        String LQ=quadList.remove(quadList.size()-1);

        String entry=IC.NEWTEMP("int").getName();
        IC.GENQUAD("+",LQ,RQ,entry);
        quadList.add(entry);

    }


    public void inAMinusExpr(AMinusExpr node){
    }
    public void outAMinusExpr(AMinusExpr node){
        String [] right= argType.remove(argType.size()-1);

        String []  left= argType.remove(argType.size()-1);

        if (left[0].equals("int") && left[1].equals("0")&& right[0].equals("int") && right[1].equals("0")){
            String [] entry =new String[2];
            entry[0]="int";
            entry[1]="0";
            argType.add(entry);
        }else {
            errorFound("["+line+",-]: Operands must be of type int");
        }
        String RQ=quadList.remove(quadList.size()-1);
        String LQ=quadList.remove(quadList.size()-1);

        String entry=IC.NEWTEMP("int").getName();
        IC.GENQUAD("-",LQ,RQ,entry);
        quadList.add(entry);

    }


    public void inAFCallExpr(AFCallExpr node){
        Node f=S.getHash(node.getId().getText().trim(),"func");
        if(f==null){
            errorFound("["+node.getId().getLine()+","+node.getId().getPos()+"] : Function "+node.getId().getText()+" has not been declared");
        }
    }
    public void  outAFCallExpr(AFCallExpr node) {

        Node f = S.getHash(node.getId().getText().trim(), "func");

        List<String[]> Sub = argType.subList(argType.size() - node.getArg().size(), argType.size());
        f=f.match(Sub);
        if (f==null){
            errorFound("["+node.getId().getLine()+","+node.getId().getPos()+"] : Function call "+node.getId()+" does not match any declaration");
        }
        argType.removeAll(Sub);
        String[] entry = new String[2];
        entry[0]=f.getSymbol().getType();
        entry[1]="0";
        argType.add(entry);
    }

    public void  inAFullBracksExpr(AFullBracksExpr node){
        //System.out.println("ARRAY: "+node.toString());
        dimList.add(Integer.parseInt(node.getConstInteger().getText()));
    }
    public void outAFullBracksExpr(AFullBracksExpr node){

    }

    public void inAEmptyBracksExpr(AEmptyBracksExpr node){
        dimList.add(Integer.parseInt("0"));
    }
    public void outAEmptyBracksExpr(AEmptyBracksExpr node){

    }

    //SIGN
    public void inAPosSign(APosSign node){
    }
    public void outAPosSign(APosSign node){          }


    public void inANegSign(ANegSign node){
    }
    public void outANegSign(ANegSign node){          }


    //COND
    public void inAOpCond(AOpCond node){
    }
    public void outAOpCond(AOpCond node){
        String [] right= argType.remove(argType.size()-1);

        String []  left= argType.remove(argType.size()-1);
        if(!(left[0].equals("int") && left[1].equals("0"))|| !left[0].equals(right[0]) || !left[1].equals(right[1])){
            errorFound("["+line+",-]: Invalid comparison");
        }
        String RQ=quadList.remove(quadList.size()-1);
        String LQ=quadList.remove(quadList.size()-1);

        tempVar cond = IC.NEWTEMP("cond");
        labelList.add(IC.NEXTQUAD());
        cond.setTRUE(IC.MAKELIST(IC.NEXTQUAD()));
        IC.GENQUAD(node.getComparison().getText().trim(), LQ, RQ,"*");
        cond.setFALSE(IC.MAKELIST(IC.NEXTQUAD()));
        IC.GENQUAD("jump","-", "-","*");
        IC.BACKPATCH(cond.getTRUE(),IC.NEXTQUAD());
        condList.add(cond);

    }


    public void inAAndCond(AAndCond node){
    }
    public void outAAndCond(AAndCond node){
        tempVar cond2=condList.remove(condList.size()-1);

        tempVar cond1=condList.remove(condList.size()-1);


        String RLabel=labelList.remove(labelList.size()-1);
        String LLabel=labelList.remove(labelList.size()-1);


        IC.BACKPATCH(cond1.getTRUE(),RLabel);
        System.out.println("Next quad ="+ IC.NEXTQUAD());
        tempVar cond=IC.NEWTEMP("cond");
        labelList.add(IC.NEXTQUAD());
        cond.setFALSE(IC.MERGE(cond1.getFALSE(),cond2.getFALSE()));
        cond.setTRUE(cond2.getTRUE());
        condList.add(cond);

    }


    public void inAOrCond(AOrCond node){
    }
    public void outAOrCond(AOrCond node){
        tempVar right=condList.remove(condList.size()-1);
        tempVar left=condList.remove(condList.size()-1);


        String RLabel=labelList.remove(labelList.size()-1);
        String LLabel=labelList.remove(labelList.size()-1);
        IC.BACKPATCH(left.getFALSE(),RLabel);
        tempVar cond=IC.NEWTEMP("cond");
        labelList.add(IC.NEXTQUAD());
        cond.setTRUE(IC.MERGE(left.getTRUE(),right.getTRUE()));
        cond.setFALSE(left.getFALSE());
        condList.add(cond);
    }


    public void inANotCond(ANotCond node){
    }
    public void outANotCond(ANotCond node){
        tempVar poppedCond=condList.remove(condList.size()-1);
        tempVar cond=IC.NEWTEMP("cond");
        cond.setTRUE(poppedCond.getFALSE());
        cond.setFALSE(poppedCond.getTRUE());
        condList.add(cond);
    }


    //ARG
    public void inAExprArg(AExprArg node){
    }
    public void outAExprArg(AExprArg node){          }


    //DATA_TYPE
    public void inAIntegerDataType(AIntegerDataType node){
    }
    public void outAIntegerDataType(AIntegerDataType node){          }


    public void inACharacterDataType(ACharacterDataType node){
    }
    public void outACharacterDataType(ACharacterDataType node){          }


    public void inAFTypeDataType(AFTypeDataType node) {
        dimList= new ArrayList<Integer>();
    }
    public void outAFTypeDataType(AFTypeDataType node){
        /*If Function Parameter is an array */
        if(node.getDims()!=null && node.getDims().size()>0  || node.getEmpty()!=null){
            int total=node.getDims().size();
            /*Adding dimensions in last variableList Entry*/
            for( Variable v: varList) {
                if(v instanceof ArrayVar){
                ((ArrayVar) v).setSize(dimList);
                }
            }
        }
        dimList.clear();
    }


    //FPAR_DEF
    public void inAFDefinitionFparDef(AFDefinitionFparDef node){
        varList=new ArrayList<Variable>();  /*Varlist contains variables in fpardef*/
        /*Adding function parameter in temporary varList ArrayList*/
        varList.clear();
        varList=new ArrayList<Variable>();
        if(((AFTypeDataType) node.getDataType()).getDims()!=null && ((AFTypeDataType) node.getDataType()).getDims().size()>0 || ((AFTypeDataType)node.getDataType()).getEmpty()!=null){
            if(!addVar(new ArrayVar(node.getId().getText().trim(),((AFTypeDataType)node.getDataType()).getDataType().toString().trim(),node.getKeyRef()!=null,"local"))){
                  /*If such parameter already exists in function definition, print out error message and exit program*/
                errorFound( " ["+ node.getId().getLine()+","+node.getId().getPos()+"] Redefinition of parameter "+node.getId().toString());
            }
            for (int i = 0; i <node.getIds().size() ; i++) {
                if(!addVar(new ArrayVar(((AFIdExpr)node.getIds().get(i)).getId().getText().trim(),((AFTypeDataType)node.getDataType()).getDataType().toString().trim(),node.getKeyRef() != null,"local"))){
                         /*If such parameter already exists in function definition, print out error message and exit program*/
                    errorFound( " ["+ ((AFIdExpr)node.getIds().get(i)).getId().getLine()+","+((AFIdExpr)node.getIds().get(i)).getId().getPos()+"] Redefinition of parameter "+((AFIdExpr)node.getIds().get(i)).getId().getText().trim());
                }
            }
        }else {
            if(!addVar(new Variable(node.getId().getText().trim(),((AFTypeDataType)node.getDataType()).getDataType().toString().trim(),node.getKeyRef()!=null,"local"))){
                  /*If such parameter already exists in function definition, print out error message and exit program*/
                errorFound( " ["+ node.getId().getLine()+","+node.getId().getPos()+"] Redefinition of parameter "+node.getId().toString());
            }
            for (int i = 0; i <node.getIds().size() ; i++) {
                if(!addVar(new Variable(((AFIdExpr)node.getIds().get(i)).getId().getText().trim(),((AFTypeDataType)node.getDataType()).getDataType().toString().trim(),node.getKeyRef() != null,"local"))){
                         /*If such parameter already exists in function definition, print out error message and exit program*/
                    errorFound( " ["+ ((AFIdExpr)node.getIds().get(i)).getId().getLine()+","+((AFIdExpr)node.getIds().get(i)).getId().getPos()+"] Redefinition of parameter "+((AFIdExpr)node.getIds().get(i)).getId().getText().trim());
                }
            }
        }
    }
    public void outAFDefinitionFparDef(AFDefinitionFparDef node){
        for (Variable v: varList ) {
            paramList.add(v);
        }
    }


    //RET_TYPE
    public void inANothingRetType(ANothingRetType node){
    }
    public void outANothingRetType(ANothingRetType node){ }


    public void inADataTypeRetType(ADataTypeRetType node){    }
    public void outADataTypeRetType(ADataTypeRetType node){}


    //TYPE
    public void inAArrayType(AArrayType node){    }
    public void outAArrayType(AArrayType node){  }


    //HEADER
    public void inAFHeaderHeader(AFHeaderHeader node){
        /*If function Parameters' List is not empty, a new varList is being created to temporarily store parameters while traversing tree*/
        if (node.getList() != null) {
            paramList=new ArrayList<Variable>();
            varList= new ArrayList<Variable>();
        }

    }
    public void outAFHeaderHeader(AFHeaderHeader node){
        /*Insert new function node in stack of Symbol Table*/
        Node n=new Node(node.getId().getText().trim(),node.getRetType().toString().trim(),paramList);
        /*Check for previous definition of function*/
        if(S.lookup(n)==true ) {
            Node f=S.getFunction(n);
            if(f!=null){    /*Record found in symbol table is a function*/
                if(((Function)f.getSymbol()).isDefined()){

                    errorFound(" [" + node.getId().getLine() + "," + node.getId().getPos() + "]: Redefinition of function " + node.getId().getText());
                }else {
                    if(declarationFlag) {
                        errorFound("["+node.getId().getLine()+","+node.getId().getPos()+"]: Too many declarations of function "+node.getId().getText());
                    }else {
                        ((Function) f.getSymbol()).setDefined();
                        ((Function) f.getSymbol()).setParams(((Function)n.getSymbol()).getParams());
                    }
                }
            }
        }else {
            S.insert(n);   /*Inserting Function in Symbol Table with current scope*/
        }
        if(declarationFlag){return;}
        ((Function) n.getSymbol()).setDefined();
        S.enter();     /*Definition of Function means entering a new scope*/

        /*Insert parameters of function in the Symbol Table*/
        for (Variable var :paramList) {
            if (var instanceof ArrayVar) {
                S.insert(new Node(var.getName(),var.getType(),var.getReference(),var.getLocality() ,((ArrayVar) var).getSize()));
            }else{
                S.insert(new Node(var.getName(),var.getType(),var.getReference(),var.getLocality()));
            }
        }
    }


    //LOCAL_DEF
    public void inAFuncDefLocalDef(AFuncDefLocalDef node){
        returnStack.push(((AFHeaderHeader)node.getHeader()).getRetType().toString().trim());
        retFlag=false;
        declarationFlag=false;
    }
    public void outAFuncDefLocalDef(AFuncDefLocalDef node){
        Node n=S.getHash(((AFHeaderHeader)node.getHeader()).getId().getText().trim(),"func");

        if(retFlag==false && !((AFHeaderHeader)node.getHeader()).getRetType().toString().trim().equals("nothing")){
            errorFound("["+((AFHeaderHeader) node.getHeader()).getId().getLine()+","+((AFHeaderHeader) node.getHeader()).getId().getPos()+"] : No return statement found in function \""+((AFHeaderHeader) node.getHeader()).getId()+"\"");
        }
        if(!S.exit()){
            errorFound(" Some declared functions in "+((AFHeaderHeader) node.getHeader()).getId().getText().trim()+" were never implemented");
        }      /*When done with function definition, function's scope is being destroyed*/
    }


    public void inAFHeadercolonLocalDef(AFHeadercolonLocalDef node){
        declarationFlag=true;
    }
    public void outAFHeadercolonLocalDef(AFHeadercolonLocalDef node){
        //S.getHash(((AFHeaderHeader)node.getHeader()).getId().getText().trim(),"func").print();
        declarationFlag=false;
    }


    public void inAVariableDefLocalDef(AVariableDefLocalDef node){
        if (((AArrayType)node.getType()).getExpr()!=null && ((AArrayType)node.getType()).getExpr().size()!=0){
            dimList=new ArrayList<Integer>();
        }
        varList=new ArrayList<Variable>();
        //System.out.println(((AArrayType)node.getType()).getExpr().toString());
    }
    public void outAVariableDefLocalDef(AVariableDefLocalDef node){
        Node n;
        String id;
        if (((AArrayType)node.getType()).getDataType() instanceof  AIntegerDataType){
            /*If variable is not an array */
            if(((AArrayType)node.getType()).getExpr()==null ||((AArrayType)node.getType()).getExpr().size()==0 ) {
                n= new Node(node.getId().getText().trim(), "int", false, "-");
                if (S.lookup(n)){
                    errorFound( " ["+ node.getId().getLine()+","+node.getId().getPos()+"] Redefinition of variable "+node.getId().toString());
                }
                S.insert(n);
                for (int i = 0; i <node.getList().size() ; i++) {
                    id= node.getList().get(i).toString().trim();
                    n=  new Node(id, "int", false, "-");
                    if (S.lookup(n)){
                        errorFound( " ["+ ((AFIdExpr)(node.getList().get(i))).getId().getLine()+","+((AFIdExpr)(node.getList().get(i))).getId().getPos()+"] Redefinition of variable "+((AFIdExpr)(node.getList().get(i))).getId().getText());
                    }
                    S.insert(n);
                }
            }else {
                n= new Node(node.getId().getText().trim(),"int",false,"-", dimList);
                if (S.lookup(n)){
                    errorFound( " ["+ node.getId().getLine()+","+node.getId().getPos()+"] Redefinition of variable "+node.getId().toString());
                }
                S.insert(n);
                for (int i = 0; i <node.getList().size() ; i++) {
                    id= node.getList().get(i).toString().trim();
                    n=  new Node(id, "int", false, "-",dimList);
                    if (S.lookup(n)){
                        errorFound( " ["+ ((AFIdExpr)(node.getList().get(i))).getId().getLine()+","+((AFIdExpr)(node.getList().get(i))).getId().getPos()+"] Redefinition of variable "+((AFIdExpr)(node.getList().get(i))).getId().getText());
                    }
                    S.insert(n);
                }
            }
        }else if(((AArrayType)node.getType()).getDataType() instanceof  ACharacterDataType){
            /*If variable is not an array */
            if(((AArrayType)node.getType()).getExpr()==null ||((AArrayType)node.getType()).getExpr().size()==0 ) {
                n=  new Node(node.getId().getText().trim(), "char", false, "-");
                if (S.lookup(n)){
                    errorFound( " ["+ node.getId().getLine()+","+node.getId().getPos()+"] Redefinition of variable "+node.getId().toString());
                }
                S.insert(n);
                for (int i = 0; i <node.getList().size() ; i++) {
                    id= node.getList().get(i).toString().trim();
                    n=  new Node(id, "char", false, "-");
                    if (S.lookup(n)){
                        errorFound( " ["+ ((AFIdExpr)(node.getList().get(i))).getId().getLine()+","+((AFIdExpr)(node.getList().get(i))).getId().getPos()+"] Redefinition of variable "+((AFIdExpr)(node.getList().get(i))).getId().getText());
                    }
                    S.insert(n);
                }
            }else {
                n = new Node(node.getId().getText().trim(),"char",false,"-", dimList);
                if (S.lookup(n)){
                    errorFound( " ["+ node.getId().getLine()+","+node.getId().getPos()+"] Redefinition of variable "+node.getId().toString());
                }
                S.insert(n);
                for (int i = 0; i <node.getList().size() ; i++) {
                    id= node.getList().get(i).toString().trim();
                    n=  new Node(id, "char", false, "-",dimList);
                    if (S.lookup(n)){
                        errorFound( " ["+ ((AFIdExpr)(node.getList().get(i))).getId().getLine()+","+((AFIdExpr)(node.getList().get(i))).getId().getPos()+"] Redefinition of variable "+((AFIdExpr)(node.getList().get(i))).getId().getText());
                    }
                    S.insert(n);
                }
            }
        }
    }


    //STMNT
    public void inAAssignStmnt(AAssignStmnt node){
    }
    public void outAAssignStmnt(AAssignStmnt node){
        String [] right= argType.remove(argType.size()-1);
        String []  left= argType.remove(argType.size()-1);
        if(!left[0].equals(right[0]) || !left[1].equals(right[1])){
            errorFound("["+line+",-]: Invalid assignment");
        }
        String RQ=quadList.remove(quadList.size()-1);
        String LQ=quadList.remove(quadList.size()-1);
        labelList.add(IC.NEXTQUAD());
        IC.GENQUAD("<-",RQ,"-",LQ);
        quadList.add(LQ);
    }


    public void inAFuncCallStmnt(AFuncCallStmnt node){
    }
    public void outAFuncCallStmnt(AFuncCallStmnt node){          }


    public void inAIfStmnt(AIfStmnt node){
    }
    public void outAIfStmnt(AIfStmnt node){
        tempVar condR=condList.remove(condList.size()-1);
        tempVar cond=IC.NEWTEMP("cond");
        cond.setTRUE(condR.getTRUE());
        cond.setFALSE(condR.getFALSE());
        IC.BACKPATCH(cond.getFALSE(),IC.NEXTQUAD());

    }


    public void inAIfElseStmnt(AIfElseStmnt node){
    }
    public void outAIfElseStmnt(AIfElseStmnt node){
        tempVar condR=condList.remove(condList.size()-1);
        tempVar cond=IC.NEWTEMP("cond");
        cond.setTRUE(condR.getTRUE());
    }


    public void inAWhileStmntStmnt(AWhileStmntStmnt node){
    }
    public void outAWhileStmntStmnt(AWhileStmntStmnt node){          }


    public void inAReturnStmnt(AReturnStmnt node){

    }
    public void outAReturnStmnt(AReturnStmnt node){
        retFlag=true;
        String expected=returnStack.peek();
        if (expected.equals("nothing") && node.getExpr()!=null){
            errorFound("["+line+",-]: Ivalid return value - function returns \"nothing\" ");
        }
        if(node.getExpr()==null){
            if(!expected.equals("nothing")){
                errorFound("["+line+",-]: Ivalid return value - Function returns nothing, while \""+ expected +"\" is expected");
            }else{return;}
        }
//        System.out.println(argType.get(argType.size()-1)[0]+"   "+argType.get(argType.size()-1)[1]);
        if(!argType.get(argType.size()-1)[0].equals(expected) || !argType.get(argType.size()-1)[1].equals("0") ){
            errorFound("["+line+",-]: Ivalid return value - Function returns \"" +argType.get(argType.size()-1)[0]+"\", while \""+expected+"\" is expected");
        }
        retFlag=true;
    }


    public void inAIfIfStmnt(AIfIfStmnt node){
    }
    public void outAIfIfStmnt(AIfIfStmnt node){          }


    public void inAWElseStmnt(AWElseStmnt node){
    }
    public void outAWElseStmnt(AWElseStmnt node){            }


    public void inAReturnSthStmnt(AReturnSthStmnt node){

    }
    public void outAReturnSthStmnt(AReturnSthStmnt node){            }



    public void inAEmptyStmntStmnt(AEmptyStmntStmnt node){
    }
    public void outAEmptyStmntStmnt(AEmptyStmntStmnt node){          }


    public  void inABlockStmnt(ABlockStmnt node){

    }
    public void outABlockStmnt(ABlockStmnt node){            }


    //PROGRAM
    public void  inAFDefProgram(AFDefProgram node){
        S.getScope().enter();       /*Creating outer scope*/
        InsertLibrary();
        argType=new ArrayList<String[]>();
        returnStack =new Stack<String>();
    }
    public void outAFDefProgram(AFDefProgram node){
        S.getScope().exit();        /*Destroying outer scope*/
        IC.print();
    }
}