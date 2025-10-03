package de.bethibande.memory.impl;

import java.lang.foreign.MemorySegment;

public class PooledBuffer extends DefaultBuffer {

    protected final PooledAllocator allocator;

    public PooledBuffer(final MemorySegment segment, final PooledAllocator allocator) {
        super(segment);
        this.allocator = allocator;
    }

    @Override
    protected void free() {
        this.allocator.release(this);
    }
}
