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
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import sun.misc.Unsafe;

/**
 * This class contains black magic stuff based mostly on proprietary
 * APIs and hacks. Use at your own risk.
 */
@SuppressWarnings({"restriction"})
public final class BlackMagic {
  /**
   * Returns Unsafe if available or throw a RuntimeException.
   */
  public static sun.misc.Unsafe getUnsafe() {
    try {
      final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      final Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      return (sun.misc.Unsafe) unsafeField.get(null);
    } catch (Throwable t) {
      throw new RuntimeException("Unsafe not available.", t);
    }
  }

  /**
   * Attempts to dump physical object's memory as a string.
   */
  public static String objectMemoryAsString(Object o) {
    final Unsafe unsafe = getUnsafe();
    final ByteOrder byteOrder = ByteOrder.nativeOrder();
    
    StringBuilder b = new StringBuilder();
    final int obSize = (int) RamUsageEstimator.shallowSizeOf(o); 
    for (int i = 0; i < obSize; i += 2) {
      if ((i & 0xf) == 0) {
        if (i > 0) b.append("\n");
        b.append(String.format(Locale.ENGLISH, "%#06x", i));
      }
  
      // we go short by short because J9 fails on odd addresses (everything is aligned,
      // including byte fields.
      int shortValue = unsafe.getShort(o, (long) i);
  
      if (byteOrder == ByteOrder.BIG_ENDIAN) {
        b.append(String.format(Locale.ENGLISH, " %02x", (shortValue >>> 8) & 0xff));
        b.append(String.format(Locale.ENGLISH, " %02x", (shortValue & 0xff)));
      } else {
        b.append(String.format(Locale.ENGLISH, " %02x", (shortValue & 0xff)));
        b.append(String.format(Locale.ENGLISH, " %02x", (shortValue >>> 8) & 0xff));
      }
    }
    return b.toString();
  }

  /**
   * Attempts to dump a layout of a class's fields in memory 
   * (offsets from base object pointer).
   */
  @SuppressWarnings({"unchecked"})
  public static String fieldsLayoutAsString(Class<?> clazz) {
    Unsafe unsafe = getUnsafe();
    TreeMap<Long, String> fields = new TreeMap<Long, String>(); 
    for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
      for (Field f : c.getDeclaredFields()) {
        fields.put(
            unsafe.objectFieldOffset(f),
            f.getDeclaringClass().getSimpleName() + "." + f.getName());
      }
    }
    fields.put(
        RamUsageEstimator.shallowSizeOfInstance(clazz), "#shallowSizeOfInstance(" + clazz.getName() + ")");

    StringBuilder b = new StringBuilder();
    Object [] entries = fields.entrySet().toArray();
    for (int i = 0; i < entries.length; i++) {
      Map.Entry<Long, String> e    = (Map.Entry<Long, String>) entries[i];
      Map.Entry<Long, String> next = (i + 1 < entries.length ? (Map.Entry<Long, String>) entries[i + 1] : null);
  
      b.append(String.format(Locale.ENGLISH,
          "@%02d %2s %s\n", 
          e.getKey(),
          next == null ? "" : next.getKey() - e.getKey(),
          e.getValue()));
    }
    return b.toString();
  }
}
