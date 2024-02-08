package edu.illinois.cs.cs124.ay2023.mp.activities;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.cs.cs124.ay2023.mp.R;
import edu.illinois.cs.cs124.ay2023.mp.application.CourseableApplication;
import edu.illinois.cs.cs124.ay2023.mp.helpers.ResultMightThrow;
import edu.illinois.cs.cs124.ay2023.mp.models.Course;
import edu.illinois.cs.cs124.ay2023.mp.models.Rating;
import edu.illinois.cs.cs124.ay2023.mp.models.Summary;
import java.util.function.Consumer;

public class CourseActivity extends AppCompatActivity {

  private RatingBar ratingBar;
  private Summary summary;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_course);
    Intent courseIntent = getIntent();
    ObjectMapper mapper = new ObjectMapper();
    ratingBar = findViewById(R.id.rating); // 假设您的RatingBar的ID是rating_bar
    String sumString = courseIntent.getStringExtra("summary");
    String summaryJson = getIntent().getStringExtra("summary");
    TextView descriptionTextView = findViewById(R.id.description);
//    try {
//      Summary summaryConversion = mapper.readValue(sumString, Summary.class);
//      CourseableApplication application = (CourseableApplication) getApplication();
//
//      // 请求评分数据
//      requestRatingData(application, summaryConversion);
//
//      // 请求课程详细信息
//      requestCourseDetails(application, summaryConversion);
//    } catch (JsonProcessingException e) {
//      e.printStackTrace();
//      Log.e(TAG, "Error parsing summary JSON: " + e);
//    }
//
//    private void requestRatingData(CourseableApplication application, Summary summaryConversion) {
//      application.getClient().getRating(
//          summaryConversion,
//          (ratingResult) -> handleRatingResult(ratingResult)
//      );
//    }
//
//    private void handleRatingResult(ResultMightThrow<Rating> ratingResult) {
//      try {
//        Rating rating = ratingResult.getValue();
//        runOnUiThread(() -> ratingBar.setRating(rating.getRating()));
//      } catch (Exception e) {
//        e.printStackTrace();
//        Log.e(TAG, "Error in getRating: " + e);
//      }
//    }
//
//    private void requestCourseDetails
//    (CourseableApplication application, Summary summaryConversion) {
//      final Consumer<ResultMightThrow<Course>> courseCallBack = (result) -> {
//        try {
//          Course course = result.getValue();
//          String courseDescription = course.getDescription();
//          runOnUiThread(() -> descriptionTextView.setText(courseDescription));
//        } catch (Exception e) {
//          e.printStackTrace();
//          Log.e(TAG, "Error in getCourse: " + e);
//        }
//      };
//      application.getClient().getCourse(summaryConversion, courseCallBack);
//    }
    try {
      Summary summaryConversion = mapper.readValue(sumString, Summary.class);
      // Initiate a request for the course
      CourseableApplication application = (CourseableApplication) getApplication();
      runOnUiThread(
          () -> {
            application
                .getClient()
                .getRating(
                    summaryConversion,
                    (ratingCallBack) -> {
                      Rating rating = ratingCallBack.getValue();
                      ratingBar.setRating(rating.getRating());
                    });
          });
      final Consumer<ResultMightThrow<Course>> courseCallBack =
          (result) -> {
            try {
              Course course = result.getValue();
              String courseString = course.toString();
              String courseDescription = course.getDescription();
              descriptionTextView.setText(courseString + courseDescription);
            } catch (Exception e) {
              e.printStackTrace();
              Log.e(TAG, "getCourse threw an exception: " + e);
            }
          };
      final Consumer<ResultMightThrow<Rating>> ratingCallBack =
          (result) -> {
            try {
              runOnUiThread(
                  () -> {
                    Rating rating = result.getValue();
                    ratingBar.setRating(rating.getRating());
                  });
            } catch (Exception e) {
              e.printStackTrace();
              Log.e(TAG, "getRating threw an exception: " + e);
            }
          };
      Summary finalS = summaryConversion;
      ratingBar.setOnRatingBarChangeListener(
          (ratingBar1, rating, fromUser) -> {
            Rating tempRating = new Rating(finalS, rating);
            application.getClient().postRating(tempRating, ratingCallBack);
          });
      // Course update
      application.getClient().getCourse(summaryConversion, courseCallBack);
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}


