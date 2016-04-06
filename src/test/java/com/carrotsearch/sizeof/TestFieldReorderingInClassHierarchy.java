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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import sun.misc.Unsafe;

import com.carrotsearch.sizeof.BlackMagic;
import com.carrotsearch.sizeof.RamUsageEstimator;
import com.carrotsearch.sizeof.experiments.WildClasses;

@SuppressWarnings("restriction")
public class TestFieldReorderingInClassHierarchy {

  @SuppressWarnings({"deprecation"})
  @Test
  public void testFieldsOrdered() throws Exception {
    Unsafe unsafe = BlackMagic.getUnsafe();
    for (Class<?> clz : WildClasses.ALL) {
      TreeMap<Integer, Field> fields = new TreeMap<Integer, Field>(); 
      for (Class<?> c = clz; c != null; c = c.getSuperclass()) {
        for (Field f : c.getDeclaredFields()) {
          fields.put(unsafe.fieldOffset(f), f);
        }
      }

      Field prev = null;
      int reordered = 0;
      for (Map.Entry<Integer,Field> e : fields.entrySet()) {
        if (prev == null) prev = e.getValue();
        
        Class<?> fieldClass = e.getValue().getDeclaringClass();
        Class<?> c = prev.getDeclaringClass();

        // If a class changes it has to be a field belonging to a subclass.
        if (!c.isAssignableFrom(fieldClass)) {
          reordered++;
        }
        prev = e.getValue();
      }

      if (reordered > 0) {
        System.out.println("This JVM has reordered fields!");
        System.out.println(RamUsageEstimator.JVM_INFO_STRING);
        System.out.println("Example class with reordered fields:");
        System.out.println(BlackMagic.fieldsLayoutAsString(clz));
        return;
      }
    }
  }
}
