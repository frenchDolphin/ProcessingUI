package com.ekkongames.elib.manip;

/**
 * This class provides utility methods related to String manipulation.
 */
public class Strings {

  private Strings() {}

  /**
   * Limits the length of a String to a specified amount, replacing any
   * excess with the String "...". For Strings shorter than
   * <code>maxLength</code>, no modifications will be made.
   * 
   * @param s the String to limit
   * @param maxLength the length to limit the String to
   * 
   * @return the manipulated String
   * @see Strings#limit(String, int, String)
   */
  public static String limit(String s, int maxLength) {
    return limit(s, maxLength, "...");
  }

  /**
   * Limits the length of a String to a specified amount, replacing any
   * excess with a given String. For Strings shorter than
   * <code>maxLength</code>, no modifications will be made.
   * 
   * @param s the String to limit
   * @param maxLength the length to limit the String to
   * @param ellipsis the String to replace any excess with
   * 
   * @return the manipulated String
   * @see Strings#limit(String, int)
   */
  public static String limit(String s, int maxLength, String ellipsis) {
    if (s.length() > maxLength) {
      return s.substring(0, maxLength - ellipsis.length()) + ellipsis;
    }
    return s;
  }

  /**
   * Constructs a <code>String</code> that is equivalent to a given
   * <code>String</code> a certain number of times. For example,
   * the method call <code>Strings.multiply("n", 5)</code> would
   * return "nnnnn". This method provides the same functionality as
   * <code>"n" * 5</code> does in <code>Python</code>.
   * 
   * @param s the String to multiply
   * @param times the number of times to multiply the String
   * @return the manipulated String
   */
  public static String multiply(String s, int times) {
    return new String(new char[times]).replace("\0", s);
  }

}