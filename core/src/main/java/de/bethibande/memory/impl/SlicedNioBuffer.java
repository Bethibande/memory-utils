package de.bethibande.memory.impl;

import java.nio.ByteBuffer;

/**
 * The SlicedNioBuffer class is a specialized type of {@link JavaNioBuffer}
 * that represents a sliced view of an existing {@link ByteBuffer}.
 */
public class SlicedNioBuffer extends JavaNioBuffer {

    private final JavaNioBuffer parent;

    public SlicedNioBuffer(final ByteBuffer buffer, final JavaNioBuffer parent) {
        super(buffer);
        this.parent = parent;
    }

    @Override
    protected void free() {
        this.parent.release();
    }
}
