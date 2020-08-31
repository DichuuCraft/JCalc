package com.hadroncfy.jcalc.ast;

import com.hadroncfy.jcalc.parser.TextRange;

public class LeftVariableNode extends VariableNode {

    public LeftVariableNode(TextRange range, String name) {
        super(range, name);
    }
    
}