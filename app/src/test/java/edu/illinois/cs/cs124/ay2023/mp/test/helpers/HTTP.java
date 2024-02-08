package edu.illinois.cs.cs124.ay2023.mp.test.helpers;

import static com.google.common.truth.Truth.assertWithMessage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import edu.illinois.cs.cs124.ay2023.mp.application.CourseableApplication;
import edu.illinois.cs.cs124.ay2023.mp.helpers.ResultMightThrow;
import edu.illinois.cs.cs124.ay2023.mp.network.Client;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/*
 * This file contains helper code used by the test suites.
 * You should not need to modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 *
 * The helper methods in this file assist with testing the API server and client.
 */
public class HTTP {
  // Private HTTP client for testing
  private static final OkHttpClient httpClient = new OkHttpClient();

  /** Test a GET call to the backend API server . */
  @SuppressWarnings("unchecked")
  public static <T> T testServerGet(String route, int responseCode, Object bodyClass)
      throws IOException {
    // Create the request
    Request request = new Request.Builder().url(CourseableApplication.SERVER_URL + route).build();

    // Make the request
    // try-with-resources ensures the response is cleaned up properly
    try (Response response = httpClient.newCall(request).execute()) {

      if (responseCode == HttpURLConnection.HTTP_OK) {
        // The request should have succeeded
        assertWithMessage("GET request for " + route + " should have succeeded")
            .that(response.code())
            .isEqualTo(HttpURLConnection.HTTP_OK);
      } else {
        // The request should have failed the the correct code
        assertWithMessage(
                "GET request for " + route + " should have failed with code " + responseCode)
            .that(response.code())
            .isEqualTo(responseCode);
        return null;
      }

      // The response body should not be null
      ResponseBody body = response.body();
      assertWithMessage("GET response for " + route + " body should not be null")
          .that(body)
          .isNotNull();

      // Deserialize based on type passed to the method
      if (bodyClass == null) {
        return (T) Data.OBJECT_MAPPER.readTree(body.string());
      } else if (bodyClass instanceof Class<?> it) {
        return (T) Data.OBJECT_MAPPER.readValue(body.string(), it);
      } else if (bodyClass instanceof TypeReference<?> it) {
        return (T) Data.OBJECT_MAPPER.readValue(body.string(), it);
      } else {
        throw new IllegalStateException("Bad deserialization class passed to testServerGet");
      }
    }
  }

  // testServerGet overrides
  public static <T> T testServerGet(String route, Object klass) throws IOException {
    return testServerGet(route, HttpURLConnection.HTTP_OK, klass);
  }

  public static <T> T testServerGet(String route, int responseCode) throws IOException {
    return testServerGet(route, responseCode, null);
  }

  public static JsonNode testServerGet(String route) throws IOException {
    return testServerGet(route, HttpURLConnection.HTTP_OK, null);
  }

  /** Test a POST to the backend API server. */
  @SuppressWarnings("unchecked")
  public static <T> T testServerPost(
      String route, int responseCode, Object requestBody, Object responseBodyClass)
      throws IOException {
    // Create the request
    Request request =
        new Request.Builder()
            .url(CourseableApplication.SERVER_URL + route)
            .post(
                RequestBody.create(
                    Data.OBJECT_MAPPER.writeValueAsString(requestBody),
                    MediaType.parse("application/json")))
            .build();

    // Make the request
    // try-with-resources ensures the response is cleaned up properly
    try (Response response = httpClient.newCall(request).execute()) {

      if (responseCode == HttpURLConnection.HTTP_OK) {
        // The request should have succeeded
        assertWithMessage(
                "POST request for " + route + " should have succeeded but was " + response.code())
            .that(response.code())
            .isEqualTo(HttpURLConnection.HTTP_OK);
      } else {
        // The request should have failed the the correct code
        assertWithMessage(
                "POST request for " + route + " should have failed with code " + responseCode)
            .that(response.code())
            .isEqualTo(responseCode);
        return null;
      }

      // The response body should not be null
      ResponseBody responseBody = response.body();
      assertWithMessage("POST response for " + route + " body should not be null")
          .that(responseBody)
          .isNotNull();

      // Deserialize based on type passed to the method
      if (responseBodyClass == null) {
        return (T) Data.OBJECT_MAPPER.readTree(responseBody.string());
      } else if (responseBodyClass instanceof Class<?> it) {
        return (T) Data.OBJECT_MAPPER.readValue(responseBody.string(), it);
      } else if (responseBodyClass instanceof TypeReference<?> it) {
        return (T) Data.OBJECT_MAPPER.readValue(responseBody.string(), it);
      } else {
        throw new IllegalStateException("Bad deserialization class passed to testServerPost");
      }
    }
  }

  // testServerPost overrides
  public static <T> T testServerPost(String route, Object requestBody, Object responseBodyClass)
      throws IOException {
    return testServerPost(route, HttpURLConnection.HTTP_OK, requestBody, responseBodyClass);
  }

  public static <T> T testServerPost(String route, Object requestBody, int responseCode)
      throws IOException {
    return testServerPost(route, responseCode, requestBody, null);
  }

  // Private API client for testing
  private static Client apiClient = null;

  // Retrieve the client, starting if needed
  public static Client getAPIClient() {
    if (apiClient == null) {
      apiClient = Client.start();
    }
    return apiClient;
  }

  /** Helper method for API client testing. */
  public static <T> T testClient(Consumer<Consumer<ResultMightThrow<T>>> method) throws Exception {
    // Ensure the client started up properly
    assertWithMessage("Client should be connected").that(apiClient.getConnected()).isTrue();

    // A CompletableFuture allows us to wait for the result of an asynchronous call
    CompletableFuture<ResultMightThrow<T>> completableFuture = new CompletableFuture<>();

    // When the client call returns, it causes the CompletableFuture to complete
    method.accept(completableFuture::complete);

    // Wait for the CompletableFuture to complete
    ResultMightThrow<T> result = completableFuture.get();

    // Throw if the call threw
    if (result.getException() != null) {
      throw result.getException();
    }

    // Shouldn't ever happen, but doesn't hurt to check
    assertWithMessage("Client call expected to succeed returned null")
        .that(result.getValue())
        .isNotNull();

    return result.getValue();
  }
}

// md5: 73f29bc8b6e4e52feedc49f2f57f5ae7 // DO NOT REMOVE THIS LINE
