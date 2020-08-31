package com.hadroncfy.jcalc.ast;

import com.hadroncfy.jcalc.parser.TextRange;

public class BinaryNode implements Node {
    private Node left, right;
    private final TextRange range;
    private final int type;

    public BinaryNode(TextRange range, int type,  Node left, Node right){
        this.left = left;
        this.right = right;
        this.range = range;
        this.type = type;
    }

    public int getType(){
        return type;
    }

    @Override
    public Node accept(NodeVisitor visitor) throws VisitTerminatedException {
        if (visitor.enter(this)){
            return this;
        }
        left = left.accept(visitor);
        right = right.accept(visitor);
        return visitor.leave(this);
    }

    @Override
    public TextRange getTextRange() {
        return range;
    }
    
}