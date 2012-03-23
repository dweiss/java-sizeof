package com.carrotsearch.sizeof;

import org.junit.Assert;
import org.junit.Test;

/**
 * Check stack overflows on long chains. These shouldn't happen.
 */
public class TestLongChains {
  public static class ListElement {
    ListElement next;
  }
  
  @Test
  public void testOverflowMaxChainLength() {
    int UPPERLIMIT = 100000;
    int lower = 0;
    int upper = UPPERLIMIT;
    
    while (lower + 1 < upper) {
      int mid = (lower + upper) / 2;
      try {
        ListElement first = new ListElement();
        ListElement last = first;
        for (int i = 0; i < mid; i++) {
          last = (last.next = new ListElement());
        }
        RamUsageEstimator.sizeOf(first); // cause SOE or pass.
        lower = mid;
      } catch (StackOverflowError e) {
        upper = mid;
      }
    }

    if (lower + 1 < UPPERLIMIT) {
      Assert.fail("Max object chain length till stack overflow: " + lower);
    }
  }
}
