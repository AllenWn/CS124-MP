package edu.illinois.cs.cs124.ay2023.mp.test.helpers;

import static android.os.Looper.getMainLooper;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import edu.illinois.cs.cs124.ay2023.mp.activities.MainActivity;
import org.robolectric.shadows.ShadowLog;

/*
 * This file contains helper code used by the test suites.
 * You should not need to modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 */
public class Helpers {

  // Helper method to start the MainActivity
  public static void startMainActivity(ActivityScenario.ActivityAction<MainActivity> action) {
    try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.RESUMED);
      pause();
      scenario.onActivity(action);
    }
  }

  // Helper method to start an Activity using an Intent
  public static <T extends Activity> void startActivity(
      Intent intent, ActivityScenario.ActivityAction<T> action) {
    try (ActivityScenario<T> scenario = ActivityScenario.launch(intent)) {
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.RESUMED);
      pause();
      scenario.onActivity(action);
    }
  }

  // Pause helpers to improve the stability of our Robolectric tests
  public static void pause(int length) {
    try {
      shadowOf(getMainLooper()).runToEndOfTasks();
      Thread.sleep(length);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  // Default pause override
  public static void pause() {
    pause(100);
  }

  // Set up logging properly for testing
  public static void configureLogging() {
    if (System.getenv("OFFICIAL_GRADING") == null) {
      ShadowLog.setLoggable("LifecycleMonitor", Log.WARN);
      ShadowLog.stream = new FilteringPrintStream();
    }
  }
}

// md5: 5ca8a749e23149fb27eb0e88777ca9b8 // DO NOT REMOVE THIS LINE
