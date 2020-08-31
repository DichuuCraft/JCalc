package com.hadroncfy.jcalc.ast;

import com.hadroncfy.jcalc.parser.TextRange;

public class AssignNode implements Node {
    private final TextRange range;
    private Node left, right;

    public AssignNode(TextRange range, Node left, Node right){
        this.left = left;
        this.right = right;
        this.range = range;
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
    
    public Node getLeft(){
        return left;
    }

    public Node getRight(){
        return right;
    }
}