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

    public static TextRange between(TextRange r1, TextRange r2){
        TextRange r = new TextRange(r1);
        r.endColumn = r2.endColumn;
        r.endLine = r2.endLine;
        return r;
    }
}