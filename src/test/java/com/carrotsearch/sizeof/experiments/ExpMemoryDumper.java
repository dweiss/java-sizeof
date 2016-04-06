package com.carrotsearch.sizeof.experiments;

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

import org.junit.Test;


import com.carrotsearch.sizeof.BlackMagic;
import com.carrotsearch.sizeof.RamUsageEstimator;

/**
 * An example showing object's physical memory dump.
 */
public class ExpMemoryDumper {
  public static class Super {
    public int superMarker = 0x11223344;
  }

  public static class Sub extends Super {
    public Super ref1;
    public Super ref2;
    public int subMarker = 0xa1a2a3a4;
  }

  @Test
  public void testDumper() throws Exception {
    System.out.println(RamUsageEstimator.JVM_INFO_STRING);

    Super o1 = new Super();
    Sub   o2 = new Sub();
    o2.ref1 = o2;
    o2.ref2 = o1;

    System.out.println(BlackMagic.objectMemoryAsString(o1));
    System.out.println();
    System.out.println(BlackMagic.objectMemoryAsString(o2));
  }
}
