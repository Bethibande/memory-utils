package de.bethibande.memory;

import de.bethibande.memory.impl.CompositeBuffer;

public class Test {

    static void main() {
        final long capacity = (long) Math.pow(2, 16);
        final Buffer a = Buffer.direct(capacity);
        final Buffer b = Buffer.direct(capacity);

        final CompositeBuffer composite = Buffer.fastComposite(16, a, b);
        composite.set(capacity - 4, 12345L);

        System.out.println(composite);

        //composite.write(12345L);
        //final Buffer slice = composite.slice(0, 8);

        //final Buffer c = Buffer.direct(4);
        //composite.expand(c, 1);

        //System.out.println(slice.readLong());
        //System.out.println(composite.capacity());
    }

}
