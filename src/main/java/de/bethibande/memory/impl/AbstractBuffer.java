package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

public abstract class AbstractBuffer implements Buffer {

    private volatile long writePosition;
    private volatile long readPosition;

    @Override
    public long writePosition() {
        return this.writePosition;
    }

    @Override
    public void writePosition(final long position) {
        this.writePosition = position;
    }

    @Override
    public long readPosition() {
        return this.readPosition;
    }

    @Override
    public void readPosition(final long position) {
        this.readPosition = position;
    }

}
