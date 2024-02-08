package edu.illinois.cs.cs124.ay2023.mp.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertWithMessage;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Data.SUMMARY_COUNT;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.HTTP.getAPIClient;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.HTTP.testClient;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.HTTP.testServerGet;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Helpers.configureLogging;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Helpers.startMainActivity;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.fasterxml.jackson.databind.JsonNode;
import edu.illinois.cs.cs124.ay2023.mp.R;
import edu.illinois.cs.cs124.ay2023.mp.models.Summary;
import edu.illinois.cs.cs124.ay2023.mp.network.Client;
import edu.illinois.cs.cs124.ay2023.mp.network.Server;
import edu.illinois.cs.cs125.gradlegrader.annotations.Graded;
import java.io.IOException;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.annotation.LooperMode;

/*
 * This is the MP0 test suite.
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
 * Our test suites will also include a mixture of graded and ungraded tests.
 * The graded tests are marking with the `@Graded` annotation which contains a point total.
 * Ungraded tests do not have this annotation.
 * Some ungraded tests will work immediately, and are there to help you pinpoint regressions:
 * meaning changes that you made that might have broken things that were working previously.
 * The ungraded tests below were actually written by me (Geoff) during MP development.
 * Other ungraded tests are simply there to help your development process.
 */

@RunWith(Enclosed.class)
public final class MP0Test {
  // Unit tests that don't require simulating the entire app, and usually complete quickly
  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class UnitTests {

    // Static blocks run when the static class is created, very much like a constructor
    static {
      // Start the API server
      Server.start();
    }

    // THIS TEST SHOULD WORK
    // Test whether the GET /summary/ server route works properly
    @Test(timeout = 1000L)
    public void test0_SummaryRoute() throws IOException {
      JsonNode nodes = testServerGet("/summary/");
      assertWithMessage("Summary list is not the right size").that(nodes).hasSize(SUMMARY_COUNT);
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

    // Graded test that the activity displays the correct title
    @Test(timeout = 10000L)
    @Graded(points = 90, friendlyName = "Test MainActivity Title")
    public void test1_ActivityTitle() {
      // Start the MainActivity
      startMainActivity(
          activity -> {
            // Once the activity starts, check that it has the correct title
            assertWithMessage("MainActivity has wrong title")
                .that(activity.getTitle())
                .isEqualTo("Search Courses");
          });
    }

    // THIS TEST SHOULD WORK
    // Test that the API client retrieves the summary list correctly
    @Test(timeout = 10000L)
    public void test2_ClientGetSummary() throws Exception {
      // Create an API client for testing
      Client apiClient = getAPIClient();

      // Retrieve the list of course summaries using our API client
      List<Summary> summaries = testClient(apiClient::getSummary);

      // Check that the List<Summary> has the correct size
      assertWithMessage("Summary list is not the right size")
          .that(summaries)
          .hasSize(SUMMARY_COUNT);
    }

    // THIS TEST SHOULD WORK
    // Test that the main activity displays the right number of summaries after launch
    @Test(timeout = 10000L)
    public void test3_ActivitySummaryCount() {
      // Start the MainActivity
      startMainActivity(
          activity -> {
            // Once the activity starts, check that it displays the correct number of summaries
            onView(withId(R.id.recycler_view)).check((v, m) -> v.hasFocus());
          });
    }
  }
}

// md5: d124143a9a48792fe5a677653372708a // DO NOT REMOVE THIS LINE
