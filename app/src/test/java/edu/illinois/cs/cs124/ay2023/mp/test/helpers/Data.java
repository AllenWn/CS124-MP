package edu.illinois.cs.cs124.ay2023.mp.test.helpers;

import static com.google.common.truth.Truth.assertWithMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.illinois.cs.cs124.ay2023.mp.models.Summary;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/*
 * This file contains helper code used by the test suites.
 * You should not need to modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 *
 * The helper methods in this file assist with loading course data for testing.
 */
public class Data {
  // Object mapper used by the test suites
  public static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  // Fingerprint of the courses.json file
  private static final String COURSES_FINGERPRINT = "b9cc2290b7b01ad51362b98c68bb7620";

  // List of summaries for testing
  public static final List<Summary> SUMMARIES = loadSummaries();

  // Number of summaries that we expect
  public static final int SUMMARY_COUNT = SUMMARIES.size();

  // List of courses for testing
  public static final List<String> COURSES = Collections.unmodifiableList(loadCourses());

  // Load JSON and create summaries
  public static List<Summary> loadSummaries() {
    List<Summary> summaries = new ArrayList<>();

    String coursesJson = fingerprintCourses();

    try {
      JsonNode coursesNodes = OBJECT_MAPPER.readTree(coursesJson);
      for (Iterator<JsonNode> it = coursesNodes.elements(); it.hasNext(); ) {
        JsonNode node = it.next();

        // Use only summary fields
        ObjectNode summary = OBJECT_MAPPER.createObjectNode();
        summary.set("subject", node.get("subject"));
        summary.set("number", node.get("number"));
        summary.set("label", node.get("label"));
        summaries.add(OBJECT_MAPPER.readValue(summary.toPrettyString(), Summary.class));
      }
      return Collections.unmodifiableList(summaries);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  // Load JSON and create courses
  public static List<String> loadCourses() {
    List<String> courses = new ArrayList<>();

    String coursesJson = fingerprintCourses();

    try {
      JsonNode coursesNodes = OBJECT_MAPPER.readTree(coursesJson);
      for (Iterator<JsonNode> it = coursesNodes.elements(); it.hasNext(); ) {
        JsonNode node = it.next();
        courses.add(node.toPrettyString());
      }
      return courses;
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  // Check the fingerprint of the courses.json file to ensure it hasn't been modified
  public static String fingerprintCourses() {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (Exception e) {
      throw new IllegalStateException("MD5 algorithm should be available", e);
    }

    String input =
        new Scanner(Helpers.class.getResourceAsStream("/courses.json"), StandardCharsets.UTF_8)
            .useDelimiter("\\A")
            .next();

    String toFingerprint =
        Arrays.stream(input.split("\n"))
            .map(String::stripTrailing)
            .collect(Collectors.joining("\n"));

    String currentFingerprint =
        String.format(
                "%1$32s",
                new BigInteger(1, digest.digest(toFingerprint.getBytes(StandardCharsets.UTF_8)))
                    .toString(16))
            .replace(' ', '0');

    if (!currentFingerprint.equals(COURSES_FINGERPRINT)) {
      throw new IllegalStateException(
          "courses.json has been modified. Please restore the original version of the file.");
    }
    return input;
  }

  // Helper method to convert a JsonNode containing a summary into a backend API path suffix
  public static String nodeToPath(JsonNode node) {
    return node.get("subject").asText() + "/" + node.get("number").asText();
  }

  // Helper method to compare two Strings containing full course information as JSON
  public static void compareCourses(String expectedString, String foundString) {
    try {
      JsonNode expected = OBJECT_MAPPER.readTree(expectedString);
      JsonNode found = OBJECT_MAPPER.readTree(foundString);
      for (String component : Arrays.asList("subject", "number", "label", "description")) {
        assertWithMessage("Summary " + component + " is incorrect")
            .that(found.get(component).asText())
            .isEqualTo(expected.get(component).asText());
      }
    } catch (JsonProcessingException e) {
      assertWithMessage("Deserialization failed: " + e.getMessage()).fail();
      throw new RuntimeException(e);
    }
  }
}

// md5: 58928c6bc336e336e0c00747d2648918 // DO NOT REMOVE THIS LINE
