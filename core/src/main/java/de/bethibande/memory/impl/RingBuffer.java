package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

/**
 * A {@code RingBuffer} is an advanced implementation of {@code FastCompositeBuffer} designed
 * to handle data in a circular or ring-like manner. It allows for efficient use of memory by
 * recycling buffer space once consumed, while dynamically expanding to accommodate additional data if needed.
 * <br>
 * The buffer operates using fixed-size regions determined by the exponent parameter, ensuring
 * predictable memory allocation and fast access times. It maintains separate read and write positions
 * to track available data for consumption and space for new data, supporting both fixed and dynamic memory usage patterns.
 * <br>
 * If there are not enough bytes to write according to {@link #writable()},
 * the buffer will automatically expand by one or more buffers ahead of the position to be written to accommodate the new data.
 * If there are enough bytes left to write and the read-position is not 0, the buffer will simply wrap around to the beginning of the buffer if necessary.
 * <br>
 * The read and write-positions are incremented indefinitely and may exceed the capacity of the buffer.
 */
public class RingBuffer extends FastCompositeBuffer {

    public RingBuffer(final int initialBufferCount, final int exponent) {
        super(initialBufferCount, exponent);
    }

    public RingBuffer(final Buffer[] buffers, final int exponent) {
        super(buffers, exponent);
    }

    @Override
    protected long writeIdx(final long bytes) {
        final long position = writePosition();
        final long remaining = writableAt(position);

        if (remaining >= bytes) {
            return super.writeIdx(bytes);
        }

        while (writableAt(position) < bytes) {
            expandAt(position);
        }

        return super.writeIdx(bytes);
    }

    public void expandAt(final long position) {
        final int idx = bufferIdxAfter(position);
        expand(allocateBuffer(), idx + 1);
    }

    public long writableAt(final long position) {
        return capacity() - (position - readPosition());
    }

    @Override
    public long writable() {
        return capacity() - readable();
    }

    @Override
    public long readable() {
        return writePosition() - readPosition();
    }

    protected int bufferIdxAfter(final long position) {
        final int regionCount = super.regions.length;
        long nextRegion = ((position + expectedRegionSize() - 1) >> exponent) % regionCount;
        return (int) ((nextRegion + 1) % regionCount);
    }

    @Override
    protected int bufferIdxAt(final long offset) {
        return (int) (offset >> exponent) % super.regions.length;
    }

    @Override
    protected String className() {
        return "RingBuffer";
    }
}
