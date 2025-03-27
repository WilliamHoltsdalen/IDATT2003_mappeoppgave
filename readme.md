# IDATT2003 - BoardGame Application
This project is developed as part of the IDATx2003 (Programming 2) course at NTNU. It focuses on creating a digital board game framework where players can load game boards, create or import players, and simulate classic board games like Snakes and Ladders.

## V2.x.x Features
In this V2.x.x release, the following core functionalities are implemented:
- **Multiple Board Variants**: Load boards from JSON files or use hardcoded “classic” layouts.
- **Player Management**: Read and write player lists to CSV files.
- **Game Mechanics**: Roll dice, move players, and apply tile actions (like slides and ladders).
- **Text-Based UI**: A simple console interface for testing the core logic and functionality.
- **Design Patterns**: Includes the Factory and Observer patterns to keep the design modular.

## Tech Stack
The application is built using the following technologies:
- **Java** (version 21)
- **JavaFX** (planned for the GUI in a future release)
- **Maven** (for dependency management)
- **JUnit** (for unit testing)
- **Gson** (for JSON serialization/deserialization)
- **Figma** (for wireframe design)

## Installation Guide
1. Clone or download this repository.  
2. Open the project in an IDE (e.g., IntelliJ) with **Maven** support.
3. Make sure you have **Java 21** installed.
4. Run `mvn clean compile javafx:run` to build and launch the application (GUI in future versions).
5. For the text-based interface, you can simply run the main class (`App`) from your IDE or via Maven.

## User Manual
Currently, the application provides a **text-based interface** to:
1. Load a game board (from file or hardcoded in the `BoardFactory`).
2. Load players from CSV, or create them manually by entering names and colors.
3. Start the game and observe dice rolls, player moves, and tile actions in the console.

In future versions, a **JavaFX GUI** will be introduced.

## Future Development
Some planned improvements and additional features include:
- **Full JavaFX GUI** 
- **Support for even more board variants** with advanced tile actions (e.g., teleport, lose a turn, etc.).
- **Refined Observer** integration for real-time UI updates.
- **Additional Board Games** or custom game logic (Monopoly-like or similar).

## Contributors
This project is developed by the following team members:
- [Vetle Nilsen](https://github.com/vetnil1)  
- [William Holtsdalen](https://github.com/williamholtsdalen)  
