package net.covers1624.aoc2025;

import net.covers1624.quack.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

// Args for dev: -f 0 -wi 0 -i 1 -r 0 -bs 1
public class Day {

    public final Logger LOGGER = LogManager.getLogger(getClassName(getClass()));

    // Main method will run any function annotated with Benchmark or DayEntry, supplying a BlackHole param if required.
    // Used for testing without spinning up jmh.
    void main() throws Throwable {
        Class<?> clazz = getClass();
        for (Method method : clazz.getMethods()) {
            if (method.getAnnotation(Benchmark.class) == null && method.getAnnotation(DayEntry.class) == null) continue;
            if (method.accessFlags().contains(AccessFlag.STATIC)) return;

            var params = method.getParameters();
            Object[] paramValues = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                var param = params[i];
                var pType = param.getType();
                if (pType.equals(Blackhole.class)) {
                    paramValues[i] = testBlackhole();
                } else {
                    throw new RuntimeException("Unknown parameter type. " + pType.getName());
                }
            }

            LOGGER.info("Running {}:", method.getName());
            method.invoke(this, paramValues);
        }
    }

    public Blackhole testBlackhole() {
        return new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
    }

    public String load(String file) {
        return new String(bytes(file), StandardCharsets.UTF_8);
    }

    public List<String> loadLines(String file) {
        try {
            return IOUtils.readAll(bytes(file));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public byte[] bytes(String file) {
        try (InputStream is = open(file)) {
            return IOUtils.toBytes(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public InputStream open(String file) {
        return Day.class.getResourceAsStream("/" + getClassName(getClass()).toLowerCase(Locale.ROOT) + "/" + file);
    }

    public void assertResult(long result, long expected) {
        if (result != expected) {
            throw new RuntimeException("Expected " + expected + " got " + result);
        }
    }

    private static String getClassName(Class<?> clazz) {
        while (clazz.getName().contains("_jmhType")) {
            clazz = clazz.getSuperclass();
        }
        return clazz.getSimpleName();
    }

    @Target (ElementType.METHOD)
    @Retention (RetentionPolicy.RUNTIME)
    public @interface DayEntry { }
}
