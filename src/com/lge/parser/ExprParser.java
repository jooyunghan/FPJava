package com.lge.parser;

public class ExprParser {
    /*
     *  grammar
     *  
     *   expr  ::= nat | '(' expr op expr ')'
     *   op    ::= '+' | '-'
     *   nat   ::= {digit}+
     *   digit ::= '0' | '1' | ... | '9'
     *   
     */
    
    public static Parser<Expr> expr() {
        return constant().or(binary().paren()).token();
    }
    
    public static Parser<Expr> constant() {
        return Parser.nat().flatMap(n -> Parser.unit(Expr.con(n)));
    }
    
    public static Parser<Expr> binary() {
        return expr().flatMap(e1 -> op().flatMap(p -> expr().flatMap(e2 -> Parser.unit(Expr.bin(p, e1, e2))))); // right associative & infinite recursion
    }
    
    public static Parser<Op> op() {
        return Parser.symbol("+").flatMap(x -> Parser.unit(Op.PLUS)).or(Parser.symbol("-").flatMap(x -> Parser.unit(Op.MINUS)));
    }
    
}
