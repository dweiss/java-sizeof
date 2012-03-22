package com.carrotsearch.sizeof.experiments;


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
