package com.hadroncfy.jcalc.ast;

import java.util.List;

import com.hadroncfy.jcalc.parser.TextRange;

public class ExprListNode implements Node {
    private final List<Node> nodes;

    public ExprListNode(List<Node> nodes){
        this.nodes = nodes;
    }

    @Override
    public Node accept(NodeVisitor visitor) throws VisitTerminatedException {
        if (visitor.enter(this)){
            return this;
        }
        Node.acceptAll(nodes, visitor);
        return visitor.leave(this);
    }

    @Override
    public TextRange getTextRange() {
        return TextRange.between(nodes.get(0).getTextRange(), nodes.get(nodes.size() - 1).getTextRange());
    }
    
}