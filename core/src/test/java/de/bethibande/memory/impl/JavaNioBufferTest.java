package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaNioBufferTest {

    @Test
    public void testWritePosition() {
        final Buffer buffer = Buffer.directNio(1024);

        assertEquals(0, buffer.writePosition());
        buffer.write(1L);
        assertEquals(8, buffer.writePosition());
    }

    @Test
    public void testReadPosition() {
        final Buffer buffer = Buffer.directNio(1024);

        assertEquals(0, buffer.readPosition());
        buffer.readLong();
        assertEquals(8, buffer.readPosition());
    }

}
