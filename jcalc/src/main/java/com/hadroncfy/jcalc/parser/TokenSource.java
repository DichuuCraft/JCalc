package com.hadroncfy.jcalc.parser;

import java.io.IOException;

public interface TokenSource {
    Token nextToken() throws IOException, CompilationException;
}