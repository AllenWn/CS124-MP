package edu.illinois.cs.cs124.ay2023.mp.models;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Model holding the course summary information shown in the summary list.
 *
 * @noinspection unused
 */
public class Summary implements Comparable<Summary> {
  private String subject;

  /**
   * Get the subject for this Summary.
   *
   * @return the subject for this Summary
   */
  @NotNull
  public final String getSubject() {
    return subject;
  }

  private String number;

  /**
   * Get the number for this Summary.
   *
   * @return the number for this Summary
   */
  @NotNull
  public final String getNumber() {
    return number;
  }

  private String label;

  /**
   * Get the label for this Summary.
   *
   * @return the label for this Summary
   */
  @NotNull
  public final String getLabel() {
    return label;
  }

  /** Create an empty Summary. */
  public Summary() {}

  /**
   * Create a Summary with the provided fields.
   *
   * @param setSubject the department for this Summary
   * @param setNumber the number for this Summary
   * @param setLabel the label for this Summary
   */
  public Summary(@NonNull String setSubject, @NonNull String setNumber, @NotNull String setLabel) {
    subject = setSubject;
    number = setNumber;
    label = setLabel;
  }

  /** {@inheritDoc} */
  @NonNull
  @Override
  public String toString() {
    return subject + " " + number + ": " + label;
  }

  @Override
  public int compareTo(Summary o) {
    if (this.number.compareTo(o.number) > 0) {
      return 1;
    } else if (this.number.compareTo(o.number) < 0) {
      return -1;
    } else {
      if (this.subject.compareTo(o.subject) > 0) {
        return 1;
      } else if (this.subject.compareTo(o.subject) < 0) {
        return -1;
      } else {
        return 0;
      }
    }
  }

  public static List<Summary> filter(List<Summary> list, String searchTerm) {
    final String searchTermCopy = searchTerm.trim().toLowerCase();
    List<Summary> filteredList = new ArrayList<>();
    for (Summary index : list) {
      if (index.toString().trim().toLowerCase().contains(searchTermCopy.trim().toLowerCase())) {
        filteredList.add(index);
      }
    }

    Collections.sort(filteredList);

    Collections.sort(
        filteredList,
        new Comparator<>() {
          @Override
          public int compare(Summary o1, Summary o2) {
            int index1 = o1.toString().toLowerCase().indexOf(searchTermCopy);
            int index2 = o2.toString().toLowerCase().indexOf(searchTermCopy);

            if (index1 == index2) {
              return 0;
            } else if (index2 == -1) {
              return -1;
            } else if (index1 == -1) {
              return 1;
            } else {
              return index1 - index2;
            }
          }
        });
    return filteredList;
  }
}
