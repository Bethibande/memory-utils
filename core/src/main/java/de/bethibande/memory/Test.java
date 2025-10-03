package de.bethibande.memory;

import de.bethibande.memory.impl.RingBuffer;

public class Test {

    static void main() {
        final long capacity = (long) Math.pow(2, 3);
        final Buffer a = Buffer.direct(capacity);
        final Buffer b = Buffer.direct(capacity);

        final RingBuffer ring = Buffer.ring(3, a, b);

        System.out.println(ring);


        /*for (int i = 0; i < 10_000; i++) {
            ring.write(1234L);
        }

        final long start = System.currentTimeMillis();
        for (int i = 0; i < 100_000_000; i++) {
            ring.write(235434L);
        }
        final long time = System.currentTimeMillis() - start;
        System.out.println("Time: " + time + " ms");*/

        System.out.println(ring);
    }

}
