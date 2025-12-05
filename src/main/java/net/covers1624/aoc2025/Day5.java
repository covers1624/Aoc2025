package net.covers1624.aoc2025;

import net.covers1624.quack.collection.FastStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Comparator;
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
public class Day5 extends Day {

    public final List<String> testInput = FastStream.of(loadLines("test.txt"))
            .map(String::trim)
//            .filterNot(String::isEmpty)
            .toList();

    public final List<String> input = FastStream.of(loadLines("input.txt"))
            .map(String::trim)
//            .filterNot(String::isEmpty)
            .toList();

    @DayEntry
//    @Benchmark
    public void p1Test(Blackhole bh) {
        var result = solveP1(testInput);
        assertResult(result, 3);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p1(Blackhole bh) {
        var result = solveP1(input);
        assertResult(result, 758);
        bh.consume(result);
    }

    @DayEntry
//    @Benchmark
    public void p2Test(Blackhole bh) {
        var result = solveP2(testInput);
        assertResult(result, 14);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p2(Blackhole bh) {
        var result = solveP2(input);
        assertResult(result, 343143696885053L);
        bh.consume(result);
    }

    private int solveP1(List<String> lines) {
        int fresh = 0;
        boolean parsedRanges = false;
        var rSet = new RangeSet();
        for (String line : lines) {
            if (!parsedRanges) {
                int dash = line.indexOf('-');
                if (dash != -1) {
                    rSet.addRange(Long.parseLong(line, 0, dash, 10), Long.parseLong(line, dash + 1, line.length(), 10));
                } else {
                    rSet.compact();
                    parsedRanges = true;
                }
                continue;
            }

            for (Range range : rSet.ranges) {
                if (range.isInside(Long.parseLong(line))) {
                    fresh++;
                    break;
                }
            }
        }
        return fresh;
    }

    private long solveP2(List<String> lines) {
        var rSet = new RangeSet();
        for (String line : lines) {
            int dash = line.indexOf('-');
            if (dash != -1) {
                rSet.addRange(Long.parseLong(line, 0, dash, 10), Long.parseLong(line, dash + 1, line.length(), 10));
                continue;
            }
            break;
        }

        rSet.compact();

        long total = 0;
        for (Range range : rSet.ranges) {
            total += (range.end - range.start) + 1;
        }

        return total;
    }

    private record RangeSet(List<Range> ranges) {

        public RangeSet() {
            this(new ArrayList<>());
        }

        public void addRange(long start, long end) {
            var range = new Range(start, end);

            int i = 0;
//            while (i < ranges.size() && range.compareTo(ranges.get(i)) > 0) {
//                i++;
//            }
            ranges.add(i, range);
        }

        public void compact() {
            ranges.sort(Comparator.naturalOrder());
            for (int i = 0; i < ranges.size() - 1; i++) {
                Range curr = ranges.get(i);
                Range next = ranges.get(i + 1);

                if (curr.isInside(next.start)) {
                    ranges.set(i, new Range(curr.start, Math.max(curr.end, next.end)));
                    ranges.remove(i + 1);
                    i--;
                }
            }
        }
    }

    private record Range(long start, long end) implements Comparable<Range> {

        public boolean isInside(long value) {
            return value >= start && value <= end;
        }

        @Override
        public int compareTo(Range r) {
            int a = Long.compare(start, r.start);
            if (a != 0) return a;

            return Long.compare(end, r.end);
        }
    }
}
