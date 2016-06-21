package com.ekkongames.elib.gui.listeners;

/**
 * A <code>UIListener</code> is the base class of various event-triggered
 * listeners. You may use these listeners to, for example, track whenever
 * a password field is submitted.
 */
public abstract class UIListener {
  
  private final int trigger = 0;

  /**
   * Constructs a new <code>UIListener</code> with a trigger.
   */
  public UIListener(int trigger) {
  }
  
  public final int getTrigger() {
    return trigger;
  }
  
  public abstract void onTrigger();
}