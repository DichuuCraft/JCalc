package com.hadroncfy.jcalc.ast;

import com.hadroncfy.jcalc.parser.TextRange;

public interface Node {
    Node accept(NodeVisitor visitor) throws VisitTerminatedException;
    TextRange getTextRange();
}