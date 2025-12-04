package net.covers1624.aoc2025;

import net.covers1624.quack.collection.FastStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.TimeUnit;

//@Fork (0) // For debugger attachment.
@Fork (3)
@Threads (4)
@State (Scope.Benchmark)
@BenchmarkMode (Mode.AverageTime)
@Warmup (iterations = 3, time = 5)
@Measurement (iterations = 3, time = 5)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
public class Day1 extends Day {

    private final List<Rotation> testInput = FastStream.of(loadLines("test.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .map(e -> new Rotation(e.startsWith("R"), Integer.parseInt(e.substring(1))))
            .toList();

    private final List<Rotation> input = FastStream.of(loadLines("input.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .map(e -> new Rotation(e.startsWith("R"), Integer.parseInt(e.substring(1))))
            .toList();

    @DayEntry
//    @Benchmark
    public void p1Test(Blackhole bh) {
        int result = solveP1(testInput, 50);
        assertResult(result, 3);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p1(Blackhole bh) {
        int result = solveP1(input, 50);
        assertResult(result, 1105);
        bh.consume(result);
    }

    @DayEntry
//    @Benchmark
    public void p2Test(Blackhole bh) {
        int result = solveP2(testInput, 50);
        assertResult(result, 6);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p2(Blackhole bh) {
        int result = solveP2(input, 50);
        assertResult(result, 6599);
        bh.consume(result);
    }

    private int solveP1(List<Rotation> rotations, int start) {
        int atZero = 0;
        int ptr = start;
        for (Rotation rotation : rotations) {
            ptr = wrap99P1(ptr + (rotation.right ? rotation.clicks : -rotation.clicks));
            if (ptr == 0) {
                atZero++;
            }
        }
        return atZero;
    }

    private int wrap99P1(int value) {
        while (true) {
            if (value < 0) {
                value = 100 + value;
                continue;
            }
            if (value > 99) {
                value = value - 100;
                continue;
            }
            break;
        }
        return value;
    }

    private int solveP2(List<Rotation> rotations, int start) {
        int ptr = start;
        int pointAtZero = 0;
        for (Rotation rotation : rotations) {
            int value = ptr;
            int move = rotation.clicks;
            while (move != 0) {
                value += (rotation.right ? 1 : -1);
                if (value == 100) value = 0;
                if (value == -1) value = 99;

                if (value == 0) pointAtZero++;

                move--;
            }

            ptr = value;
        }
        return pointAtZero;
    }

    private record Rotation(boolean right, int clicks) { }
}
