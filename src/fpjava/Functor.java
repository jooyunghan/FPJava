package fpjava;

import java.util.function.Function;

public interface Functor<F> {
    <A, B> _1<F, B> fmap(Function<A, B> f, _1<F, A> fa);
}
