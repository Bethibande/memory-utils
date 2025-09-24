package de.bethibande.memory;

public class Test {

    static void main() {
        final Buffer a = Buffer.direct(4);
        final Buffer b = Buffer.direct(4);

        final Buffer composite = Buffer.composite(a, b);

        composite.write(1L);
        System.out.println(composite.readLong());
    }

}
