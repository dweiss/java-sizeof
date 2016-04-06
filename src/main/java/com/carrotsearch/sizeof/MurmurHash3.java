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

/**
 * Hash routines for primitive types. The implementation is based on the
 * finalization step from Austin Appleby's <code>MurmurHash3</code>.
 * 
 * @see "http://sites.google.com/site/murmurhash/"
 */
final class MurmurHash3 {
  private MurmurHash3() {
    // no instances.
  }
  
  /**
   * Hashes a 4-byte sequence (Java int).
   */
  public static int hash(int k) {
    k ^= k >>> 16;
    k *= 0x85ebca6b;
    k ^= k >>> 13;
    k *= 0xc2b2ae35;
    k ^= k >>> 16;
    return k;
  }
  
  /**
   * Hashes an 8-byte sequence (Java long).
   */
  public static long hash(long k) {
    k ^= k >>> 33;
    k *= 0xff51afd7ed558ccdL;
    k ^= k >>> 33;
    k *= 0xc4ceb9fe1a85ec53L;
    k ^= k >>> 33;
    
    return k;
  }
}
