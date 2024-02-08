package edu.illinois.cs.cs124.ay2023.mp.test.helpers;

import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.widget.RatingBar;
import android.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.ViewMatchers;
import org.hamcrest.Matcher;

/*
 * This file contains helper code used by the test suites.
 * You should not need to modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 *
 * The helper methods in this file assist with checking UI components during testing.
 */
public class Views {
  /** Count the number of items in a RecyclerView. */
  public static ViewAssertion countRecyclerView(int expected) {
    return (v, noViewFoundException) -> {
      if (noViewFoundException != null) {
        throw noViewFoundException;
      }
      RecyclerView view = (RecyclerView) v;
      RecyclerView.Adapter<?> adapter = view.getAdapter();
      assertWithMessage("View adapter should not be null").that(adapter).isNotNull();
      assertWithMessage("Adapter should have " + expected + " items")
          .that(adapter.getItemCount())
          .isEqualTo(expected);
    };
  }

  /** Search for text in a SearchView component. */
  public static ViewAction searchFor(String query) {
    return searchFor(query, false);
  }

  /** ViewAction used to support searching for text in a SearchView component. */
  public static ViewAction searchFor(String query, boolean submit) {
    return new ViewAction() {
      @Override
      public Matcher<View> getConstraints() {
        return allOf(isDisplayed());
      }

      @Override
      public String getDescription() {
        if (submit) {
          return "Set query to " + query + " and submit";
        } else {
          return "Set query to " + query + " but don't submit";
        }
      }

      @Override
      public void perform(UiController uiController, View view) {
        SearchView v = (SearchView) view;
        v.setQuery(query, submit);
      }
    };
  }

  /** ViewAssertion for testing RatingBar components. */
  public static ViewAssertion hasRating(int rating) {
    return (view, noViewFoundException) -> {
      assertWithMessage("Should have found view").that(noViewFoundException).isNull();
      assertWithMessage("View should be a RatingBar").that(view).isInstanceOf(RatingBar.class);

      RatingBar ratingBar = (RatingBar) view;
      assertWithMessage("RatingBar should have rating " + rating)
          .that(ratingBar.getRating())
          .isEqualTo(rating);
    };
  }

  /** ViewAction for modifying RatingBar components. */
  public static ViewAction setRating(int rating) {
    return actionWithAssertions(
        new ViewAction() {
          @Override
          public Matcher<View> getConstraints() {
            return ViewMatchers.isAssignableFrom(RatingBar.class);
          }

          @Override
          public String getDescription() {
            return "Custom view action to set rating.";
          }

          @Override
          public void perform(UiController uiController, View view) {
            RatingBar ratingBar = (RatingBar) view;
            ratingBar.setRating(rating);
          }
        });
  }
}

// md5: 130e63c8ec48bd260b20e5231f09ca47 // DO NOT REMOVE THIS LINE
