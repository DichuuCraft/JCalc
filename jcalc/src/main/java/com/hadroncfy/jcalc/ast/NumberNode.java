package com.hadroncfy.jcalc.ast;

import com.hadroncfy.jcalc.parser.TextRange;
import com.hadroncfy.jcalc.run.NumberHolder;

public class NumberNode implements Node {
    private final NumberHolder val;
    private final boolean imag;
    private final TextRange range;

    public NumberNode(TextRange range, NumberHolder val, boolean imag){
        this.range = range;
        this.val = val;
        this.imag = imag;
    }

    @Override
    public Node accept(NodeVisitor visitor) throws VisitTerminatedException {
        return visitor.visit(this);
    }

    @Override
    public TextRange getTextRange() {
        return range;
    }

    public NumberHolder getVal(){
        return val;
    }

    public boolean isImag(){
        return imag;
    }
}