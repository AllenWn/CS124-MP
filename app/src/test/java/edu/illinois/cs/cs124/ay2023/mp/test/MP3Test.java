package edu.illinois.cs.cs124.ay2023.mp.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static com.google.common.truth.Truth.assertWithMessage;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Data.COURSES;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Data.OBJECT_MAPPER;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Data.SUMMARIES;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Data.nodeToPath;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.HTTP.getAPIClient;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.HTTP.testClient;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.HTTP.testServerGet;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.HTTP.testServerPost;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Helpers.configureLogging;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Helpers.pause;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Helpers.startActivity;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Views.hasRating;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Views.setRating;

import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.illinois.cs.cs124.ay2023.mp.R;
import edu.illinois.cs.cs124.ay2023.mp.activities.CourseActivity;
import edu.illinois.cs.cs124.ay2023.mp.models.Rating;
import edu.illinois.cs.cs124.ay2023.mp.models.Summary;
import edu.illinois.cs.cs124.ay2023.mp.network.Client;
import edu.illinois.cs.cs124.ay2023.mp.network.Server;
import edu.illinois.cs.cs125.gradlegrader.annotations.Graded;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.annotation.LooperMode;

/*
 * This is the MP3 test suite.
 * The code below is used to evaluate your app during testing, local grading, and official grading.
 * You may not understand all of the code below, but you'll need to have some understanding of how
 * it works so that you can determine what is wrong with your app and what you need to fix.
 *
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 * You can and should modify the code below if it is useful during your own local testing,
 * but any changes you make will be discarded during official grading.
 * The local grader will not run if the test suites have been modified, so you'll need to undo any
 * local changes before you run the grader.
 *
 * Note that this means that you should not fix problems with the app by modifying the test suites.
 * The test suites are always considered to be correct.
 *
 * Our test suites are broken into two parts.
 * The unit tests (in the UnitTests class) are tests that we can perform without running your app.
 * They test things like whether a method works properly or the behavior of your API server.
 * Unit tests are usually fairly fast.
 *
 * The integration tests (in the IntegrationTests class) are tests that require simulating your app.
 * This allows us to test things like your API client, and higher-level aspects of your app's
 * behavior, such as whether it displays the right thing on the display.
 * Because integration tests require simulating your app, they run more slowly.
 *
 * The MP3 test suite includes no ungraded tests.
 * These tests are fairly idiomatic, in that they resemble tests you might write for an actual
 * Android programming project.
 */

