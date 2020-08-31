package com.hadroncfy.jcalc.run;

public interface Function {
    Complex apply(Complex[] args);
    int getArgCount();
}