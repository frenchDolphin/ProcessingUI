package com.ekkongames.elib.gui;

// for clipboard access
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;

import processing.core.PApplet;
import processing.core.PGraphics;
import com.ekkongames.elib.io.Console;

/**
 * A <code>UITextBox</code> is a rectangular text input. This particular implementation provides
 * various capabilities such as selecting text and copying and pasting.
 */
public class UITextBox extends UIElement {
  public static final int CURSOR_BLINK_FREQUENCY = 500;
  public static final int CTRL_C = 0x3;
  public static final int CTRL_V = 0x16;
  public static final int CTRL_X = 0x18;
  public static final int CTRL_A = 0x1;
  public static final int HOME = 0x24;
  public static final int END = 0x23;

  public int padding = 5;

  protected String text = "";
  private int textAlign = LEFT;
  public int defaultbackgroundShade = 245;
  public int backgroundShade = defaultbackgroundShade;

  private int cursorLastToggled = 0;
  private boolean cursorShowing = false;

  private int cursorPosition = 0;
  private int selectionPosition = cursorPosition;
  private int selectionStart = cursorPosition;
  private int selectionEnd = cursorPosition;

  private int shouldRefresh = 1;
  private int hoverState = 0;
  private PGraphics renderLayer;
  private PGraphics textBuffer;
  private Toolkit toolkit;

  private boolean[] modifiers = {false, false, false};
  private int controlIndex = 0;
  private int shiftIndex = 1;
  private int altIndex = 2;

  /**
   * Constructs a new <code>UITextBox</code>, at position (x, y).
   * 
   * @param x the starting x position
   * @param y the starting y position
   */
  public UITextBox(int x, int y) {
    this(x, y, 400, 50);
  }

  /**
   * Constructs a new <code>UITextBox</code>, at position (x, y) and with a specific width and height.
   * 
   * @param x the starting x position
   * @param y the starting y position
   * @param xWidth the starting width
   * @param yHeight the starting height
   */
  public UITextBox(int x, int y, int xWidth, int yHeight) {
    this.x = x;
    this.y = y;
    this.elWidth = xWidth;
    this.elHeight = yHeight;

    toolkit = Toolkit.getDefaultToolkit();
  }

  void add(UIManager mgr) {
    super.add(mgr);

    renderLayer = mgr.app.createGraphics(Math.round(elWidth + 1), Math.round(elHeight + 1));
    textBuffer = mgr.app.createGraphics(Math.round(elWidth - (padding * 2)), Math.round(elHeight));

    textBuffer.beginDraw();
    textBuffer.textFont(mgr.app.createFont("Arial", 40, true), 40);
    textBuffer.fill(0);
    textBuffer.endDraw();
  }

  private void update() {
    if (focused && mgr.app.millis() - cursorLastToggled >= CURSOR_BLINK_FREQUENCY) {
      cursorLastToggled = mgr.app.millis();
      cursorShowing = !cursorShowing;
    }
  }

  protected void render() {
    if (shouldRefresh > 0) {
      if (shouldRefresh == 1) {
        shouldRefresh = 0;
      }
      update();

      renderLayer.beginDraw();
      renderLayer.background(204);

      renderLayer.fill(backgroundShade);
      renderLayer.rect(0, 0, elWidth, elHeight);

      textBuffer.beginDraw();
      textBuffer.background(0, 0);

      String displayText = getDisplayText();
      if (isSelection()) {
        textBuffer.noStroke();
        textBuffer.fill(0, 55, 255);
        textBuffer.rect(getTextWidth(displayText.substring(0, selectionStart)), padding, getTextWidth(displayText.substring(selectionStart, selectionEnd)), textBuffer.height - (padding * 2));
      }

      char[] textChars = displayText.toCharArray();
      if (textAlign == LEFT) {
        renderCursor(getTextWidth(displayText.substring(0, cursorPosition)));

        for (int i = 0; i < textChars.length; i++) {
          textBuffer.fill(0);
          if (i >= selectionStart && i < selectionEnd) {
            textBuffer.fill(255);
          }

          textBuffer.text(textChars[i], getTextWidth(displayText.substring(0, i)), 39);
        }
      } else if (textAlign == RIGHT) {
        renderCursor(textBuffer.width - getTextWidth(displayText.substring(selectionStart, displayText.length())));

        for (int i = textChars.length - 1; i >= 0; i--) {
          textBuffer.fill(0);
          if (i >= selectionStart && i < selectionEnd) {
            textBuffer.fill(255);
          }

          textBuffer.text(textChars[i], textBuffer.width - getTextWidth(displayText.substring(i, displayText.length())), 39);
        }
      }

      textBuffer.endDraw();

      renderLayer.image(textBuffer, padding, 0);
      renderLayer.endDraw();
    }
    mgr.app.image(renderLayer, x, y);
  }

