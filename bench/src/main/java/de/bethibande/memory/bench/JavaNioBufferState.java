package de.bethibande.memory.bench;

import de.bethibande.memory.Buffer;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class JavaNioBufferState {

    public final Buffer buffer = Buffer.directNio(1024);

}
