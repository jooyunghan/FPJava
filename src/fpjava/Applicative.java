package fpjava;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Applicative<F> extends Functor<F> {
    default <A, B, C> _1<F, C> liftA2(BiFunction<A, B, C> f, _1<F, A> fa, _1<F, B> fb) {
        return ap(ap(pure(() -> (A a) -> (B b) -> f.apply(a, b)), fa), fb);
    }

    default <A, B> _1<F, B> fmap(Function<A, B> f, _1<F, A> fa) {
        return ap(pure(() -> f), fa);
    }

    <A> _1<F, A> pure(Supplier<A> f);

    <A, B> _1<F, B> ap(_1<F, Function<A, B>> f, _1<F, A> fa);
}
