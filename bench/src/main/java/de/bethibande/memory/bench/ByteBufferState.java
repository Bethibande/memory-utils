package de.bethibande.memory.bench;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.nio.ByteBuffer;

@State(Scope.Benchmark)
public class ByteBufferState {

    public final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

}
