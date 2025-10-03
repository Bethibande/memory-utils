package de.bethibande.memory.impl;

import de.bethibande.memory.Allocator;
import de.bethibande.memory.Buffer;

public class DirectAllocator implements Allocator {

    @Override
    public Buffer allocate(final long size) {
        return Buffer.direct(size);
    }
}
