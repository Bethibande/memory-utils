package de.bethibande.memory;

import java.nio.ByteBuffer;

public interface Writable {

    void write(final byte[] bytes);

    void write(final byte[] bytes, final int offset, final int length);

    void write(final ByteBuffer readable);

    void write(final byte b);

    void write(final short s);

    void write(final int i);

    void write(final long l);

    void write(final float f);

    void write(final double d);

    void write(final boolean b);

    void write(final char c);

}
