/**
 * TextField Demo
 * Author: Julian Dominguez-Schatz
 * Date: 17/01/16
 * Description: One of the most significant features of the ELib library
 * is the inclusion of a fully functional text field. This includes selecting
 * text, as well as copying and pasting.
 */

import com.ekkongames.elib.gui.*;

UITextBox box1;
UIManager manager;

void setup() {
  size(500, 500);

  manager = new UIManager(this);

  box1 = new UITextBox(50, 20);
  manager.add(box1);
}

void draw() {
}