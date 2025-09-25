package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

import java.util.ArrayList;
import java.util.List;

public class CompositeBuffer extends AbstractBuffer {

    private CompositeRegion[] regions;
    private long[] index;

    private long size;

    public CompositeBuffer(final Buffer[] buffers) {
        this.regions = new CompositeRegion[buffers.length];
        this.index = new long[buffers.length];
        this.size = calculateSize(buffers);
        buildIndex(buffers);
    }

    private void buildIndex(final Buffer[] buffers) {
        long offset = 0;
        for (int i = 0; i < regions.length; i++) {
            final Buffer buffer = buffers[i];
            final CompositeRegion region = new CompositeRegion(buffer, offset);
            this.regions[i] = region;
            this.index[i] = offset;
            offset += buffer.capacity();
        }
    }

    private long calculateSize(final Buffer[] buffers) {
        long size = 0;
        for (Buffer buffer : buffers) {
            size += buffer.capacity();
        }
        return size;
    }

    @Override
    public Buffer slice(final long offset, final long length) {
        final List<Buffer> buffers = new ArrayList<>();

        long currentOffset = offset;
        long remainingBytes = length;
        while (remainingBytes > 0) {
            final CompositeRegion region = regionAt(currentOffset);
            final Buffer buffer = region.buffer();

            // Always slicing the buffer will ensure we always get a buffer with the correct capacity.
            // It will also increase the reference count of the buffer and decrease it when the slice is released.
            buffers.add(buffer.slice(region.pos(currentOffset), Math.min(remainingBytes, buffer.capacity())));

            currentOffset += buffer.capacity();
            remainingBytes -= buffer.capacity();
        }

        return new CompositeBuffer(buffers.toArray(Buffer[]::new));
    }

    @Override
    public long capacity() {
        return size;
    }

    @Override
    protected void free() {
        for (int i = 0; i < regions.length; i++) { // No enhanced for loop; iterators are slower and litter the heap
            regions[i].buffer().release();
        }
    }

    protected CompositeRegion regionAt(final long offset) {
        int low = 0;
        int high = index.length - 1;
        while (low <= high) {
            final int mid = (low + high) >>> 1;
            final long midVal = index[mid];
            if (midVal < offset) {
                low = mid + 1;
            } else if (midVal > offset) {
                high = mid - 1;
            } else {
                return regions[mid];
            }
        }
        return regions[low];
    }

    @Override
    public byte getByte(final long position) {
        final CompositeRegion region = regionAt(position);
        return region.buffer().getByte(position - region.offset());
    }

    @Override
    public void get(final long position, final byte[] bytes) {
        get(position, bytes, 0, bytes.length);
    }

    @Override
    public void get(final long position, final byte[] bytes, final int offset, final int length) {
        final CompositeRegion region = regionAt(position);
        if (region.canFit(position, length)) {
            region.buffer().get(position - region.offset(), bytes, offset, length);
        } else {
            final Buffer buffer = region.buffer();
            final long bytesToRead = buffer.capacity() - (position - region.offset());
            buffer.get(position - region.offset(), bytes, offset, (int) bytesToRead);

            // TODO: Remove recursion?
            get(position + bytesToRead, bytes, offset + (int) bytesToRead, length - (int) bytesToRead);
        }
    }

    @Override
    public short getShort(final long position) {
        final CompositeRegion region = regionAt(position);
        if (region.canFit(position, 2)) {
            return region.buffer().getShort(position - region.offset());
        } else {
            final byte a = getByte(position);
            final byte b = getByte(position + 1);
            return (short) (((a & 0xFF) << 8) | (b & 0xFF));
        }
    }

    @Override
    public int getInt(final long position) {
        final CompositeRegion region = regionAt(position);
        if (region.canFit(position, 4)) {
            return region.buffer().getInt(position - region.offset());
        } else {
            final short a = getShort(position);
            final short b = getShort(position + 2);
            return ((a & 0xFFFF) << 16) | (b & 0xFFFF);
        }
    }

    @Override
    public long getLong(final long position) {
        final CompositeRegion region = regionAt(position);
        if (region.canFit(position, 8)) {
            return region.buffer().getLong(position - region.offset());
        } else {
            final int a = getInt(position);
            final int b = getInt(position + 4);
            return ((long) a << 32) | (b & 0xFFFFFFFFL);
        }
    }

    @Override
    public float getFloat(final long position) {
        return Float.intBitsToFloat(getInt(position));
    }

    @Override
    public double getDouble(final long position) {
        return Double.longBitsToDouble(getLong(position));
    }

