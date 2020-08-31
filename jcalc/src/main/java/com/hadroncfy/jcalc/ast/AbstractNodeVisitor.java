package com.hadroncfy.jcalc.ast;

public abstract class AbstractNodeVisitor implements NodeVisitor {

    protected boolean enterDefault(Node node){
        return false;
    }

    protected Node leaveDefault(Node node){
        return node;
    }

    protected Node visitDefault(Node node){
        return node;
    }

    @Override
    public boolean enter(BinaryNode node) {
        return enterDefault(node);
    }

    @Override
    public Node leave(BinaryNode node) {
        return leaveDefault(node);
    }

    @Override
    public boolean enter(UnaryNode node) {
        return enterDefault(node);
    }

    @Override
    public Node leave(UnaryNode node) {
        return leaveDefault(node);
    }

    @Override
    public boolean enter(FunctionNode node) {
        return enterDefault(node);
    }

    @Override
    public Node leave(FunctionNode node) {
        return leaveDefault(node);
    }

    @Override
    public boolean enter(AssignNode node) {
        return enterDefault(node);
    }

    @Override
    public Node leave(AssignNode node) {
        return leaveDefault(node);
    }


    @Override
    public boolean enter(DeleteNode node) {
        return enterDefault(node);
    }

    @Override
    public Node leave(DeleteNode node) {
        return leaveDefault(node);
    }

    @Override
    public Node visit(NumberNode node) {
        return visitDefault(node);
    }

    @Override
    public Node visit(VariableNode node) {
        return visitDefault(node);
    }
    
}