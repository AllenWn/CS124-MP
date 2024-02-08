package edu.illinois.cs.cs124.ay2023.mp.test.helpers;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/*
 * This file contains helper code used by the test suites.
 * You should not need to modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 *
 * This helper class allows filtering test results to remove extraneous output.
 */
public class FilteringPrintStream extends PrintStream {
  public FilteringPrintStream() {
    super(OutputStream.nullOutputStream());
  }

  private static final List<String> IGNORED_TAGS =
      Arrays.asList(
          "LifecycleMonitor",
          "ActivityScenario",
          "AppCompatDelegate",
          "ViewInteraction",
          "Tracing",
          "EventInjectionStrategy");

  @Override
  public void println(String line) {
    String[] parts = line.split(": ");
    if (parts.length < 2) {
      System.out.println(line);
      return;
    }
    String[] tagParts = parts[0].split("/");
    if (tagParts.length != 2) {
      System.out.println(line);
      return;
    }
    if (IGNORED_TAGS.contains(tagParts[1])) {
      return;
    }
    System.out.println(line);
  }
}

// md5: c3b50ace798846bc4d9e23a1a029fe67 // DO NOT REMOVE THIS LINE
