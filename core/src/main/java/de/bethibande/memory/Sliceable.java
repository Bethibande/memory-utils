package de.bethibande.memory;

public interface Sliceable {

    Buffer slice(final long offset, final long length);

}
