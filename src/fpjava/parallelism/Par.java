package fpjava.parallelism;

import fpjava.Actor;
import fpjava.data.List;
import fpjava.data.Either;
import fpjava.data.Option;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static fpjava.data.Either.left;
import static fpjava.data.Either.right;

public abstract class Par<A> {
    abstract Future<A> execute(ExecutorService es);

    public <B> Par<B> flatMap(Function<A, Par<B>> f) {
        final Par<A> outer = this;
        return new Par<B>() {

            @Override
            Future<B> execute(ExecutorService es) {
                return k -> outer.execute(es).apply(a -> f.apply(a).execute(es).apply(k));
            }
        };
    }

    public <B> Par<B> map(Function<A, B> f) {
        return flatMap(a -> unit(f.apply(a)));
    }

    interface Future<A> {
        void apply(Consumer<A> k);
    }

    private static class Unit<A> extends Par<A> {
        private final A a;

        Unit(A a) {
            this.a = a;
        }

        @Override
        Future<A> execute(ExecutorService es) {
            return k -> k.accept(a);
        }
    }

    public static <A> Par<A> unit(A a) {
        return new Unit<>(a);
    }

    public static <A, B, C> Par<C> map2(Par<A> pa, Par<B> pb, BiFunction<A, B, C> f) {
        return new Par<C>() {
            @Override
            Future<C> execute(ExecutorService es) {
                return k -> {
                    final Actor<Either<A, B>> combiner = new Actor<Either<A, B>>(es) {
                        Option<A> ar = Option.none();
                        Option<B> br = Option.none();
                        @Override
                        protected void handle(Either<A, B> either) {
                            either.foldVoid(a -> {
                                if (br.isNone()) ar = Option.some(a);
                                else eval(es, () -> k.accept(f.apply(a, br.get())));
                            }, b -> {
                                if (ar.isNone()) br = Option.some(b);
                                else eval(es, () -> k.accept(f.apply(ar.get(), b)));
                            });
                        }
                    };
                    pa.execute(es).apply(a -> combiner.send(left(a)));
                    pb.execute(es).apply(b -> combiner.send(right(b)));
                };
            }
        };
    }

    public static <A> Par<A> fork(Supplier<Par<A>> a) {
        return new Par<A>() {
            @Override
            Future<A> execute(ExecutorService es) {
                return k -> eval(es, () -> a.get().execute(es).apply(k));
            }
        };
    }

    static void eval(ExecutorService es, Runnable r) {
        es.submit(r);
    }

    public static <A> A run(ExecutorService es, Par<A> pa) throws InterruptedException {
        AtomicReference<A> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        pa.execute(es).apply(a -> {
            ref.set(a);
            latch.countDown();
        });
        latch.await();
        return ref.get();
    }

    // p.109 #7.5
    public static <A> Par<List<A>> sequence(List<Par<A>> ps) {
        if (ps.isNil()) {
            return unit(List.nil());
        } else {
            return map2(ps.head(), sequence(ps.tail()), (h,t) -> List.cons(h,t));
        }
    }
}
