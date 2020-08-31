package com.hadroncfy.jcalc.run;

import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class StaticMethodFunction implements Function {
    private final Method method;

    public StaticMethodFunction(Method method) {
        this.method = method;
        method.setAccessible(true);
    }

    static boolean isValidFuncMethod(Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            if (method.getAnnotation(Func.class) == null) {
                return false;
            }
            if (method.getReturnType() != Complex.class) {
                return false;
            }
            for (AnnotatedType type : method.getAnnotatedParameterTypes()) {
                if (type.getType() != Complex.class){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Complex apply(Complex[] args) {
        try {
            return (Complex) method.invoke(null, (Object[])args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            return Complex.ZERO;
        }
    }

    @Override
    public int getArgCount() {
        return method.getParameterCount();
    }
}