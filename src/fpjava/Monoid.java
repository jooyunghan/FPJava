package fpjava;

import fpjava.data.List;

import java.util.function.UnaryOperator;

public interface Monoid<M> {
    M mempty();

    M mappend(M a, M b);

    default M mconcat(List<M> ms) {
        return ms.foldl(this::mappend, mempty());
    }

    static <A> Monoid<UnaryOperator<A>> endo() {
        return new Monoid<UnaryOperator<A>>() {
            @Override
            public UnaryOperator<A> mempty() {
                return x -> x;
            }

            @Override
            public UnaryOperator<A> mappend(UnaryOperator<A> a, UnaryOperator<A> b) {
                return x -> a.apply(b.apply(x));
            }
        };
    }
}
