package de.bethibande.memory.bench;

import de.bethibande.memory.Buffer;
import de.bethibande.memory.impl.FastCompositeBuffer;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class FastCompositeBufferState {

    public final long capacity = (long) Math.pow(2, 16);

    private final Buffer a = Buffer.direct(capacity);
    private final Buffer b = Buffer.direct(capacity);

    public final Buffer composite = Buffer.fastComposite(16, a, b);

}
