package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.textuserinterface.view.TextUserInterface;

public class Main {

  public static void main(String[] args) {
    TextUserInterface tui = new TextUserInterface();
    tui.init();
    tui.start();
  }
}