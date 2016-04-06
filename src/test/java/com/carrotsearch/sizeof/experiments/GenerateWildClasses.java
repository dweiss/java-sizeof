package com.carrotsearch.sizeof.experiments;

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

import java.io.PrintWriter;
import java.util.*;

/**
 * Generate wild classes with all kinds of weird fields.
 */
public class GenerateWildClasses {
  private static int numClasses = 100;
  private static int numInheritanceLevels = 8;
  private static int maxFields = 50;
  private static int maxPreallocSize = 64;

  public static void main(String[] args) throws Exception {
    Random rnd = new Random(0xdeadbeef);
    
    PrintWriter pw = new PrintWriter("WildClasses.java", "UTF-8");;
    pw.println("package com.carrotsearch.sizeof;");
    pw.println("public final class WildClasses {");

    ArrayList<String> allClasses = new ArrayList<String>();
    ArrayList<String> superClasses = new ArrayList<String>();
    for (int level = 0; level < numInheritanceLevels; level++) {
      ArrayList<String> thisLevelClasses = new ArrayList<String>();
      for (int i = 0; i < numClasses; i++) {
        String uniqueSuffix = level + "_" + i;
        String clName = "Wild_" + uniqueSuffix;
        thisLevelClasses.add(clName);

        pw.print("  public static class " + clName);
        if (level > 0) {
          pw.print(" extends " + superClasses.get(rnd.nextInt(superClasses.size())));
        }
        pw.println(" {");
        for (int f = 0; f < rnd.nextInt(maxFields); f++) {
          randomFieldDecl(rnd, pw, "fld_" + f + "_" + uniqueSuffix);
        }
        pw.println("  }");
        pw.println();
      }
      superClasses.clear();
      superClasses.addAll(thisLevelClasses);
      allClasses.addAll(thisLevelClasses);
    }
    
    pw.println("  public final static Class<?> [] ALL = {");
    for (String clazz : allClasses) {
      pw.println("    " + clazz + ".class,");
    }
    pw.println("  };");
    
    pw.println("}");
    pw.flush();
  }

  private final static String [] scopes = {
    "public", "private", "protected", /* package */ ""
  };

  private final static String [] types = {
    "byte", "boolean", "short", "char", "int", "float", "long", "double", "Object"
  };

  private static void randomFieldDecl(Random rnd, PrintWriter pw, String fieldName) {
    String scope = scopes[rnd.nextInt(scopes.length)];
    String type = types[rnd.nextInt(types.length)];
    int v = rnd.nextInt(100);
    if (v < 10) {
      // 20% of object[] or primitive[]
      pw.println(String.format(Locale.ENGLISH,
          "    %-10s %s[] %s = new %s [%d];",
          scope, type, fieldName, type, rnd.nextInt(maxPreallocSize)));
    } else {
      // 80% regular fields.
      pw.println(String.format(Locale.ENGLISH,
          "    %-10s %s %s;",
          scope, type, fieldName));
    }
  }
}
