package com.lge.parser;

public class Expr {
    /** constructors */
    public static Expr con(int n) {
        return new Con(n);
    }
    
    public static Expr bin(Op op, Expr left, Expr right) {
        return new Bin(op, left, right);
    }
    
    /** internals */
    
    private static class Con extends Expr {
        public final int n;

        public Con(int n) {
            this.n = n;
        }
    }
    
    public static class Bin extends Expr {
        public final Op op;
        public final Expr left;
        public final Expr right;

        public Bin(Op op, Expr left, Expr right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }
    }
}
