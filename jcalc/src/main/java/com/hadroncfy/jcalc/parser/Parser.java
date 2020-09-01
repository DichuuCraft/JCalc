package com.hadroncfy.jcalc.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.hadroncfy.jcalc.ast.AssignNode;
import com.hadroncfy.jcalc.ast.DeleteNode;
import com.hadroncfy.jcalc.ast.ExprListNode;
import com.hadroncfy.jcalc.ast.FunctionNode;
import com.hadroncfy.jcalc.ast.NumberNode;
import com.hadroncfy.jcalc.ast.Node;
import com.hadroncfy.jcalc.ast.UnaryNode;
import com.hadroncfy.jcalc.ast.VariableNode;
import com.hadroncfy.jcalc.ast.VisitTerminatedException;

public class Parser {
    private final TokenSource tokenSource;

    private Token t = null;

    public Parser(TokenSource tokenSource) {
        this.tokenSource = tokenSource;
    }

    private Token next() throws IOException, CompilationException {
        return t = tokenSource.nextToken();
    }

    private void expect(int type) throws CompilationException, IOException {
        if (type == t.getType()) {
            next();
        } else {
            throw new CompilationException(t.getRange(), "jcalc.error.unexpected_token");
        }
    }

    public Node parse() throws CompilationException, IOException {
        t = tokenSource.nextToken();
        Node ret = parseExpressionList();
        expect(Token.T_EOF);
        return ret;
    }

    private Node parseExpressionList() throws CompilationException, IOException {
        List<Node> ret = new ArrayList<>();
        ret.add(parseExpression());
        while (t.getType() == ','){
            next();
            ret.add(parseExpression());
        }
        return new ExprListNode(ret);
    }

    private Node parseExpression() throws CompilationException, IOException {
        return parseAssignExpression();
    }

    private void checkLeftValue(Node node) throws CompilationException {
        LeftValueChecker checker = new LeftValueChecker();
        try {
            node.accept(checker);
        } catch (VisitTerminatedException e) {
            // unreachable
        }

        if (!checker.getStatus()){
            throw new CompilationException(node.getTextRange(), "jcalc.error.invalid_rvalue");
        }
    }

    private Node parseAssignExpression() throws CompilationException, IOException {
        Node left = parseBinaryExpression();
        if (t.getType() == '='){
            Token token = t;
            next();
            checkLeftValue(left);
            return new AssignNode(token.getRange(), left, parseAssignExpression());
        }
        return left;
    }

    private Node parseBinaryExpression() throws CompilationException, IOException {
        Node top = parseUnaryExpression();
        if (BinaryExpressionParser.isBinaryOptr(t.getType())){
            BinaryExpressionParser parser = new BinaryExpressionParser(top);
            while (BinaryExpressionParser.isBinaryOptr(t.getType())){
                Token token = t;
                next();
                Node n2 = parseUnaryExpression();
                parser.parse(token, n2);
            }
            top = parser.end();
        }
        return top;
    }

    private Node parseUnaryExpression() throws CompilationException, IOException {
        Token token = t;
        if (t.getType() == '+' || t.getType() == '-'){
            next();
            return new UnaryNode(token.getRange(), token.getType(), parseUnaryExpression());
        }
        else if (t.getType() == Token.T_DELETE){
            next();
            return new DeleteNode(token.getRange(), parseDeleteExpression());
        }
        else {
            return parsePrimitive();
        }
    }

    private Node parseDeleteExpression() throws IOException, CompilationException {
        if (t.getType() == Token.T_NAME){
            Token token = t;
            next();
            return new VariableNode(token.getRange(), token.getText());
        }
        else {
            throw new CompilationException(t.getRange(), "jcalc.error.invalid_deletion_target");
        }
    }

    private Node parsePrimitive() throws CompilationException, IOException {
        Node ret;
        Token token;
        switch(t.getType()){
            case Token.T_NUMBER:   ret = new NumberNode(t.getRange(), t.getNumber(), t.isImag());     next(); return ret;
            case '(':
                next();
                ret = parseExpression();
                expect(')');
                return ret;
            case Token.T_NAME:
                token = t;
                next();
                if (t.getType() == '('){
                    next();
                    List<Node> args = new ArrayList<>();
                    if (t.getType() != ')'){
                        args.add(parseExpression());
                        while (t.getType() == ','){
                            next();
                            args.add(parseExpression());
                        }
                    }
                    expect(')');
                    return new FunctionNode(token.getRange(), token.getText(), args);
                }
                else {
                    return new VariableNode(token.getRange(), token.getText());
                }
            default:
                throw new CompilationException(t.getRange(), "jcalc.error.unexpected_token");
        }
    }
}