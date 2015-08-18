package com.lge.parser;

import static org.junit.Assert.*;

import org.junit.Test;
import static com.lge.parser.Expr.*;
import static com.lge.parser.ExprParser.*;

public class ExprParserTest {

    @Test
    public void testLeftAssociativity() {
        assertEquals(bin(Op.MINUS, bin(Op.MINUS, con(6), con(2)), con(3)), expr().parse("6-2-3"));
    }

}
