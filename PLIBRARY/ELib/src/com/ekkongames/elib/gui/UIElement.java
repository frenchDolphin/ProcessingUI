package com.ekkongames.elib.gui;

import processing.core.PConstants;
import com.ekkongames.elib.manip.Numbers;

public abstract class UIElement implements PConstants {

  abstract void render();

  /**
   * Runs whenever this <code>UIElement</code> receives focus in its parent {@link UIManager}.
   */
  public void focus() {
    focused = true;
  }


  /**
   * Runs whenever this <code>UIElement</code> loses focus in its parent {@link UIManager}.
   */
  public void unfocus() {
    focused = false;
  }

  protected boolean focused = false;
  protected boolean focusable = true;
  protected boolean visible = true;
  
  protected float x, y, elWidth, elHeight;
  protected int zIndex = 0;
  protected UIManager mgr;

  boolean containsPoint(float px, float py) {
    return Numbers.between(px, x, x + elWidth)
        && Numbers.between(py, y, y + elHeight);
  }

  /**
   * Returns <code>true</code> if this element has focus.
   * 
   * @return whether this element has focus.
   */
  public boolean isFocused() {
    return focused;
  }

  /**
   * Returns <code>true</code> if this element can receive focus.
   * 
   * @return whether this element can receive focus.
   */
  public boolean isFocusable() {
    return focusable;
  }

  /**
   * Returns <code>true</code> if this element is visible.
   * 
   * @return whether this element is visible.
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Returns the z-index of this element. This number is used in the parent {@link UIManager}
   * to determine which components are rendered first.
   * 
   * @return the z-index of this element.
   */
  public int getZIndex() {
    return zIndex;
  }

  void add(UIManager mgr) {
    this.mgr = mgr;
  }

  void mouseMoved() {
  }
  void mouseDragged() {
  }
  void mousePressed() {
  }
  void mouseReleased() {
  }
  void mouseClicked() {
  }

  void keyPressed() {
  }
  void keyReleased() {
  }
  void keyTyped() {
  }
}