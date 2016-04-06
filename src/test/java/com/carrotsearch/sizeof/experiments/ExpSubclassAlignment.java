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
 * Shows fields layout in a class.
 */
public class ExpSubclassAlignment {
  
  public static class Super {
    public long subLong;
    public byte superByte;
  }

  public static class Sub extends Super {
    public long subLong;
    public byte subByte;
  }

  public static class SubSub extends Sub {
    public long subSubLong;
    public byte subSubByte;
  }

  @Test
  public void testLongInSubclass() throws Exception {
    System.out.println(RamUsageEstimator.JVM_INFO_STRING);
    Class<?> clazz = SubSub.class;
    System.out.println(BlackMagic.fieldsLayoutAsString(clazz));
  }
}
