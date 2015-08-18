package com.lge.parser;
import static com.lge.fp.Tuples.tuples;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import com.lge.fp.Ints;
import com.lge.fp.List;
import com.lge.fp.Tuple2;
import com.lge.fp.Unit;

/**
 * Monadic parser.
 * 
 * @author jooyung.han
 *
 * @param <T>
 */
public class Parser<T> {

    private final Function<String, List<Tuple2<T, String>>> run;

    public Parser(Function<String, List<Tuple2<T, String>>> run) {
        this.run = run;
    }

    public List<Tuple2<T, String>> run(String string) {
        return run.apply(string);
    }

    /** monad unit */
    public static <T> Parser<T> unit(T t) {
        return new Parser<T>(s -> List.unit(tuples(t, s)));
    }

    /** monad bind */
    public <S> Parser<S> flatMap(Function<T, Parser<S>> f) {
        return new Parser<S>(s -> run(s).flatMap(tuple -> f.apply(tuple._1).run(tuple._2)));
    }

    /**
     * basic parsers
     */

    private static final Parser<Character> getc = new Parser<Character>(s -> s.isEmpty() ? List.nil() : List.unit(tuples(s.charAt(0), s.substring(1))));
    public static Parser<Character> getc() {
        return getc;
    }

    public static Parser<Character> sat(Predicate<Character> pred) {
//        return getc().flatMap(c -> pred.test(c) ? unit(c) : fail());
        return getc().flatMap(c -> guard(pred.test(c)).flatMap(x -> unit(c)));
    }

    private static final Parser<?> fail = new Parser<Object>(s -> List.nil());
    @SuppressWarnings("unchecked")
    private static <T> Parser<T> fail() {
        return (Parser<T>) fail;
    }
    
    public static Parser<?> guard(boolean b) {
        return b ? unit(Unit.unit()) : fail(); 
    }

    public static Parser<Unit> char_(char c) {
        return sat(x -> c == x).flatMap(x -> unit(Unit.unit()));
    }
    
    public static Parser<Unit> string(String s) {
        return s.isEmpty() ? unit(Unit.unit()) : char_(s.charAt(0)).flatMap(x -> string(s.substring(1)));
    }
    
    private static final Parser<Character> lower = sat(Character::isLowerCase);
    public static Parser<Character> lower() {
        return lower;
    }

    private static final Parser<Integer> digit = sat(Character::isDigit).flatMap(d -> unit(d - '0'));
    public static Parser<Integer> digit() {
        return digit;
    }

    /**
     * choice and repetition 
     */

    public Parser<T> or(Parser<T> q) {
        return new Parser<T>(s -> {
            List<Tuple2<T, String>> result = this.run(s);
            return result.isNil() ? q.run(s) : result;
        });
    }

//    private static final Parser<List<Character>> lowers = lower().flatMap(c -> lowers().flatMap(cs -> unit(List.cons(c, cs)))).or(unit(List.nil()));
    private static final Parser<List<Character>> lowers = lower().many();
    public static Parser<List<Character>> lowers() {
        return lowers;
    }

    public Parser<List<T>> many() {
        return optional(some());
    }
    
    public Parser<List<T>> some() {
        return this.flatMap(t -> many().flatMap(ts -> unit(List.cons(t, ts))));
    }
    
    public static <T> Parser<List<T>> optional(Parser<List<T>> p) {
        return p.or(none());
    }
    
    private static final Parser<?> none = unit(List.nil());
    @SuppressWarnings("unchecked")
    public static <T> Parser<List<T>> none() {
        return (Parser<List<T>>) none;
    }
    
    private static final Parser<Unit> space = sat(Character::isWhitespace).many().flatMap(x -> unit(Unit.unit()));
    public static Parser<Unit> space() {
        return space;
    }
    
    public static Parser<Unit> symbol(String s) {
//        return space().flatMap(x -> string(s));
        return string(s).token();
    }
    
    public Parser<T> token() {
        return space().flatMap(x -> this);
    }
    
    public Parser<List<T>> manywith(Parser<?> sep) {
        return optional(somewith(sep));
    }
    
    public Parser<List<T>> somewith(Parser<?> sep) {
        return this.flatMap(t -> sep.flatMap(x -> this).many().flatMap(ts -> unit(List.cons(t, ts))));
    }

    
    /** ints example */
    
    private static final Parser<Integer> nat = digit().some().flatMap(ds -> {
        BinaryOperator<Integer> shiftl = (m,n) -> m*10 + n;
        return unit(ds.foldl1(shiftl));
    });
    /** non-negative without leading spaces */
    public static Parser<Integer> nat() {
        return nat;
    }

    private static final Parser<Integer> natural = nat().token();
    /** non-negative with leading spaces */
    public static Parser<Integer> natural() {
        return natural;
    }

//    private static final Parser<Integer> int_ = symbol("-").flatMap(x -> natural().flatMap(n -> unit(-n))).or(natural()); // inefficient
    private static final Parser<Integer> int_ = space().flatMap(x -> char_('-').flatMap(y -> unit(Ints.negate)).or(unit(Ints.id)).flatMap(f -> nat().flatMap(n -> unit(f.apply(n)))));
    public static Parser<Integer> int_() {
        return int_;
    }

    public Parser<T> bracket() {
        return symbol("[").flatMap(x -> this.flatMap(t -> symbol("]").flatMap(z -> unit(t))));
    }
    
    private static final Parser<List<Integer>> ints = int_().manywith(symbol(",")).bracket();
    public static Parser<List<Integer>> ints() {
        return ints;
    }
}
