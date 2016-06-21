package com.ekkongames.elib.manip;

/**
 * This class provides utility methods related to String manipulation.
 */
public class Numbers {

  private Numbers() {}

  /**
   * Determines whether a number is contained within a given
   * inclusive range.
   * 
   * @param n the number to check
   * @param min the lowest number n can be
   * @param max the highest number n can be
   * 
   * @return whether or not the number is in the range
   * @see #between(double, double, double)
   */
  public static boolean between(int n, int min, int max) {
    // switch min and max if min > max
    if (min > max) {
      min -= max;
      max += min;
      min = max - min;
    }

    return n >= min && n <= max;
  }

  /**
   * Determines whether a number is contained within a given
   * inclusive range. This version supports floating point numbers.
   * 
   * @param n the number to check
   * @param min the lowest number n can be
   * @param max the highest number n can be
   * 
   * @return whether or not the number is in the range
   * @see #between(int, int, int)
   */
  public static boolean between(double n, double min, double max) {
    // switch min and max if min > max
    if (min > max) {
      min -= max;
      max += min;
      min = max - min;
    }

    return n >= min && n <= max;
  }

}