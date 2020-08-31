package com.hadroncfy.jcalc.ast;

import com.hadroncfy.jcalc.parser.TextRange;

public class UnaryNode implements Node {
    private final TextRange range;
    private final int type;

    private Node child;

    public UnaryNode(TextRange range, int type, Node child){
        this.range = range;
        this.type = type;
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

    public int getType(){
        return type;
    }
}