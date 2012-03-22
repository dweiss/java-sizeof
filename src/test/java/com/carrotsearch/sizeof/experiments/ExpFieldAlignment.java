package com.carrotsearch.sizeof.experiments;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import sun.misc.Unsafe;

import com.carrotsearch.sizeof.RamUsageEstimator;

@SuppressWarnings("restriction")
public class ExpFieldAlignment {

  @SuppressWarnings({"deprecation"})
  @Test
  public void testFieldsOrdered() throws Exception {
    System.out.println(RamUsageEstimator.JVM_INFO_STRING);
    Unsafe unsafe = getUnsafe();
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
        System.out.println("# packed fields: " + reordered);
        System.out.println(ExpSubclassAlignment.dumpFields(clz));
      }
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
