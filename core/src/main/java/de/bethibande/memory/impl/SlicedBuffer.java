package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

import java.lang.foreign.MemorySegment;

public class SlicedBuffer extends DefaultBuffer {

    private final Buffer parent;

    public SlicedBuffer(final MemorySegment segment, final Buffer parent) {
        super(segment);

        this.parent = parent;
    }

    public Buffer parent() {
        return parent;
    }

    @Override
    protected void free() {
        this.parent.release();
    }
}
