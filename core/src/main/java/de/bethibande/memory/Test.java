package de.bethibande.memory;

import de.bethibande.memory.impl.CompositeBuffer;

public class Test {

    static void main() {
        final Buffer a = Buffer.direct(4);
        final Buffer b = Buffer.direct(4);

        final CompositeBuffer composite = (CompositeBuffer) Buffer.composite(a, b);

        composite.write(12345L);
        final Buffer slice = composite.slice(0, 8);

        final Buffer c = Buffer.direct(8);
        composite.expand(c, 1);

        System.out.println(slice.readLong());
        System.out.println(composite.capacity());
    }

}
