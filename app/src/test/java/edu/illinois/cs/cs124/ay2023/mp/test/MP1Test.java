package edu.illinois.cs.cs124.ay2023.mp.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.truth.Truth.assertWithMessage;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Data.SUMMARIES;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Data.SUMMARY_COUNT;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Helpers.pause;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Helpers.startMainActivity;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.RecyclerViewMatcher.withRecyclerView;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Views.countRecyclerView;
import static edu.illinois.cs.cs124.ay2023.mp.test.helpers.Views.searchFor;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import edu.illinois.cs.cs124.ay2023.mp.R;
import edu.illinois.cs.cs124.ay2023.mp.models.Summary;
import edu.illinois.cs.cs125.gradlegrader.annotations.Graded;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.annotation.LooperMode;

/*
 * This is the MP1 test suite.
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
 * The MP1 test suite includes no ungraded tests.
 * Note that test0_SummaryComparison and test1_SummaryFilter were generated from the MP reference
 * solution, and as such do not represent what a real-world test suite would typically look like.
 * (It would have fewer examples chosen more carefully.)
 */

@RunWith(Enclosed.class)
public final class MP1Test {
  // Unit tests that don't require simulating the entire app, and usually complete quickly
  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class UnitTests {
    // Private copy of the summaries list, shuffled to improve testing
    private static final List<Summary> SHUFFLED_SUMMARIES;

    // Set up our shuffled list
    static {
      List<Summary> shuffledSummaries = new ArrayList<>(SUMMARIES);
      // Seed the random number generator for reproducibility
      Collections.shuffle(shuffledSummaries, new Random(124));
      // Make the list immutable to detect modifications during testing
      SHUFFLED_SUMMARIES = Collections.unmodifiableList(shuffledSummaries);
    }

    // Helper method for test0_SummaryComparison
    private void summaryComparatorHelper(int firstIndex, int secondIndex, int expected) {
      // first and second should be indices into our shuffled list of summaries
      Summary first = SHUFFLED_SUMMARIES.get(firstIndex);
      assertWithMessage("Invalid summary index: " + firstIndex).that(first).isNotNull();
      Summary second = SHUFFLED_SUMMARIES.get(secondIndex);
      assertWithMessage("Invalid summary index: " + secondIndex).that(second).isNotNull();

      // Test the forward comparison
      int forward = first.compareTo(second);
      if (expected == 0) {
        assertWithMessage("Summaries " + first + " and " + second + " should be compared equal")
            .that(forward)
            .isEqualTo(0);
      } else if (expected < 0) {
        assertWithMessage("Summary " + first + " should be less than " + second)
            .that(forward)
            .isLessThan(0);
      } else {
        assertWithMessage("Summary " + first + " should be greater than " + second)
            .that(forward)
            .isGreaterThan(0);
      }

      // Test the reverse comparison
      int reverse = second.compareTo(first);
      if (expected == 0) {
        assertWithMessage("Summaries " + second + " and " + first + " should be compared equal")
            .that(reverse)
            .isEqualTo(0);
      } else if (expected < 0) {
        assertWithMessage("Summary " + second + " should be less than " + first)
            .that(reverse)
            .isGreaterThan(0);
      } else {
        assertWithMessage("Summary " + second + " should be greater than " + first)
            .that(reverse)
            .isLessThan(0);
      }
    }

    /** Test the summary default comparison (compareTo). */
    @Test(timeout = 1000L)
    @Graded(points = 25, friendlyName = "Summary Comparison")
    public void test0_SummaryComparison() {
      // Test a variety of pairs chosen randomly from our shuffled list of summaries
      // Test self-comparisons
      summaryComparatorHelper(200, 200, 0);
      summaryComparatorHelper(10, 10, 0);
      summaryComparatorHelper(178, 178, 0);
      summaryComparatorHelper(92, 92, 0);
      // Test random pairs
      summaryComparatorHelper(52, 118, 16);
      summaryComparatorHelper(208, 193, -2);
      summaryComparatorHelper(284, 88, -2);
      summaryComparatorHelper(120, 221, -2);
      summaryComparatorHelper(248, 262, -6);
      summaryComparatorHelper(248, 101, 4);
      summaryComparatorHelper(270, 2, -3);
      summaryComparatorHelper(57, 253, -3);
      summaryComparatorHelper(105, 17, 1);
      summaryComparatorHelper(70, 287, -2);
      summaryComparatorHelper(278, 64, 2);
      summaryComparatorHelper(272, 81, -6);
      summaryComparatorHelper(288, 255, -2);
      summaryComparatorHelper(73, 36, 2);
      summaryComparatorHelper(266, 76, 6);
      summaryComparatorHelper(63, 283, 1);
      summaryComparatorHelper(23, 121, -6);
      summaryComparatorHelper(63, 63, 0);
      summaryComparatorHelper(200, 110, -2);
      summaryComparatorHelper(78, 255, -1);
      summaryComparatorHelper(12, 182, -1);
      summaryComparatorHelper(79, 111, -16);
      summaryComparatorHelper(46, 262, -3);
      summaryComparatorHelper(200, 89, -3);
      summaryComparatorHelper(35, 119, -6);
      summaryComparatorHelper(46, 97, -2);
      summaryComparatorHelper(184, 140, -2);
      summaryComparatorHelper(145, 241, -3);
      summaryComparatorHelper(176, 299, 2);
      summaryComparatorHelper(263, 94, 5);
      summaryComparatorHelper(103, 161, 4);
      summaryComparatorHelper(166, 128, -6);
      summaryComparatorHelper(171, 154, -16);
      summaryComparatorHelper(115, 6, -1);
      summaryComparatorHelper(3, 67, 6);
      summaryComparatorHelper(165, 10, -1);
      summaryComparatorHelper(245, 210, -1);
      summaryComparatorHelper(257, 269, -1);
      summaryComparatorHelper(51, 300, 1);
      summaryComparatorHelper(28, 263, 1);
      summaryComparatorHelper(142, 141, -3);
      summaryComparatorHelper(32, 184, -1);
      summaryComparatorHelper(33, 91, -3);
      summaryComparatorHelper(201, 292, 2);
      summaryComparatorHelper(18, 79, 1);
      summaryComparatorHelper(162, 38, -16);
      summaryComparatorHelper(28, 217, 2);
      summaryComparatorHelper(202, 55, 1);
      summaryComparatorHelper(63, 0, -1);
      summaryComparatorHelper(241, 43, -6);
      summaryComparatorHelper(100, 171, -4);
      summaryComparatorHelper(116, 95, 16);
      summaryComparatorHelper(98, 72, -3);
      summaryComparatorHelper(98, 179, -2);
      summaryComparatorHelper(108, 122, 6);
      summaryComparatorHelper(299, 5, -2);
      summaryComparatorHelper(66, 188, -6);
      summaryComparatorHelper(184, 262, -1);
      summaryComparatorHelper(153, 52, -2);
      summaryComparatorHelper(300, 32, 6);
      summaryComparatorHelper(80, 70, 2);
      summaryComparatorHelper(184, 238, -1);
      summaryComparatorHelper(194, 9, 6);
      summaryComparatorHelper(124, 77, -3);
    }

    // Helper method to convert a list of summaries into a list of indices into our
    // shuffled list of summaries
    private List<Integer> summaryListToPositionList(List<Summary> list) {
      return list.stream().map(SHUFFLED_SUMMARIES::indexOf).collect(Collectors.toList());
    }

    // Helper method for test1_SummaryFilter
    private void summaryFilterHelper(
        List<Summary> list, String filter, int size, List<Integer> expectedPositions) {
      // Filter the list using the summary filter
      List<Summary> filteredList = Summary.filter(list, filter);
      // Filtered list should never be null
      assertWithMessage("List filtered with \"" + filter + "\" should not be null")
          .that(filteredList)
          .isNotNull();
      // Filtered list should return a new list
      assertWithMessage("List filter should return a new list")
          .that(filteredList)
          .isNotSameInstanceAs(list);
      // Check the size of the filtered list
      assertWithMessage("List filtered with \"" + filter + "\" should have size " + size)
          .that(filteredList)
          .hasSize(size);
      // Check whether the filtered list includes the right summaries in the correct positions
      if (expectedPositions != null) {
        List<Integer> positions = summaryListToPositionList(filteredList);
        assertWithMessage("List positions filtered with \"" + filter + "\" is not the right size")
            .that(positions)
            .hasSize(expectedPositions.size());
        for (int i = 0; i < positions.size(); i++) {
          assertWithMessage("Summary in incorrect position using filter \"" + filter + "\"")
              .that(positions.get(i))
              .isEqualTo(expectedPositions.get(i));
        }
      }
    }

    /**
     * Test summary filtering.
     *
     * @noinspection SpellCheckingInspection, RedundantSuppression
     */
    @Test(timeout = 1000L)
    @Graded(points = 25, friendlyName = "Summary Filtering")
    public void test1_SummaryFilter() {
      // Test a variety of filtering calls, most on our shuffled list of summaries
      // Test a few searches on the empty list
      summaryFilterHelper(Collections.emptyList(), "", 0, Collections.emptyList());
      summaryFilterHelper(Collections.emptyList(), " ", 0, Collections.emptyList());
      summaryFilterHelper(Collections.emptyList(), " test ", 0, Collections.emptyList());

      // Test a variety of searches on the shuffled list of summaries
      summaryFilterHelper(SHUFFLED_SUMMARIES, " dio Computing Laborat", 1, List.of(45));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "8:", 19, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "advanced", 36, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "f493", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "48n:", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "S 443: Reinforcement Le", 1, List.of(17));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "ctpyright", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "CS 5q9: Distribute", 0, List.of());
      summaryFilterHelper(
          SHUFFLED_SUMMARIES, "S 200: Professional Skills in Information Sci", 1, List.of(113));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "IS 401", 1, List.of(3));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "youth", 5, Arrays.asList(204, 179, 106, 16, 147));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "Computer Graphi", 1, List.of(224));
      summaryFilterHelper(SHUFFLED_SUMMARIES, " 434: Survival Analys", 1, List.of(6));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "8a ", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "uctsres for Dat", 0, List.of());
      summaryFilterHelper(
          SHUFFLED_SUMMARIES, "id", 7, Arrays.asList(250, 129, 72, 305, 175, 239, 124));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "in", 178, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, " Human", 5, Arrays.asList(25, 304, 115, 193, 11));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "&", 24, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "is", 177, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "analysis", 14, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, ": Data Minin", 2, Arrays.asList(240, 196));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "4", 148, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "  5", 149, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, " 546: Advanc", 1, List.of(39));
      summaryFilterHelper(SHUFFLED_SUMMARIES, ":", 306, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "S 527: Netw", 1, List.of(132));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "study", 9, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "C", 267, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "opics in P", 2, Arrays.asList(211, 255));
      summaryFilterHelper(SHUFFLED_SUMMARIES, " is", 177, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, " topics", 39, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "96y Topics", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, " Statisti", 26, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "ied Paracle", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "computer", 21, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, " Advanced Topics ", 22, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "systems", 15, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, " cloud", 1, List.of(237));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "527:", 3, Arrays.asList(148, 132, 282));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "543:", 2, Arrays.asList(248, 262));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "topics ", 39, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "d", 178, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "i", 295, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "iatural", 0, List.of());
      summaryFilterHelper(
          SHUFFLED_SUMMARIES, "j", 8, Arrays.asList(223, 256, 182, 218, 272, 122, 150, 141));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "n", 266, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "o", 244, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "cs", 169, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "57t:", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "q", 4, Arrays.asList(23, 106, 249, 230));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "451:", 1, List.of(219));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "ms & Model", 1, List.of(82));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "t", 289, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, ": Programminv", 0, List.of());
      summaryFilterHelper(
          SHUFFLED_SUMMARIES, "x", 8, Arrays.asList(11, 275, 68, 104, 52, 86, 250, 252));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "thelry", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "information", 39, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "STAT 430: Topics in Appli", 1, List.of(260));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "d Advertisi", 1, List.of(263));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "the iSchool with a Huma", 1, List.of(11));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "thv", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "data", 36, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "Numerical Methods for PD", 1, List.of(171));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "480:", 1, List.of(202));
      summaryFilterHelper(SHUFFLED_SUMMARIES, " 429: Time Series Anal", 1, List.of(226));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "trodu", 15, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "yst", 18, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "oductidn ", 0, List.of());
      summaryFilterHelper(
          SHUFFLED_SUMMARIES, "ng for Information P", 3, Arrays.asList(169, 200, 53));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "3: Advanced St", 1, List.of(116));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "velopment M", 1, List.of(1));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "STAT 420: Met", 1, List.of(24));
      summaryFilterHelper(SHUFFLED_SUMMARIES, " 173: Disc ", 1, List.of(231));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "deduction", 1, List.of(173));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "55: Numerical M", 1, List.of(171));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "cultures", 2, Arrays.asList(251, 46));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "437:", 2, Arrays.asList(44, 127));
      summaryFilterHelper(SHUFFLED_SUMMARIES, " 49: S", 1, List.of(76));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "577: ", 1, List.of(196));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "510m", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "he Art of ", 1, List.of(201));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "CS 574: Rondomized ", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "aspects", 1, List.of(33));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "al Network An", 1, List.of(158));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "learning", 16, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "477: ", 2, Arrays.asList(1, 227));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "dmpirical", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "ional Strategie", 1, List.of(230));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "numerical", 4, Arrays.asList(257, 295, 171, 20));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "T 528: Advanced", 1, List.of(167));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "385:", 1, List.of(60));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "yg ", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "oe", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "design", 16, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "tle Bits to Bi", 1, List.of(124));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "algorithmic", 2, Arrays.asList(246, 289));
      summaryFilterHelper(SHUFFLED_SUMMARIES, " 400: Collo", 1, List.of(23));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "uction to Biog", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "signnls", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "STAT 390: ", 1, List.of(305));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "cybtrsecurity", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "resources", 2, Arrays.asList(19, 147));
      summaryFilterHelper(SHUFFLED_SUMMARIES, " 524:tConcurrent Pr", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "teen", 1, List.of(244));
      summaryFilterHelper(SHUFFLED_SUMMARIES, " advanced ", 36, null);
      summaryFilterHelper(SHUFFLED_SUMMARIES, "S 311: Histo", 1, List.of(36));
      summaryFilterHelper(SHUFFLED_SUMMARIES, "o568: Us", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "fter Sci", 0, List.of());
      summaryFilterHelper(SHUFFLED_SUMMARIES, "h Rese", 0, List.of());
    }
  }

  // Integration tests that require simulating the entire app, and are usually slower
  @RunWith(AndroidJUnit4.class)
  @LooperMode(LooperMode.Mode.PAUSED)
  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class IntegrationTests {
    /** Test summary view to make sure that the correct courses are displayed in the right order. */
    @Test(timeout = 10000L)
    @Graded(points = 20, friendlyName = "Summary View")
    public void test2_SummaryView() {
      startMainActivity(
          activity -> {
            // Check that the right number of summaries are displayed
            onView(ViewMatchers.withId(R.id.recycler_view)).check(countRecyclerView(SUMMARY_COUNT));

            // Check that full summary titles are shown, and that the the order is correct
            onView(withRecyclerView(R.id.recycler_view).atPosition(0))
                .check(matches(hasDescendant(withText("CS 100: Computer Science Orientation"))));
            onView(withRecyclerView(R.id.recycler_view).atPosition(1))
                .check(matches(hasDescendant(withSubstring("IS 100"))));
            onView(withRecyclerView(R.id.recycler_view).atPosition(2))
                .check(matches(hasDescendant(withSubstring("STAT 100"))));

            // Check a pair that won't sort properly just based on number
            onView(withId(R.id.recycler_view)).perform(scrollToPosition(73));
            onView(withRecyclerView(R.id.recycler_view).atPosition(73))
                .check(matches(hasDescendant(withSubstring("CS 403"))));
            onView(withId(R.id.recycler_view)).perform(scrollToPosition(74));
            onView(withRecyclerView(R.id.recycler_view).atPosition(74))
                .check(matches(hasDescendant(withSubstring("IS 403"))));

            // Check the endpoint
            onView(withId(R.id.recycler_view)).perform(scrollToPosition(SUMMARY_COUNT - 1));
            onView(withRecyclerView(R.id.recycler_view).atPosition(SUMMARY_COUNT - 1))
                .check(matches(hasDescendant(withText("STAT 599: Thesis Research"))));
          });
    }

    /**
     * Test search interaction to make sure that the correct courses are shown when the search
     * feature is used.
     */
    @Test(timeout = 10000L)
    @Graded(points = 20, friendlyName = "Filtered View")
    public void test3_FilteredView() {
      startMainActivity(
          activity -> {
            // Check that the right number of courses are displayed initially
            onView(withId(R.id.recycler_view)).check(countRecyclerView(SUMMARY_COUNT));

            // Make sure blank searches work
            // Some manual delay is required for these tests to run reliably
            onView(withId(R.id.search)).perform(searchFor("  "));
            pause();
            onView(withId(R.id.recycler_view)).check(countRecyclerView(SUMMARY_COUNT));

            // Illinois has no super boring courses!
            onView(withId(R.id.search)).perform(searchFor("Super Boring Course"));
            pause();
            onView(withId(R.id.recycler_view)).check(countRecyclerView(0));

            // CS 124 should return one result
            onView(withId(R.id.search)).perform(searchFor("CS 124"));
            pause();
            onView(withId(R.id.recycler_view)).check(countRecyclerView(1));

            // study matches several courses
            onView(withId(R.id.search)).perform(searchFor("study"));
            pause();
            onView(withId(R.id.recycler_view)).check(countRecyclerView(9));
            onView(withRecyclerView(R.id.recycler_view).atPosition(2))
                .check(matches(hasDescendant(withText("IS 189: Independent Study"))));
          });
    }
  }
}

// md5: 8c6d7349de029630713a95ac6309320f // DO NOT REMOVE THIS LINE
