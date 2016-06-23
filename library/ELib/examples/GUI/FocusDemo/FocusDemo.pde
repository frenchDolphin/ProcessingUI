/**
 * Focus Demo
 * Author: Julian Dominguez-Schatz
 * Date: 22/06/16
 * Description: One UIElement per UIManager can have "focus";
 * that is, can receive events.
 */

import com.ekkongames.elib.gui.*;

UITextBox box1;
UICheckBox box2;
UIManager manager;

void setup() {
  size(500, 500);

  manager = new UIManager(this);

  box1 = new UITextBox(50, 20);
  manager.add(box1);

  box2 = new UICheckBox(50, 70);
  manager.add(box2);
}

void draw() {
}

void keyPressed() {
  box2.focus();
}