package de.bethibande.memory;

import de.bethibande.memory.impl.BufferImpl;
import de.bethibande.memory.impl.CompositeBuffer;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;

public interface Buffer extends Gettable, Settable, Readable, Writable, Sliceable, ReferenceCounted {

    static Buffer direct(final long capacity) {
        return new BufferImpl(Arena.global().allocate(capacity));
    }

    static Buffer allocate(final int capacity) {
        return new BufferImpl(MemorySegment.ofBuffer(ByteBuffer.allocate(capacity)));
    }

    static Buffer composite(final Buffer... buffers) {
        return new CompositeBuffer(buffers);
    }

    long capacity();

    long writePosition();

    long readPosition();

    void writePosition(final long position);

    void readPosition(final long position);

    default long readable() {
        return writePosition() - readPosition();
    }

    default long writable() {
        return capacity() - writePosition();
    }

    default void reset() {
        readPosition(0L);
        writePosition(0L);
    }

}
