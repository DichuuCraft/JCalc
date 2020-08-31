package com.hadroncfy.jcalc.ast;

import java.util.List;
import java.util.ListIterator;

import com.hadroncfy.jcalc.parser.TextRange;

public class FunctionNode implements Node {
    private final TextRange range;
    private final String name;
    private final List<Node> args;

    public FunctionNode(TextRange range, String name, List<Node> args){
        this.range = range;
        this.name = name;
        this.args = args;
    }

    @Override
    public Node accept(NodeVisitor visitor) throws VisitTerminatedException {
        if (visitor.enter(this)){
            return this;
        }
        for (ListIterator<Node> it = args.listIterator(); it.hasNext(); ){
            Node node = it.next();
            Node node2 = node.accept(visitor);
            if (node != node2){
                it.set(node2);
            }
        }
        return visitor.leave(this);
    }

    @Override
    public TextRange getTextRange() {
        return range;
    }

    public String getName(){
        return name;
    }
    public int getArgCount(){
        return args.size();
    }
}