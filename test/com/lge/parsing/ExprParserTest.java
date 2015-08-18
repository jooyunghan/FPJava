package com.lge.parsing;

import static com.lge.parsing.Expr.*;
import static com.lge.parsing.ExprParser.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.lge.parsing.Op;

public class ExprParserTest {

    @Test
    public void testLeftAssociativity() {
        assertEquals(bin(Op.MINUS, bin(Op.MINUS, con(6), con(2)), con(3)), expr().parse("6-2-3"));
    }

}
