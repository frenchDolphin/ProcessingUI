package com.ekkongames.elib.gui;

import java.util.ArrayList;

/**
 * A <code>UIMenu</code> is the superclass for several different types of menus. It provides common
 * functionality between these types. For example, it allows a user to add child elements, showing
 * and hiding these elements as appropriate.
 */
public class UIMenu extends UIElement {
  
  public ArrayList<UIElement> children;
  
  public UIMenu() {
    this(0, 0);
  }

  /**
   * Constructs a new <code>UIMenu</code>, at position (x, y). The menu will start hidden.
   * 
   * @param x the starting x position
   * @param y the starting y position
   */
  public UIMenu(float x, float y) {
    this.x = x;
    this.y = y;
    this.elWidth = 50;
    this.elHeight = 0;
    this.focusable = false;
    
    children = new ArrayList<UIElement>();
  }

  /**
   * Adds a {@link UIElement} to this <code>UIMenu</code>. This element will only be rendered if
   * this menu is visible.
   * 
   * @param child the element to add
   * @see UIElement
   */
  public void add(UIElement child) {
    child.x = x;
    child.y = y + elHeight;
    child.elWidth = elWidth;
    elHeight += child.elHeight;
    child.visible = visible;
    
    children.add(child);
    mgr.add(child);
  }
  
  protected void render() {
    mgr.app.stroke(155);
    mgr.app.fill(255);
    mgr.app.rect(x, y, elWidth, elHeight);
  }

  /**
   * Displays this <code>UIMenu</code> and any child elements.
   */
  public void show() {
    visible = true;
    for (UIElement child : children) {
      child.visible = true;
    }
  }

  /**
   * Hides this <code>UIMenu</code> and any child elements.
   */
  public void hide() {
    visible = false;
    for (UIElement child : children) {
      child.visible = false;
    }
  }

  public void focus() {}
  public void unfocus() {}
  
}