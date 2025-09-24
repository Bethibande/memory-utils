package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

public record CompositeRegion(
        Buffer buffer,
        long offset
) {

    public long pos(final long globalOffset) {
        return globalOffset - this.offset;
    }

    public boolean canFit(final long globalOffset, final int length) {
        return globalOffset + length <= this.offset + this.buffer.capacity();
    }

}
