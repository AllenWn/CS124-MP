package edu.illinois.cs.cs124.ay2023.mp.network;

import static edu.illinois.cs.cs124.ay2023.mp.helpers.Helpers.CHECK_SERVER_RESPONSE;
import static edu.illinois.cs.cs124.ay2023.mp.helpers.Helpers.OBJECT_MAPPER;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
import edu.illinois.cs.cs124.ay2023.mp.application.CourseableApplication;
// import edu.illinois.cs.cs124.ay2023.mp.models.Course;
import edu.illinois.cs.cs124.ay2023.mp.models.Course;
import edu.illinois.cs.cs124.ay2023.mp.models.Rating;
import edu.illinois.cs.cs124.ay2023.mp.models.Summary;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
// import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Development course API server.
 *
 * <p>Normally you would run this server on another machine, which the client would connect to over
 * the internet. For the sake of development, we're running the server alongside the app on the same
 * device. However, all communication between the course API client and course API server is still
 * done using the HTTP protocol. Meaning that it would be straightforward to move this code to an
 * actual server, which could provide data for all course API clients.
 */
public final class Server extends Dispatcher {
  private Set<String> courseKeys;

  /** List of summaries as a JSON string. */
  private final String summariesJSON;

  /** Helper method to create a 200 HTTP response with a body. */
  private MockResponse makeOKJSONResponse(@NonNull String body) {
    return new MockResponse()
        .setResponseCode(HttpURLConnection.HTTP_OK)
        .setBody(body)
        .setHeader("Content-Type", "application/json; charset=utf-8");
  }

  /** Helper value storing a 404 Not Found response. */
  private static final MockResponse HTTP_NOT_FOUND =
      new MockResponse()
          .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
          .setBody("404: Not Found");

  /** Helper value storing a 400 Bad Request response. */
  private static final MockResponse HTTP_BAD_REQUEST =
      new MockResponse()
          .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
          .setBody("400: Bad Request");

  /** GET the JSON with the list of course summaries. */
  private MockResponse getSummaries() {
    return makeOKJSONResponse(summariesJSON);
  }

