package fpjava.data;

import fpjava._1;
import org.junit.Test;

import static fpjava.data.List.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ListTest {

    @Test
    public void sequenceListOfList() {
        final _1<List.z, _1<List.z, Integer>> sequence = traversable.sequence(monad, of(of(1, 2), of(5, 6)));
        assertEquals(sequence, of(of(1, 5), of(1, 6), of(2, 5), of(2, 6)));
    }

}