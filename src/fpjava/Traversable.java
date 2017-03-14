package fpjava;

import java.util.function.Function;

public interface Traversable<T> extends Functor<T>, Foldable<T> {
    default <A, B, C> _1<A, _1<T, C>> traverse(Applicative<A> applicative, Function<B, _1<A, C>> f, _1<T, B> ta) {
        return sequenceA(applicative, fmap(f, ta));
    }

    default <A, C> _1<A, _1<T, C>> sequenceA(Applicative<A> applicative, _1<T, _1<A, C>> ta) {
        return traverse(applicative, x -> x, ta);
    }

    default <M, A, B> _1<M, _1<T, B>> mapM(Monad<M> monad, Function<A, _1<M, B>> f, _1<T, A> ta) {
        return traverse(monad, f, ta);
    }

    default <M, A> _1<M, _1<T, A>> sequence(Monad<M> monad, _1<T, _1<M, A>> ta) {
        return sequenceA(monad, ta);
    }
}
