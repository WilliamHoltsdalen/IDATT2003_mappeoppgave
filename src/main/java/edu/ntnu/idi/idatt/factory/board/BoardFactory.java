package edu.ntnu.idi.idatt.factory.board;

import edu.ntnu.idi.idatt.model.board.Board;

public interface BoardFactory {

  Board createBoard(String variant);

  Board createBoardFromFile(String filePath);

  Board createBlankBoard(int rows, int columns);
}
