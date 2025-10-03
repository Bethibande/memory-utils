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
    public long writable() {
        return capacity() - readable();
    }

    @Override
    public long readable() {
        return writePosition() - readPosition();
    }

    @Override
    protected CompositeRegion regionAt(final long offset) {
        return super.regions[(int) (offset >> exponent) % super.regions.length];
    }

    @Override
    protected String className() {
        return "RingBuffer";
    }
}
