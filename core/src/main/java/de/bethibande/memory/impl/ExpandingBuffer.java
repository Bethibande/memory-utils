package de.bethibande.memory.impl;

import de.bethibande.memory.Allocator;
import de.bethibande.memory.Buffer;

import java.nio.ByteBuffer;

import static java.lang.Math.max;

/**
 * The {@code ExpandingBuffer} class is a specialized buffer that can dynamically expand its capacity
 * when necessary. It extends {@link FastCompositeBuffer} and is optimized for scenarios requiring
 * multiple buffers in a composite structure. It provides mechanisms for automatically allocating
 * and integrating additional buffers into the structure when the writable space is insufficient.
 * <br>
 * This class leverages an {@link Allocator} implementation to manage the creation of new buffers
 * and handles dynamic memory management efficiently.
 * <br>
 * It is recommended to use this class in combination with a {@link PooledAllocator}
 */
public class ExpandingBuffer extends FastCompositeBuffer {

    private final Allocator allocator;

    public ExpandingBuffer(final int initialBufferCount, final int exponent, final Allocator allocator) {
        final Buffer[] buffers = new Buffer[initialBufferCount];
        for (int i = 0; i < initialBufferCount; i++) {
            buffers[i] = allocator.allocate(-1);
        }

        this(buffers, exponent, allocator);
    }

    public ExpandingBuffer(final Buffer[] buffers, final int exponent, final Allocator allocator) {
        super(buffers, exponent);

        this.allocator = allocator;
    }

    @Override
    protected Buffer allocateBuffer() {
        return allocator.allocate(expectedRegionSize());
    }

    @Override
    protected long writeIdx(final long bytes) {
        final long currentWritePosition = writePosition();
        final long nextWritePosition = currentWritePosition + bytes;

        final int requiredRegionIndex = (int) ((nextWritePosition - 1) >> exponent);

        while (requiredRegionIndex >= super.regions.length) {
            expand();
        }

        try {
            return currentWritePosition;
        } finally {
            writePosition(nextWritePosition);
        }
    }

    public long writableAt(final long position) {
        return capacity() - position;
    }

    /**
     * Compact the underlying buffer regions by identifying and releasing unused buffer segments,
     * then updates the structure of the buffer regions to reflect the reduced memory footprint.
     * <br>
     * This method iteratively checks if buffer regions up to the current read position can be
     * released based on the expected allocation size. Regions that are no longer needed are
     * released, and the remaining regions are shifted to the start of the internal structures.
     * Additionally, the read and write positions are adjusted to account for the released memory regions.
     */
    public void compact() {
        final long allocationSize = expectedRegionSize();

        long offset = 0;
        int bufferCount = 0;
        while (offset + allocationSize <= readPosition()) {
            final CompositeRegion region = regionAt(offset);
            region.buffer().release();
            bufferCount++;
            offset += allocationSize;
        }

        final int remainingBufferCount = this.regions.length - bufferCount;
        final CompositeRegion[] regions = new CompositeRegion[max(remainingBufferCount, 1)];
        System.arraycopy(this.regions, bufferCount, regions, 0, remainingBufferCount);

        if (remainingBufferCount == 0) {
            regions[0] = region(allocateBuffer(), 0);
        }

        this.regions = regions;
        this.size = regions.length * allocationSize;

        final long capacityReduction = bufferCount * allocationSize;
        readPosition(readPosition() - capacityReduction);
        writePosition(writePosition() - capacityReduction);
    }

    @Override
    public void write(final ByteBuffer readable) {
        final int bytes = readable.remaining();
        set(writeIdx(bytes), readable, readable.position(), bytes);
    }

    @Override
    protected String className() {
        return "ExpandingBuffer";
    }
}
