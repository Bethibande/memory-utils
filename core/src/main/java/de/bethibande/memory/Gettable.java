package de.bethibande.memory;

import java.nio.ByteBuffer;

public interface Gettable {

    void get(final long position, final byte[] bytes);

    void get(final long position, final byte[] bytes, final int offset, final int length);

    void get(final long position, final ByteBuffer buffer, final int offset, final int length);

    byte getByte(final long position);

    short getShort(final long position);

    int getInt(final long position);

    long getLong(final long position);

    float getFloat(final long position);

    double getDouble(final long position);

    boolean getBoolean(final long position);

    char getChar(final long position);

}
