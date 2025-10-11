package de.bethibande.memory.impl;

import de.bethibande.memory.Allocator;
import de.bethibande.memory.Buffer;

/**
 * An implementation of the {@link Allocator} interface that provides functionality
 * for allocating instances of the {@link JavaNioBuffer} class.
 */
public class DirectNioAllocator implements Allocator {

    @Override
    public Buffer allocate(final long size) {
        return Buffer.directNio((int) size);
    }
}
