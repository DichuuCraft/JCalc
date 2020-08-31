package com.hadroncfy.jcalc.parser;

import com.hadroncfy.jcalc.run.NumberHolder;

public class Token {
    public static final int T_EOF                  = 256;
    public static final int T_NAME                 = 257;
    public static final int T_NUMBER               = 258;
    
    public static final int T_EXP                  = 260;
    public static final int T_LEFT_SHIFT           = 261;
    public static final int T_RIGHT_SHIFT          = 262;
    public static final int T_RIGHT_SHIFT_UNSIGNED = 263;
    public static final int T_DELETE               = 264;

    private static final String[] TOKEN_NAMES = {
        "<EOF>", "<name>", "<number>", "<>", "**", "<<", ">>", ">>>", "delete"
    };

    private final TextRange range;
    private final int type;
    private final String text;
    private final NumberHolder num;
    private final boolean complex;

    public Token(TextRange range, int type){
        this.range = range;
        this.type = type;
        this.text = null;
        this.num = null;
        this.complex = false;
    }

    public Token(TextRange range, NumberHolder n, boolean complex){
        this.range = range;
        this.type = T_NUMBER;
        this.text = null;
        this.num = n;
        this.complex = complex;
    }

    public Token(TextRange range, String text){
        this.range = range;
        this.type = T_NAME;
        this.text = text;
        this.num = null;
        this.complex = false;
    }

    @Override
    public String toString() {
        return new StringBuilder("Token[")
            .append(type <= 255 ? (char)type : TOKEN_NAMES[type - 256])
            .append("]")
            .toString();
    }

    public int getType(){
        return type;
    }

    public TextRange getRange(){
        return range;
    }

    public boolean isImag(){
        return complex;
    }

    public NumberHolder getNumber(){
        return num;
    }

    public String getText(){
        return text;
    }
}