  private void renderCursor(int cx) {
    if (!isSelection() && cursorShowing) {
      if (cx < 0) {
        cx = 0;
      }

      if (cx > textBuffer.width - 1) {
        cx = textBuffer.width - 1;
      }

      textBuffer.stroke(0);
      textBuffer.line(cx, padding, cx, textBuffer.height - padding);
    }
  }
  
  protected String getDisplayText() {
    return text;
  }

  protected void mouseMoved() {
    if (mgr.isTop(this, mgr.app.mouseX, mgr.app.mouseY)) {
      hoverState = hoverState == 2 ? 2 : 1;
    } else {
      hoverState = hoverState == -1 ? -1 : 0;
    }
    if (hoverState == 1) {
      hoverState = 2;
      mgr.app.cursor(TEXT);
    } else if (hoverState == 0) {
      hoverState = -1;
      mgr.app.cursor(ARROW);
    }
    super.mouseMoved();
  }

  protected void mouseDragged() {
    int index = getClosestIndex(mgr.app.mouseX);

    setSelection(index - cursorPosition);
    updateSelection();
    
    super.mouseDragged();
  }

  protected void mousePressed() {
    if (mgr.isTop(this, mgr.app.mouseX, mgr.app.mouseY) && mgr.app.mouseButton == LEFT) {
      focus();
      setCursorPosition(getClosestIndex(mgr.app.mouseX));
    } else if (focused) {
      unfocus();
    }
    updateSelection();
    
    super.mousePressed();
  }

  protected void keyPressed() {
    if (focused) {
      boolean selection = isSelection();
      if (mgr.app.key != CODED) {
        boolean doDefault = false;
        switch (mgr.app.key) {
          case ESC:
            unfocus();
            mgr.app.key = 0;
            break;
          case ENTER:
          case RETURN:
            onSubmit(text);
            setCursorPosition(text.length());
            unfocus();
            break;
          case DELETE:
            if (selection) {
              erase(selectionStart, selectionEnd);
            } else {
              if (cursorPosition < text.length()) {
                erase(cursorPosition, cursorPosition + 1);
              }
            }
            break;
          case BACKSPACE:
            if (selection) {
              erase(selectionStart, selectionEnd);
            } else {
              if (cursorPosition > 0) {
                erase(cursorPosition - 1, cursorPosition);
              }
            }
            break;
          case TAB:
            mgr.cycleFocus(this);
            break;
          case 'c':
          case 'C':
          case CTRL_C:
            if (modifiers[controlIndex]) {
              copySelection();
              break;
            }
            doDefault = true;
            break;
          case 'v':
          case 'V':
          case CTRL_V:
            if (modifiers[controlIndex]) {
              String pastedText = getClipboardString().replaceAll("\n", "");
              if (!pastedText.isEmpty()) {
                erase(selectionStart, selectionEnd);
                insertAt(pastedText, cursorPosition);
              }
              break;
            }
            doDefault = true;
            break;
          case 'x':
          case 'X':
          case CTRL_X:
            if (modifiers[controlIndex]) {
              copySelection();
              erase(selectionStart, selectionEnd);
              break;
            }
            doDefault = true;
            break;
          case 'a':
          case 'A':
          case CTRL_A:
            if (modifiers[controlIndex]) {
              selectAll();
              break;
            }
            doDefault = true;
            break;
          default:
            doDefault = true;
        }

        if (doDefault) {
          if (!(modifiers[controlIndex] || modifiers[altIndex])) {
            erase(selectionStart, selectionEnd);
            insertAt(mgr.app.key + "", cursorPosition);
          } else {
            Console.outln("Unknown key: " + (mgr.app.key + 0));
          }
        }
      } else {
        boolean shiftPressed = modifiers[shiftIndex];
        switch (mgr.app.keyCode) {
          case HOME:
          case UP:
            if (shiftPressed) {
              setSelection(cursorPosition - selectionEnd);
            } else {
              setCursorPosition(0);
            }
            break;
          case END:
          case DOWN:
            if (shiftPressed) {
              setSelection(text.length() - cursorPosition);
            } else {
              setCursorPosition(text.length());
            }
            break;
          case LEFT:
            if (shiftPressed) {
              setSelection(selectionPosition - cursorPosition - 1);
            } else {
              if (isSelection()) {
                setCursorPosition(selectionStart);
              } else {
                setCursorPosition(cursorPosition - 1);
              }
            }
            break;
          case RIGHT:
            if (shiftPressed) {
              setSelection(selectionPosition - cursorPosition + 1);
            } else {
              if (isSelection()) {
                setCursorPosition(selectionEnd);
              } else {
                setCursorPosition(cursorPosition + 1);
              }
            }
            break;
          case CONTROL:
            modifiers[controlIndex] = true;
            break;
          case SHIFT:
            modifiers[shiftIndex] = true;
            break;
          case ALT:
            modifiers[altIndex] = true;
            break;
          default:
            Console.outln("Unknown key code: " + mgr.app.keyCode);
        }
      }

      if (getTextWidth(getDisplayText()) > textBuffer.width) {
        textAlign = RIGHT;
      } else {
        textAlign = LEFT;
      }
    }
    updateSelection();
    
    super.keyPressed();
  }

