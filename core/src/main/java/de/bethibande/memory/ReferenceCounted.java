package de.bethibande.memory;

public interface ReferenceCounted {

    void retain();

    void release();

    int referenceCount();

}
