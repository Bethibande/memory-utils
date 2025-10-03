package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

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