    @Override
    public boolean getBoolean(final long position) {
        final CompositeRegion region = regionAt(position);
        return region.buffer().getBoolean(position - region.offset());
    }

    @Override
    public char getChar(final long position) {
        return (char) getShort(position);
    }

    protected long read(final int bytes) {
        final long readPosition = readPosition();
        try {
            return readPosition;
        } finally {
            readPosition(readPosition + bytes);
        }
    }

    @Override
    public void read(final byte[] bytes) {
        get(read(bytes.length), bytes);
    }

    @Override
    public void read(final byte[] bytes, final int offset, final int length) {
        get(read(length), bytes, offset, length);
    }

    @Override
    public byte readByte() {
        return getByte(read(1));
    }

    @Override
    public short readShort() {
        return getShort(read(2));
    }

    @Override
    public int readInt() {
        return getInt(read(4));
    }

    @Override
    public long readLong() {
        return getLong(read(8));
    }

    @Override
    public float readFloat() {
        return getFloat(read(4));
    }

    @Override
    public double readDouble() {
        return getDouble(read(8));
    }

    @Override
    public boolean readBoolean() {
        return getBoolean(read(1));
    }

    @Override
    public char readChar() {
        return getChar(read(2));
    }

    @Override
    public void set(final long position, final byte[] bytes) {
        set(position, bytes, 0, bytes.length);
    }

    @Override
    public void set(final long position, final byte[] bytes, final int offset, final int length) {
        final CompositeRegion region = regionAt(position);
        if (region.canFit(position, length)) {
            region.buffer().set(region.pos(position), bytes, offset, length);
        } else {
            final Buffer buffer = region.buffer();
            final long bytesToWrite = buffer.capacity() - region.pos(position);
            buffer.set(region.pos(position), bytes, offset, (int) bytesToWrite);
            set(position + bytesToWrite, bytes, offset + (int) bytesToWrite, length - (int) bytesToWrite);
        }
    }

    @Override
    public void set(final long position, final byte b) {
        final CompositeRegion region = regionAt(position);
        region.buffer().set(region.pos(position), b);
    }

    @Override
    public void set(final long position, final short s) {
        final CompositeRegion region = regionAt(position);
        if (region.canFit(position, 2)) {
            region.buffer().set(region.pos(position), s);
        } else {
            set(position, (byte) (s >> 8));
            set(position + 1, (byte) (s & 0xFF));
        }
    }

    @Override
    public void set(final long position, final int i) {
        final CompositeRegion region = regionAt(position);
        if (region.canFit(position, 4)) {
            region.buffer().set(region.pos(position), i);
        } else {
            set(position, (short) (i >> 16));
            set(position + 2, (short) (i & 0xFFFF));
        }
    }

    @Override
    public void set(final long position, final long l) {
        final CompositeRegion region = regionAt(position);
        if (region.canFit(position, 8)) {
            region.buffer().set(region.pos(position), l);
        } else {
            set(position, (int) (l >> 32));
            set(position + 4, (int) (l & 0xFFFFFFFFL));
        }
    }

    @Override
    public void set(final long position, final float f) {
        set(position, Float.floatToIntBits(f));
    }

    @Override
    public void set(final long position, final double d) {
        set(position, Double.doubleToLongBits(d));
    }

    @Override
    public void set(final long position, final boolean b) {
        set(position, b ? (byte) 1 : (byte) 0);
    }

    @Override
    public void set(final long position, final char c) {
        set(position, (short) c);
    }

    protected long writeIdx(final long bytes) {
        final long writePosition = writePosition();
        try {
            return writePosition;
        } finally {
            writePosition(writePosition + bytes);
        }
    }

    @Override
    public void write(final byte[] bytes) {
        set(writeIdx(bytes.length), bytes);
    }

    @Override
    public void write(final byte[] bytes, final int offset, final int length) {
        set(writeIdx(length), bytes, offset, length);
    }

    @Override
    public void write(final byte b) {
        set(writeIdx(1), b);
    }

    @Override
    public void write(final short s) {
        set(writeIdx(2), s);
    }

    @Override
    public void write(final int i) {
        set(writeIdx(4), i);
    }

    @Override
    public void write(final long l) {
        set(writeIdx(8), l);
    }

    @Override
    public void write(final float f) {
        set(writeIdx(4), Float.floatToIntBits(f));
    }

    @Override
    public void write(final double d) {
        set(writeIdx(8), Double.doubleToLongBits(d));
    }

    @Override
    public void write(final boolean b) {
        set(writeIdx(1), b ? (byte) 1 : (byte) 0);
    }

    @Override
    public void write(final char c) {
        set(writeIdx(2), (short) c);
    }
}