@RunWith(Enclosed.class)
public final class MP3Test {
  // Unit tests that don't require simulating the entire app, and usually complete quickly
  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class UnitTests {
    static {
      // Start the API server
      Server.start();
    }

    /** Reset the server before each test. */
    @Before
    public void resetServer() {
      try {
        Server.reset();
      } catch (Exception e) {
      }
    }

    /** Test the GET rating server route. */
    @Test(timeout = 10000L)
    @Graded(points = 10, friendlyName = "Server GET /rating/")
    public void test0_ServerGETRating() throws IOException {
      // Note that until you complete the POST /rating/ route, this test is fairly limited
      // Test good requests
      for (String courseString : COURSES) {
        JsonNode node = OBJECT_MAPPER.readTree(courseString);
        Rating rating = testServerGet("/rating/" + nodeToPath(node), Rating.class);
        assertWithMessage("Incorrect rating for unrated course")
            .that(rating.getRating())
            .isEqualTo(Rating.NOT_RATED);
      }

      // Test bad requests
      // Bad URL
      testServerGet("/rating/CS/", HttpURLConnection.HTTP_BAD_REQUEST);
      // Non-existent course
      testServerGet("/rating/CS/188/", HttpURLConnection.HTTP_NOT_FOUND);
      // Non-existent URL
      testServerGet("/ratings/CS/124/", HttpURLConnection.HTTP_NOT_FOUND);
    }

    /** Test the POST rating server route. */
    @Test(timeout = 20000L)
    @Graded(points = 20, friendlyName = "Server POST /rating/")
    public void test1_ServerPOSTRating() throws IOException {

      // Proceed through courses in a deterministic random order
      Random random = new Random(124);
      List<String> shuffledCourses = new ArrayList<>(COURSES);
      Collections.shuffle(shuffledCourses, random);

      // Perform initial GET /rating/ requests
      for (String courseString : shuffledCourses) {
        // Construct URL
        JsonNode node = OBJECT_MAPPER.readTree(courseString);
        String url = "/rating/" + nodeToPath(node);

        // Perform initial GET
        Rating rating = testServerGet(url, Rating.class);
        assertWithMessage("Incorrect rating for unrated course")
            .that(rating.getRating())
            .isEqualTo(Rating.NOT_RATED);
      }

      // Perform POST /rating/ requests to change ratings
      Map<String, Float> ratings = new HashMap<>();
      Collections.shuffle(shuffledCourses, random);
      for (String courseString : shuffledCourses) {
        JsonNode node = OBJECT_MAPPER.readTree(courseString);

        // POST to change rating
        float testRating = random.nextInt(51) / 10.0f;

        // Construct POST rating body
        ObjectNode newRating = OBJECT_MAPPER.createObjectNode();
        newRating.set("summary", OBJECT_MAPPER.convertValue(node, JsonNode.class));
        newRating.set("rating", OBJECT_MAPPER.convertValue(testRating, JsonNode.class));

        Rating rating = testServerPost("/rating/", newRating, Rating.class);
        assertWithMessage("Incorrect rating from rating POST")
            .that(rating.getRating())
            .isEqualTo(testRating);

        // Save rating value for next stage
        ratings.put(nodeToPath(node), testRating);
      }

      // Second route of GET /rating/ requests to ensure ratings are saved
      Collections.shuffle(shuffledCourses, random);
      for (String courseString : shuffledCourses) {
        // Construct URL
        JsonNode node = OBJECT_MAPPER.readTree(courseString);
        String url = "/rating/" + nodeToPath(node);

        // Retrieve saved rating
        float savedRating = ratings.get(nodeToPath(node));

        // Final GET
        Rating rating = testServerGet(url, Rating.class);
        assertWithMessage("Incorrect rating for course: should be " + savedRating)
            .that(rating.getRating())
            .isEqualTo(savedRating);
      }

      // Bad requests
      Summary testingSummary = new Summary("CS", "124", "");
      ObjectNode newRating = OBJECT_MAPPER.createObjectNode();
      newRating.set("summary", OBJECT_MAPPER.convertValue(testingSummary, JsonNode.class));
      newRating.set("rating", OBJECT_MAPPER.convertValue(3.0, JsonNode.class));

      // Bad URL
      testServerPost("/ratings/", testingSummary, HttpURLConnection.HTTP_NOT_FOUND);

      // Non-existing course in rating
      Summary nonexistentSummary = new Summary("CS", "123", "");
      newRating.set("summary", OBJECT_MAPPER.convertValue(nonexistentSummary, JsonNode.class));
      testServerPost("/rating/", newRating, HttpURLConnection.HTTP_NOT_FOUND);

      // Bad body
      testServerPost("/rating/", "test me", HttpURLConnection.HTTP_BAD_REQUEST);
    }
  }

