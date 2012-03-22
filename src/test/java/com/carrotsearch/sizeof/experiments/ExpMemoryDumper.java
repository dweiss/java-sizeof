package com.carrotsearch.sizeof.experiments;

import java.util.Locale;

import org.junit.Test;

import sun.misc.Unsafe;

import com.carrotsearch.sizeof.RamUsageEstimator;

@SuppressWarnings("restriction")
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

    System.out.println(dumpObjectMem(o1));
    System.out.println();
    System.out.println(dumpObjectMem(o2));
  }

  @SuppressWarnings("deprecation")
  public static String dumpObjectMem(Object o) {
    Unsafe unsafe = ExpSubclassAlignment.getUnsafe();

    StringBuilder b = new StringBuilder();
    final int obSize = (int) RamUsageEstimator.shallowSizeOf(o); 
    for (int i = 0; i < obSize; i++) {
      if ((i & 0xf) == 0) {
        if (i > 0) b.append("\n");
        b.append(String.format(Locale.ENGLISH, "%#06x", i));
      }
      
      b.append(" ");
      int byteValue = unsafe.getByte(o, i) & 0xff;
      b.append(String.format(Locale.ENGLISH, "%02x", byteValue)); // lazy :)
    }
    return b.toString();
  }
}
