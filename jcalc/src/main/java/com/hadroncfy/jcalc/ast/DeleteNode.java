package com.hadroncfy.jcalc.ast;

import com.hadroncfy.jcalc.parser.TextRange;

public class DeleteNode implements Node {
    private final TextRange range;
    private Node child;

    public DeleteNode(TextRange range, Node child) {
        this.range = range;
        this.child = child;
    }

    @Override
    public Node accept(NodeVisitor visitor) throws VisitTerminatedException {
        if (visitor.enter(this)){
            return this;
        }
        child = child.accept(visitor);
        return visitor.leave(this);
    }

    @Override
    public TextRange getTextRange() {
        return range;
    }

    public Node getChild(){
        return child;
    }
}