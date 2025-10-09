# memory-utils
A collection of buffer implementations in Java.

> [!WARNING]
> These buffer implementations prioritize performance over safety. Memory safety is guaranteed by the JDK through the MemorySegment class.
> Read & Write offsets and many other things are never validated, incorrect usage can result in a lot of issues that are difficult to identify & debug.
