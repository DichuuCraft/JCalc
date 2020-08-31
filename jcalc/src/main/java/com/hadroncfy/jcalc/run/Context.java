package com.hadroncfy.jcalc.run;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.hadroncfy.jcalc.ast.Node;
import com.hadroncfy.jcalc.ast.VisitTerminatedException;
import com.hadroncfy.jcalc.parser.CompilationException;
import com.hadroncfy.jcalc.parser.Parser;
import com.hadroncfy.jcalc.parser.Scanner;

public class Context {
    private final Context parent;
    private final Map<String, Complex> variables = new HashMap<>();
    private final Map<String, Function> functions = new HashMap<>();
    private int variableLimit = -1;

    public Context(Context parent) {
        this.parent = parent;
    }

    public Complex getVariable(String name) {
        Context ctx = this;
        while (ctx != null) {
            Complex c = ctx.variables.get(name);
            if (c != null) {
                return c;
            }
            ctx = ctx.parent;
        }
        return null;
    }

    public Map<String, Complex> getVariables(){
        return variables;
    }

    public Complex removeVariable(String name) {
        return variables.remove(name);
    }

    public void setVariableLimit(int limit){
        variableLimit = limit;
    }

    public boolean setVariable(String name, Complex val) {
        if (variableLimit != -1 && variables.size() >= variableLimit && !variables.containsKey(name)){
            return false;
        }
        else {
            variables.put(name, val);
            return true;
        }
    }

    public Function getFunction(String name) {
        Context ctx = this;
        while (ctx != null) {
            Function c = ctx.functions.get(name);
            if (c != null) {
                return c;
            }
            ctx = ctx.parent;
        }
        return null;
    }

    public void defFunctions(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (StaticMethodFunction.isValidFuncMethod(method)) {
                Func f = method.getAnnotation(Func.class);
                String name = f.name();
                if (name.equals("")) {
                    name = method.getName();
                }
                functions.put(name, new StaticMethodFunction(method));
            }
        }
    }

    public Complex eval(String name) throws CompilationException, ExecutionException {
        Reader reader = new StringReader(name);
        Node node;
        try {
            node = new Parser(new Scanner(reader)).parse();
            Evaluator exec = new Evaluator(this);
            node.accept(exec);
            return exec.getResult();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (VisitTerminatedException e) {
            Throwable c = e.getCause();
            if (c instanceof ExecutionException){
                throw (ExecutionException)c;
            }
        }
        return null;
    }
}