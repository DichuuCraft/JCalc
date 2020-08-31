package com.hadroncfy.jcalc.parser;

import com.hadroncfy.jcalc.ast.AbstractNodeVisitor;
import com.hadroncfy.jcalc.ast.Node;
import com.hadroncfy.jcalc.ast.VariableNode;

public class LeftValueChecker extends AbstractNodeVisitor {
    private boolean status = true;

    public boolean getStatus(){
        return status;
    }

    @Override
    protected boolean enterDefault(Node node) {
        status = false;
        return super.enterDefault(node);
    }

    @Override
    protected Node visitDefault(Node node) {
        status = false;
        return super.visitDefault(node);
    }

    @Override
    public Node visit(VariableNode node) {
        return node;
    }
}