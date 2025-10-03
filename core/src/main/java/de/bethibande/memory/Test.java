package de.bethibande.memory;

import de.bethibande.memory.impl.RingBuffer;

public class Test {

    static void main() {
        final long capacity = (long) Math.pow(2, 3);
        final Buffer a = Buffer.direct(capacity);
        final Buffer b = Buffer.direct(capacity);

        final RingBuffer ring = Buffer.ring(3, a, b);

        System.out.println(ring);


        ring.write(1L);
        System.out.println(ring);

        ring.write(2L);
        System.out.println(ring);

        ring.readLong();
        ring.write(3L);

        System.out.println(b.getLong(0));

        System.out.println(ring);
    }

}
