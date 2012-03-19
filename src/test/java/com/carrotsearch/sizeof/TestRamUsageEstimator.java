package com.carrotsearch.sizeof;

import static com.carrotsearch.sizeof.RamUsageEstimator.*;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestRamUsageEstimator {
  
  @Test
  public void testBasic() {
    assertTrue(sizeOf(new String("test strin")) > shallowSizeOfInstance(String.class));
    
    Holder holder = new Holder();
    holder.holder = new Holder("string2", 5000L);
    assertTrue(sizeOf(holder) > shallowSizeOfInstance(Holder.class));
    assertTrue(sizeOf(holder) > sizeOf(holder.holder));
    
    assertTrue(shallowSizeOfInstance(HolderSubclass.class) >= shallowSizeOfInstance(Holder.class));
    assertEquals(shallowSizeOfInstance(Holder.class),
        shallowSizeOfInstance(HolderSubclass2.class));
    
    String[] strings = new String[] {new String("test strin"),
        new String("hollow"), new String("catchmaster")};
    assertTrue(sizeOf(strings) > shallowSizeOf(strings));
  }
  
  @Test
  public void testStaticOverloads() {
    Random rnd = new Random();
    
    {
      byte[] array = new byte[rnd.nextInt(1024)];
      assertEquals(sizeOf(array), sizeOf((Object) array));
    }
    
    {
      boolean[] array = new boolean[rnd.nextInt(1024)];
      assertEquals(sizeOf(array), sizeOf((Object) array));
    }
    
    {
      char[] array = new char[rnd.nextInt(1024)];
      assertEquals(sizeOf(array), sizeOf((Object) array));
    }
    
    {
      short[] array = new short[rnd.nextInt(1024)];
      assertEquals(sizeOf(array), sizeOf((Object) array));
    }
    
    {
      int[] array = new int[rnd.nextInt(1024)];
      assertEquals(sizeOf(array), sizeOf((Object) array));
    }
    
    {
      float[] array = new float[rnd.nextInt(1024)];
      assertEquals(sizeOf(array), sizeOf((Object) array));
    }
    
    {
      long[] array = new long[rnd.nextInt(1024)];
      assertEquals(sizeOf(array), sizeOf((Object) array));
    }
    
    {
      double[] array = new double[rnd.nextInt(1024)];
      assertEquals(sizeOf(array), sizeOf((Object) array));
    }
  }
  
  @Test
  public void testReferenceSize() {
    if (!isSupportedJVM()) {
      System.err
          .println("WARN: Your JVM does not support the Oracle/Sun extensions (Hotspot diagnostics, sun.misc.Unsafe),");
      System.err.println("so the memory estimates may be inprecise.");
      System.err
          .println("Please report this to the Lucene mailing list, noting your JVM version: "
              + Constants.JAVA_VENDOR + " " + Constants.JAVA_VERSION);
    }
    
    assertTrue(NUM_BYTES_OBJECT_REF == 4 || NUM_BYTES_OBJECT_REF == 8);
    if (!Constants.JRE_IS_64BIT) {
      assertEquals("For 32bit JVMs, reference size must always be 4", 4,
          NUM_BYTES_OBJECT_REF);
    }
  }
  
  @SuppressWarnings("unused")
  private static class Holder {
    long field1 = 5000L;
    String name = "name";
    Holder holder;
    long field2, field3, field4;
    
    Holder() {}
    
    Holder(String name, long field1) {
      this.name = name;
      this.field1 = field1;
    }
  }
  
  @SuppressWarnings("unused")
  private static class HolderSubclass extends Holder {
    byte foo;
    int bar;
  }
  
  private static class HolderSubclass2 extends Holder {
    // empty, only inherits all fields -> size should be identical to superclass
  }
}
