package net.covers1624.aoc2025;

import net.covers1624.quack.collection.FastStream;
import org.openjdk.jmh.annotations.*;

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
public class Day4 extends Day {

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
    public void p1Test() {
        var result = solveP1(testInput);
        assertResult(result, 13);
    }

    @DayEntry
    @Benchmark
    public void p1() {
        var result = solveP1(input);
        assertResult(result, 1409);
    }

    @DayEntry
//    @Benchmark
    public void p2Test() {
        var result = solveP2(testInput);
        assertResult(result, 43);
    }

    @DayEntry
    @Benchmark
    public void p2() {
        var result = solveP2(input);
        assertResult(result, 8366);
    }

    public int solveP1(List<String> input) {
        return count(Grid.of(input));
    }

    public int solveP2(List<String> input) {
        var grid = Grid.of(input);

        int nRemoved = 0;
        boolean removed = true;
        while (removed) {
            removed = false;

            for (int x = 0; x < grid.width; x++) {
                for (int y = 0; y < grid.height; y++) {
                    if (grid.get(x, y) && grid.countNeighbours(x, y) < 4) {
                        grid.remove(x, y);
                        removed = true;
                        nRemoved++;
                    }
                }
            }
//            LOGGER.info(grid.toString());
        }

        return nRemoved;
    }

    private static int count(Grid grid) {
        int canMove = 0;
        for (int x = 0; x < grid.width; x++) {
            for (int y = 0; y < grid.height; y++) {
                if (grid.get(x, y) && grid.countNeighbours(x, y) < 4) {
                    canMove++;
                }
            }
        }

        return canMove;
    }

    public record Grid(char[/*Y*/][/*X*/] values, int width, int height) {

        public static Grid of(List<String> lines) {
            int width = lines.getFirst().length();
            int height = lines.size();

            char[][] grid = new char[height][];
            for (int y = 0; y < lines.size(); y++) {
                grid[y] = lines.get(y).toCharArray();
            }
            return new Grid(grid, width, height);
        }

        public boolean get(int x, int y) {
            return values[y][x] == '@';
        }

        public void remove(int x, int y) {
            values[y][x] = '.';
        }

        public int countNeighbours(int x, int y) {
            int count = 0;
            for (int xOff = -1; xOff <= 1; xOff++) {
                for (int yOff = -1; yOff <= 1; yOff++) {
                    int rx = x + xOff;
                    int ry = y + yOff;
                    if (rx < 0 || rx >= width) continue;
                    if (ry < 0 || ry >= height) continue;
                    if (rx == x && ry == y) continue;

                    if (get(rx, ry)) {
                        count++;
                    }
                }
            }
            return count;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < height; y++) {
                sb.append("\n");
                sb.append(values[y]);
            }
            return sb.toString();
        }
    }
}
