package com.ekkongames.elib.gui;

/**
 * A <code>UICheckBox</code> is a simple UI element that has only two states: checked and unchecked.
 * This implementation is very simple; future implementations could support skins, custom shape, and
 * more.
 */
public class UICheckBox extends UIElement {

  public int size = 20;
  public int padding = 4;

  public boolean checked = false;
  public int checkColour = 255;

  /**
   * Constructs a new <code>UICheckBox</code>.
   */
  public UICheckBox() {
    this(50, 50, 20, 20);
  }

  /**
   * Constructs a new <code>UICheckBox</code>, at position (x, y).
   * 
   * @param x the initial x position
   * @param y the initial y position
   */
  public UICheckBox(int x, int y) {
    this(x, y, 20, 20);
  }

  /**
   * Constructs a new <code>UICheckBox</code>, at position (x, y),
   * and with a certain size.
   * 
   * @param x the initial x position
   * @param y the initial y position
   * @param size the initial size
   */
  public UICheckBox(int x, int y, int size) {
    this(x, y, size, size);
  }

  /**
   * Constructs a new <code>UICheckBox</code>, at position (x, y),
   * and with a certain width and height.
   * 
   * @param x the initial x position
   * @param y the initial y position
   * @param width the initial width
   * @param height the initial height
   */
  public UICheckBox(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  protected void render() {
    updateColour();

    if (isFocused()) {
      mgr.app.fill(255);
    } else {
      mgr.app.fill(245);
    }

    mgr.app.stroke(0);
    mgr.app.rect(x, y, width, height);

    mgr.app.fill(checkColour);
    mgr.app.noStroke();

    mgr.app.rect(x + padding, y + padding, width - (padding * 2) + 1, height - (padding * 2) + 1);
  }

  public void focus() {
    super.focus();
  }

  public void unfocus() {
    super.unfocus();
  }

  /**
   * Toggles the state of this <code>UICheckBox</code> between checked and unchecked.
   */
  public void toggle() {
    checked = !checked;
  }

  protected void mousePressed() {
    if (containsPoint(mgr.mouseX, mgr.mouseY)) {
      toggle();
    }
    super.mousePressed();
  }

  protected void keyPressed() {
    if (mgr.key == TAB) {
      mgr.cycleFocus(this);
    } else if ((mgr.key == ' ' || mgr.key == ENTER) && isFocused()) {
      toggle();
    }
    super.keyPressed();
  }

  private void updateColour() {
    if (containsPoint(mgr.mouseX, mgr.mouseY)) {
      if (checked) {
        checkColour = 85;
      } else {
        checkColour = 155;
      }
    } else {
      if (checked) {
        checkColour = 0;
      } else {
        checkColour = 205;
      }
    }
  }
}