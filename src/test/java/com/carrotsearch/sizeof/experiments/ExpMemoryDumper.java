package com.carrotsearch.sizeof.experiments;

import java.nio.ByteOrder;
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

  public static class ByteOrderCheck {
    public short check = 0x1122;
  }
  
  public static String dumpObjectMem(Object o) {
    final Unsafe unsafe = ExpSubclassAlignment.getUnsafe();
    final ByteOrder byteOrder = ByteOrder.nativeOrder();
    
    StringBuilder b = new StringBuilder();
    final int obSize = (int) RamUsageEstimator.shallowSizeOf(o); 
    for (int i = 0; i < obSize; i += 2) {
      if ((i & 0xf) == 0) {
        if (i > 0) b.append("\n");
        b.append(String.format(Locale.ENGLISH, "%#06x", i));
      }

      // we go short by short because J9 fails on odd addresses.
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
}
