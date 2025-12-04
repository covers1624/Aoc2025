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
public class Day3 extends Day {

    public final List<String> testInput = FastStream.of(loadLines("test.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();

    public final List<String> input = FastStream.of(loadLines("input.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();

    @DayEntry
//    @Benchmark
    public void p1Test(Blackhole bh) {
        var result = solve(testInput, 2);
        assertResult(result, 357);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p1(Blackhole bh) {
        var result = solve(input, 2);
        assertResult(result, 17311);
        bh.consume(result);
    }

    @DayEntry
//    @Benchmark
    public void p2Test(Blackhole bh) {
        var result = solve(testInput, 12);
        assertResult(result, 3121910778619L);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p2(Blackhole bh) {
        var result = solve(input, 12);
        assertResult(result, 171419245422055L);
        bh.consume(result);
    }

    private long solve(List<String> input, int digits) {
        long total = 0;
        for (String s : input) {
            total += largestJoltage(s.toCharArray(), digits);
        }
        return total;
    }

    private static long largestJoltage(char[] chars, int digits) {
        // Assume last 2 are the largest.
        int[] positions = new int[digits];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = chars.length - digits + i;
        }

        int digitUseMin = -1;
        // Process each digit position
        for (int pI = 0; pI < positions.length; pI++) {
            // Try and move digit to the left (lower index)
            // if there is a higher (or equal) number.
            for (int i = positions[pI] - 1; i >= 0 && i > digitUseMin; i--) {
                char n = chars[i];
                char c = chars[positions[pI]];
                if (n >= c) {
                    positions[pI] = i;
                }
            }
            // Make sure we don't push any number past this.
            digitUseMin = positions[pI];
        }

        long largest = 0;
        for (int p : positions) {
            largest = largest * 10 + (chars[p] - '0');
        }
        return largest;
    }
}
