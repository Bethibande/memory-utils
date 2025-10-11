package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

/**
 * CompositeRegion is a simple data structure that represents a logical region of memory within a {@link CompositeBuffer}.
 * This class is meant for internal use only and should not be used by external code.
 */
public class CompositeRegion {

    private final Buffer buffer;
    private long offset;

    public CompositeRegion(final Buffer buffer, final long offset) {
        this.buffer = buffer;
        this.offset = offset;
    }

    public Buffer buffer() {
        return buffer;
    }

    public long offset() {
        return offset;
    }

    public void offset(final long offset) {
        this.offset = offset;
    }

    public long pos(final long globalOffset) {
        return globalOffset - this.offset;
    }

    public boolean canFit(final long globalOffset, final int length) {
        return globalOffset + length <= this.offset + this.buffer.capacity();
    }

    @Override
    public String toString() {
        return "CompositeRegion { " +
               "capacity: " + this.buffer.capacity() + ", " +
               "offset: " + this.offset +
               " }";
    }
}
