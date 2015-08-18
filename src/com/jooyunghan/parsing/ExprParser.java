package com.jooyunghan.parsing;

public class ExprParser {
    /*
     *  grammar
     *  
     *   expr   ::= term {addop term}*
     *   term   ::= factor {mulop factor}*
     *   factor ::= nat | '(' expr ')'
     *   addop  ::= '+' | '-'
     *   mulop  ::= '*' | '/'
     *   
     */
    
    public static Parser<Expr> expr() {
        return term().flatMap(x -> rest(x)).token();
    }
    
    private static Parser<Expr> rest(Expr e1) {
        return addop().flatMap(p -> term().flatMap(e2 -> rest(Expr.bin(p, e1, e2)))).or(()->Parser.unit(e1));
    }

    private static Parser<Expr> term() {
        return factor().flatMap(x -> more(x));
    }
    
    private static Parser<Expr> more(Expr e1) {
        return mulop().flatMap(p -> factor().flatMap(e2 -> more(Expr.bin(p, e1, e2)))).or(()->Parser.unit(e1));
    }
    
    private static Parser<Expr> factor() {
        return constant().or(()->expr().paren()).token();
    }
    
    private static Parser<Expr> constant() {
        return Parser.nat().flatMap(n -> Parser.unit(Expr.con(n)));
    }
    
    private static Parser<Op> addop() {
        return Parser.symbol("+").flatMap(x -> Parser.unit(Op.PLUS)).or(()->Parser.symbol("-").flatMap(x -> Parser.unit(Op.MINUS)));
    }
    
    private static Parser<Op> mulop() {
        return Parser.symbol("*").flatMap(x -> Parser.unit(Op.MUL)).or(()->Parser.symbol("/").flatMap(x -> Parser.unit(Op.DIV)));
    }
    
}
