package com.carrotsearch.sizeof.experiments;

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
