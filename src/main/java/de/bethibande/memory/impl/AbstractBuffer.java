package de.bethibande.memory.impl;

import de.bethibande.memory.Buffer;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractBuffer implements Buffer {

    private volatile long writePosition;
    private volatile long readPosition;

    protected final AtomicInteger refCount = new AtomicInteger(1);

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

    @Override
    public void retain() {
        refCount.incrementAndGet();
    }

    @Override
    public void release() {
        final int count = refCount.decrementAndGet();
        if (count == 0) free();
    }

    protected void free() {
        // Noop. Override if needed.
    }

    @Override
    public int referenceCount() {
        return refCount.get();
    }
}
