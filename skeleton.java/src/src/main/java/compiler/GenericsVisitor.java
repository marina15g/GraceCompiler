package compiler;

import compiler.analysis.DepthFirstAdapter;
import compiler.node.*;

import static jdk.nashorn.internal.objects.Global.print;

public class GenericsVisitor extends DepthFirstAdapter {
    private int index = 0;
    SymbolTable S;

    public GenericsVisitor(){
        S=new SymbolTable();
    }

    private void index(){
        for (int i = 0; i < index; i++) {
            System.out.print("    |"+index+"|" );
        }
    }

    //EXPR
    public void inAIdentifierExpr(AIdentifierExpr node){
        index();
        System.out.println(node.getId().getText());
        index++;
    }
    public  void outAIdentifierExpr(AIdentifierExpr node){  index--;    }


    public void inAFIdExpr(AFIdExpr node){
        index();
        System.out.println(node.getId().getText());
        index++;
    }
    public void outAFIdExpr(AFIdExpr node){     index--;    }


    public void inAStringExpr(AStringExpr node){
        index();
        System.out.println(node.getStringLiteral().getText());
        index++;
    }
    public void outAStringExpr(AStringExpr node){   index--;    }


    public void inAArrayExpr(AArrayExpr node){
        index();
        System.out.println("array id: "+node.getArrayId().toString() + " array index:" + node.getArrayIdx().toString());
        index++;
    }
    public void outAArrayExpr(AArrayExpr node){   index--;    }


    public void inAConstIntExpr(AConstIntExpr node){
        index();
        System.out.println(node.getConstInteger().getText());
        index++;
    }
    public void outAConstIntExpr(AConstIntExpr node){   index--;    }


    public void inAConstCharExpr(AConstCharExpr node){
        index();
        System.out.println(node.getConstChar().getText());
        index++;
    }
    public void outAConstCharExpr(AConstCharExpr node){     index--;    }


    public void inASignedExpr(ASignedExpr node){
        index();
        System.out.println(node.getSign().toString() + "\t" + node.getExpr().toString());
        index++;
    }
    public void outASignedExpr(ASignedExpr node){   index--;    }


    public void inATimesExpr(ATimesExpr node){
        index();
        System.out.println(node.getMultiplier().toString() + node.getMultiplicand().toString());
        index++;
    }
    public void outATimesExpr(ATimesExpr node){     index--;    }


    public void inADivExpr(ADivExpr node){
        index();
        System.out.println(node.getDividend().toString() + node.getDivisor().toString());
        index++;
    }
    public void outADivExpr(ADivExpr node){    index--;}


    public void inAModExpr(AModExpr node){
        index();
        System.out.println(node.getDivisor().toString() + node.getDivisor().toString());
        index++;
    }
    public void outAModExpr(AModExpr node){     index--;    }


    public void inAPlusExpr(APlusExpr node){
        index();
        System.out.println(node.getAdded1().toString() + node.getAdded2().toString());
        index++;
    }
    public void outAPlusExpr(APlusExpr node){   index--;}


    public void inAMinusExpr(AMinusExpr node){
        index();
        System.out.println(node.getReduced().toString() + node.getSubtrahend().toString());
        index++;
    }
    public void outAMinusExpr(AMinusExpr node){     index--;    }


    public void inAFCallExpr(AFCallExpr node){
        index();
        System.out.println(node.getId().getText() + node.getArg().toString());
        index++;
    }
    public void  outAFCallExpr(AFCallExpr node){        index--;    }


    public void  inAFullBracksExpr(AFullBracksExpr node){
        index();
        System.out.println(node.getConstInteger().getText());
        index++;
    }
    public void outAFullBracksExpr(AFullBracksExpr node){       index--;    }

    //SIGN
    public void inAPosSign(APosSign node){
        index();
        System.out.println(node.getPlus().getText());
        index++;
    }
    public void outAPosSign(APosSign node){     index--;    }


