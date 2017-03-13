package fpjava.data;


import java.util.function.Function;
import java.util.function.Supplier;

public class Option<A> {
    private final static Option<Object> NONE = new Option(null);
    private final A a;

    public Option(A a) {
        this.a = a;
    }

    public <B> Option<B> map(Function<A, B> f) {
        return isSome() ? some(f.apply(get())) : none();
    }

    public <B> Option<B> flatMap(Function<A, Option<B>> f) {
        return isSome() ? f.apply(get()) : none();
    }

    public boolean isSome() {
        return a != null;
    }

    public boolean isNone() {
        return a == null;
    }

    public A get() {
        return a;
    }

    public A getOrElse(Supplier<A> a) {
        return isSome() ? get() : a.get();
    }

    public static <A> Option<A> none() {
        return (Option<A>) NONE;
    }

    public static <A> Option<A> some(A a) {
        if (a == null)
            throw new NullPointerException();
        return new Option(a);
    }

    @Override
    public String toString() {
        return "Option{" +
                "a=" + a +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option<?> option = (Option<?>) o;

        return !(a != null ? !a.equals(option.a) : option.a != null);

    }

    @Override
    public int hashCode() {
        return a != null ? a.hashCode() : 0;
    }
}