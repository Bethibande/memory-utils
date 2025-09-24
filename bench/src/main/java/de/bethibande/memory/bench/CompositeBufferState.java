package de.bethibande.memory.bench;

import de.bethibande.memory.Buffer;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class CompositeBufferState {

    private final Buffer a = Buffer.direct(4);
    private final Buffer b = Buffer.direct(4);

    private final Buffer aLarge = Buffer.direct(64);
    private final Buffer bLarge = Buffer.direct(64);

    public final Buffer composite = Buffer.composite(a, b);
    public final Buffer compositeLarge = Buffer.composite(aLarge, bLarge);

}
