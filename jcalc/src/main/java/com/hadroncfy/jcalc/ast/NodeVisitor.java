package com.hadroncfy.jcalc.ast;

public interface NodeVisitor {
    boolean enter(BinaryNode node) throws VisitTerminatedException;
    Node leave(BinaryNode node) throws VisitTerminatedException;

    boolean enter(UnaryNode node) throws VisitTerminatedException;
    Node leave(UnaryNode node) throws VisitTerminatedException;

    boolean enter(FunctionNode node) throws VisitTerminatedException;
    Node leave(FunctionNode node) throws VisitTerminatedException;

    boolean enter(AssignNode node) throws VisitTerminatedException;
    Node leave(AssignNode node) throws VisitTerminatedException;

    boolean enter(DeleteNode node) throws VisitTerminatedException;
    Node leave(DeleteNode node) throws VisitTerminatedException;

    Node visit(NumberNode node) throws VisitTerminatedException;
    Node visit(VariableNode node) throws VisitTerminatedException;
}