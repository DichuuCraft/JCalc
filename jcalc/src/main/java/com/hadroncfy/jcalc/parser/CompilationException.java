package com.hadroncfy.jcalc.parser;

public class CompilationException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 8309072272524235277L;
    
    private final TextRange range;
    public CompilationException(TextRange range, String msg){
        super(msg);
        this.range = range;
    }

    public TextRange getRange(){
        return range;
    }
}