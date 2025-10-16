package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

import java.nio.ByteBuffer;

/**
 * JavaNioBuffer is a concrete implementation of the {@link AbstractBuffer} class,
 * wrapping a Java NIO {@link ByteBuffer} to provide buffer manipulation functions
 * for reading and writing various data types. As such the usual limits of the ByteBuffer API apply.
 * <br>
 * This class typically achieves better performance than the {@link DefaultBuffer} implementation for off-heap memory segments.
 * This is because the MemorySegment API used by the {@code DefaultBuffer} is unfortunately much slower than the NIO DirectByteBuffer implementation.
 */
public class JavaNioBuffer extends AbstractBuffer {

    private final ByteBuffer buffer;

    public JavaNioBuffer(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer unwrap() {
        return buffer.duplicate();
    }

    @Override
    public long capacity() {
        return buffer.capacity();
    }

    @Override
    public void get(final long position, final byte[] bytes) {
        buffer.get((int) position, bytes);
    }

    @Override
    public void get(final long position, final byte[] bytes, final int offset, final int length) {
        buffer.get((int) position, bytes, offset, length);
    }

    @Override
    public byte getByte(final long position) {
        return buffer.get((int) position);
    }

    @Override
    public short getShort(final long position) {
        return buffer.getShort((int) position);
    }

    @Override
    public int getInt(final long position) {
        return buffer.getInt((int) position);
    }

    @Override
    public long getLong(final long position) {
        return buffer.getLong((int) position);
    }

    @Override
    public float getFloat(final long position) {
        return buffer.getFloat((int) position);
    }

    @Override
    public double getDouble(final long position) {
        return buffer.getDouble((int) position);
    }

    @Override
    public boolean getBoolean(final long position) {
        return buffer.get((int) position) != 0;
    }

    @Override
    public char getChar(final long position) {
        return buffer.getChar((int) position);
    }

    protected int readIdx(final int bytes) {
        try {
            return (int) readPosition();
        } finally {
            readPosition(readPosition() + bytes);
        }
    }

    @Override
    public void read(final byte[] bytes) {
        buffer.get(readIdx(bytes.length), bytes);
    }

    @Override
    public void read(final byte[] bytes, final int offset, final int length) {
        buffer.get(readIdx(length), bytes, offset, length);
    }

    @Override
    public byte readByte() {
        return buffer.get(readIdx(1));
    }

    @Override
    public short readShort() {
        return buffer.getShort(readIdx(2));
    }

    @Override
    public int readInt() {
        return buffer.getInt(readIdx(4));
    }

    @Override
    public long readLong() {
        return buffer.getLong(readIdx(8));
    }

    @Override
    public float readFloat() {
        return buffer.getFloat(readIdx(4));
    }

    @Override
    public double readDouble() {
        return buffer.getDouble(readIdx(8));
    }

    @Override
    public boolean readBoolean() {
        return buffer.get(readIdx(1)) != 0;
    }

    @Override
    public char readChar() {
        return buffer.getChar(readIdx(2));
    }

    @Override
    public void set(final long position, final byte[] bytes) {
        buffer.put((int) position, bytes);
    }

    @Override
    public void set(final long position, final byte[] bytes, final int offset, final int length) {
        buffer.put((int) position, bytes, offset, length);
    }

    @Override
    public void set(final long position, final byte b) {
        buffer.put((int) position, b);
    }

    @Override
    public void set(final long position, final short s) {
        buffer.putShort((int) position, s);
    }

    @Override
    public void set(final long position, final int i) {
        buffer.putInt((int) position, i);
    }

    @Override
    public void set(final long position, final long l) {
        buffer.putLong((int) position, l);
    }

    @Override
    public void set(final long position, final float f) {
        buffer.putFloat((int) position, f);
    }

    @Override
    public void set(final long position, final double d) {
        buffer.putDouble((int) position, d);
    }

    @Override
    public void set(final long position, final boolean b) {
        buffer.put((int) position, (byte) (b ? 1 : 0));
    }

    @Override
    public void set(final long position, final char c) {
        buffer.putChar((int) position, c);
    }

    @Override
    public Buffer slice(final long offset, final long length) {
        retain();
        return new SlicedNioBuffer(buffer.slice((int) offset, (int) length), this);
    }

    protected int writeIdx(final int bytes) {
        try {
            return (int) writePosition();
        } finally {
            writePosition(writePosition() + bytes);
        }
    }

    @Override
    public void write(final byte[] bytes) {
        buffer.put(writeIdx(bytes.length), bytes);
    }

    @Override
    public void write(final byte[] bytes, final int offset, final int length) {
        buffer.put(writeIdx(length), bytes, offset, length);
    }

    @Override
    public void write(final byte b) {
        buffer.put(writeIdx(1), b);
    }

    @Override
    public void write(final short s) {
        buffer.putShort(writeIdx(2), s);
    }

    @Override
    public void write(final int i) {
        buffer.putInt(writeIdx(4), i);
    }

    @Override
    public void write(final long l) {
        buffer.putLong(writeIdx(8), l);
    }

    @Override
    public void write(final float f) {
        buffer.putFloat(writeIdx(4), f);
    }

    @Override
    public void write(final double d) {
        buffer.putDouble(writeIdx(8), d);
    }

    @Override
    public void write(final boolean b) {
        buffer.put(writeIdx(1), (byte) (b ? 1 : 0));
    }

    @Override
    public void write(final char c) {
        buffer.putChar(writeIdx(2), c);
    }
}