    public void inANegSign(ANegSign node){
        index();
        System.out.println(node.getMinus().getText());
        index++;
    }
    public void outANegSign(ANegSign node){     index--;    }


    //COND
    public void inAOpCond(AOpCond node){
        index();
        System.out.println(node.getLeft().toString() + node.getRight().toString());
        index++;
    }
    public void outAOpCond(AOpCond node){       index--;     }


    public void inAAndCond(AAndCond node){
        index();
        System.out.println(node.getOne().toString() + node.getTwo().toString());
        index++;
    }
    public void outAAndCond(AAndCond node){     index--;    }


    public void inAOrCond(AOrCond node){
        index();
        System.out.println(node.getOne().toString() + node.getTwo().toString());
        index++;
    }
    public void outAOrCond(AOrCond node){      index--;    }


    public void inANotCond(ANotCond node){
        index();
        System.out.println(node.getCond().toString());
        index++;
    }
    public void outANotCond(ANotCond node){     index--;    }


    //ARG
    public void inAExprArg(AExprArg node){
        index();
        System.out.println(node.getExpr().toString());
        index++;
    }
    public void outAExprArg(AExprArg node){     index--;    }


    //DATA_TYPE
    public void inAIntegerDataType(AIntegerDataType node){
        index();
        System.out.println(node.getKeyInt().getText());
        index++;
    }
    public void outAIntegerDataType(AIntegerDataType node){     index--;    }


    public void inACharacterDataType(ACharacterDataType node){
        index();
        System.out.println(node.getKeyChar().getText());
        index++;
    }
    public void outACharacterDataType(ACharacterDataType node){     index--;    }


    public void inAFTypeDataType(AFTypeDataType node){
        index();
       // System.out.println(node.getDataType().toString() + "\t" + node.getExpr().toString());
        index++;
    }
    public void outAFTypeDataType(AFTypeDataType node){     index--;}


    //FPAR_DEF
    public void inAFDefinitionFparDef(AFDefinitionFparDef node){
        index();
        if(node.getKeyRef() != null)    System.out.print(node.getKeyRef().getText() + " ");
        System.out.print(node.getId().getText());
        if (node.getExpr() != null)     System.out.print(node.getExpr().toString() + " ");
        System.out.println(node.getDataType().toString());
        index++;
    }
    public void outAFDefinitionFparDef(AFDefinitionFparDef node){       index--;    }


    //RET_TYPE
    public void inANothingRetType(ANothingRetType node){
        index();
        System.out.println(node.getKeyNothing().getText());
        index++;
    }
    public void outANothingRetType(ANothingRetType node){       index--;    }


    public void inADataTypeRetType(ADataTypeRetType node){
        index();
        System.out.println(node.getDataType().toString());
        index++;
    }
    public void outADataTypeRetType(ADataTypeRetType node){     index--;    }


    //TYPE
    public void inAArrayType(AArrayType node){
        index();
        System.out.print(node.getDataType().toString());
        if (node.getExpr() != null)     System.out.println(node.getExpr().toString());
        else System.out.println();
        index++;
    }
    public void outAArrayType(AArrayType node){     index--;    }


    //HEADER
    public void inAFHeaderHeader(AFHeaderHeader node){
        index();

        System.out.print(node.getId().getText());
        if (node.getList() != null) {
            System.out.print(node.getList().get(0).getClass());
        }else {
            System.out.println("Nosssss");
        }
       // if (node.getMore() != null)     System.out.print(node.getMore().toString());
        System.out.println(node.getRetType().toString());
        index++;
        S.insert(new Node(node.getId().toString(),node.getRetType().toString(),null));
        S.enter();
    }
    public void outAFHeaderHeader(AFHeaderHeader node){
        S.exit();
        index--;
    }


