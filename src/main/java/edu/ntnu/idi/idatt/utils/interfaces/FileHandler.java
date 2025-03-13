package edu.ntnu.idi.idatt.utils.interfaces;

import java.io.IOException;
import java.util.List;

/**
 * <h3>Interface for handling files.</h3>
 *
 * <p>This interface defines methods for reading from and writing to a file. It uses a generic
 * type T to represent the type of the file contents (e.g. Player, Board, etc.).
 */
public interface FileHandler<T> {

  /**
   * Reads the contents of a file and returns the file contents
   *
   * @param filePath the path to the file
   * @return A list of the file contents
   * @throws IOException if an error occurs while reading the file
   */
  List<T> readFile(String filePath) throws IOException;

  /**
   * Writes contents to a file, using the given file path.
   *
   * @param filePath the path to the file
   * @param contents the list of contents to write to the file
   * @throws IOException if an error occurs while writing to the file
   */
  void writeFile(String filePath, List<T> contents) throws IOException;
}
