package de.bethibande.memory;

import java.nio.ByteBuffer;

public interface Settable {

    void set(final long position, final byte[] bytes);

    void set(final long position, final byte[] bytes, final int offset, final int length);

    void set(final long position, final ByteBuffer buffer, final int offset, final int length);

    void set(final long position, final byte b);

    void set(final long position, final short s);

    void set(final long position, final int i);

    void set(final long position, final long l);

    void set(final long position, final float f);

    void set(final long position, final double d);

    void set(final long position, final boolean b);

    void set(final long position, final char c);

}
