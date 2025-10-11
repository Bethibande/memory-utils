package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

/**
 * This is the custom implementation of {@link CompositeRegion} for use in {@link FastCompositeBuffer}.
 */
public class FastCompositeRegion extends CompositeRegion {

    private final int mask;

    public FastCompositeRegion(final Buffer buffer, final long offset, final int mask) {
        super(buffer, offset);
        this.mask = mask;
    }

    @Override
    public long pos(final long globalOffset) {
        return globalOffset & mask;
    }

    @Override
    public boolean canFit(final long globalOffset, final int length) {
        return pos(globalOffset) + length <= buffer().capacity();
    }
}
