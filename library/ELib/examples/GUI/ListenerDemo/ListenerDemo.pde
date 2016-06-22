/**
 * Listener Demo
 * Author: Julian Dominguez-Schatz
 * Date: 21/06/16
 * Description: Another key feature of the ELib library is the
 * ability to listen to certain events occurring within an element.
 * This example demonstrates how to track two of these events: key
 * press and release.
 */

import com.ekkongames.elib.gui.*;

UITextBox box1;
UIManager manager;

void setup() {
  size(500, 500);

  manager = new UIManager(this);

  box1 = new UITextBox(50, 20);
  box1.addListener(new UIListener(UIListener.KEY_PRESSED) {
    public void onTrigger() {
      println("The key " + key + " was pressed.");
    }
  });
  box1.addListener(new UIListener(UIListener.KEY_RELEASED) {
    public void onTrigger() {
      println("The key " + key + " was released.");
    }
  });

  manager.add(box1);
}

void draw() {
}