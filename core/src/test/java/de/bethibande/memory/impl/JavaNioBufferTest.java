package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

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

    @Test
    public void testWriteBuffer() {
        final JavaNioBuffer buffer = new JavaNioBuffer(ByteBuffer.allocateDirect(8));
        final ByteBuffer src = ByteBuffer.allocateDirect(8);
        src.putLong(1234L);
        src.flip();

        buffer.write(src);

        assertEquals(1234L, buffer.getLong(0));
    }

    @Test
    public void testReadBuffer() {
        final JavaNioBuffer buffer = new JavaNioBuffer(ByteBuffer.allocateDirect(8));
        final ByteBuffer dst = ByteBuffer.allocateDirect(8);

        buffer.write(1234L);
        buffer.read(dst);

        assertEquals(1234L, dst.getLong(0));
    }

}
