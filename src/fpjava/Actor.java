package fpjava;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

class Node<A> extends AtomicReference<Node<A>> {
    A a;

    Node() {
        this.a = null;
    }

    Node(A a) {
        this.a = a;
    }
}

public abstract class Actor<A> {
    private final Strategy strategy;

    public Actor(ExecutorService es) {
        this(Strategy.fromExecutorService(es));
    }

    public Actor(Strategy strategy) {
        this.strategy = strategy;
    }

    protected abstract void handle(A a);

    private final AtomicReference<Node<A>> tail = new AtomicReference<>(new Node<>());
    private final AtomicInteger suspended = new AtomicInteger(1);
    private final AtomicReference<Node<A>> head = new AtomicReference<>(tail.get());

    public void send(A a) {
        final Node<A> n = new Node<>(a);
        head.getAndSet(n).lazySet(n);
        trySchedule();
    }

    private void trySchedule() {
        if (suspended.compareAndSet(1, 0)) schedule();
    }

    private void schedule() {
        strategy.apply(() -> {
            act();
            return null;
        });
    }

    private void act() {
        Node<A> t = tail.get();
        Node<A> n = batchHandle(t, 1024);
        if (n != t) {
            n.a = null;
            tail.lazySet(n);
            schedule();
        } else {
            suspended.set(1);
            if (n.get() != null) trySchedule();
        }
    }

    private Node<A> batchHandle(Node<A> t, int i) {
        while (true) {
            Node<A> n = t.get();
            if (n != null) {
                handle(n.a);
                if (i > 0) {
                    t = n;
                    i--;
                    continue;
                } else {
                    return n;
                }
            } else {
                return t;
            }
        }
    }

    public interface Strategy {
        <A> Supplier<A> apply(Supplier<A> a);

        static Strategy fromExecutorService(ExecutorService es) {
            return new Strategy() {

                @Override
                public <A> Supplier<A> apply(Supplier<A> a) {
                    java.util.concurrent.Future<A> f = es.submit(() -> a.get());
                    return () -> {
                        try {
                            return f.get();
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    };
                }
            };
        }
    }
}