  // Integration tests that require simulating the entire app, and are usually slower
  @RunWith(AndroidJUnit4.class)
  @LooperMode(LooperMode.Mode.PAUSED)
  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class IntegrationTests {
    static {
      // Set up logging so that you can see log output during testing
      configureLogging();
    }

    /** Reset the server before each test. */
    @Before
    public void resetServer() {
      try {
        Server.reset();
      } catch (Exception e) {
      }
    }

    /** Test the client getRating method */
    @Test(timeout = 20000L)
    @Graded(points = 10, friendlyName = "Client GET /rating/")
    public void test2_ClientGETRating() throws Exception {
      // Note that until you complete the POST /rating/ route, this test is fairly limited
      Client apiClient = getAPIClient();

      for (Summary summary : SUMMARIES) {
        Rating rating = testClient((callback) -> apiClient.getRating(summary, callback));
        assertWithMessage("Incorrect summary subject for unrated course")
            .that(rating.getSummary().getSubject())
            .isEqualTo(summary.getSubject());
        assertWithMessage("Incorrect summary number for unrated course")
            .that(rating.getSummary().getNumber())
            .isEqualTo(summary.getNumber());
        assertWithMessage("Incorrect rating for unrated course")
            .that(rating.getRating())
            .isEqualTo(Rating.NOT_RATED);
      }
    }

    /** Test the client postRating method */
    @Test(timeout = 10000L)
    @Graded(points = 20, friendlyName = "Client POST /rating/")
    public void test3_ClientPOSTRating() throws Exception {
      Client apiClient = getAPIClient();

      Random random = new Random(124);
      Map<Summary, Float> testRatings = new HashMap<>();

      // Go through all courses twice
      for (int i = 0; i < 2; i++) {
        for (Summary summary : SUMMARIES) {
          // Randomly either GET or POST
          Rating rating;
          if (random.nextBoolean()) {
            rating = testClient((callback) -> apiClient.getRating(summary, callback));
          } else {
            float testRating = random.nextInt(51) / 10.0f;
            testRatings.put(summary, testRating);
            rating =
                testClient(
                    (callback) ->
                        getAPIClient().postRating(new Rating(summary, testRating), callback));
          }

          float expectedRating = testRatings.getOrDefault(summary, Rating.NOT_RATED);
          assertWithMessage("Mismatch on rating")
              .that(rating.getRating())
              .isEqualTo(expectedRating);
          assertWithMessage("Incorrect summary subject")
              .that(rating.getSummary().getSubject())
              .isEqualTo(summary.getSubject());
          assertWithMessage("Incorrect summary number")
              .that(rating.getSummary().getNumber())
              .isEqualTo(summary.getNumber());
        }
      }
    }

    // Helper method for the UI test
    private void ratingViewHelper(int summaryIndex, int startRating, int endRating)
        throws JsonProcessingException {
      // Pull Summary and Course details
      String summaryString = OBJECT_MAPPER.writeValueAsString(SUMMARIES.get(summaryIndex));
      String courseString = COURSES.get(summaryIndex);

      // Prepare the Intent to start the CourseActivity
      Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CourseActivity.class);
      ObjectNode summaryForIntent = (ObjectNode) OBJECT_MAPPER.readTree(summaryString);
      summaryForIntent.remove("description");
      intent.putExtra("summary", summaryForIntent.toString());
      JsonNode course = OBJECT_MAPPER.readTree(courseString);

      // Start the CourseActivity
      startActivity(
          intent,
          activity -> {
            pause();
            // Test again that the title and description are shown
            String title =
                course.get("subject").asText()
                    + " "
                    + course.get("number").asText()
                    + ": "
                    + course.get("label").asText();
            onView(withSubstring(title)).check(matches(isDisplayed()));
            onView(withSubstring(course.get("description").asText())).check(matches(isDisplayed()));

            // Check that the initial rating is correct, change it, and then verify the change
            onView(withId(R.id.rating))
                .check(hasRating(startRating))
                .perform(setRating(endRating))
                .check(hasRating(endRating));
          });
    }

    /** Test rating view. */
    @Test(timeout = 30000L)
    @Graded(points = 20, friendlyName = "Rating View")
    public void test4_RatingView() throws JsonProcessingException {
      // Loop through first four courses, setting initial ratings
      for (int i = 0; i < 4; i++) {
        pause();
        ratingViewHelper(i, 0, i);
        pause();
      }
      // Loop through first four courses, modifying ratings
      for (int i = 0; i < 4; i++) {
        pause();
        ratingViewHelper(i, i, 5 - i);
        pause();
      }
      // Loop through first four courses, modifying ratings again
      for (int i = 0; i < 4; i++) {
        pause();
        ratingViewHelper(i, 5 - i, i);
        pause();
      }
    }
  }
}

// md5: 9d3fc3eebdf67664d8b1c211b6d8496f // DO NOT REMOVE THIS LINE
