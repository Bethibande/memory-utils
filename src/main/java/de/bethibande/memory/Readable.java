package de.bethibande.memory;

public interface Readable {

    void read(final byte[] bytes);

    void read(final byte[] bytes, final int offset, final int length);

    byte readByte();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    boolean readBoolean();

    char readChar();

}
