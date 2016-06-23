package com.ekkongames.elib.gui;

import processing.core.PConstants;
import com.ekkongames.elib.manip.Numbers;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class UIElement implements PConstants {
  
  private HashMap<Integer, ArrayList<UIListener>> listeners
    = new HashMap<>();

  protected boolean focused = false;
  protected boolean focusable = true;
  protected boolean visible = true;
  
  protected float x, y, width, height;
  protected int zIndex = 0;
  protected UIManager mgr;

  protected abstract void render();

  /**
   * Called whenever this <code>UIElement</code> receives focus in its parent {@link UIManager}.
   */
  void doFocus() {
    mgr.unfocus();
    
    mgr.addFocusedElement(this);
    focus();
  }

  /**
   * Called whenever this <code>UIElement</code> loses focus in its parent {@link UIManager}.
   */
  void doUnfocus() {
    mgr.removeFocusedElement(this);
    focus();
  }

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
  
  public void setPosition(int x, int y) {
    this.x = x;
    this.x = x;
  }
  
  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public boolean containsPoint(float px, float py) {
    return Numbers.between(px, x, x + width)
        && Numbers.between(py, y, y + height);
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
  
  public void addListener(UIListener listener) {
    if (listener != null) {
      int id = listener.getTrigger();
      if (!listeners.containsKey(id)) {
        listeners.put(id, new ArrayList<>());
      }
      listeners.get(id).add(listener);
    }
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

  protected void mouseMoved() {
    triggerListeners(UIListener.MOUSE_MOVED);
  }
  
  protected void mouseDragged() {
    triggerListeners(UIListener.MOUSE_DRAGGED);
  }
  
  protected void mousePressed() {
    triggerListeners(UIListener.MOUSE_PRESSED);
  }
  
  protected void mouseReleased() {
    triggerListeners(UIListener.MOUSE_RELEASED);
  }
  
  protected void mouseClicked() {
    triggerListeners(UIListener.MOUSE_CLICKED);
  }

  protected void keyPressed() {
    triggerListeners(UIListener.KEY_PRESSED);
  }
  
  protected void keyReleased() {
    triggerListeners(UIListener.KEY_RELEASED);
  }
  
  protected void keyTyped() {
    triggerListeners(UIListener.KEY_TYPED);
  }
  
  protected void submit() {
    triggerListeners(UIListener.SUBMIT);
  }
  
  private void triggerListeners(int id) {
    ArrayList<UIListener> list = listeners.get(id);
    if (list == null) {
      return;
    }
    
    for (UIListener listener : list) {
      listener.onTrigger();
    }
  }
}