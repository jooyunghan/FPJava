package fpjava.iomonad;

import java.util.function.Function;
import java.util.function.Supplier;

public class TailRec<A> {
    public <C> TailRec<C> flatMap(Function<A, TailRec<C>> g) {
        return new FlatMap(this, g);
    }

    private static class Done<A> extends TailRec<A> {
        final A a;

        public Done(A a) {
            this.a = a;
        }
    }

    private static class More<A> extends TailRec<A> {
        final Supplier<TailRec<A>> gen;

        public More(Supplier<TailRec<A>> gen) {
            this.gen = gen;
        }
    }

    private static class FlatMap<A, B> extends TailRec<B> {
        final TailRec<A> sub;
        final Function<A, TailRec<B>> k;

        public FlatMap(TailRec<A> sub, Function<A, TailRec<B>> k) {
            this.sub = sub;
            this.k = k;
        }
    }

    public static <A> TailRec<A> done(A a) {
        return new Done<>(a);
    }

    public static <A> TailRec<A> more(Supplier<TailRec<A>> gen) {
        return new More<>(gen);
    }

    public static <A> A run(TailRec<A> t) {
        while (!(t instanceof Done)) {
            if (t instanceof More) {
                t = ((More<A>) t).gen.get();
            } else {
                final TailRec x = ((FlatMap) t).sub;
                final Function f = ((FlatMap) t).k;
                if (x instanceof Done) {
                    t = (TailRec<A>) f.apply(((Done) x).a);
                } else if (x instanceof More) {
                    t = (TailRec<A>) f.apply(((More) x).gen.get());
                } else {
                    final TailRec y = ((FlatMap) x).sub;
                    final Function g = ((FlatMap) x).k;
                    t = y.flatMap(a -> ((FlatMap) g.apply(a)).flatMap(f));
                }
            }
        }
        return ((Done<A>) t).a;
    }
}
