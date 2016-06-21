package com.ekkongames.elib.gui;

/**
 * A <code>UIContextMenu</code> is a subclass of {@link UIMenu} that displays itself whenever a
 * right click event takes place. Similar to a <code>UIMenu</code>, it supports adding child
 * {@link UIElement UIElements} that display whenever the menu is visible.
 */
public class UIContextMenu extends UIMenu {

  /**
   * Constructs a new <code>UIContextMenu</code>, at position (0, 0).
   */
  public UIContextMenu() {
    this(0, 0);
  }

  /**
   * Constructs a new <code>UIContextMenu</code>, at position (x, y).
   * 
   * @param x the starting x position
   * @param y the starting y position
   */
  public UIContextMenu(float x, float y) {
    super(x, y);
    hide();
  }
  
  protected void mousePressed() {
    if (mgr.mouseButton == RIGHT) {
      show();
      updatePositions(mgr.mouseX, mgr.mouseY);
    } else if (!containsPoint(mgr.mouseX, mgr.mouseY)) {
      hide();
    }
    super.mousePressed();
  }
  
  private void updatePositions(float x, float y) {
    float deltaX = x - this.x;
    float deltaY = y - this.y;
    
    for (UIElement child : children) {
      child.x += deltaX;
      child.y += deltaY;
    }
    
    this.x += deltaX;
    this.y += deltaY;
  }
}