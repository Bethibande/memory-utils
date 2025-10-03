package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

public class FastCompositeBuffer extends CompositeBuffer {

    protected final int exponent;
    protected final int bitMask;

    public FastCompositeBuffer(final int initialBufferCount, final int exponent) {
        final int bufferSize = 1 << exponent;
        final Buffer[] buffers = new Buffer[initialBufferCount];
        for (int i = 0; i < initialBufferCount; i++) {
            buffers[i] = Buffer.direct(bufferSize);
        }

        this(buffers, exponent);
    }

    public FastCompositeBuffer(final Buffer[] buffers, final int exponent) {
        this.bitMask = (1 << exponent) - 1;
        this.exponent = exponent;

        super(buffers);

        validateBufferSizes(buffers);
    }

    public long expectedRegionSize() {
        return bitMask + 1;
    }

    private void validateBufferSizes(final Buffer[] buffers) {
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].capacity() != expectedRegionSize()) {
                throw new IllegalArgumentException("The buffer capacity " + buffers[i].capacity() + " does not match the expected size of " + expectedRegionSize());
            }
        }
    }

    /**
     * Expands the current buffer by allocating a new {@code Buffer} instance with the expected region size
     * and integrating it into the existing buffer structure.
     *
     * @return a newly allocated {@code Buffer} instance with the capacity equal to the expected region size.
     * @throws IllegalArgumentException if the capacity of the newly created buffer does not match the expected region size.
     */
    public Buffer expand() {
        final Buffer buffer = Buffer.direct(expectedRegionSize());
        expand(buffer, super.regions.length);
        return buffer;
    }

    @Override
    public void expand(final Buffer buffer, final int index) {
        if (buffer.capacity() != expectedRegionSize()) {
            throw new IllegalArgumentException("The buffer capacity " + buffer.capacity() + " does not match the expected size of " + (bitMask + 1));
        }

        super.expand(buffer, index);
    }

    @Override
    protected CompositeRegion region(final Buffer buffer, final long offset) {
        return new FastCompositeRegion(buffer, offset, this.bitMask);
    }

    @Override
    protected CompositeRegion regionAt(final long offset) {
        return super.regions[(int) (offset >> exponent)];
    }

    @Override
    protected String className() {
        return "FastCompositeBuffer";
    }
}
