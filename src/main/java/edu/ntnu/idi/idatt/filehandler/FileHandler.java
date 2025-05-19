package edu.ntnu.idi.idatt.filehandler;

import java.io.IOException;
import java.util.List;

/**
 * Interface for handling files.
 *
 * <p>This interface defines methods for reading from and writing to a file. It uses a generic
 * type T to represent the type of the file contents (e.g. Player, Board, etc.).
 */
public interface FileHandler<T> {

  /**
   * Reads the contents of a file and returns the file contents.
   * The return type can be anything, as long as it is a valid object.
   *
   * @param filePath the path to the file
   * @return The object read from the file
   * @throws IOException if an error occurs while reading the file
   */
  Object readFile(String filePath) throws IOException;

  /**
   * Writes contents to a file, using the given file path.
   *
   * @param filePath the path to the file
   * @param contents the list of contents to write to the file
   * @throws IOException if an error occurs while writing to the file
   */
  void writeFile(String filePath, List<T> contents) throws IOException;
}
