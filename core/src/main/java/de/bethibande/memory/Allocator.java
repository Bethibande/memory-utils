package de.bethibande.memory;

import de.bethibande.memory.impl.DirectAllocator;
import de.bethibande.memory.impl.PooledAllocator;

public interface Allocator {

    static Allocator direct() {
        return new DirectAllocator();
    }

    static PooledAllocator pooled(final int size) {
        return new PooledAllocator(size);
    }

    Buffer allocate(final long size);

}
