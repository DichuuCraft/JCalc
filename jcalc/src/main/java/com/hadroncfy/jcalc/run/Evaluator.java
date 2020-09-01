package com.hadroncfy.jcalc.run;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.hadroncfy.jcalc.ast.AssignNode;
import com.hadroncfy.jcalc.ast.BinaryNode;
import com.hadroncfy.jcalc.ast.DeleteNode;
import com.hadroncfy.jcalc.ast.ExprListNode;
import com.hadroncfy.jcalc.ast.FunctionNode;
import com.hadroncfy.jcalc.ast.NumberNode;
import com.hadroncfy.jcalc.ast.Node;
import com.hadroncfy.jcalc.ast.NodeVisitor;
import com.hadroncfy.jcalc.ast.UnaryNode;
import com.hadroncfy.jcalc.ast.VariableNode;
import com.hadroncfy.jcalc.ast.VisitTerminatedException;
import com.hadroncfy.jcalc.parser.TextRange;
import com.hadroncfy.jcalc.parser.Token;

public class Evaluator implements NodeVisitor {
    private final Deque<Complex> stack = new ArrayDeque<>();
    private final Context ctx;

    public Evaluator(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean enter(BinaryNode node) {
        return false;
    }

    @Override
    public Node leave(BinaryNode node) {
        Complex b = stack.pop();
        Complex a = stack.pop();
        Complex ret;
        switch (node.getType()) {
            case '+':
                ret = a.plus(b);
                break;
            case '-':
                ret = a.minus(b);
                break;
            case '*':
                ret = a.multiply(b);
                break;
            case '/':
                ret = a.divide(b);
                break;
            case '^':
                ret = a.bitXor(b);
                break;
            case '|':
                ret = a.bitOr(b);
                break;
            case '&':
                ret = a.bitAnd(b);
                break;
            case Token.T_EXP:
                ret = a.pow(b);
                break;
            case Token.T_LEFT_SHIFT:
                ret = a.bitLeftShift(b);
                break;
            case Token.T_RIGHT_SHIFT:
                ret = a.bitRightShift(b, false);
                break;
            case Token.T_RIGHT_SHIFT_UNSIGNED:
                ret = a.bitRightShift(b, true);
                break;
            default:
                throw new IllegalArgumentException("Unknown operator type " + node.getType());
        }
        stack.push(ret);
        return node;
    }

    @Override
    public boolean enter(UnaryNode node) {
        return false;
    }

    @Override
    public Node leave(UnaryNode node) {
        Complex a = stack.pop();
        Complex ret;
        switch (node.getType()) {
            case '+':
                ret = a;
                break;
            case '-':
                ret = a.neg();
                break;
            case '~':
                ret = a.bitNot();
                break;
            default:
                throw new IllegalArgumentException("Unknown operator type " + node.getType());
        }
        stack.push(ret);
        return node;
    }

    @Override
    public boolean enter(FunctionNode node) {
        return false;
    }

    @Override
    public Node leave(FunctionNode node) throws VisitTerminatedException {
        Complex[] args = new Complex[node.getArgCount()];
        for (int i = args.length - 1; i >= 0; i--) {
            args[i] = stack.pop();
        }
        Function func = ctx.getFunction(node.getName());
        if (func == null) {
            makeExecutionException(node.getTextRange(), "jcalc.error.undefined_function");
            stack.push(Complex.ZERO);
        } else if (func.getArgCount() != args.length) {
            makeExecutionException(node.getTextRange(), "jcalc.error.incorrect_arguments");
            stack.push(Complex.ZERO);
        } else {
            stack.push(func.apply(args));
        }
        return node;
    }

    @Override
    public boolean enter(AssignNode node) throws VisitTerminatedException {
        node.getRight().accept(this);
        Complex val = stack.peek();
        Node n = node.getLeft();
        if (n instanceof VariableNode) {
            if (!ctx.setVariable(((VariableNode) n).getName(), val)) {
                makeExecutionException(n.getTextRange(), "jcalc.error.too_many_variables");
            }
        } else {
            throw new IllegalArgumentException("Not a variable node!");
        }
        return true;
    }

    @Override
    public Node leave(AssignNode node) {
        return node;
    }

    @Override
    public Node visit(NumberNode node) {
        stack.push(node.isImag() ? new Complex(NumberHolder.ZERO, node.getVal())
                : new Complex(node.getVal(), NumberHolder.ZERO));
        return node;
    }

    @Override
    public Node visit(VariableNode node) throws VisitTerminatedException {
        Complex c = ctx.getVariable(node.getName());
        if (c == null) {
            makeExecutionException(node.getTextRange(), "jcalc.error.undefined_variable");
            stack.push(Complex.ZERO);
        } else {
            stack.push(c);
        }
        return node;
    }

    public Complex getResult() {
        return stack.pop();
    }

    public List<Complex> getResults(){
        return new ArrayList<>(stack);
    }

    private void makeExecutionException(TextRange range, String msg) throws VisitTerminatedException {
        throw new VisitTerminatedException(new ExecutionException(range, msg));
    }

    @Override
    public boolean enter(DeleteNode node) throws VisitTerminatedException {
        Node c = node.getChild();
        if (c instanceof VariableNode) {
            VariableNode v = (VariableNode) c;
            Complex val = ctx.removeVariable(v.getName());
            if (val != null) {
                stack.push(val);
            } else {
                makeExecutionException(v.getTextRange(), "jcalc.error.undefined_variable");
                stack.push(Complex.ZERO);
            }
        } else {
            throw new IllegalArgumentException("Unexpected node: " + c.getClass());
        }
        return true;
    }

    @Override
    public Node leave(DeleteNode node) {
        return node;
    }

    @Override
    public boolean enter(ExprListNode node) throws VisitTerminatedException {
        return false;
    }

    @Override
    public Node leave(ExprListNode node) throws VisitTerminatedException {
        return node;
    }
}