  private MockResponse getCourse(String path) {
    String json =
        new Scanner(Server.class.getResourceAsStream("/courses.json"), "UTF-8")
            .useDelimiter("\\A")
            .next();
    String[] search = path.split("/");
    if (search.length != 2 * 2 || !search[1].equals("course")) {
      return HTTP_BAD_REQUEST;
    }

    // List<Summary> summaries = new ArrayList<>();
    try {
      JsonNode nodes = OBJECT_MAPPER.readTree(json);
      for (JsonNode node : nodes) {
        Summary summary = OBJECT_MAPPER.readValue(node.toString(), Summary.class);
        if (summary.getSubject().equals(search[2]) && summary.getNumber().equals(search[3])) {
          return makeOKJSONResponse(node.toPrettyString());
        }
      }
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
    return HTTP_NOT_FOUND;
  }

  private float ratedNum = -1.0f;

  //  private MockResponse getRating(String path) {
  //    try {
  //      String courseData =
  //          new Scanner(Server.class.getResourceAsStream("/courses.json"), "UTF-8")
  //              .useDelimiter("\\A")
  //              .next();
  //      JsonNode courses = OBJECT_MAPPER.readTree(courseData);
  //      String[] urlParts = path.split("/");
  //      if (urlParts.length != 2 * 2) {
  //        return HTTP_BAD_REQUEST;
  //      }
  //
  //      for (JsonNode courseNode : courses) {
  //        String subj = courseNode.get("subject").asText().trim();
  //        String num = courseNode.get("number").asText().trim();
  //        if (urlParts[2].trim().equals(subj) && urlParts[3].trim().equals(num)) {
  //          // Constructing a new Rating object each time a rating is requested.
  //          Rating rating = new Rating(new Summary(subj, num, ""), ratedNum);
  //          return makeOKJSONResponse(OBJECT_MAPPER.writeValueAsString(rating));
  //        }
  //      }
  //      return HTTP_NOT_FOUND;
  //    } catch (IOException e) {
  //      return HTTP_BAD_REQUEST;
  //    }
  //  }
  //  private MockResponse getRating(String path) {
  //    // 解析请求路径
  //    String[] urlParts = path.split("/");
  //    if (urlParts.length != 2 * 2) {
  //      return HTTP_BAD_REQUEST;
  //    }
  //
  //    String subject = urlParts[2].trim();
  //    String number = urlParts[3].trim();
  //
  //    // 尝试从ratingMapper中获取评分
  //    String courseKey = subject + "/" + number;
  //    Float ratingVal = ratingMapper.getOrDefault(courseKey, Rating.NOT_RATED);
  //
  //    // 创建Rating对象并返回
  //    Summary summary = new Summary(subject, number, ""); // 假设存在相应的label
  //    Rating rating = new Rating(summary, ratingVal);
  //
  //    try {
  //      return makeOKJSONResponse(OBJECT_MAPPER.writeValueAsString(rating));
  //    } catch (JsonProcessingException e) {
  //      return HTTP_BAD_REQUEST;
  //    }
  //  }

  private MockResponse getRating(String path) {
    try {
      String courseData =
          new Scanner(Server.class.getResourceAsStream("/courses.json"), "UTF-8")
              .useDelimiter("\\A")
              .next();
      JsonNode courses = OBJECT_MAPPER.readTree(courseData);
      String[] urlParts = path.split("/");
      if (urlParts.length != 2 * 2) {
        return HTTP_BAD_REQUEST;
      }

      for (JsonNode courseNode : courses) {
        Course course = OBJECT_MAPPER.readValue(courseNode.toString(), Course.class);
        Summary summary = OBJECT_MAPPER.readValue(courseNode.toString(), Summary.class);
        Rating rating = new Rating(summary, -1.0f);
        String ratingToString = OBJECT_MAPPER.writeValueAsString(rating);
        String subj = courseNode.get("subject").asText().trim();
        String num = courseNode.get("number").asText().trim();
        if (urlParts[2].trim().equals(subj) && urlParts[3].trim().equals(num)) {
          if (comingFromPostRating) {
            if (ratingMapper.get(summary.toString()) != null) {
              rating.setRating(ratingMapper.get(summary.toString()));
              ratingToString = OBJECT_MAPPER.writeValueAsString(rating);
            }
          }
          return makeOKJSONResponse(ratingToString);
        }
      }
      return HTTP_NOT_FOUND;
    } catch (IOException e) {
      return HTTP_BAD_REQUEST;
    }
  }

  private boolean comingFromPostRating = false;
  private static Map<String, Float> ratingMapper = new HashMap<>();

  //  private static final Map<String, Float> ratings = new HashMap<>();
  //
  //  private MockResponse postRating(String subject, String number, String body) {
  //    try {
  //      JsonNode requestBody = OBJECT_MAPPER.readTree(body);
  //      float newRating = requestBody.get("rating").floatValue();
  //      String courseKey = subject + number;
  //      ratings.put(courseKey, newRating);
  //      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK);
  //    } catch (Exception e) {
  //      return HTTP_BAD_REQUEST;
  //    }
  //  }

  //  private MockResponse postRating(RecordedRequest request) {
  //    String body = request.getBody().readUtf8();
  //    try {
  //      Rating rating = OBJECT_MAPPER.readValue(body, Rating.class);
  //      String subject = rating.getSummary().getSubject().trim();
  //      String number = rating.getSummary().getNumber().trim();
  //
  //      // 从文件中读取课程数据
  //      String courseData =
  //          new Scanner(Server.class.getResourceAsStream("/courses.json"), "UTF-8")
  //              .useDelimiter("\\A")
  //              .next();
  //      JsonNode courses = OBJECT_MAPPER.readTree(courseData);
  //      boolean courseFound = false;
  //      ObjectNode updatedCourseNode = null;
  //
  //      for (JsonNode courseNode : courses) {
  //        if (courseNode.get("subject").asText().trim().equals(subject)
  //            && courseNode.get("number").asText().trim().equals(number)) {
  //          courseFound = true;
  //          ((ObjectNode) courseNode).put("rating", rating.getRating());
  //          updatedCourseNode = (ObjectNode) courseNode;
  //          break;
  //        }
  //      }
  //
  //      if (!courseFound) {
  //        return HTTP_NOT_FOUND;
  //      }
  //
  //      // 构建并返回更新后的评分信息
  //      return makeOKJSONResponse(updatedCourseNode.toString());
  //    } catch (JsonProcessingException e) {
  //      return HTTP_BAD_REQUEST;
  //    }
  //  }

  //  private MockResponse postRating(RecordedRequest request) {
  //    String body = request.getBody().readUtf8();
  //    try {
  //      Rating rating = OBJECT_MAPPER.readValue(body, Rating.class);
  //      Summary summary = rating.getSummary();
  //      String summaryString = summary.toString(); // 根据 Summary 对象生成键
  //
  //      // 更新评分
  //      ratingMapper.put(summaryString, rating.getRating());
  //      comingFromPostRating = true; // 标记我们刚刚处理了一个 POST 请求
  //
  //      // 构建并返回更新后的评分信息
  //      String ratingToString = OBJECT_MAPPER.writeValueAsString(rating);
  //      return makeOKJSONResponse(ratingToString);
  //    } catch (JsonProcessingException e) {
  //      comingFromPostRating = false; // 在异常情况下重置标记
  //      return HTTP_BAD_REQUEST;
  //    } finally {
  //      comingFromPostRating = false; // 确保在方法退出时重置标记
  //    }
  //  }

  //  private MockResponse postRating(RecordedRequest request) {
  //    try {
  //      // 解析请求体
  //      Rating rating = OBJECT_MAPPER.readValue(request.getBody().readUtf8(), Rating.class);
  //      Summary summary = rating.getSummary();
  //
  //      // 更新评分
  //      String courseKey = summary.getSubject() + "/" + summary.getNumber();
  //      ratingMapper.put(courseKey, rating.getRating());
  //
  //      // 返回更新后的评分
  //      return makeOKJSONResponse(OBJECT_MAPPER.writeValueAsString(rating));
  //    } catch (JsonProcessingException e) {
  //      return HTTP_BAD_REQUEST;
  //    }
  //  }

  private MockResponse postRating(RecordedRequest request) {

    String body = request.getBody().readUtf8();
    try {
      // ratedNum = -1.0f;
      // If the request is bad then return a http bad request
      Rating rating = OBJECT_MAPPER.readValue(body, Rating.class);
      ratingMapper.put(rating.getSummary().toString(), rating.getRating());
      // Save the rating for GET /rating/
      // System.out.println("The real rating is " + rating.getRating());
      //      ratedNum = rating.getRating();
      //      System.out.println("the rating is being set to " + ratedNum);
      String ratingPath =
          "/rating/" + rating.getSummary().getSubject() + "/" + rating.getSummary().getNumber();
      comingFromPostRating = true;
      return new MockResponse()
          .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
          .setHeader("Location", ratingPath)
          .setBody("Content-Type; charset=utf-8");
    } catch (JsonProcessingException e) {
      return HTTP_BAD_REQUEST;
    }
  }

  private MockResponse resetRating() {
    String json =
        new Scanner(Server.class.getResourceAsStream("/courses.json"), "UTF-8")
            .useDelimiter("\\A")
            .next();

    try {
      JsonNode nodes = OBJECT_MAPPER.readTree(json);
      // Looping through every single node of the tree

      for (JsonNode node : nodes) {

        Summary summary = OBJECT_MAPPER.readValue(node.toString(), Summary.class);
        Rating rating = new Rating(summary, -1.0f);
        String ratingToString = OBJECT_MAPPER.writeValueAsString(rating);
        return makeOKJSONResponse(ratingToString);
      }

      return HTTP_NOT_FOUND;
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * HTTP request dispatcher.
   *
   * <p>This method receives HTTP requests from clients and determines how to handle them, based on
   * the request path and method.
   */
  @NonNull
  @Override
  public MockResponse dispatch(@NonNull RecordedRequest request) {
    try {
      // Reject requests without a path or method
      if (request.getPath() == null || request.getMethod() == null) {
        return HTTP_BAD_REQUEST;
      }

      // Normalize trailing slashes and method
      String path = request.getPath().replaceAll("/+", "/");
      String method = request.getMethod().toUpperCase();

      // Handle /rating/ requests
      //      if (path.startsWith("/rating/")) {
      //        String[] parts = path.split("/");
      //        if (parts.length == 2 * 2) {
      //          String subject = parts[2];
      //          String number = parts[3];
      //          if (method.equals("GET")) {
      //            // TODO: Implement the logic to get the rating for the course
      //            return handleGetRating(sub ject, number);
      //          } else if (method.equals("POST")) {
      //            // TODO: Implement the logic to update the rating for the course
      //            return handlePostRating(subject, number, request.getBody().readUtf8());
      //          }
      //        } else {
      //          return HTTP_BAD_REQUEST;
      //        }
      //      }

      // Main dispatcher tree tree
      if (path.equals("/") && method.equals("GET")) {
        // Used by API client to validate server
        return new MockResponse()
            .setBody(CHECK_SERVER_RESPONSE)
            .setResponseCode(HttpURLConnection.HTTP_OK);
      } else if (path.equals("/reset/") && method.equals("GET")) {
        return resetRating();
      } else if (path.equals("/summary/") && method.equals("GET")) {
        return getSummaries();
      } else if (path.startsWith("/course/") && method.equals("GET")) {
        return getCourse(path);
      } else if (path.startsWith("/rating/") && method.equals("GET")) {
        return getRating(path);
      } else if (path.equals("/rating/") && method.equals("POST")) {
        return postRating(request);
      } else {
        // Default is not found
        return HTTP_NOT_FOUND;
      }
    } catch (Exception e) {
      // Log an error and return 500 if an exception is thrown
      e.printStackTrace();
      return new MockResponse()
          .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
          .setBody("500: Internal Error");
    }
  }

  //  private static final Map<String, Float> ratings = new HashMap<>();

  //  private MockResponse handleGetRating(String subject, String number) {
  //    String courseKey = subject + number;
  //    if (!courseKeys.contains(courseKey)) {
  //      // 课程不存在，返回404 Not Found
  //      return HTTP_NOT_FOUND;
  //    }
  //
  //    Float courseRating = ratings.getOrDefault(courseKey, Rating.NOT_RATED);
  //    // 创建一个新的Rating对象用于返回
  //    Rating rating = new Rating();
  //    rating.setRating(courseRating);
  //    // 使用OBJECT_MAPPER将Rating对象转换为JSON字符串
  //    String jsonResponse = OBJECT_MAPPER.writeValueAsString(rating);
  //    return makeOKJSONResponse(jsonResponse);
  //  }
  //
  //  private MockResponse handlePostRating(String subject, String number, String body) {
  //    try {
  //      JsonNode requestBody = OBJECT_MAPPER.readTree(body);
  //      JsonNode ratingNode = requestBody.get("rating");
  //      // 确保请求体中包含评分信息
  //      if (ratingNode == null || !ratingNode.canConvertToFloat()) {
  //        return HTTP_BAD_REQUEST;
  //      }
  //      float newRating = ratingNode.floatValue();
  //
  //      // 构建课程的键
  //      String courseKey = subject + number;
  //      // 检查课程是否存在
  //      if (!courseKeys.contains(courseKey)) {
  //        return HTTP_NOT_FOUND;
  //      }
  //      // 更新评分
  //      ratings.put(courseKey, newRating);
  //      // 返回更新后的评分
  //      Rating updatedRating = new Rating();
  //      updatedRating.setRating(newRating);
  //      String jsonResponse = OBJECT_MAPPER.writeValueAsString(updatedRating);
  //      return makeOKJSONResponse(jsonResponse);
  //    } catch (Exception e) {
  //      return HTTP_BAD_REQUEST;
  //    }
  //  }
  /**
   * Start the server if has not already been started, and wait for startup to finish.
   *
   * <p>Done in a separate thread to avoid blocking the UI.
   */
  public static void start() {
    if (isRunning(false)) {
      return;
    }
    new Server();
    if (!isRunning(true)) {
      throw new IllegalStateException("Server should be running");
    }
  }

  /** Number of times to check the server before failing. */
  private static final int RETRY_COUNT = 8;

  /** Delay between retries. */
  private static final int RETRY_DELAY = 512;

  /**
   * Determine if the server is currently running.
   *
   * @param wait whether to wait or not
   * @return whether the server is running or not
   * @throws IllegalStateException if something else is running on our port
   */
  public static boolean isRunning(boolean wait) {
    return isRunning(wait, RETRY_COUNT, RETRY_DELAY);
  }

  public static boolean isRunning(boolean wait, int retryCount, long retryDelay) {
    for (int i = 0; i < retryCount; i++) {
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder().url(CourseableApplication.SERVER_URL).get().build();
      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          if (Objects.requireNonNull(response.body()).string().equals(CHECK_SERVER_RESPONSE)) {
            return true;
          } else {
            throw new IllegalStateException(
                "Another server is running on port " + CourseableApplication.DEFAULT_SERVER_PORT);
          }
        }
      } catch (IOException ignored) {
        if (!wait) {
          break;
        }
        try {
          Thread.sleep(retryDelay);
        } catch (InterruptedException ignored1) {
        }
      }
    }
    return false;
  }

  /**
   * Reset the server. Used to reset the server between tests.
   *
   * @noinspection UnusedReturnValue, unused
   */
  public static boolean reset() throws IOException {
    OkHttpClient client = new OkHttpClient();
    Request request =
        new Request.Builder().url(CourseableApplication.SERVER_URL + "/reset/").get().build();
    try (Response response = client.newCall(request).execute()) {
      return response.isSuccessful();
    }
  }

  private Server() {
    // Disable server logging, since this is a bit verbose
    Logger.getLogger(MockWebServer.class.getName()).setLevel(Level.OFF);

    // Load data used by the server
    summariesJSON = loadData();

    // 初始化评分数据
    //    initializeCourses();
    //    initializeRatings();

    try {
      // This resource needs to outlive the try-catch
      //noinspection resource
      MockWebServer server = new MockWebServer();
      server.setDispatcher(this);
      server.start(CourseableApplication.DEFAULT_SERVER_PORT);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException(e.getMessage());
    }
  }

  //  private void initializeCourses() {
  //    courseKeys = new HashSet<>();
  //    ObjectMapper mapper = new ObjectMapper();
  //    try {
  //      // 读取 courses.json 文件内容
  //      String json = new Scanner(Server.class.getResourceAsStream("/courses.json"), "UTF-8")
  //          .useDelimiter("\\A")
  //          .next();
  //      JsonNode nodes = mapper.readTree(json);
  //      for (JsonNode node : nodes) {
  //        String key = node.get("subject").asText() + node.get("number").asText();
  //        courseKeys.add(key);
  //        ratings.put(key, Rating.NOT_RATED); // 同时初始化评分
  //      }
  //    } catch (Exception e) {
  //      throw new IllegalStateException("Failed to initialize courses", e);
  //    }
  //  }
  //
  //  private void initializeRatings() {
  //    try {
  //      // 假设 "/courses.json" 包含所有课程的概要
  //      String json =
  //          new Scanner(Server.class.getResourceAsStream("/courses.json"), "UTF-8")
  //              .useDelimiter("\\A")
  //              .next();
  //      JsonNode nodes = OBJECT_MAPPER.readTree(json);
  //      for (JsonNode node : nodes) {
  //        Summary summary = OBJECT_MAPPER.treeToValue(node, Summary.class);
  //        String key = summary.getSubject() + summary.getNumber();
  //        ratings.put(key, Rating.NOT_RATED);
  //      }
  //    } catch (Exception e) {
  //      throw new IllegalStateException("Failed to initialize ratings", e);
  //    }
  //  }

  /**
   * Helper method to load data used by the server.
   *
   * @return the summary JSON string.
   */
  private String loadData() {

    // Load the JSON string
    String json =
        // new
        // Scanner(Server.class.getResourceAsStream("/courses.json")).useDelimiter("\\A").next();
        new Scanner(Server.class.getResourceAsStream("/courses.json"), "UTF-8")
            .useDelimiter("\\A")
            .next();

    // Build the list of summaries
    List<Summary> summaries = new ArrayList<>();
    try {
      // Iterate through the list of JsonNodes returned by deserialization
      JsonNode nodes = OBJECT_MAPPER.readTree(json);
      for (JsonNode node : nodes) {
        // Deserialize as Summary and add to the list
        Summary summary = OBJECT_MAPPER.readValue(node.toString(), Summary.class);
        summaries.add(summary);
      }
      // Convert the List<Summary> to a String and return it
      return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(summaries);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}
