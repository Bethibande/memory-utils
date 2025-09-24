package de.bethibande.memory;

public interface Buffer extends Readable, Writable, Gettable, Settable {

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
