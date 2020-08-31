package com.hadroncfy.jcalc.run;

public class Complex {
    public final NumberHolder re, im;

    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);

    public static final Complex COMPLEX_INFINITY = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Complex NaN = new Complex(Double.NaN, Double.NaN);

    public Complex(long re, long im){
        this.re = new NumberHolder(re);
        this.im = new NumberHolder(im);
    }

    public Complex(double re, double im){
        this.re = new NumberHolder(re);
        this.im = new NumberHolder(im);
    }

    public Complex(NumberHolder re, NumberHolder im){
        this.re = re;
        this.im = im;
    }

    public NumberHolder norm2(){
        return re.multiply(re).add(im.multiply(im));
    }

    public Complex conj(){
        return new Complex(re, im.neg());
    }

    public Complex neg(){
        return new Complex(re.neg(), im.neg());
    }

    public boolean isReal(){
        return im.isInt() && im.getAsInt() == 0;
    }

    public boolean isInfinity(){
        return re.isInfinity() || im.isInfinity();
    }

    public boolean isNaN(){
        return re.isNaN() || im.isNaN();
    }

    public Complex plus(Complex a){
        return new Complex(re.add(a.re), im.add(a.im));
    }
    public Complex minus(Complex a){
        return new Complex(re.minus(a.re), im.minus(a.im));
    }
    public Complex multiply(Complex a){
        NumberHolder re2 = re.multiply(a.re).minus(im.multiply(a.im));
        NumberHolder im2 = re.multiply(a.im).add(im.multiply(a.re));
        return new Complex(re2, im2);
    }
    public Complex divide(Complex a){
        NumberHolder norm2 = a.norm2();
        if (norm2.equals(NumberHolder.ZERO)){
            return equals(ZERO) ? NaN : COMPLEX_INFINITY;
        }
        return new Complex(re.divide(norm2), im.divide(norm2)).multiply(a.conj());
    }
    public Complex pow(Complex a){
        if (a.isReal() && a.re.isInt()){
            long p = a.re.getAsInt();
            boolean inv = false;
            if (p < 0){
                inv = true;
                p = -p;
            }
            Complex acc = new Complex(1, 0);
            while (p > 0){
                acc = acc.multiply(this);
                p--;
            }
            if (inv){
                acc = ONE.divide(acc);
            }
            return acc;
        }
        else {
            double arg = Math.atan2(im.getAsFloat(), re.getAsFloat());
            double norm = Math.sqrt(norm2().getAsFloat());
            double m = a.re.getAsFloat(), n = a.im.getAsFloat();
            double k = Math.pow(norm, m) * Math.exp(-arg * n);
            double l = arg * m + n * Math.log(norm);
            return new Complex(k * Math.cos(l), k * Math.sin(l));
        }
    }

    @Override
    public String toString() {
        if (isInfinity()){
            return "ComplexInfinity";
        }
        if (isNaN()){
            return "NaN";
        }
        if (equals(ZERO)){
            return "0";
        }
        if (re.equals(NumberHolder.ZERO)){
            return im.toString() + "I";
        }
        if (im.equals(NumberHolder.ZERO)){
            return re.toString();
        }
        String n1 = re.toString(), n2 = im.toString();
        String connector = "+";
        if (n2.charAt(0) == '-'){
            n2 = n2.substring(1);
            connector = "-";
        }

        return new StringBuilder().append(n1).append(" ").append(connector).append(" ").append(n2).append("I").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Complex){
            Complex c = (Complex)obj;
            return isInfinity() && c.isInfinity() ||
                isNaN() && c.isNaN() ||
                re.equals(c.re) && im.equals(c.im);
        }
        return super.equals(obj);
    }

    public Complex bitNot(){
        return new Complex(~re.getAsInt(), ~im.getAsInt());
    }

    public Complex bitAnd(Complex a){
        long i = a.re.getAsInt();
        return new Complex(re.getAsInt() & i, im.getAsInt() & i);
    }

    public Complex bitOr(Complex a){
        long i = a.re.getAsInt();
        return new Complex(re.getAsInt() | i, im.getAsInt() | i);
    }

    public Complex bitXor(Complex a){
        long i = a.re.getAsInt();
        return new Complex(re.getAsInt() ^ i, im.getAsInt() ^ i);
    }

    public Complex bitLeftShift(Complex a){
        long i = a.re.getAsInt();
        return new Complex(re.getAsInt() << i, im.getAsInt() << i);
    }

    public Complex bitRightShift(Complex a, boolean unsigned){
        long i = a.re.getAsInt();
        if (unsigned){
            return new Complex(re.getAsInt() >>> i, im.getAsInt() >>> i);
        }
        else {
            return new Complex(re.getAsInt() >> i, im.getAsInt() >> i);
        }
    }
}