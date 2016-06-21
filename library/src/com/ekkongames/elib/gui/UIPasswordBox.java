package com.ekkongames.elib.gui;

import com.ekkongames.elib.manip.Strings;

/**
 * A <code>UIPasswordBox</code> is similar to a {@link UITextBox},
 * except that all input is hidden from the user.
 */
public class UIPasswordBox extends UITextBox {

  private static String replace = "\u2022";

  /**
   * Constructs a new <code>UIPasswordBox</code>, at position (x, y).
   * 
   * @param x the starting x position
   * @param y the starting y position
   */
  public UIPasswordBox(int x, int y) {
    super(x, y);
  }

  /**
   * Constructs a new <code>UIPasswordBox</code>, at position (x, y)
   * and with a specific width and height.
   * 
   * @param x the starting x position
   * @param y the starting y position
   * @param xWidth the starting width
   * @param yHeight the starting height
   */
  public UIPasswordBox(int x, int y, int xWidth, int yHeight) {
    super(x, y, xWidth, yHeight);
  }
  
  protected String getDisplayText() {
    return Strings.multiply(replace, text.length());
  }
  /**
   * Copies any selected text to the system clipboard.
   */
  @Override
  public void copySelection() {
    // we want to do nothing; you shouldn't be able to copy from a password box!
  }

}