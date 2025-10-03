package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RingBufferTest {

    @Test
    public void testWrapAround() {
        final Buffer a = Buffer.direct(8);
        final Buffer b = Buffer.direct(8);
        final Buffer buffer = Buffer.ring(3, a, b);

        buffer.write(1L);
        buffer.write(2L);

        assertEquals(1L, a.getLong(0));
        assertEquals(2L, b.getLong(0));

        buffer.readLong();
        buffer.readLong(); // Move the read index to ensure the buffer doesn't expand

        buffer.write(3L);

        assertEquals(3L, a.getLong(0));
        assertEquals(2L, b.getLong(0));
    }

}
