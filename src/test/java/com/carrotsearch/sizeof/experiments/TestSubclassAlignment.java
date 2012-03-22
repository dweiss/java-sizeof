package com.carrotsearch.sizeof.experiments;

import java.lang.reflect.Field;
import java.util.*;

import org.junit.Test;

import com.carrotsearch.sizeof.RamUsageEstimator;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class TestSubclassAlignment {
  
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

  @SuppressWarnings({"deprecation", "unchecked"})
  @Test
  public void testLongInSubclass() throws Exception {
    System.out.println(RamUsageEstimator.JVM_INFO_STRING);
    Unsafe unsafe = getUnsafe();
    TreeMap<Integer, String> fields = new TreeMap<Integer, String>(); 
    for (Class<?> c = SubSub.class; c != null; c = c.getSuperclass()) {
      for (Field f : c.getDeclaredFields()) {
        fields.put(unsafe.fieldOffset(f),
            f.getDeclaringClass().getSimpleName() + "." + f.getName());
      }
    }
    fields.put(
        (int) RamUsageEstimator.shallowSizeOfInstance(SubSub.class), "sizeOf(SubSub.class instance)");

    Object [] entries = fields.entrySet().toArray();
    for (int i = 0; i < entries.length; i++) {
      Map.Entry<Integer, String> e    = (Map.Entry<Integer, String>) entries[i];
      Map.Entry<Integer, String> next = 
          (i + 1 < entries.length ? (Map.Entry<Integer, String>) entries[i + 1] : null);

      System.out.println(String.format(Locale.ENGLISH,
          "@%s %2s %s", 
          e.getKey(),
          next == null ? "" : next.getKey() - e.getKey(),
          e.getValue()));
    }
  }

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
}
