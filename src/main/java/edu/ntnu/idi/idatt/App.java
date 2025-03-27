package edu.ntnu.idi.idatt;

import edu.ntnu.idi.idatt.view.tui.TextUserInterface;

public class App {

  public static void main(String[] args) {
    TextUserInterface tui = new TextUserInterface();
    tui.init();
    tui.run();
  }
}