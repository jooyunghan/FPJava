package fpjava;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Foldable<F> {
    default <M, A> M foldMap(Monoid<M> monoid, Function<A, M> f, _1<F, A> ta) {
        return foldr((a, z) -> monoid.mappend(f.apply(a), z), monoid.mempty(), ta);
    }

    default <A, B> B foldr(BiFunction<A, B, B> f, B z, _1<F, A> ta) {
        return foldMap(Monoid.<B>endo(), a -> b -> f.apply(a, b), ta).apply(z);
    }

    default <M> M fold(Monoid<M> monoid, _1<F, M> values) {
        return foldMap(monoid, x -> x, values);
    }
}
