package com.hadroncfy.jcalc.parser;

import java.util.ArrayDeque;
import java.util.Deque;

import com.hadroncfy.jcalc.ast.BinaryNode;
import com.hadroncfy.jcalc.ast.Node;

public class BinaryExpressionParser {
    private final Deque<Token> optrs = new ArrayDeque<>();
    private final Deque<Node> operands = new ArrayDeque<>();

    public BinaryExpressionParser(Node start){
        operands.push(start);
    }

    private static int getPrecedence(int type){
        switch (type){
            case '|': return 1;
            case '^': return 2;
            case '&': return 3;
            case Token.T_LEFT_SHIFT:
            case Token.T_RIGHT_SHIFT:
            case Token.T_RIGHT_SHIFT_UNSIGNED: return 4;
            case '+':
            case '-': return 5;
            case '*':
            case '/': return 6;
            case Token.T_EXP: return 7;
            default: return -1;
        }
    }

    private static boolean isLeftAssoc(int type){
        switch (type){
            case '|':
            case '^': 
            case '&':
            case Token.T_LEFT_SHIFT:
            case Token.T_RIGHT_SHIFT:
            case Token.T_RIGHT_SHIFT_UNSIGNED:
            case '+':
            case '-':
            case '*':
            case '/': return true;
            case Token.T_EXP: return false;
            default: return false;
        }
    }

    public static boolean isBinaryOptr(int type){
        return getPrecedence(type) != -1;
    }

    private boolean shouldPopStack(int prec){
        if (optrs.isEmpty()){
            return false;
        }
        int p2 = getPrecedence(optrs.peek().getType());
        return p2 > prec || p2 == prec && isLeftAssoc(p2);
    }

    private void popStack(){
        Node right = operands.pop();
        Node left = operands.pop();
        Token t = optrs.pop();
        operands.push(new BinaryNode(t.getRange(), t.getType(), left, right));
    }

    public void parse(Token optr, Node operand){
        int prec = getPrecedence(optr.getType());
        while (shouldPopStack(prec)){
            popStack();
        }
        optrs.push(optr);
        operands.push(operand);
    }

    public Node end(){
        while (!optrs.isEmpty()){
            popStack();
        }
        return operands.pop();
    }
}