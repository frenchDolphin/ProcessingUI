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
public class UITextBox_old extends UIElement {
  /* -----TODO-----
   * FIX selection (i.e. if something is already selected, and up/down
   * is pressed, move selection PROPERLY.
   */

  public int padding = 5;

  protected String text = "";
  private int textAlign = LEFT;
  public int defaultbackgroundShade = 245;
  public int backgroundShade = defaultbackgroundShade;

  private int cursorLastShowed = 0;
  private boolean cursorShowing = false;

  private int selectionStart = 0;
  private int selectionEnd = 0;

  private int shouldRefresh = 1;
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
  public UITextBox_old(int x, int y) {
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
  public UITextBox_old(int x, int y, int xWidth, int yHeight) {
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

  void update() {
    if (mgr.app.millis() - cursorLastShowed >= 500) {
      cursorLastShowed = mgr.app.millis();
      cursorShowing = !cursorShowing;
    }
  }

  void render() {
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

      if (!isNoSelection()) {
        textBuffer.noStroke();
        textBuffer.fill(0, 55, 255);
        textBuffer.rect(getTextWidth(text.substring(0, selectionStart)), padding, getTextWidth(text.substring(selectionStart, selectionEnd)), textBuffer.height - (padding * 2));
      }

      char[] textChars = text.toCharArray();
      if (textAlign == LEFT) {
        renderCursor(getTextWidth(text.substring(0, selectionStart)));

        for (int i = 0; i < textChars.length; i++) {
          textBuffer.fill(0);
          if (i >= selectionStart && i < selectionEnd) {
            textBuffer.fill(255);
          }

          textBuffer.text(textChars[i], getTextWidth(text.substring(0, i)), 39);
        }
      } else if (textAlign == RIGHT) {
        renderCursor(textBuffer.width - getTextWidth(text.substring(selectionStart, text.length())));

        for (int i = textChars.length - 1; i >= 0; i--) {
          textBuffer.fill(0);
          if (i >= selectionStart && i < selectionEnd) {
            textBuffer.fill(255);
          }

          textBuffer.text(textChars[i], textBuffer.width - getTextWidth(text.substring(i, text.length())), 39);
        }
      }

      textBuffer.endDraw();

      renderLayer.image(textBuffer, padding, 0);
      renderLayer.endDraw();
    }
    mgr.app.image(renderLayer, x, y);
  }

  private void renderCursor(int cx) {
    if (isNoSelection() && cursorShowing) {
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

  int hoverState = 0;
  void mouseMoved() {
    if (mgr.isTop(this, mgr.mouseX, mgr.mouseY)) {
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
  }

  void mouseDragged() {
    int index = getClosestIndex(mgr.mouseX);

    if (index > selectionStart) {
      setSelection(selectionStart, index);
    } else {
      setSelection(index, selectionEnd);
    }
  }

  void mousePressed() {
    if (mgr.isTop(this, mgr.mouseX, mgr.mouseY) && mgr.mouseButton == LEFT) {
      focus();
      setCursorPosition(getClosestIndex(mgr.mouseX));
    } else if (focused) {
      unfocus();
    }
  }

  void mouseReleased() {
  }

  void keyPressed() {
    if (focused) {
      if (mgr.key != CODED) {
        boolean doDefault = false;
        switch (mgr.key) {
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
          if (!isNoSelection()) {
            erase(selectionStart, selectionEnd);
            setCursorPosition(selectionStart);
          } else {
            if (selectionEnd < text.length()) {
              erase(selectionEnd, selectionEnd + 1);
              setCursorPosition(selectionEnd);
            }
          }
          break;
        case BACKSPACE:
          if (!isNoSelection()) {
            erase(selectionStart, selectionEnd);
            setCursorPosition(selectionStart);
          } else {
            if (selectionStart > 0) {
              erase(selectionStart - 1, selectionStart);
              setCursorPosition(selectionStart - 1);
            }
          }
          break;
        case TAB:
          mgr.cycleFocus(this);
          break;
        case 'c':
        case 'C':
        case 0x3:
          if (modifiers[controlIndex]) {
            copySelection();
            break;
          }
          doDefault = true;
          break;
        case 'v':
        case 'V':
        case 0x16:
          if (modifiers[controlIndex]) {
            String pastedText = getClipboardString().replaceAll("\n", "");
            if (!pastedText.isEmpty()) {
              erase(selectionStart, selectionEnd);
              insertAt(pastedText, selectionStart);
            }
            setCursorPosition(selectionStart + pastedText.length());
            break;
          }
          doDefault = true;
          break;
        case 'x':
        case 'X':
        case 0x24:
          if (modifiers[controlIndex]) {
            copySelection();
            erase(selectionStart, selectionEnd);
            break;
          }
          doDefault = true;
          break;
        case 'a':
        case 'A':
        case 0x1:
          if (modifiers[controlIndex]) {
            setSelection(0, text.length());
            break;
          }
          doDefault = true;
          break;
        default:
          doDefault = true;
        }

        if (doDefault) {
          if (!modifiers[controlIndex] && !modifiers[altIndex]) {
            erase(selectionStart, selectionEnd);
            insertAt(mgr.key + "", selectionStart);
            setCursorPosition(selectionStart + 1);
          } else {
            Console.outln("Unknown key: " + (mgr.key + 0));
          }
        }
      } else {
        boolean shiftPressed = modifiers[shiftIndex];
        switch (mgr.keyCode) {
        case UP:
          if (shiftPressed) {
            setSelection(0, selectionEnd);
          } else {
            setCursorPosition(0);
          }
          Console.outln(selectionStart - selectionEnd);
          break;
        case DOWN:
          if (shiftPressed) {
            setSelection(selectionStart, text.length());
          } else {
            setCursorPosition(text.length());
          }
          break;
        case LEFT:
          int startLeft = PApplet.constrain(selectionStart - 1, 0, text.length());
          if (shiftPressed) {
            setSelection(startLeft, selectionEnd);
          } else {
            if (isNoSelection()) {
              setCursorPosition(startLeft);
            } else {
              setCursorPosition(selectionStart);
            }
          }
          break;
        case RIGHT:
          int startRight = PApplet.constrain(selectionStart + 1, 0, text.length());
          int end = PApplet.constrain(selectionEnd + 1, 0, text.length());
          if (shiftPressed) {
            setSelection(startRight, selectionEnd);
          } else {
            if (isNoSelection()) {
              setCursorPosition(startRight);
            } else {
              setCursorPosition(selectionEnd);
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
          Console.outln("Unknown key code: " + mgr.keyCode);
        }
      }

      if (getTextWidth(text) > textBuffer.width) {
        textAlign = RIGHT;
      } else {
        textAlign = LEFT;
      }
    }
  }

  void keyReleased() {
    if (mgr.key == CODED) {
      switch (mgr.keyCode) {
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
      if (mgr.key == TAB) {
        mgr.canTab = true;
      }
    }
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
  }

  /**
   * Erases the text between the two specified indices.
   * 
   * @param startIndex the start index of the selection to erase
   * @param endIndex the end index of the selection to erase
   */
  public void erase(int startIndex, int endIndex) {
    text = text.substring(0, startIndex) + text.substring(endIndex, text.length());
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
        Console.errln("Clipboard data unsupported!");
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
    setSelection(position, position);
  }

  /**
   * Selects the text in this textbox in the specified range.
   * 
   * @param newStart the start index of the new selection
   * @param newEnd the end index of the new selection
   */
  public void setSelection(int newStart, int newEnd) {
    if (newStart > newEnd) {
      newStart -= newEnd;
      newEnd += newStart;
      newStart = newEnd - newStart;
    }

    selectionStart = newStart;
    selectionEnd = newEnd;
  }

  private boolean isNoSelection() {
    return focused && selectionStart == selectionEnd;
  }

  public void focus() {
    if (!focused) {
      cursorShowing = true;
      cursorLastShowed = mgr.app.millis();

      setSelection(0, text.length());
    }

    shouldRefresh = 2;
    focused = true;
    backgroundShade = 255;
  }

  public void unfocus() {
    if (focused) {
      shouldRefresh = 1;
      focused = false;
      backgroundShade = defaultbackgroundShade;

      setCursorPosition(0);
      cursorShowing = false;
    }
  }

  private int getTextWidth(String text) {
    return Math.round(textBuffer.textWidth(text));
  }

  private int getClosestIndex(int xPos) {
    for (int i = 0; i < text.length(); i++) {
      if (xPos - x - 4 <= getTextWidth(text.substring(0, i)) + getTextWidth(text.charAt(i) + "") / 2) {
        return i;
      }
    }

    return text.length();
  }
}