package net.covers1624.aoc2025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.function.LongPredicate;
import java.util.stream.IntStream;

//@Fork (0) // For debugger attachment.
@Fork (3)
@Threads (4)
@State (Scope.Benchmark)
@BenchmarkMode (Mode.AverageTime)
@Warmup (iterations = 3, time = 5)
@Measurement (iterations = 3, time = 5)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
public class Day2 extends Day {

    private static final long[] POW_10 = IntStream.range(0, 16)
            .mapToLong(e -> Math.unsignedPowExact(10L, e))
            .toArray();

    public final String testInput = load("test.txt").trim();
    public final String input = load("input.txt").trim();

    @DayEntry
    //    @Benchmark
    public void p1Test(Blackhole bh) {
        var result = solve(testInput, this::checkP1);
        assertResult(result, 1227775554L);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p1(Blackhole bh) {
        var result = solve(input, this::checkP1);
        assertResult(result, 22062284697L);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p1Math(Blackhole bh) {
        var result = solve(input, this::checkP1Math);
        assertResult(result, 22062284697L);
        bh.consume(result);
    }

    @DayEntry
    //    @Benchmark
    public void p2Test(Blackhole bh) {
        var result = solve(testInput, this::checkP2);
        assertResult(result, 4174379265L);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p2(Blackhole bh) {
        var result = solve(input, this::checkP2);
        assertResult(result, 46666175279L);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p2Math(Blackhole bh) {
        var result = solve(input, this::checkP2Math);
        assertResult(result, 46666175279L);
        bh.consume(result);
    }

    public long solve(String input, LongPredicate pred) {
        long sum = 0;
        for (var range : input.split(",")) {
            int sep = range.indexOf("-");
            long start = Long.parseLong(range, 0, sep, 10);
            long end = Long.parseLong(range, sep + 1, range.length(), 10);
            for (long i = start; i <= end; i++) {
                if (!pred.test(i)) {
                    sum += i;
                }
            }
        }
        return sum;
    }

    private boolean checkP1(long i) {
        String num = Long.toString(i);
        float halfLenF = num.length() / 2F;
        int halfLen = (int) halfLenF;
        if (halfLenF != (float) halfLen) return true; // Must be even split.

        return !num.regionMatches(0, num, halfLen, halfLen);
    }

    private boolean checkP1Math(long i) {
        int d = countDigits(i);
        // Odd values can never have invalid dupes here.
        if ((d & 1) == 1) return true;

        long p = pow10(d / 2);
        long high = i / p;
        long low = i % p;
        return high != low;
    }

    private boolean checkP2(long i) {
        if (!checkP1(i)) return false;

        String num = Long.toString(i);
        int len = num.length();
        int half = (int) Math.floor(len / 2F);

        for (int j = 1; j <= half; j++) {
            if ((len % j) != 0) continue;

            if (isPattern(num, num, 0, j)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPattern(String str, String pattern, int pStart, int pLen) {
        int len = str.length();
        for (int i = pLen; i < len; i += pLen) {
            if (!str.regionMatches(i, pattern, pStart, pLen)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkP2Math(long i) {
        int d = countDigits(i);

        for (int k = 1; k <= d / 2; k++) {
            // total len must be multiple of block len
            if (d % k != 0) continue;

            // pull the block we want to test
            long block = i / pow10(d - k);

            int r = d / k; // num repeats
            long pow10d = pow10(k);

            long rep = 0; // append r repeats of the pattern.
            for (int j = 0; j < r; j++) {
                rep = rep * pow10d + block;
            }
            // are same? we found repeat
            if (rep == i) {
                return false;
            }
        }
        return true;
    }

    private static int countDigits(long l) {
        int n = 0;
        while (l > 0) {
            l /= 10;
            n++;
        }
        return n;
    }

    private static long pow10(int n) {
        return POW_10[n];
    }
}
