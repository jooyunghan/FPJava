package fpjava.iomonad;


import fpjava.parallelism.Par;

import java.util.function.Function;

/**
 * Created by jooyung.han on 09/03/2017.
 */
public class Async<A> {

    public <B> Async<B> flatMap(Function<A, Async<B>> f) {
        return new FlatMap<A, B>(this, f);
    }

    public <B> Async<B> map(Function<A, B> f) {
        return flatMap(a -> new Return<B>(f.apply(a)));
    }

    private static class Return<A> extends Async<A> {
        private final A a;

        Return(A a) {
            this.a = a;
        }
    }

    private static class Suspend<A> extends Async<A> {
        private final Par<A> resume;

        Suspend(Par<A> resume) {
            this.resume = resume;
        }
    }

    private static class FlatMap<A, B> extends Async<B> {
        private final Async<A> sub;
        private final Function<A, Async<B>> k;

        FlatMap(Async<A> sub, Function<A, Async<B>> k) {
            this.sub = sub;
            this.k = k;
        }
    }

    public static <A> Par<A> run(Async<A> async) {
        Async<A> s = step(async);
        if (s instanceof Return) {
            return Par.unit(((Return<A>)s).a);
        } else if (s instanceof Suspend) {
            return ((Suspend<A>)s).resume;
        } else {
            Async x = ((FlatMap)s).sub;
            Function f = ((FlatMap)s).k;
            if (x instanceof Suspend) {
                return ((Suspend)x).resume.flatMap(a -> run((Async<A>) f.apply(a)));
            } else {
                throw new RuntimeException("Impossible to reach here");
            }
        }
    }

    public static <A> Async<A> step(Async<A> async) {
        while (async instanceof FlatMap) {
            Async sub = ((FlatMap) async).sub;
            Function k = ((FlatMap) async).k;
            if (sub instanceof Return) {
                async = (Async<A>) k.apply(((Return)sub).a);
            } else if (sub instanceof FlatMap) {
                async = ((FlatMap)sub).sub.flatMap(a -> ((Async)((FlatMap)sub).k.apply(a)).flatMap(k));
            } else {
                break;
            }
        }
        return async;
    }
}
