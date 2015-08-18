package com.jooyunghan.parsing;

public class ParserMain {

    public static void main(String[] args) {
        Expr e = Expr.bin(Op.MUL, Expr.bin(Op.PLUS, Expr.con(1), Expr.con(2)), Expr.con(3));
        System.out.println(e.show());
    }

}