  protected void keyReleased() {
    if (mgr.app.key == CODED) {
      switch (mgr.app.keyCode) {
        case CONTROL:
          modifiers[controlIndex] = false;
          break;
        case SHIFT:
          modifiers[shiftIndex] = false;
          break;
        case ALT:
          modifiers[altIndex] = false;
          break;
      }
    } else {
      if (mgr.app.key == TAB) {
        mgr.canTab = true;
      }
    }
    super.keyReleased();
  }

  private void updateSelection() {
    selectionStart = PApplet.min(cursorPosition, selectionPosition);
    selectionEnd = PApplet.max(cursorPosition, selectionPosition);
  }

  /**
   * Runs whenever <code>ENTER</code> is pressed.
   * 
   * @param currentText the current text in the textbox
   */
  public void onSubmit(String currentText) {
    text = "";
  }

  /**
   * Inserts the specified text into this text box at the specified index.
   * 
   * @param toInsert the text to insert
   * @param index the index to insert the text at
   */
  protected void insertAt(String toInsert, int index) {
    text = text.substring(0, index) + toInsert + text.substring(index, text.length());
    setCursorPosition(index + toInsert.length());
  }

  /**
   * Erases the text between the two specified indices.
   * 
   * @param startIndex the start index of the selection to erase
   * @param endIndex the end index of the selection to erase
   */
  public void erase(int startIndex, int endIndex) {
    text = text.substring(0, startIndex) + text.substring(endIndex, text.length());
    setCursorPosition(cursorPosition = startIndex);
  }

  /**
   * Returns the current text contained in this <code>UITextBox</code>.
   * 
   * @return the current text contained in this <code>UITextBox</code>.
   */
  public String getText() {
    return text;
  }

  /**
   * Copies any selected text to the system clipboard.
   */
  public void copySelection() {
    int selectionStart = PApplet.min(cursorPosition, selectionPosition);
    int selectionEnd = PApplet.max(cursorPosition, selectionPosition);
    if (selectionStart >= 0 && selectionEnd <= text.length()) {
      String selection = text.substring(selectionStart, selectionEnd);
      if (!selection.isEmpty()) {
        StringSelection data = new StringSelection(selection);
        toolkit.getSystemClipboard().setContents(data, data);
        Console.outln("Text \"" + text.substring(selectionStart, selectionEnd) + "\" copied to clipboard!!!");
      }
    }
  }

  private String getClipboardString() {
    Transferable data = toolkit.getSystemClipboard().getContents(null);

    DataFlavor flavor = DataFlavor.stringFlavor;
    if (data != null && data.isDataFlavorSupported(flavor)) {
      try {
        return (String) data.getTransferData(flavor);
      } 
      catch (Exception e) {
        Console.errln("Clipboard data format unsupported!");
      }
    }
    return "";
  }

  /**
   * Sets the position of the cursor (the location where text is currently inputted).
   * 
   * @param position the new cursor index
   */
  public void setCursorPosition(int position) {
    cursorPosition = PApplet.constrain(position, 0, text.length());
    selectionPosition = cursorPosition;
  }

  /**
   * Selects the text in this textbox in the specified range.
   * 
   * @param length the length of the new selection
   */
  public void setSelection(int length) {
    selectionPosition = PApplet.constrain(cursorPosition + length, 0, text.length());
  }

  /**
   * Selects all text in this textbox.
   * 
   * @see setSelection
   */
  public void selectAll() {
    setCursorPosition(text.length());
    setSelection(-text.length());
  }

  /**
   * Returns whether or not any text is selected
   * 
   * @see setSelection
   */
  private boolean isSelection() {
    return focused && (cursorPosition != selectionPosition);
  }

  public void focus() {
    if (!focused) {
      cursorShowing = true;
      cursorLastToggled = mgr.app.millis();

      selectAll();
    }

    focused = true;
    shouldRefresh = 2;
    backgroundShade = 255;
  }

  public void unfocus() {
    if (focused) {
      shouldRefresh = 1;
      backgroundShade = defaultbackgroundShade;

      setCursorPosition(0);
      cursorShowing = false;
    }
    focused = false;
  }

  private int getTextWidth(String text) {
    return Math.round(textBuffer.textWidth(text));
  }

  private int getClosestIndex(int xPos) {
    String displayText = getDisplayText();
    for (int i = 0; i < displayText.length(); i++) {
      if (xPos - x - 4 <= getTextWidth(displayText.substring(0, i)) + getTextWidth(displayText.charAt(i) + "") / 2) {
        return i;
      }
    }

    return displayText.length();
  }
}
