package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

import java.util.ArrayList;
import java.util.List;

public class CompositeBuffer extends AbstractBuffer {

    protected CompositeRegion[] regions;
    protected long[] index;

    protected long size;

    public CompositeBuffer(final Buffer[] buffers) {
        this.regions = new CompositeRegion[buffers.length];
        this.index = new long[buffers.length];
        init(buffers);
        this.size = calculateSize(this.regions);
    }

    protected CompositeRegion region(final Buffer buffer, final long offset) {
        return new CompositeRegion(buffer, offset);
    }

    private void init(final Buffer[] buffers) {
        long offset = 0;
        for (int i = 0; i < regions.length; i++) {
            final Buffer buffer = buffers[i];
            final CompositeRegion region = region(buffer, offset);
            this.regions[i] = region;
            this.index[i] = offset;
            offset += buffer.capacity();
        }
    }

    private long calculateSize(final CompositeRegion[] regions) {
        long size = 0;
        for (int i = 0; i < regions.length; i++) {
            size += regions[i].buffer().capacity();
        }
        return size;
    }

    private long[] updateIndex(final CompositeRegion[] regions) {
        final long[] output = new long[regions.length];

        long offset = 0;
        for (int i = 0; i < regions.length; i++) {
            final CompositeRegion region = regions[i];

            output[i] = offset;
            region.offset(offset);
            offset += region.buffer().capacity();
        }

        return output;
    }

    public void expand(final Buffer buffer, final int index) {
        final CompositeRegion[] regions = new CompositeRegion[this.regions.length + 1];
        if (index > 0) {
            System.arraycopy(this.regions, 0, regions, 0, index);
        }

        final CompositeRegion newRegion = region(buffer, 0);
        regions[index] = newRegion;

        if (index < this.regions.length) {
            System.arraycopy(this.regions, index, regions, index + 1, this.regions.length - index);
        }

        this.regions = regions;
        this.index = updateIndex(regions);
        this.size = calculateSize(regions);
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

        retain();
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

    public Buffer bufferAt(final long offset) {
        return regionAt(offset).buffer();
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

    protected byte getByte(final long position, final CompositeRegion region) {
        return region.buffer().getByte(region.pos(position));
    }

    @Override
    public byte getByte(final long position) {
        final CompositeRegion region = regionAt(position);
        return getByte(position, region);
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

    protected short getShort(final long position, final CompositeRegion region) {
        if (region.canFit(position, 2)) {
            return region.buffer().getShort(region.pos(position));
        } else {
            final byte a = getByte(position, region);
            final byte b = getByte(position + 1);
            return (short) (((a & 0xFF) << 8) | (b & 0xFF));
        }
    }

    @Override
    public short getShort(final long position) {
        final CompositeRegion region = regionAt(position);
        return getShort(position, region);
    }

    protected int getInt(final long position, final CompositeRegion region) {
        if (region.canFit(position, 4)) {
            return region.buffer().getInt(region.pos(position));
        } else {
            final short a = getShort(position, region);
            final short b = getShort(position + 2);
            return ((a & 0xFFFF) << 16) | (b & 0xFFFF);
        }
    }

    @Override
    public int getInt(final long position) {
        final CompositeRegion region = regionAt(position);
        return getInt(position, region);
    }

    protected long getLong(final long position, final CompositeRegion region) {
        if (region.canFit(position, 8)) {
            return region.buffer().getLong(region.pos(position));
        } else {
            final int a = getInt(position, region);
            final int b = getInt(position + 4);
            return ((long) a << 32) | (b & 0xFFFFFFFFL);
        }
    }

    @Override
    public long getLong(final long position) {
        final CompositeRegion region = regionAt(position);
        return getLong(position, region);
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

    protected void set(final long position, final byte b, final CompositeRegion region) {
        region.buffer().set(region.pos(position), b);
    }

    @Override
    public void set(final long position, final byte b) {
        final CompositeRegion region = regionAt(position);
        set(position, b, region);
    }

    protected void set(final long position, final short s, final CompositeRegion region) {
        if (region.canFit(position, 2)) {
            region.buffer().set(region.pos(position), s);
        } else {
            set(position, (byte) (s >> 8), region);
            set(position + 1, (byte) (s & 0xFF));
        }
    }

    @Override
    public void set(final long position, final short s) {
        final CompositeRegion region = regionAt(position);
        set(position, s, region);
    }

    protected void set(final long position, final int i, final CompositeRegion region) {
        if (region.canFit(position, 4)) {
            region.buffer().set(region.pos(position), i);
        } else {
            set(position, (short) (i >> 16), region);
            set(position + 2, (short) (i & 0xFFFF));
        }
    }

    @Override
    public void set(final long position, final int i) {
        final CompositeRegion region = regionAt(position);
        set(position, i, region);
    }

    protected void set(final long position, final long l, final CompositeRegion region) {
        if (region.canFit(position, 8)) {
            region.buffer().set(region.pos(position), l);
        } else {
            set(position, (int) (l >> 32), region);
            set(position + 4, (int) (l & 0xFFFFFFFFL));
        }
    }

    @Override
    public void set(final long position, final long l) {
        final CompositeRegion region = regionAt(position);
        set(position, l, region);
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

    @Override
    public String toString() {
        return "CompositeBuffer{ " +
               "buffers: " + regions.length + ", " +
               "writePosition: " + writePosition() + ", " +
               "readPosition: " + readPosition() +
               " }";
    }
}
