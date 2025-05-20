# IDATT2003 - BoardGame Application
This project is developed as part of the IDATx2003 (Programming 2) course at NTNU. It focuses on creating a digital board game framework where players can load game boards, create or import players, and simulate classic board games like Snakes and Ladders.

## V3.2.0 Features
In this V3.2.0 release, the following core functionalities are implemented:
- **JavaFX GUI**: A modern and interactive graphical user interface.
- **MVC Architecture**: Clearly structured Model-View-Controller pattern for separation of concerns.
- **Modular Game Design**: Supports multiple game types (currently Chutes and Ladders, and Ludo).
- **Player Management**: Create new players, assign colors, and manage bot players. Load and save player lists to CSV.
- **Configurable Boards**: Load custom game boards from JSON files or use preloaded classic layouts. It is also possible to create new and edit existing game boards.
- **Game Mechanics**: In addition to game logic that is specific to each game type, the application has: 
  - Animated dice rolling.
  - Animated player movement on the board.
  - Tile actions (e.g., ladders, slides, portals).
- **Observer Pattern**: For real-time UI updates in response to game state changes.

## Tech Stack
The application is built using the following technologies:
- **Java** (version 21)
- **JavaFX** (for the graphical user interface)
- **Maven** (for dependency management and build automation)
- **JUnit** (for unit testing)
- **Gson** (for JSON serialization/deserialization)
- **SLF4J** (for logging)
- **Figma** (for initial wireframe design)

## Installation Guide
1. Clone or download this repository.  
2. Open the project in an IDE (e.g., IntelliJ) with **Maven** support, or a terminal with access to `mvn` commands.
3. Make sure you have **Java 21** installed.
4. Run `mvn clean package` to run all tests and build the application, and `mvn javafx:run` to launch the application.

## User Manual
The application features a **JavaFX GUI** allowing users to:
1. **Select a Game**: Choose between the available games: 
    - Chutes and Ladders
    - Ludo
2. **Set Up Game**:
   - Select a game board (import from a JSON file or select a preloaded layout). You can also create new boards and edit existing ones by entering the board creator view from the game menu. 
   - Create players manually (assigning names, colors, and designating if they are bot controlled), or load players from a CSV file. Optionally save the current list of players to a CSV file.
   
3. **Play Game**:
   - Observe dice rolls and player token movements on the visual game board.
   - View player information, current scores/positions, and game logs.
   - Interact with game controls (e.g., roll dice, restart, quit).

## Future Development
Planned improvements and additional features include:
- **Additional Board Games** 
- **Network multiplayer capabilities**
- **Support for other devices such as phones and tablets**

## Contributors
This project is developed by the following team members:
- [Vetle Nilsen](https://github.com/vetnil1)  
- [William Holtsdalen](https://github.com/williamholtsdalen)  
