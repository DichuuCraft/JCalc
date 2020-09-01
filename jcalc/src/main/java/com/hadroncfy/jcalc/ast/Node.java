package com.hadroncfy.jcalc.ast;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.hadroncfy.jcalc.parser.TextRange;

public interface Node {
    Node accept(NodeVisitor visitor) throws VisitTerminatedException;
    TextRange getTextRange();

    static void acceptAll(List<Node> nodes, NodeVisitor visitor) throws VisitTerminatedException {
        for (ListIterator<Node> it = nodes.listIterator(); it.hasNext();){
            Node node = it.next();
            Node n = node.accept(visitor);
            if (n != null){
                it.set(n);
            }
        }
    }
}