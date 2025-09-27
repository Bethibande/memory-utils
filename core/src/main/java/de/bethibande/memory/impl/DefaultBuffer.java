package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;

public class DefaultBuffer extends AbstractBuffer {

    private final MemorySegment segment;
    private final long capacity;

    public DefaultBuffer(final MemorySegment segment) {
        this.segment = segment;
        this.capacity = segment.byteSize();
    }

    @Override
    protected void free() {
        reset();
    }

    @Override
    public Buffer slice(final long offset, final long length) {
        return new SlicedBuffer(this.segment.asSlice(offset, length), this);
    }

    public ByteBuffer asNioBuffer() {
        return this.segment.asByteBuffer();
    }

    public Buffer asReadOnly() {
        return new DefaultBuffer(this.segment.asReadOnly());
    }

    public boolean isMapped() {
        return this.segment.isMapped();
    }

    public boolean isReadOnly() {
        return this.segment.isReadOnly();
    }

    public boolean isNative() {
        return this.segment.isNative();
    }

    public long address() {
        return this.segment.address();
    }

    @Override
    public long capacity() {
        return this.capacity;
    }

    @Override
    public void get(final long position, final byte[] bytes) {
        MemorySegment.ofArray(bytes).copyFrom(this.segment.asSlice(position, bytes.length));
    }

    @Override
    public void get(final long position, final byte[] bytes, final int offset, final int length) {
        MemorySegment.ofArray(bytes).asSlice(offset, length).copyFrom(this.segment.asSlice(position, length));
    }

    @Override
    public byte getByte(final long position) {
        return this.segment.get(ValueLayout.JAVA_BYTE, position);
    }

    @Override
    public short getShort(final long position) {
        return this.segment.get(ValueLayout.JAVA_SHORT_UNALIGNED, position);
    }

    @Override
    public int getInt(final long position) {
        return this.segment.get(ValueLayout.JAVA_INT_UNALIGNED, position);
    }

    @Override
    public long getLong(final long position) {
        return this.segment.get(ValueLayout.JAVA_LONG_UNALIGNED, position);
    }

    @Override
    public float getFloat(final long position) {
        return this.segment.get(ValueLayout.JAVA_FLOAT_UNALIGNED, position);
    }

    @Override
    public double getDouble(final long position) {
        return this.segment.get(ValueLayout.JAVA_DOUBLE_UNALIGNED, position);
    }

    @Override
    public boolean getBoolean(final long position) {
        return this.segment.get(ValueLayout.JAVA_BOOLEAN, position);
    }

    @Override
    public char getChar(final long position) {
        return this.segment.get(ValueLayout.JAVA_CHAR_UNALIGNED, position);
    }

    @Override
    public void read(final byte[] bytes) {
        MemorySegment.ofArray(bytes).copyFrom(this.segment);
        this.readPosition(this.readPosition() + bytes.length);
    }

    @Override
    public void read(final byte[] bytes, final int offset, final int length) {
        MemorySegment.ofArray(bytes).asSlice(offset, length).copyFrom(this.segment.asSlice(this.readPosition(), length));
        this.readPosition(this.readPosition() + length);
    }

    protected <T> T readAndIncrement(final T value, final int increment) {
        this.readPosition(this.readPosition() + increment);
        return value;
    }

    @Override
    public byte readByte() {
        return readAndIncrement(this.segment.get(ValueLayout.JAVA_BYTE, this.readPosition()), 1);
    }

    @Override
    public short readShort() {
        return readAndIncrement(this.segment.get(ValueLayout.JAVA_SHORT_UNALIGNED, this.readPosition()), 2);
    }

    @Override
    public int readInt() {
        return readAndIncrement(this.segment.get(ValueLayout.JAVA_INT_UNALIGNED, this.readPosition()), 4);
    }

    @Override
    public long readLong() {
        return readAndIncrement(this.segment.get(ValueLayout.JAVA_LONG_UNALIGNED, this.readPosition()), 8);
    }

    @Override
    public float readFloat() {
        return readAndIncrement(this.segment.get(ValueLayout.JAVA_FLOAT_UNALIGNED, this.readPosition()), 4);
    }

    @Override
    public double readDouble() {
        return readAndIncrement(this.segment.get(ValueLayout.JAVA_DOUBLE_UNALIGNED, this.readPosition()), 8);
    }

