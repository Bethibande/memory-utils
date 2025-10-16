package de.bethibande.memory.impl;

import de.bethibande.memory.Allocator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpandingBufferTest {

    @Test
    public void testAllocate() {
        final PooledAllocator allocator = Allocator.pooled(1 << 3);
        final ExpandingBuffer buffer = new ExpandingBuffer(1, 3, allocator);

        buffer.write(1L);
        assertEquals(1L, buffer.getLong(0));
        assertEquals(8, buffer.capacity());

        buffer.write(2L);
        assertEquals(2L, buffer.getLong(8));
        assertEquals(16, buffer.capacity());
    }

    @Test
    public void testCompact() {
        final PooledAllocator allocator = Allocator.pooled(1 << 3);
        final ExpandingBuffer buffer = new ExpandingBuffer(1, 3, allocator);

        buffer.write(1L);
        buffer.write(2L);

        buffer.readLong(); // Increase read position

        assertEquals(16, buffer.capacity());
        assertEquals(8, buffer.readPosition());
        assertEquals(16, buffer.writePosition());

        buffer.compact();

        assertEquals(8, buffer.capacity());
        assertEquals(0, buffer.readPosition());
        assertEquals(8, buffer.writePosition());
        assertEquals(2, allocator.poolSize());
        assertEquals(1, allocator.queueSize());
    }

}
