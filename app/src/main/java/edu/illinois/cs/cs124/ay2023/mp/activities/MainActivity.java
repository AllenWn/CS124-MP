package edu.illinois.cs.cs124.ay2023.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.cs.cs124.ay2023.mp.R;
import edu.illinois.cs.cs124.ay2023.mp.adapters.SummaryListAdapter;
import edu.illinois.cs.cs124.ay2023.mp.application.CourseableApplication;
import edu.illinois.cs.cs124.ay2023.mp.helpers.ResultMightThrow;
import edu.illinois.cs.cs124.ay2023.mp.models.Summary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/** Main activity showing the course summary list. */
public final class MainActivity extends AppCompatActivity
    implements SearchView.OnQueryTextListener {
  /** Tag to identify the MainActivity in the logs. */
  @SuppressWarnings("unused")
  private static final String TAG = MainActivity.class.getSimpleName();

  /** List of summaries received from the server, initially empty. */
  private List<Summary> summaries = Collections.emptyList();

  private List<Summary> summariesCopy = Collections.emptyList();

  /** Adapter that connects our list of summaries with the list displayed on the display. */
  private SummaryListAdapter listAdapter;

  private List<Summary> filteredList = new ArrayList<>();

  /**
   * Called when this activity is created.
   *
   * <p>This method is called when the activity is first launched, and at points later if the app is
   * terminated to save memory. For more details, see consult the Android activity lifecycle
   * documentation.
   *
   * @param unused saved instance state, currently unused and always empty or null
   */
  @Override
  protected void onCreate(@Nullable Bundle unused) {
    super.onCreate(unused);

    // Load this activity's layout and set the title
    setContentView(R.layout.activity_main);
    setTitle("Search Courses");

    // Setup the list adapter for the list of summaries
    //    listAdapter = new SummaryListAdapter(summaries, this);
    listAdapter =
        new SummaryListAdapter(
            summaries,
            this,
            summary -> {
              Intent courseIntent = new Intent(this, CourseActivity.class);
              String summaryString;
              try {
                ObjectMapper objectmapper = new ObjectMapper();

                summaryString = objectmapper.writeValueAsString(summary);

                courseIntent.putExtra("summary", summaryString);
                System.out.println(courseIntent);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
              startActivity(courseIntent);
            });

    // Add the list to the layout
    RecyclerView recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(listAdapter);

    // Initiate a request for the summary list
    CourseableApplication application = (CourseableApplication) getApplication();
    application.getClient().getSummary(summaryCallback);

    // Register this component as a callback for changes to the search view component shown above
    // the summary list. We'll use these events to initiate summary list filtering.
    SearchView searchView = findViewById(R.id.search);
    searchView.setOnQueryTextListener(this);

    // Register our toolbar
    setSupportActionBar(findViewById(R.id.toolbar));
  }

  /** Callback used to update the list of summaries during onCreate. */
  private final Consumer<ResultMightThrow<List<Summary>>> summaryCallback =
      (result) -> {
        try {
          summaries = result.getValue();
          summariesCopy = result.getValue();
          Collections.sort(summaries);
          listAdapter.setSummaries(summaries);
        } catch (Exception e) {
          e.printStackTrace();
          Log.e(TAG, "getSummary threw an exception: " + e);
        }
      };

  /**
   * Callback fired when the user edits the text in the search query box.
   *
   * <p>This fires every time the text in the search bar changes. We'll handle this by updating the
   * list of visible summaries.
   *
   * @param query the text to use to filter the summary list
   * @return true because we handled the action
   */
  @Override
  public boolean onQueryTextChange(@NonNull String query) {
    summariesCopy = Summary.filter(summaries, query);
    listAdapter.setSummaries(summariesCopy);
    return true;
  }

  /**
   * Callback fired when the user submits a search query.
   *
   * <p>This would correspond to them hitting enter or a submit button. Because we update the list
   * on each change to the search value, we do not handle this callback.
   *
   * @param unused current query text
   * @return false because we did not handle this action
   */
  @Override
  public boolean onQueryTextSubmit(@NonNull String unused) {
    return false;
  }
}
