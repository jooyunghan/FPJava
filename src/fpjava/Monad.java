package fpjava;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Monad<M> extends Applicative<M> {

    <A> _1<M, A> unit(Supplier<A> f);

    <A, B> _1<M, B> bind(Function<A, _1<M, B>> f, _1<M, A> ma);

    // Overrides

    default <A, B> _1<M, B> fmap(Function<A, B> f, _1<M, A> ma) {
        return bind(a -> unit(() -> f.apply(a)), ma);
    }

    default <A> _1<M, A> pure(Supplier<A> f) {
        return unit(f);
    }

    default <A, B> _1<M, B> ap(_1<M, Function<A, B>> mf, _1<M, A> ma) {
        return liftM2(Function::apply, mf, ma);
    }

    default <A, B, C> _1<M, C> liftM2(BiFunction<A, B, C> f, _1<M, A> ma, _1<M, B> mb) {
        return bind(a -> bind(b -> unit(() -> f.apply(a, b)), mb), ma);
    }

    default <T, A> _1<M, _1<T, A>> sequence(Traversable<T> traversable, _1<T, _1<M, A>> mas) {
        return traversable.sequence(this, mas);
    }

    default <T, A, B> _1<M, _1<T, B>> mapM(Traversable<T> traversable, Function<A, _1<M, B>> f, _1<T, A> ta) {
        return traversable.mapM(this, f, ta);
    }
}
