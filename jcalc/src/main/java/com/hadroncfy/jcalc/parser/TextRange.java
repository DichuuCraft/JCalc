package com.hadroncfy.jcalc.parser;

public class TextRange {
    private int startLine, startColumn, endLine, endColumn;

    public TextRange(){

    }

    public TextRange(TextRange r){
        startLine = r.startLine;
        startColumn = r.startColumn;
        endLine = r.endLine;
        endColumn = r.endColumn;
    }
    public void resetStart(){
        startLine = endLine;
        startColumn = endColumn;
    }
    public void newLine(){
        endLine++;
        endColumn = 0;
    }
    public void advance(){
        endColumn++;
    }

    public int getStartColumn(){
        return startColumn;
    }

    public int getEndColumn(){
        return endColumn;
    }
}