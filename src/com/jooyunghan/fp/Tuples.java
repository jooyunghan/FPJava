package com.jooyunghan.fp;

/**
 * Contructors for tuples
 * @author jooyung.han
 *
 */
public class Tuples {

    public static <A,B> Tuple2<A,B> tuples(A a, B b) {
        return new Tuple2<A, B>(a, b);
    }

}