    @Override
    public boolean readBoolean() {
        return readAndIncrement(this.segment.get(ValueLayout.JAVA_BOOLEAN, this.readPosition()), 1);
    }

    @Override
    public char readChar() {
        return readAndIncrement(this.segment.get(ValueLayout.JAVA_CHAR_UNALIGNED, this.readPosition()), 2);
    }

    @Override
    public void set(final long position, final byte[] bytes) {
        this.segment.asSlice(position).copyFrom(MemorySegment.ofArray(bytes));
    }

    @Override
    public void set(final long position, final byte[] bytes, final int offset, final int length) {
        this.segment.asSlice(position).copyFrom(MemorySegment.ofArray(bytes).asSlice(offset, length));
    }

    @Override
    public void set(final long position, final byte b) {
        this.segment.set(ValueLayout.JAVA_BYTE, position, b);
    }

    @Override
    public void set(final long position, final short s) {
        this.segment.set(ValueLayout.JAVA_SHORT_UNALIGNED, position, s);
    }

    @Override
    public void set(final long position, final int i) {
        this.segment.set(ValueLayout.JAVA_INT_UNALIGNED, position, i);
    }

    @Override
    public void set(final long position, final long l) {
        this.segment.set(ValueLayout.JAVA_LONG_UNALIGNED, position, l);
    }

    @Override
    public void set(final long position, final float f) {
        this.segment.set(ValueLayout.JAVA_FLOAT_UNALIGNED, position, f);
    }

    @Override
    public void set(final long position, final double d) {
        this.segment.set(ValueLayout.JAVA_DOUBLE_UNALIGNED, position, d);
    }

    @Override
    public void set(final long position, final boolean b) {
        this.segment.set(ValueLayout.JAVA_BOOLEAN, position, b);
    }

    @Override
    public void set(final long position, final char c) {
        this.segment.set(ValueLayout.JAVA_CHAR_UNALIGNED, position, c);
    }

    @Override
    public void write(final byte[] bytes) {
        this.segment.asSlice(this.writePosition()).copyFrom(MemorySegment.ofArray(bytes));
        this.writePosition(this.writePosition() + bytes.length);
    }

    @Override
    public void write(final byte[] bytes, final int offset, final int length) {
        this.segment.asSlice(this.writePosition()).copyFrom(MemorySegment.ofArray(bytes).asSlice(offset, length));
        this.writePosition(this.writePosition() + length);
    }

    @Override
    public void write(final byte b) {
        this.segment.set(ValueLayout.JAVA_BYTE, this.writePosition(), b);
        this.writePosition(this.writePosition() + 1);
    }

    @Override
    public void write(final short s) {
        this.segment.set(ValueLayout.JAVA_SHORT_UNALIGNED, this.writePosition(), s);
        this.writePosition(this.writePosition() + 2);
    }

    @Override
    public void write(final int i) {
        this.segment.set(ValueLayout.JAVA_INT_UNALIGNED, this.writePosition(), i);
        this.writePosition(this.writePosition() + 4);
    }

    @Override
    public void write(final long l) {
        this.segment.set(ValueLayout.JAVA_LONG_UNALIGNED, this.writePosition(), l);
        this.writePosition(this.writePosition() + 8);
    }

    @Override
    public void write(final float f) {
        this.segment.set(ValueLayout.JAVA_FLOAT_UNALIGNED, this.writePosition(), f);
        this.writePosition(this.writePosition() + 4);
    }

    @Override
    public void write(final double d) {
        this.segment.set(ValueLayout.JAVA_DOUBLE_UNALIGNED, this.writePosition(), d);
        this.writePosition(this.writePosition() + 8);
    }

    @Override
    public void write(final boolean b) {
        this.segment.set(ValueLayout.JAVA_BOOLEAN, this.writePosition(), b);
        this.writePosition(this.writePosition() + 1);
    }

    @Override
    public void write(final char c) {
        this.segment.set(ValueLayout.JAVA_CHAR_UNALIGNED, this.writePosition(), c);
        this.writePosition(this.writePosition() + 2);
    }

    @Override
    public String toString() {
        return "DefaultBuffer{ " +
               "segment: " + segment + ", " +
               "writePosition: " + writePosition() + ", " +
               "readPosition: " + readPosition() +
               " }";
    }
}
