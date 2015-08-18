package com.lge.parsing;

import java.util.function.Function;

public interface ShowS extends Function<String, String> {
    static ShowS showChar(char c) {
        return s -> c + s;
    }
    static ShowS showString(String s) {
        return s2 -> s + s2;
    }
    static ShowS showParen(boolean b, ShowS p) {
        return b? showChar('(').compose(p).compose(showChar(')')) : p;
    }
    default ShowS compose(ShowS before) {
        return s -> this.apply(before.apply(s));
    }
}
