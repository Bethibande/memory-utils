package de.bethibande.memory.impl;

import de.bethibande.memory.Allocator;
import de.bethibande.memory.Buffer;

/**
 * DirectAllocator is an implementation of the Allocator interface for allocating off-heap memory buffers using the {@link DefaultBuffer} implementation.
 */
public class DirectAllocator implements Allocator {

    @Override
    public Buffer allocate(final long size) {
        return Buffer.direct(size);
    }
}
