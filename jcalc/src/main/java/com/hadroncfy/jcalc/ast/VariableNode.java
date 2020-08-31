package com.hadroncfy.jcalc.ast;

import com.hadroncfy.jcalc.parser.TextRange;

public class VariableNode implements Node {
    private final TextRange range;
    private final String name;

    public VariableNode(TextRange range, String name){
        this.range = range;
        this.name = name;
    }

    @Override
    public Node accept(NodeVisitor visitor) throws VisitTerminatedException {
        return visitor.visit(this);
    }

    @Override
    public TextRange getTextRange() {
        return range;
    }
    
    public String getName(){
        return name;
    }
}