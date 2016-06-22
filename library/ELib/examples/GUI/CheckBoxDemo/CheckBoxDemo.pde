/**
 * Check Box Demo
 * Author: Julian Dominguez-Schatz
 * Date: 21/06/16
 * Description: A check box is a simple element that can be toggled
 * between two states: checked and not checked.
 */

import com.ekkongames.elib.gui.*;

UICheckBox box1;
UIManager manager;

void setup() {
  size(500, 500);

  manager = new UIManager(this);

  box1 = new UICheckBox(50, 20);
  manager.add(box1);
}

void draw() {
}