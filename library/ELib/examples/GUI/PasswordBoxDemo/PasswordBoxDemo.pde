/**
 * PasswordBox Demo
 * Author: Julian Dominguez-Schatz
 * Date: 22/02/16
 * Description: The UIPasswordBox class is an extension of the base UITextBox class.
 * A password box hides its input from the user using a preset character.
 */

import com.ekkongames.elib.gui.*;

UIPasswordBox box1;
UIManager manager;

void setup() {
  size(500, 500);

  manager = new UIManager(this);

  box1 = new UIPasswordBox(50, 20);
  manager.add(box1);
}

void draw() {
  background(204);
  
  fill(0);
  text("Text: " + box1.getText(), 50, 90);
}