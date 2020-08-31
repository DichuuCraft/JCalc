package com.hadroncfy.jcalc.run;

import com.hadroncfy.jcalc.parser.TextRange;

public class ExecutionException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 5964699750269152029L;
    private final String msg;
    private final TextRange range;

    public ExecutionException(TextRange range, String msg){
        super(msg);
        this.range = range;
        this.msg = msg;
    }

    public TextRange getRange(){
        return range;
    }
}