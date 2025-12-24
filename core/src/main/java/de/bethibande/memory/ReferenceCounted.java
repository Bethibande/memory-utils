package de.bethibande.memory;

public interface ReferenceCounted {

    void retain(final int count);

    void retain();

    void release();

    int referenceCount();

}
