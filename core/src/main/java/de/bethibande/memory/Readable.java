package de.bethibande.memory;

import java.nio.ByteBuffer;

public interface Readable {

    void read(final byte[] bytes);

    void read(final byte[] bytes, final int offset, final int length);

    void read(final ByteBuffer buffer);

    byte readByte();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    boolean readBoolean();

    char readChar();

}
