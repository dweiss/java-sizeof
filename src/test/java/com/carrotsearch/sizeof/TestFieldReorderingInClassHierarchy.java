package com.carrotsearch.sizeof;

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
