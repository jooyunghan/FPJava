package com.jooyunghan.parsing;

import static com.jooyunghan.parsing.Expr.*;
import static com.jooyunghan.parsing.ExprParser.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.jooyunghan.parsing.Op;

public class ExprParserTest {

    @Test
    public void testLeftAssociativity() {
        assertEquals(bin(Op.MINUS, bin(Op.MINUS, con(6), con(2)), con(3)), expr().parse("6-2-3"));
    }

    @Test
    public void testShowAndParse() {
        Expr e = Expr.bin(Op.MUL, Expr.bin(Op.PLUS, Expr.con(1), Expr.con(2)), Expr.con(3));
        assertEquals(e, expr().parse(e.show()));
    }

}
