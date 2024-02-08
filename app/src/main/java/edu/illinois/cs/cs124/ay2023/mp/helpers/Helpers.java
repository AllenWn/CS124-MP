package edu.illinois.cs.cs124.ay2023.mp.helpers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Helper class holding a few broadly-useful items. */
public final class Helpers {
  // Jackson instance for serialization and deserialization
  public static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  // Magic server response used by the client to determine that it's properly connected
  public static final String CHECK_SERVER_RESPONSE = "AY2023";
}
