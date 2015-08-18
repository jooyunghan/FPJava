package com.lge.parsing;

public interface Show {
    default String show() {
        return showsPrec(0).apply("");
    }

    default ShowS showsPrec(int i) {
        return s -> show() + s;
    }
}