    //LOCAL_DEF
    public void inAFuncDefLocalDef(AFuncDefLocalDef node){
        index();
        System.out.print(node.getHeader().toString());
        if (node.getLocalDef() != null)     System.out.println(node.getLocalDef().toString());
        if (node.getStmnt() != null)        System.out.println(node.getStmnt().toString());
        index++;
    }
    public void outAFuncDefLocalDef(AFuncDefLocalDef node){     index--;    }


    public void inAFHeadercolonLocalDef(AFHeadercolonLocalDef node){
        index();
        System.out.println(node.getHeader().toString());
        index++;
    }
    public void outAFHeadercolonLocalDef(AFHeadercolonLocalDef node){       index--;    }


    public void inAVariableDefLocalDef(AVariableDefLocalDef node){
       /* index();
        System.out.print(node.getId().getText());
        if (node.getExpr() != null)     System.out.println(node.getExpr().toString());
        System.out.println(node.getType().toString());
        index++;*/
    }
    public void outAVariableDefLocalDef(AVariableDefLocalDef node){     index--;    }


    //STMNT
    public void inAAssignStmnt(AAssignStmnt node){
        index();
        System.out.println(node.getLeft().toString() + node.getRight().toString());
        index++;
    }
    public void outAAssignStmnt(AAssignStmnt node){     index--;    }


    public void inAFuncCallStmnt(AFuncCallStmnt node){
        index();
        System.out.println(node.getExpr().toString());
        index++;
    }
    public void outAFuncCallStmnt(AFuncCallStmnt node){     index--;    }


    public void inAIfStmnt(AIfStmnt node){
        index();
        System.out.println(node.getCond().toString() + node.getStmnt().toString());
        index++;
    }
    public void outAIfStmnt(AIfStmnt node){     index--;    }


    public void inAIfElseStmnt(AIfElseStmnt node){
        index();
        System.out.println(node.getCond().toString() + node.getLeft().toString() + node.getRight().toString());
        index++;
    }
    public void outAIfElseStmnt(AIfElseStmnt node){     index--;}


    public void inAWhileStmntStmnt(AWhileStmntStmnt node){
        index();
        System.out.println(node.getCond().toString() + node.getStmnt().toString());
        index++;
    }
    public void outAWhileStmntStmnt(AWhileStmntStmnt node){     index--;    }


    public void inAReturnStmnt(AReturnStmnt node){
        index();
        if (node.getExpr() != null)     System.out.println(node.getExpr().toString());
        index++;
    }
    public void outAReturnStmnt(AReturnStmnt node){     index--;    }


    public void inAIfIfStmnt(AIfIfStmnt node){

        index();
        System.out.println(node.getCond().toString() + node.getLeft().toString() + node.getRight().toString());
        index++;
    }
    public void outAIfIfStmnt(AIfIfStmnt node){     index--;    }


    public void inAWElseStmnt(AWElseStmnt node){
        index();
        System.out.println(node.getCond().toString() + node.getStmnt().toString());
        index++;
    }
    public void outAWElseStmnt(AWElseStmnt node){       index--;    }


    public void inAReturnSthStmnt(AReturnSthStmnt node){
        index();
        if (node.getExpr() != null )    System.out.println(node.getExpr().toString());
        index++;
    }
    public void outAReturnSthStmnt(AReturnSthStmnt node){       index--;    }


    public void inAEmptyStmntStmnt(AEmptyStmntStmnt node){
        index();
        System.out.println(node.getSemicolon().getText());
        index++;
    }
    public void outAEmptyStmntStmnt(AEmptyStmntStmnt node){     index--;    }


    public  void inABlockStmnt(ABlockStmnt node){
        index();
        if (node.getStmnt() != null)    System.out.println(node.getStmnt().toString());
        index++;
    }
    public void outABlockStmnt(ABlockStmnt node){       index--;    }


    //PROGRAM
    public void  inAFDefProgram(AFDefProgram node){
        S.getScope().enter();
        index();
        System.out.println(node.getLocalDef().toString());
        index++;
    }
    public void outAFDefProgram(AFDefProgram node){
        index--;
        S.getScope().exit();
    }
}