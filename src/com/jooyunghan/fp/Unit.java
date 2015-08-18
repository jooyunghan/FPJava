package com.jooyunghan.fp;

public class Unit {
    
    private static final Unit unit_ = new Unit();

    public static Unit unit() {
        return unit_ ;
    }

}
