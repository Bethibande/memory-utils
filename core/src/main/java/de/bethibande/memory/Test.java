package de.bethibande.memory;

import de.bethibande.memory.impl.ExpandingBuffer;

public class Test {

    static void main() {
        final ExpandingBuffer buffer = Buffer.expanding(3);

        System.out.println(buffer);
        buffer.write(1L);
        System.out.println(buffer);
        buffer.write(2L);
        System.out.println(buffer);
        buffer.readLong();
        buffer.compact();
        System.out.println(buffer);
    }

}
