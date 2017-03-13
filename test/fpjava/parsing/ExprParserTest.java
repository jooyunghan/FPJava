package fpjava.parsing;

import org.junit.jupiter.api.Test;

import static fpjava.parsing.ExprParser.expr;
import static org.junit.Assert.assertEquals;

public class ExprParserTest {

    @Test
    public void testLeftAssociativity() {
        assertEquals(Expr.bin(Op.MINUS, Expr.bin(Op.MINUS, Expr.con(6), Expr.con(2)), Expr.con(3)), expr().parse("6-2-3"));
    }

    @Test
    public void testShowAndParse() {
        Expr e = Expr.bin(Op.MUL, Expr.bin(Op.PLUS, Expr.con(1), Expr.con(2)), Expr.con(3));
        assertEquals(e, expr().parse(e.show()));
    }

}
