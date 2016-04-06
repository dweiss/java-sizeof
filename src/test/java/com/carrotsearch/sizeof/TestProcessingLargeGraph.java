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
