package fpjava.data;

import fpjava.*;

import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Linked list.
 * <p>
 * Not optimized for recursion or laziness
 *
 * @param <T>
 * @author jooyung.han
 */
public abstract class List<T> implements _1<List.z, T> {
    public static class z {
    }

    static class _z implements Monad<z>, Traversable<z> {

        @Override
        public <A, B> B foldr(BiFunction<A, B, B> f, B z, _1<z, A> ta) {
            final List<A> list = (List<A>) ta;
            return list.foldr(f, z);
        }

        @Override
        public <F, A> _1<F, _1<z, A>> sequenceA(Applicative<F> applicative, _1<z, _1<F, A>> ta) {
            final List<_1<F, A>> list = (List<_1<F, A>>) ta;
            return list.foldr((x, xs) -> applicative.liftA2((a, as) -> cons(a, (List<A>) as), x, xs), applicative.pure(List::nil));
        }

        @Override
        public <A> _1<z, A> unit(Supplier<A> f) {
            return List.unit(f.get());
        }

        @Override
        public <A, B> _1<z, B> bind(Function<A, _1<z, B>> f, _1<z, A> ma) {
            return ((List<A>) ma).flatMap(a -> (List<B>) f.apply(a));
        }
    }

    private static _z __z = new _z();
    public static Monad<z> monad = __z;
    public static Traversable<z> traversable = __z;

    private static final List<Object> NIL = new Nil();

    /**
     * constructors
     */

    @SuppressWarnings("unchecked")
    public static <T> List<T> nil() {
        return (List<T>) NIL;
    }

    public static <T> List<T> cons(T head, List<T> tail) {
        return new Cons<T>(head, tail);
    }

    public static <T> List<T> of(T... ts) {
        List<T> list = nil();
        for (int i = ts.length - 1; i >= 0; i++) {
            list = cons(ts[i], list);
        }
        return list;
    }

    /**
     * monad unit
     */
    public static <T> List<T> unit(T element) {
        return cons(element, nil());
    }

    /**
     * monad bind
     */
    public <S> List<S> flatMap(Function<T, List<S>> f) {
        return join(map(f));
    }

    /** List operations */

    /**
     * functor map
     */
    public <S> List<S> map(Function<T, S> f) {
        return isNil() ? nil() : cons(f.apply(head()), tail().map(f));
    }

    public static <T> List<T> join(List<List<T>> tss) {
        return tss.isNil() ? nil() : tss.head().append(join(tss.tail()));
    }

    public List<T> append(List<T> app) {
        return isNil() ? app : cons(head(), tail().append(app));
    }

    public T foldl1(BinaryOperator<T> join) {
        return tail().foldl(join, head());
    }

    public <R> R foldl(BiFunction<R, T, R> join, R z) {
        List<T> cur = this;
        while (!cur.isNil()) {
            z = join.apply(z, cur.head());
            cur = cur.tail();
        }
        return z;
    }

    public <R> R foldr(BiFunction<T, R, R> join, R z) {
        return isNil() ? z : join.apply(head(), tail().foldr(join, z));
    }

    abstract public boolean isNil();

    abstract public T head();

    abstract public List<T> tail();

    /**
     * internals
     */

    private static class Nil extends List<Object> {

        @Override
        public String toString() {
            return "Nil []";
        }

        @Override
        public Object head() {
            throw new NoSuchElementException("head from nil");
        }

        @Override
        public List<Object> tail() {
            throw new NoSuchElementException("tail from nil");
        }

        @Override
        public boolean isNil() {
            return true;
        }

    }

    private static class Cons<T> extends List<T> {

        private final T head;
        private final List<T> tail;

        public Cons(T head, List<T> tail) {
            this.head = head;
            this.tail = tail;
        }

        @Override
        public String toString() {
            return "Cons [" + (head != null ? "head=" + head + ", " : "") + (tail != null ? "tail=" + tail : "") + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((head == null) ? 0 : head.hashCode());
            result = prime * result + ((tail == null) ? 0 : tail.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Cons<?> other = (Cons<?>) obj;
            if (head == null) {
                if (other.head != null) {
                    return false;
                }
            } else if (!head.equals(other.head)) {
                return false;
            }
            if (tail == null) {
                if (other.tail != null) {
                    return false;
                }
            } else if (!tail.equals(other.tail)) {
                return false;
            }
            return true;
        }

        @Override
        public T head() {
            return head;
        }

        @Override
        public List<T> tail() {
            return tail;
        }

        @Override
        public boolean isNil() {
            return false;
        }

    }

}
