package edu.illinois.cs.cs124.ay2023.mp.application;

import android.app.Application;
import android.os.Build;
import edu.illinois.cs.cs124.ay2023.mp.network.Client;
import edu.illinois.cs.cs124.ay2023.mp.network.Server;

/**
 * Application class for the Courseable app.
 *
 * <p>Starts the development server and creates the course API client.
 */
public final class CourseableApplication extends Application {
  /** Course API server port. You can change this if needed. */
  public static final int DEFAULT_SERVER_PORT = 8023;

  /** Course API server URL. */
  public static final String SERVER_URL = "http://localhost:" + DEFAULT_SERVER_PORT;

  /** Course API client created during application startup. */
  private Client client;

  /** Start the app, including the server and client. */
  @Override
  public void onCreate() {
    super.onCreate();

    // Start the API server
    if (Build.FINGERPRINT.equals("robolectric")) {
      Server.start();
    } else {
      // In a new thread if we're not testing
      new Thread() {
        @Override
        public void run() {
          Server.start();
        }
      }.start();
    }

    // Start the API client
    client = Client.start();
  }

  /**
   * Retrieve the course API client instance for this app.
   *
   * @return the course API client instance.
   */
  public Client getClient() {
    return client;
  }
}
