import fpjava.parallelism.Par;
import fpjava.iomonad.TailRec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static fpjava.iomonad.TailRec.done;
import static fpjava.iomonad.TailRec.more;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            nums.add(i);
        }
        Par<Integer> sum = sum(nums);
        final ExecutorService es = Executors.newFixedThreadPool(1);
        System.out.println(Par.run(es, sum));

        TailRec<Boolean> result = even(100001);
        System.out.println(TailRec.run(result));

        Function<Integer, TailRec<Integer>> f = x -> done(x);
        Function<Integer, TailRec<Integer>> g = f;
        for (int i = 0; i < 10000; i++) {
            g = compose(f, g);
        }
        System.out.println(TailRec.run(g.apply(42)));

        es.shutdown();
    }

    private static Par<Integer> sum(List<Integer> nums) {
        if (nums.size() == 0) return Par.unit(0);
        if (nums.size() == 1) return Par.unit(nums.get(0));
        final List<Integer> l = nums.subList(0, nums.size() / 2);
        final List<Integer> r = nums.subList(nums.size() / 2, nums.size());
        return Par.map2(Par.fork(() -> sum(l)), Par.fork(() -> sum(r)), (a, b) -> a + b);
    }

    private static <A, B, C>
    Function<A, TailRec<C>> compose(Function<A, TailRec<B>> f, Function<B, TailRec<C>> g) {
        return a -> f.apply(a).flatMap(g);
    }


    private static TailRec<Boolean> even(int n) {
        if (n == 0) return done(true);
        return more(() -> odd(n - 1));
    }

    private static TailRec<Boolean> odd(int n) {
        if (n == 0) return done(false);
        return more(() -> even(n - 1));
    }
}
