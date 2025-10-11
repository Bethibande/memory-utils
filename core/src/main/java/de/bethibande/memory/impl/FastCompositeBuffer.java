package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

/**
 * A {@link CompositeBuffer} implementation that uses a fixed-size buffer for each region.
 * This makes memory access and finding the backing buffer at any position much faster at the cost of flexibility.
 * Each buffer has a fixed size and is allocated using a given exponent, resulting in buffer sizes of 2^exponent.
 */
public class FastCompositeBuffer extends CompositeBuffer {

    protected final int exponent;
    protected final int bitMask;

    /**
     * Constructs a FastCompositeBuffer with a specified number of initial buffers, each having a size defined by an exponent.
     * The buffer size is calculated as 2 raised to the power of the given exponent.
     *
     * @param initialBufferCount the number of buffers to initialize the composite buffer with
     * @param exponent the power of 2 that defines the size of each buffer, resulting in buffer sizes of 2^exponent
     * @throws IllegalArgumentException if any buffer's size does not match the calculated size based on the exponent
     */
    public FastCompositeBuffer(final int initialBufferCount, final int exponent) {
        final int bufferSize = 1 << exponent;
        final Buffer[] buffers = new Buffer[initialBufferCount];
        for (int i = 0; i < initialBufferCount; i++) {
            buffers[i] = Buffer.direct(bufferSize);
        }

        this(buffers, exponent);
    }

    /**
     * Constructs a FastCompositeBuffer using an array of {@code Buffer} objects and an exponent that defines
     * the buffer size as 2 raised to the power of the exponent.
     *
     * @param buffers an array of {@code Buffer} objects used to initialize the composite buffer
     * @param exponent the power of 2 that defines the size of each buffer, where buffer size is calculated as 2^exponent
     * @throws IllegalArgumentException if any buffer in the array does not match the expected size calculated from the exponent
     */
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

    protected Buffer allocateBuffer() {
        return Buffer.direct(expectedRegionSize());
    }

    /**
     * Expands the current buffer by allocating a new {@code Buffer} instance with the expected region size
     * and integrating it into the existing buffer structure.
     *
     * @return a newly allocated {@code Buffer} instance with the capacity equal to the expected region size.
     * @throws IllegalArgumentException if the capacity of the newly created buffer does not match the expected region size.
     */
    public Buffer expand() {
        final Buffer buffer = allocateBuffer();
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
    protected int bufferIdxAt(final long offset) {
        return (int) (offset >> exponent);
    }

    @Override
    protected String className() {
        return "FastCompositeBuffer";
    }
}
