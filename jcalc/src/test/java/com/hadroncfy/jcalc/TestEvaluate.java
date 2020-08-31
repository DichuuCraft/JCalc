package com.hadroncfy.jcalc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.hadroncfy.jcalc.parser.CompilationException;
import com.hadroncfy.jcalc.run.Complex;
import com.hadroncfy.jcalc.run.Context;
import com.hadroncfy.jcalc.run.ExecutionException;
import com.hadroncfy.jcalc.run.Func;

import org.junit.Test;

public class TestEvaluate {
    @Test
    public void constExpressions() {
        Context ctx = new Context(null);
        try {
            assertEquals(new Complex(12, 0), ctx.eval("3 + 9"));
            assertEquals(new Complex(1100, 0), ctx.eval("1e2 + 1e3"));
            assertEquals(new Complex(1e2 + 1e-3, 0), ctx.eval("1e2 + 1e-3"));
            assertEquals(new Complex(0, 2), ctx.eval("(1 + 1i) ** 2"));
            assertEquals(new Complex(87 * 54 + 121 * (45 - 8), 0), ctx.eval("87 * 54 +121 * (45 - 8)"));
            assertEquals(new Complex(8, 4), ctx.eval("(2 + 1i) << (2 + 60i)"));
            assertTrue(ctx.eval("(1 + 2i) ** 2").re.isInt());
        } catch (CompilationException | ExecutionException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void functions(){
        Context ctx = new Context(null);
        ctx.defFunctions(Funcs.class);

        try {
            assertEquals(new Complex(15, 6), ctx.eval("hkm(3 + 1i) * 3"));
        } catch (CompilationException | ExecutionException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void testToString(){
        Context ctx = new Context(null);
        try {
            assertEquals("ComplexInfinity", ctx.eval("7 / 0").toString());
            assertEquals("NaN", ctx.eval("7 + 8 + 0 / (0 + 0)").toString());
        } catch (CompilationException | ExecutionException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void variables(){
        Context ctx = new Context(null);
        try {
            ctx.eval("a = 1 + 1i");
            assertEquals(new Complex(1, 1), ctx.getVariable("a"));
            assertEquals(new Complex(2, 1), ctx.eval("delete a + 1"));
            assertEquals(null, ctx.getVariable("a"));
        } catch (CompilationException | ExecutionException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private static class Funcs {
        @Func(name = "hkm")
        public static Complex test(Complex a){
            return a.plus(new Complex(2, 1));
        }
    };
}