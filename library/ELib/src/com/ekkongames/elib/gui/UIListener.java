package com.ekkongames.elib.gui;

/**
 * A <code>UIListener</code> is the base class of various event-triggered
 * listeners. You may use these listeners to, for example, track whenever
 * a password field is submitted.
 */
public abstract class UIListener {
  public static final int MOUSE_MOVED = 1;
  public static final int MOUSE_DRAGGED = 3;
  public static final int MOUSE_PRESSED = 5;
  public static final int MOUSE_RELEASED = 7;
  public static final int MOUSE_CLICKED = 9;
  public static final int KEY_PRESSED = 11;
  public static final int KEY_RELEASED = 13;
  public static final int KEY_TYPED = 15;
  public static final int SUBMIT = 17;
  
  private int trigger = 0;

  /**
   * Constructs a new <code>UIListener</code> with a trigger.
   * 
   * @param trigger the type of event to listen for
   */
  public UIListener(int trigger) {
    this.trigger = trigger;
  }
  
  int getTrigger() {
    return trigger;
  }
  
  public abstract void onTrigger();
}