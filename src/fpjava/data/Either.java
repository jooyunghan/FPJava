package fpjava.data;

import java.util.function.Consumer;
import java.util.function.Function;

public class Either<A, B> {
    public <C> C fold(Function<A, C> left, Function<B, C> right) {
        if (this instanceof Left) return left.apply(((Left<A>) this).a);
        else return right.apply(((Right<B>) this).b);
    }

    public void foldVoid(Consumer<A> left, Consumer<B> right) {
        if (this instanceof Left) left.accept(((Left<A>) this).a);
        else right.accept(((Right<B>) this).b);
    }


    public static <A, B> Either<A, B> left(A a) {
        return new Left(a);
    }

    public static <A, B> Either<A, B> right(B b) {
        return new Right(b);
    }

    private static class Left<A> extends Either<A, Object> {
        private final A a;

        Left(A a) {
            this.a = a;
        }
    }

    private static class Right<B> extends Either<Object, B> {
        private final B b;

        Right(B b) {
            this.b = b;
        }
    }
}
