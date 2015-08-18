package com.lge.fp;

import java.util.function.UnaryOperator;

public class Ints {
    public static final UnaryOperator<Integer> negate = n -> -n;
    public static final UnaryOperator<Integer> id = n -> n;
}
