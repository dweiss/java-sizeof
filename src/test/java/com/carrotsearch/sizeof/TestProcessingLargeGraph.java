package com.carrotsearch.sizeof;

import java.util.HashSet;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.sizeof.experiments.WildClasses;

/**
 * Check large graph of instances with lots of classes. 
 */
public class TestProcessingLargeGraph extends AbstractBenchmark {
  private static HashSet<Object> randomObjects;

  @BeforeClass
  public static void prepare() throws Exception {
    final Random rnd = new Random(0xdeadbeef);
    HashSet<Object> all = new HashSet<Object>();
    Class<?>[] classes = WildClasses.ALL;
    for (int i = 0; i < 50000; i++) {
      all.add(classes[rnd.nextInt(classes.length)].newInstance());
    }
    randomObjects = all;
  }

  volatile long guard;

  @BenchmarkOptions(callgc = false, benchmarkRounds = 5, warmupRounds = 3)
  @Test
  public void testWildClasses() {
    guard = RamUsageEstimator.sizeOf(randomObjects);
    System.out.println(guard);
  }
}
