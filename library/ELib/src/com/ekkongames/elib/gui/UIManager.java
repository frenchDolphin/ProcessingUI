package com.ekkongames.elib.gui;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.event.MouseEvent;
import processing.event.KeyEvent;

/**
 * A <code>UIManager</code> is essentially a container for
 * {@link UIElement UIElements}. It internally maintains a list of all elements
 * added, and manages drawing and event delivery.
 * 
 * A UIManager must be instantiated using the constructor
 * UIManager(PApplet), usually by padding in <code>this</code>.
 * Children may the be added using the {@link UIManager#add(UIElement)}
 * method.
 */
public class UIManager {

  public PApplet app;
  private ArrayList<UIElement> elements;
  private ArrayList<UIElement> focusedElements;
  boolean canTab = true;
  
  char key;
  int keyCode;
  int mouseX, mouseY, mouseButton;

  /**
   * Constructs a new <code>UIManager</code>. Each instance is
   * fully independent of any other instance, and may have a virtually
   * unlimited number of {@link UIElement UIElements} added. In
   * addition, this constructor registers this <code>UIManager</code>
   * with the Processing environment, so it receives key and mouse
   * events and can render itself.
   * 
   * @param app the main applet to attach this <code>UIManager</code> to
   */
  public UIManager(PApplet app) {
    this.app = app;
    elements = new ArrayList<UIElement>();
    focusedElements = new ArrayList<UIElement>();

    app.registerMethod("mouseEvent", this);
    app.registerMethod("keyEvent", this);
    
    app.registerMethod("draw", this);
  }

  /**
   * Registers a {@link UIElement} with this <code>UIManager</code>.
   * This means that the element will automatically be rendered and
   * receive events when appropriate.
   * 
   * @param element the element to add
   * 
   * @see UIElement
   */
  public void add(UIElement element) {
    elements.add(element);
    element.add(this);
  }
  
  public void addFocusedElement(UIElement el) {
    focusedElements.add(el);
  }
  
  public void removeFocusedElement(UIElement el) {
    focusedElements.remove(el);
  }
  
  public void unfocus() {
    for (UIElement el : focusedElements) {
      if (el.isFocused()) {
        el.unfocus();
      }
    }
  }

  /**
   * Moves focus to the next element added to this UIManager. This is is used, for example, when
   * pressing <code>TAB</code>, to move to the logical next element.
   * 
   * @param current the current element
   * 
   * @see UIElement#focus
   * @see UIElement#unfocus
   * @see UIElement#isFocused
   */
  public void cycleFocus(UIElement current) {
    internalCycleFocus(current, 0);
  }

  private void internalCycleFocus(UIElement current, int els) {
    if (canTab) {
      canTab = false;

      UIElement next = nextElement(elements.indexOf(current) + els);
      if (next.isFocusable() && next.isVisible()) {
        current.unfocus();
        next.doFocus();
      } else if (els < elements.size()) {
        internalCycleFocus(current, els + 1);
      }
    }
  }

  private UIElement nextElement(int i) {
    int next = i + 1;
    if (next < 0) {
      next += elements.size();
    }

    if (next >= elements.size()) {
      next -= elements.size();
    }

    return elements.get(next);
  }

  /**
   * Renders all child elements of this <code>UIManager</code>. <b>This method should not be called
   * by any Processing program; the Processing environment calls it automatically.</b>
   */
  public void draw() {
    for (UIElement element : elements) {
      if (element.isVisible()) {
        element.render();
      }
    }
  }

  /**
   * Notifies all child elements of a <code>MouseEvent</code>. <b>This method should not be called
   * by any Processing program; the Processing environment calls it automatically.</b>
   *
   * @param e the event that triggered this call
   */
  public void mouseEvent(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
    mouseButton = e.getButton();
    switch (e.getAction()) {
      case MouseEvent.MOVE:
        mouseMoved();
        break;
      case MouseEvent.DRAG:
        mouseDragged();
        break;
      case MouseEvent.PRESS:
        mousePressed();
        break;
      case MouseEvent.RELEASE:
        mouseReleased();
        break;
      case MouseEvent.CLICK:
        mouseClicked();
        break;
    }
  }

  /**
   * Notifies all child elements of a <code>KeyEvent</code>. <b>This method should not be called
   * by any Processing program; the Processing environment calls it automatically.</b>
   *
   * @param e the event that triggered this call
   */
  public void keyEvent(KeyEvent e) {
    key = e.getKey();
    keyCode = e.getKeyCode();
    switch (e.getAction()) {
      case KeyEvent.PRESS:
        keyPressed();
        break;
      case KeyEvent.RELEASE:
        keyReleased();
        break;
      case KeyEvent.TYPE:
        keyTyped();
        break;
    }
  }

  private void mouseMoved() {
    for (UIElement element : elements) {
      element.mouseMoved();
    }
  }

  private void mouseDragged() {
    for (UIElement element : elements) {
      element.mouseDragged();
    }
  }

  private void mousePressed() {
    for (UIElement element : elements) {
      element.mousePressed();
    }
  }

  private void mouseReleased() {
    for (UIElement element : elements) {
      element.mouseReleased();
    }
  }

  private void mouseClicked() {
    for (UIElement element : elements) {
      element.mouseClicked();
    }
  }

  private void keyPressed() {
    for (UIElement element : elements) {
      element.keyPressed();
    }
  }

  private void keyReleased() {
    for (UIElement element : elements) {
      element.keyReleased();
    }
  }

  private void keyTyped() {
    for (UIElement element : elements) {
      element.keyTyped();
    }
  }

  private ArrayList<UIElement> getContainingElements(float mouseX, float mouseY) {
    ArrayList<UIElement> containingElements = new ArrayList<UIElement>();
    for (UIElement element : elements) {
      if (element.containsPoint(mouseX, mouseY)) {
        containingElements.add(element);
      }
    }
    return containingElements;
  }

  boolean isTop(UIElement el, float x, float y) {
    return el != null && el.equals(getTop(getContainingElements(x, y)));
  }

  private UIElement getTop(ArrayList<UIElement> elementsToCheck) {
    if (elementsToCheck.size() == 0) {
      return null;
    }

    UIElement topElement = elementsToCheck.get(0);
    for (int i = 1; i < elementsToCheck.size(); i++) {
      int zIndex = elementsToCheck.get(i).getZIndex();
      if (zIndex >= topElement.getZIndex()) {
        topElement = elementsToCheck.get(i);
      }
    }

    return topElement;
  }
}