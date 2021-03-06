package com.hadroncfy.jcalc.run;

public class NumberHolder implements Comparable {
    public static final NumberHolder ZERO = new NumberHolder(0);

    private final long intVal;
    private final double floatVal;
    private final boolean isInt;
    public NumberHolder(long i){
        intVal = i;
        floatVal = 0;
        isInt = true;
    }
    public NumberHolder(double d){
        intVal = 0;
        floatVal = d;
        isInt = false;
    }

    public double getAsFloat(){
        return isInt ? (double)intVal : floatVal;
    }
    public long getAsInt(){
        return isInt ? intVal : (long)floatVal;
    }

    public NumberHolder neg(){
        return isInt ? new NumberHolder(-intVal) : new NumberHolder(-floatVal);
    }

    public boolean isInt(){
        return isInt;
    }

    public NumberHolder add(NumberHolder a){
        if (isInt && a.isInt){
            try {
                return new NumberHolder(Math.addExact(intVal, a.intVal));
            }
            catch(ArithmeticException e){
                // fall through
            }
        }
        return new NumberHolder(getAsFloat() + a.getAsFloat());
    }

    public NumberHolder add(long n){
        if (isInt){
            try {
                return new NumberHolder(Math.addExact(intVal, n));
            }
            catch(ArithmeticException e){
                // fall through
            }
        }
        return new NumberHolder(getAsFloat() + (double)n);
    }

    public NumberHolder add(double a){
        return new NumberHolder(getAsFloat() + a);
    }

    public NumberHolder minus(NumberHolder a){
        if (isInt && a.isInt){
            try {
                return new NumberHolder(Math.subtractExact(intVal, a.intVal));
            }
            catch(ArithmeticException e){
                // fall through
            }
        }
        return new NumberHolder(getAsFloat() - a.getAsFloat());
    }

    public NumberHolder multiply(NumberHolder a){
        if (isInt && a.isInt){
            try {
                return new NumberHolder(Math.multiplyExact(intVal, a.intVal));
            }
            catch(ArithmeticException e){
                // fall through
            }
        }
        return new NumberHolder(getAsFloat() * a.getAsFloat());
    }

    public NumberHolder multiply(long n){
        if (isInt){
            try {
                return new NumberHolder(Math.multiplyExact(intVal, n));
            }
            catch(ArithmeticException e){
                // fall through
            }
        }
        return new NumberHolder(getAsFloat() * (double)n);
    }

    public NumberHolder divide(NumberHolder a){
        if (isInt && a.isInt && (a.intVal == 0 || intVal % a.intVal == 0)){
            if (a.intVal == 0){
                return new NumberHolder(intVal == 0 ? Double.NaN : Double.POSITIVE_INFINITY);
            }
            else {
                return new NumberHolder(intVal / a.intVal);
            }
        }
        else {
            return new NumberHolder(getAsFloat() / a.getAsFloat());
        }
    }

    public NumberHolder divide(long a){
        if (isInt && (a == 0 || intVal % a == 0)){
            if (a == 0){
                return new NumberHolder(intVal == 0 ? Double.NaN : Double.POSITIVE_INFINITY);
            }
            else {
                return new NumberHolder(intVal / a);
            }
        }
        else {
            return new NumberHolder(getAsFloat() / a);
        }
    }

    public NumberHolder pow(NumberHolder a){
        if (isInt && a.isInt && (intVal == 1 || a.intVal >= 0)){
            long i = 1, n = a.intVal;
            try {
                while (n --> 0){
                    i = Math.multiplyExact(i, intVal);
                }
                return new NumberHolder(i);
            }
            catch(ArithmeticException e){
                // fall through
            }
        }
        return new NumberHolder(Math.pow(getAsFloat(), a.getAsFloat()));
    }

    @Override
    public String toString() {
        return isInt ? Long.toString(intVal) : Double.toString(floatVal);
    }

    public boolean isInfinity(){
        return !isInt && Double.isInfinite(floatVal);
    }

    public boolean isNaN(){
        return !isInt && Double.isNaN(floatVal);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NumberHolder){
            NumberHolder h = (NumberHolder)obj;
            if (h.isInt && isInt){
                return intVal == h.intVal;
            }
            else if (!h.isInt && !isInt){
                return floatVal == h.floatVal;
            }
            else {
                return getAsFloat() == h.getAsFloat();
            }
        }
        return super.equals(obj);
    }

    public static NumberHolder abs(NumberHolder a){
        if (a.isInt){
            return a.intVal < 0 ? new NumberHolder(-a.intVal) : new NumberHolder(a.intVal);
        }
        return new NumberHolder(Math.abs(a.getAsFloat()));
    }

    public static NumberHolder sqrt(NumberHolder a){
        if (a.isInt){
            long i = (long)Math.sqrt(a.intVal);
            if (i * i == a.intVal){
                return new NumberHolder(i);
            }
            if ((i + 1) * (i + 1) == a.intVal){
                return new NumberHolder(i + 1);
            }
        }
        return new NumberHolder(Math.sqrt(a.getAsFloat()));
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof NumberHolder){
            NumberHolder h = (NumberHolder)o;
            if (isInt && h.isInt){
                return Long.compare(intVal, h.intVal);
            }
            else {
                return Double.compare(getAsFloat(), h.getAsFloat());
            }
        }
        return 0;
    }

    public int compareTo(long i) {
        if (isInt){
            return Long.compare(intVal, i);
        }
        else {
            return Double.compare(getAsFloat(), i);
        }
    }
}