package de.bethibande.memory.impl;

import de.bethibande.memory.Allocator;
import de.bethibande.memory.Buffer;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A pooled allocator designed to manage and reuse memory buffers efficiently.
 * Buffers are allocated with a specified size, reused through an internal pool, and released back into the pool
 * when no longer needed.
 * The allocated buffers will be an instance of {@link JavaNioBuffer}.
 * <br>
 * This allocator enhances performance by limiting repeated memory allocation and deallocation, while
 * maintaining a consistent size for memory buffers.
 */
public class PooledAllocator implements Allocator {

    private final AtomicInteger allocated = new AtomicInteger();
    private final Queue<PooledBuffer> pool = new ArrayDeque<>();
    private final int allocationSize;

    public PooledAllocator(final int allocationSize) {
        this.allocationSize = allocationSize;
    }

    protected Buffer doAllocate() {
        allocated.incrementAndGet();
        return new PooledBuffer(ByteBuffer.allocateDirect(allocationSize), this);
    }

    /**
     * Allocates a buffer of the specified size from the internal pool if available or creates a new one.
     * If a size other than the pre-configured allocation size is requested, an exception is thrown.
     *
     * @param size the size of the buffer to allocate. Must match the pre-defined allocation size or be -1.
     * @return a buffer of the requested size, either from the pool or newly allocated.
     * @throws IllegalArgumentException if the size does not match the pre-defined allocation size and is not -1.
     */
    @Override
    public Buffer allocate(final long size) {
        if (size != -1 && size != allocationSize) throw new IllegalArgumentException("Invalid allocation size!");

        final Buffer buffer = pool.poll();
        if (buffer != null) return buffer;

        return doAllocate();
    }

    protected void release(final PooledBuffer buffer) {
        buffer.reset();
        pool.offer(buffer);
    }

    /**
     * Retrieves the current size of the internal pool queue.
     *
     * @return the number of buffers currently present in the pool queue.
     */
    public int queueSize() {
    	return pool.size();
    }

    /**
     * Retrieves the total number of allocated buffers managed by the pool.
     *
     * @return the total number of buffers that have been allocated.
     */
    public int poolSize() {
        return allocated.get();
    }


    /**
     * PooledBuffer is a subclass of {@link JavaNioBuffer} owned by a {@link PooledAllocator}.
     * This class is for internal use only.
     */
    protected static class PooledBuffer extends JavaNioBuffer {

        protected final PooledAllocator allocator;

        public PooledBuffer(final ByteBuffer buffer, final PooledAllocator allocator) {
            super(buffer);
            this.allocator = allocator;
        }

        @Override
        protected void free() {
            this.allocator.release(this);
        }
    }

}
