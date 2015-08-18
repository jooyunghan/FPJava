package com.jooyunghan.parsing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jooyunghan.fp.List;
import com.jooyunghan.fp.Tuples;
import com.jooyunghan.parsing.Parser;

public class ParserTest {

    @Test
    public void testGetc() {
        assertEquals(List.unit(Tuples.tuples('a', "bc")), Parser.getc().run("abc"));
    }

    @Test
    public void testSat() {
        assertEquals(List.unit(Tuples.tuples('a', "bc")), Parser.sat(c -> c == 'a').run("abc"));
        assertEquals(List.nil(), Parser.sat(c -> c == 'a').run("cde"));
    }

    @Test
    public void testLowers() {
        assertEquals(List.unit(Tuples.tuples(toList("abc"), "DEF")), Parser.lowers().run("abcDEF"));
    }

    private static List<Character> toList(String string) {
        return toList(string, 0);
    }

    private static List<Character> toList(String s, int start) {
        return (start >= s.length()) ? List.nil() : List.cons(s.charAt(start), toList(s, start + 1));
    }

    @SafeVarargs
    private static <T> List<T> toList(T... ts) {
        return toList(ts, 0);
    }

    private static <T> List<T> toList(T[] ts, int start) {
        return (start >= ts.length) ? List.nil() : List.cons(ts[start], toList(ts, start + 1));
    }

    @Test
    public void testInts() {
        assertEquals(List.unit(Tuples.tuples(toList(2,-3,4), "")), Parser.ints().run("[2, -3, 4]"));
        assertEquals(List.nil(), Parser.ints().run("[2, -3, +4]"));
        assertEquals(List.unit(Tuples.tuples(List.nil(), "")), Parser.ints().run("[]"));
    }
    
    @Test
    public void testFloat() {
        assertEquals(3.14f, Parser.float_().parse("3.14"), 0.001f);
    }
}
