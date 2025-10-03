package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompositeBufferTest {

    @Test
    public void testWrite() {
        final Buffer buffer = Buffer.composite(Buffer.direct(8));
        final long value = 1234L;
        buffer.write(value);

        assertEquals(value, buffer.readLong());
    }

    @Test
    public void testCrossBoundaryWrite() {
        final Buffer composite = Buffer.composite(Buffer.direct(4), Buffer.direct(4));

        final long value = -1234L;
        composite.write(value);

        assertEquals(value, composite.readLong());
    }

    @Test
    public void testExpand() {
        final CompositeBuffer buffer = Buffer.composite(Buffer.direct(4));
        buffer.expand(Buffer.direct(4), 1);

        final long value = -1234L;
        buffer.write(value);

        assertEquals(value, buffer.readLong());
    }

    @Test
    public void testExpandReadPosition() {
        final CompositeBuffer composite = Buffer.composite(Buffer.direct(4));

        assertEquals(0, composite.readPosition());
        composite.expand(Buffer.direct(4), 0);
        assertEquals(4, composite.readPosition());
    }

}
