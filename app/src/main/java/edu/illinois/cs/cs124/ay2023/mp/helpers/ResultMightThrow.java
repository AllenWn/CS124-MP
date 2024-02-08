package edu.illinois.cs.cs124.ay2023.mp.helpers;

/**
 * Helper class for wrapping exceptions thrown by another thread.
 *
 * <p>Allows the main thread to retrieve exceptions thrown by the API client, rather than having
 * them be thrown on another thread. See use in MainActivity.java and Client.java.
 *
 * @param <T> type of the valid result
 */
public class ResultMightThrow<T> {
  private final T value;
  private final Exception exception;

  public ResultMightThrow(final T setResult) {
    value = setResult;
    exception = null;
  }

  public ResultMightThrow(final Exception setException) {
    value = null;
    exception = setException;
  }

  public T getValue() {
    if (exception != null) {
      throw new RuntimeException(exception);
    } else {
      return value;
    }
  }

  public Exception getException() {
    return exception;
  }
}
