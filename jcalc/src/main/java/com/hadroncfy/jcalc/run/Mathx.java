package com.hadroncfy.jcalc.run;

public final class Mathx {
    private Mathx(){}

    @Const(name = "pi")
    public static final Complex PI = new Complex(Math.PI, 0);

    @Const(name = "e")
    public static final Complex E = new Complex(Math.E, 0);

    @Func
    public static Complex exp(Complex a){
        if (a.isReal()){
            return new Complex(Math.exp(a.re.getAsFloat()), 0);
        }
        double b = Math.exp(a.re.getAsFloat());
        return new Complex(b * Math.cos(a.im.getAsFloat()), b * Math.sin(a.im.getAsFloat()));
    }

    @Func
    public static Complex sqrt(Complex a){
        if (a.isReal()){
            if (a.re.compareTo(0) < 0){
                return new Complex(NumberHolder.ZERO, NumberHolder.sqrt(a.re.neg()));
            }
            else {
                return new Complex(NumberHolder.sqrt(a.re), NumberHolder.ZERO);
            }
        }
        NumberHolder c = a.re.multiply(a.re).add(a.im.multiply(a.im));
        c = NumberHolder.sqrt(c);
        NumberHolder re = NumberHolder.sqrt(NumberHolder.abs(a.re.add(c)).divide(2));
        NumberHolder im = NumberHolder.sqrt(NumberHolder.abs(a.re.minus(c)).divide(2));
        if (a.im.compareTo(0) < 0){
            im = im.neg();
        }
        return new Complex(re, im);
    }

    @Func
    public static Complex ln(Complex a){
        if (a.isReal() && a.re.compareTo(NumberHolder.ZERO) > 0){
            int i = a.re.compareTo(NumberHolder.ZERO);
            if (i > 0){
                return new Complex(Math.log(a.re.getAsFloat()), 0);
            }
            else if (i == 0){
                return Complex.NaN;
            }
        }
        else if (a.norm2().equals(NumberHolder.ZERO)) {
            return Complex.NaN;
        }
        double norm2 = a.norm2().getAsFloat();
        double arg = a.arg();
        return new Complex(Math.log(norm2) / 2, arg);
    }

    @Func
    public static Complex sin(Complex a){
        if (a.isReal()){
            return new Complex(Math.sin(a.im.getAsFloat()), 0);
        }
        if (a.isImag()){
            return new Complex(0, Math.sinh(a.im.getAsFloat()));
        }
        double b = a.re.getAsFloat();
        double c = a.im.getAsFloat();
        return new Complex(
            Math.sin(b) * Math.cosh(c),
            Math.cos(b) * Math.sinh(c)
        );
    }

    @Func
    public static Complex cos(Complex a){
        if (a.isReal()){
            return new Complex(Math.cos(a.im.getAsFloat()), 0);
        }
        if (a.isImag()){
            return new Complex(Math.cosh(a.im.getAsFloat()), 0);
        }
        double b = a.re.getAsFloat();
        double c = a.im.getAsFloat();
        return new Complex(
            Math.cos(b) * Math.cosh(c),
            -Math.sin(b) * Math.sinh(c)
        );
    }

    @Func
    public static Complex tan(Complex a){
        return sin(a).divide(cos(a));
    }

    @Func
    public static Complex cot(Complex a){
        return cos(a).divide(sin(a));
    }

    @Func
    public static Complex sinh(Complex a){
        return exp(a).plus(exp(a.neg())).divide(new Complex(2, 0));
    }

    @Func
    public static Complex cosh(Complex a){
        return exp(a).minus(exp(a.neg())).divide(new Complex(2, 0));
    }

    @Func
    public static Complex tanh(Complex a){
        return sinh(a).divide(cosh(a));
    }

    @Func
    public static Complex arcsin(Complex a){
        if (a.isReal()){
            double c = a.re.getAsFloat();
            if (c >= -1 && c <= 1){
                return new Complex(Math.asin(c), 0);
            }
        }
        return arsinh(a.multiply(Complex.I)).multiply(Complex.I.neg());
    }

    @Func
    public static Complex arccos(Complex a){
        if (a.isReal()){
            double c = a.re.getAsFloat();
            if (c >= -1 && c <= 1){
                return new Complex(Math.acos(c), 0);
            }
        }
        return arcosh(a).multiply(Complex.I.neg());
    }

    @Func
    public static Complex arsinh(Complex a){
        if (a.isReal()){
            double x = a.re.getAsFloat();
            return new Complex(Math.log(x + Math.sqrt(1 + x * x)), 0);
        }
        return ln(a.plus(sqrt(a.multiply(a).plus(new Complex(1, 0)))));
    }

    @Func
    public static Complex arcosh(Complex a){
        if (a.isReal() && a.re.compareTo(1) > 0){
            double x = a.re.getAsFloat();
            return new Complex(Math.log(x + Math.sqrt(x * x - 1)), 0);
        }
        return ln(a.plus(sqrt(a.multiply(a).minus(new Complex(1, 0)))));
    }

    @Func
    public static Complex re(Complex a){
        return new Complex(a.re, NumberHolder.ZERO);
    }

    @Func
    public static Complex im(Complex a){
        return new Complex(a.im, NumberHolder.ZERO);
    }

    @Func
    public static Complex adj(Complex a){
        return a.conj();
    }

    @Func
    public static Complex abs(Complex a){
        return new Complex(NumberHolder.sqrt(a.norm2()), NumberHolder.ZERO);
    }
}