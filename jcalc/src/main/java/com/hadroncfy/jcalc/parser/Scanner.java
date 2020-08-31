package com.hadroncfy.jcalc.parser;

import java.io.IOException;
import java.io.Reader;

import com.hadroncfy.jcalc.run.NumberHolder;

public class Scanner implements TokenSource {
    private final Reader input;

    private int ch = -1;
    private boolean first = true;
    private final TextRange range = new TextRange();

    public Scanner(Reader input) {
        this.input = input;
    }

    private static boolean isWhitespace(int ch) {
        return ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t';
    }

    private static boolean isNumber(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isNameStart(int ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_' || ch == '$';
    }

    private static boolean isNamePart(int ch) {
        return isNameStart(ch) || isNumber(ch);
    }

    private int next() throws IOException {
        if (ch == '\r'){
            ch = input.read();
            range.newLine();
            if (ch == '\n'){
                ch = next();
            }
        }
        else if (ch == '\n'){
            ch = input.read();
            range.newLine();
        }
        else {
            ch = input.read();
            range.advance();
        }
        return ch;
    }

    private Token scanNumber() throws IOException, CompilationException {
        NumberHolder n = new NumberHolder(0);
        boolean hasIntPart = false;

        while (isNumber(ch)){
            hasIntPart = true;
            n = n.multiply(10).add((long)ch - '0');
            next();
        }
        if (ch == '.'){
            next();
            if (!hasIntPart && !isNumber(ch)){
                throw new CompilationException(getRange(), "jcalc.error.invalid_number");
            }
            double decimal = 0, factor = .1;
            while (isNumber(ch)){
                decimal += (ch - '0') * factor;
                factor /= 10;
                next();
            }
            n = n.add(decimal);
        }
        if (ch == 'e' || ch == 'E'){
            next();
            boolean neg = false;
            if (ch == '+'){
                next();
            }
            else if (ch == '-'){
                next();
                neg = true;
            }

            if (isNumber(ch)){
                NumberHolder exp = new NumberHolder(0);
                while (isNumber(ch)){
                    exp = exp.multiply(10).add((long)ch - '0');
                    next();
                }
                if (neg){
                    exp = exp.neg();
                }
                n = n.multiply(new NumberHolder(10).pow(exp));
            }
            else {
                throw new CompilationException(getRange(), "jcalc.error.invalid_number");
            }
        }

        boolean complex = false;
        if (ch == 'i' || ch == 'I'){
            next();
            complex = true;
        }

        return new Token(getRange(), n, complex);
    }

    private TextRange getRange(){
        return new TextRange(range);
    }

    public Token nextToken() throws IOException, CompilationException {
        if (first){
            ch = input.read();
            first = false;
        }
        
        while (isWhitespace(ch)){
            next();
        }

        range.resetStart();

        if (ch == -1){
            return new Token(getRange(), Token.T_EOF);
        }

        switch (ch){
            case '+': next(); return new Token(getRange(), '+');
            case '-': next(); return new Token(getRange(), '-');
            case '*':
                if (next() == '*'){
                    next();
                    return new Token(getRange(), Token.T_EXP);
                }
                else {
                    return new Token(getRange(), '*');
                }
            case '/': next(); return new Token(getRange(), '/');
            case '(': next(); return new Token(getRange(), '(');
            case ')': next(); return new Token(getRange(), ')');
            case '=': next(); return new Token(getRange(), '=');
            case '~': next(); return new Token(getRange(), '~');
            case '&': next(); return new Token(getRange(), '&');
            case '^': next(); return new Token(getRange(), '^');
            case '|': next(); return new Token(getRange(), '|');
            case ',': next(); return new Token(getRange(), ',');
            case '>':
                next();
                if (ch == '>'){
                    next();
                    if (ch == '>'){
                        next();
                        return new Token(getRange(), Token.T_RIGHT_SHIFT_UNSIGNED);
                    }
                    else {
                        return new Token(getRange(), Token.T_RIGHT_SHIFT);
                    }
                }
                else {
                    throw new CompilationException(getRange(), "jcalc.error.invalid_token");
                }
            case '<':
                next();
                if (ch == '<'){
                    next();
                    return new Token(getRange(), Token.T_LEFT_SHIFT);
                }
                else {
                    throw new CompilationException(getRange(), "jcalc.error.invalid_token");
                }
            default:
                if (isNumber(ch) || ch == '.'){
                    return scanNumber();
                }
                else if (isNameStart(ch)){
                    StringBuilder sb = new StringBuilder();
                    sb.append((char)ch);
                    next();
                    while (isNamePart(ch)){
                        sb.append((char)ch);
                        next();
                    }
                    String text = sb.toString();
                    if (text.equals("delete")){
                        return new Token(getRange(), Token.T_DELETE);
                    }
                    return new Token(getRange(), text);
                }
                else {
                    next();
                    throw new CompilationException(getRange(), "jcalc.error.invalid_token");
                }
        }
    }
}