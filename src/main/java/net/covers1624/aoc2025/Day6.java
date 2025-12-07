package net.covers1624.aoc2025;

import net.covers1624.quack.collection.ColUtils;
import net.covers1624.quack.collection.FastStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
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
public class Day6 extends Day {

    public final List<String> testInput = padLines(FastStream.of(loadLines("test.txt"))
//            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList());

    public final List<String> input = padLines(FastStream.of(loadLines("input.txt"))
//            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList());

    @DayEntry
//    @Benchmark
    public void p1Test(Blackhole bh) {
        var result = solve(parseP1(testInput));
        assertResult(result, 4277556);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p1(Blackhole bh) {
        var result = solve(parseP1(input));
        assertResult(result, 4309240495780L);
        bh.consume(result);
    }

    @DayEntry
//    @Benchmark
    public void p2Test(Blackhole bh) {
        var result = solve(parseP2(testInput));
        assertResult(result, 3263827);
        bh.consume(result);
    }

    @DayEntry
    @Benchmark
    public void p2(Blackhole bh) {
        var result = solve(parseP2(input));
        assertResult(result, 9170286552289L);
        bh.consume(result);
    }

    private long solve(List<Problem> problems) {
        long total = 0;
        for (Problem problem : problems) {
            long subTotal = problem.numbers.getFirst();
            for (long number : problem.numbers.subList(1, problem.numbers.size())) {
                if (problem.op == '*') {
                    subTotal *= number;
                } else if (problem.op == '+') {
                    subTotal += number;
                } else {
                    throw new RuntimeException();
                }
            }
            total += subTotal;
        }
        return total;
    }

    private List<Problem> parseP1(List<String> lines) {
        List<Problem> problems = new ArrayList<>();

        List<List<Long>> numbers = new ArrayList<>();
        for (String line : lines) {
            String[] split = removeDupeSpaces(line).split(" ");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.equals("*") || s.equals("+")) {
                    problems.add(new Problem(numbers.get(i), s.charAt(0)));
                } else {
                    if (numbers.size() <= i) numbers.add(new ArrayList<>());
                    numbers.get(i).add(Long.parseLong(s));
                }
            }
        }
        return problems;
    }

    private List<Problem> parseP2(List<String> lines) {
        List<Problem> problems = new ArrayList<>();

        int width = lines.getFirst().length();

        List<Long> numbers = new ArrayList<>();
        var numberLines = lines.subList(0, lines.size() - 1);
        for (int i = width - 1; i >= 0; i--) {
            char op = lines.getLast().charAt(i);
            long num = 0;
            boolean allSpaces = true;
            for (String numberLine : numberLines) {
                char ch = numberLine.charAt(i);
                if (ch != ' ') {
                    allSpaces = false;
                    num = num * 10 + (ch - '0');
                }
            }
            if (allSpaces) continue;
            numbers.add(num);
            if (op != ' ') {
                problems.add(new Problem(numbers, op));
                numbers = new ArrayList<>();
            }
        }
        if (!numbers.isEmpty()) throw new RuntimeException("EASDASD");

        return problems;
    }

    private String removeDupeSpaces(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                if (sb.isEmpty() || sb.charAt(sb.length() - 1) == ' ') continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private List<String> padLines(List<String> lines) {
        int max = ColUtils.requireMaxBy(lines, String::length).length();
        lines.replaceAll(e -> e + " ".repeat(max - e.length()));
        return lines;
    }

    private record Problem(List<Long> numbers, char op) { }
}
