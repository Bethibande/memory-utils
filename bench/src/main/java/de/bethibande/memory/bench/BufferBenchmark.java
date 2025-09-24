package de.bethibande.memory.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@Fork(value = 1, warmups = 0)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class BufferBenchmark {

    @Benchmark
    public void defaultSetLong(final DefaultBufferState state) {
        state.buffer.set(0, 1234567890123456789L);
    }

    @Benchmark
    public void defaultGetLong(final DefaultBufferState state, final Blackhole blackhole) {
        blackhole.consume(state.buffer.getLong(0));
    }

    @Benchmark
    public void compositeSetLong(final CompositeBufferState state) {
        state.composite.set(0, 1234567890123456789L);
    }

    @Benchmark
    public void compositeGetLong(final CompositeBufferState state, final Blackhole blackhole) {
        blackhole.consume(state.composite.getLong(0));
    }

    @Benchmark
    public void compositeSetLongNoBoundary(final CompositeBufferState state) {
        state.compositeLarge.set(0, 1234567890123456789L);
    }

    @Benchmark
    public void compositeGetLongNoBoundary(final CompositeBufferState state, final Blackhole blackhole) {
        blackhole.consume(state.compositeLarge.getLong(0));
    }

}
