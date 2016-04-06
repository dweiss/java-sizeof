package com.carrotsearch.sizeof;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.*;

import org.junit.*;
import org.junit.runner.JUnitCore;

import com.carrotsearch.sizeof.experiments.WildClasses;

/**
 * Try to measure size estimation quality by allocating objects with known sizes
 * and summing up the expectation until OOM.
 */
public class TestEstimationQuality {
  static final long max;
  static final long used;
  static final long free;
  
  static {
    final MemoryUsage heapMemoryUsage = 
        ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

    max = heapMemoryUsage.getMax();
    used = heapMemoryUsage.getUsed();
    free = max - used;
  }

  @Before
  public void before() {
    // Don't run on too large heaps unless forced.
    if (System.getProperty("force") == null && free > 250 * RamUsageEstimator.ONE_MB) {
      Assume.assumeTrue(false);
    }
  }
  
  public static interface Allocator {
    long newObject(Object[] vault, int i);
  }

  public static class ClassAllocator implements Allocator {
    final long size;
    
    public ClassAllocator(final Class<?> c) {
      size = RamUsageEstimator.shallowSizeOfInstance(c);
    }

    @Override
    public long newObject(Object[] vault, int i) {
      vault[i] = new Object();
      return size;
    }
  }

  /**
   * Simple Objects only. No fields, no nothing.
   */
  @Test @Ignore
  public void testObject() {
    estimate(new ClassAllocator(Object.class));
  }

  /**
   * Wild class instances (and arrays, etc.).
   */
  @Test
  public void testWildClasses() {
    estimate(new Allocator() {
      List<Class<?>> classes = new ArrayList<Class<?>>();
      HashMap<Class<?>,Long> sizes = new HashMap<Class<?>,Long>();
      Random random = new Random();

      {
        for (Class<?> c : WildClasses.ALL) {
          try {
            long size = RamUsageEstimator.sizeOf(c.newInstance());
            // Uncomment to measure small objects only.
            // if (size > 80) { continue; }
            classes.add(c);
            sizes.put(c, size);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      }

      @Override
      public long newObject(Object[] vault, int i) {
        Class<?> c = classes.get(random.nextInt(classes.size()));
        try {
          vault[i] = c.newInstance();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        if (!sizes.containsKey(c)) {
          throw new RuntimeException();
        }
        return sizes.get(c);
      }
    });
  }

  /**
   * Estimate estimation quality of {@link Allocator}'s objects. 
   */
  public void estimate(Allocator allocator) {
    final MemoryUsage heapMemoryUsage = 
        ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

    System.out.println(RamUsageEstimator.JVM_INFO_STRING);
    System.out.println(
        "Max: " + RamUsageEstimator.humanReadableUnits(max) + 
        ", Used: " + RamUsageEstimator.humanReadableUnits(used) +
        ", Committed: " + RamUsageEstimator.humanReadableUnits(heapMemoryUsage.getCommitted()));

    // Allocate an Object[] buffer for storing refs. Take 50% of available memory for it.
    Object [] vault = new Object [(int) ((free / 2) / RamUsageEstimator.NUM_BYTES_OBJECT_REF)];

    long expectedAllocated = 0;
    try {
      for (int i = 0; i < vault.length; i++) {
        long j = allocator.newObject(vault, i);
        expectedAllocated += j;
      }
      throw new RuntimeException();
    } catch (OutOfMemoryError e) {
      // Ignore.
    }

    Arrays.fill(vault, null);

    long expectedFree = free - RamUsageEstimator.sizeOfAll(vault, allocator);
    double difference = 100.0d * (expectedAllocated - expectedFree) / (double) expectedFree;
    System.out.println(String.format(Locale.ENGLISH, 
        "Expected free: %s, Allocated estimation: %s, Difference: %.2f%% (%s)",
        RamUsageEstimator.humanReadableUnits(expectedFree),
        RamUsageEstimator.humanReadableUnits(expectedAllocated),
        difference,
        RamUsageEstimator.humanReadableUnits(Math.abs(expectedAllocated - expectedFree))
    ));    
  }
  
  public static void main(String[] args) {
    System.setProperty("force", "true");
    JUnitCore.runClasses(TestEstimationQuality.class);
  }
}
