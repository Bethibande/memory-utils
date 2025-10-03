package de.bethibande.memory;

import de.bethibande.memory.impl.CompositeBuffer;
import de.bethibande.memory.impl.DefaultBuffer;
import de.bethibande.memory.impl.FastCompositeBuffer;
import de.bethibande.memory.impl.RingBuffer;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;

public interface Buffer extends Gettable, Settable, Readable, Writable, Sliceable, ReferenceCounted {

    static Buffer direct(final long capacity) {
        return new DefaultBuffer(Arena.global().allocate(capacity));
    }

    static Buffer allocate(final int capacity) {
        return new DefaultBuffer(MemorySegment.ofBuffer(ByteBuffer.allocate(capacity)));
    }

    static CompositeBuffer composite(final Buffer... buffers) {
        return new CompositeBuffer(buffers);
    }

    static FastCompositeBuffer fastComposite(final int exponent, final Buffer... buffers) {
        return new FastCompositeBuffer(buffers, exponent);
    }

    static FastCompositeBuffer fastComposite(final int exponent, final int initialBufferCount) {
        return new FastCompositeBuffer(initialBufferCount, exponent);
    }

    static RingBuffer ring(final int exponent, final int bufferCount) {
        return new RingBuffer(bufferCount, exponent);
    }

    static RingBuffer ring(final int exponent, final Buffer... buffers) {
        return new RingBuffer(buffers, exponent);
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
