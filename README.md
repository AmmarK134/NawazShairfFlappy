# Flappy Bird (Pure Java)

A production-ready Flappy Bird clone built entirely in pure Java using Swing/AWT. No third-party libraries required - runs on Java 17+ with just the JDK.

## Features

- **Pure Java Desktop App**: Uses Swing (JFrame, JPanel) with no external dependencies
- **60 FPS Game Loop**: Smooth gameplay using `javax.swing.Timer`
- **Bird Skin Selection**: Choose from 3+ different bird skins before playing
- **Full Gameplay**: 
  - Bird physics with gravity and flap mechanics
  - Randomly positioned pipes with collision detection
  - Score system with high score persistence
  - Pause/Resume functionality
- **Sound Effects**: WAV sound effects for flap, point, hit, and die events
- **High Score Persistence**: Saves high score to `~/.flappybird/highscore.txt`
- **Double-Clickable JAR**: Produces an executable JAR that runs on Windows, macOS, and Linux

## Requirements

- Java 17 or higher
- Maven 3.6+ (for building)

## Building

### Generate Assets (First Time Only)

If you're building from source, you'll need to generate placeholder assets:

```bash
# Compile asset generators
javac -d target/classes -sourcepath src/main/java src/main/java/com/example/flappy/AssetGenerator.java
javac -d target/classes -sourcepath src/main/java src/main/java/com/example/flappy/SoundGenerator.java

# Generate placeholder images
java -cp target/classes com.example.flappy.AssetGenerator

# Generate placeholder sounds
java -cp target/classes com.example.flappy.SoundGenerator
```

### Build the JAR

```bash
mvn clean package
```

This will create `target/flappy-bird-java-1.0.0.jar` with all resources bundled.

## Running

### Option 1: Double-Click (Windows/macOS)

Simply double-click the JAR file:
```
target/flappy-bird-java-1.0.0.jar
```

### Option 2: Command Line

```bash
java -jar target/flappy-bird-java-1.0.0.jar
```

## Controls

### In-Game
- **Space / Up Arrow / Left Click**: Flap (make bird jump)
- **P**: Pause/Resume
- **R**: Restart (when game over)

### Bird Selection Screen
- **Click a bird**: Select bird skin
- **Click "Start Game" button**: Begin playing

### Game Over Screen
- **R**: Restart game
- **Click "Play Again" button**: Restart game
- **Click "Quit" button**: Exit game

## Game Mechanics

- **Gravity**: Bird falls continuously under gravity
- **Flap**: Each flap gives the bird an upward impulse
- **Pipes**: Spawn at regular intervals with randomized gap positions
- **Scoring**: Score increases when passing through a pipe pair
- **Collisions**: Game ends when bird hits a pipe, ground, or ceiling
- **High Score**: Automatically saved and displayed

## Project Structure

```
src/
├── main/
│   ├── java/com/example/flappy/
│   │   ├── Main.java              # Entry point
│   │   ├── GameFrame.java         # Main window frame
│   │   ├── GamePanel.java         # Main game panel with game loop
│   │   ├── SelectionPanel.java    # Bird skin selection screen
│   │   ├── Bird.java              # Bird entity with physics
│   │   ├── Pipe.java              # Pipe entity
│   │   ├── PipeManager.java       # Pipe spawning and management
│   │   ├── GameState.java         # Game state enumeration
│   │   ├── Constants.java         # Game constants
│   │   ├── AssetLoader.java       # Resource loading utility
│   │   ├── SoundPlayer.java       # Sound playback manager
│   │   ├── HighScoreStore.java    # High score persistence
│   │   ├── AssetGenerator.java    # Utility to generate placeholder images
│   │   └── SoundGenerator.java    # Utility to generate placeholder sounds
│   └── resources/
│       ├── images/                # Game images (PNG)
│       │   ├── background.png
│       │   ├── bird1.png
│       │   ├── bird2.png
│       │   ├── bird3.png
│       │   ├── pipe_top.png
│       │   ├── pipe_bottom.png
│       │   └── ground.png
│       └── sounds/                # Sound effects (WAV)
│           ├── flap.wav
│           ├── point.wav
│           ├── hit.wav
│           └── die.wav
```

## Technical Details

- **Window Size**: 432×768 pixels (portrait phone-style), non-resizable
- **Frame Rate**: 60 FPS using `javax.swing.Timer` with ~16ms delay
- **Rendering**: Double buffering enabled, Graphics2D with antialiasing
- **Physics**: Integer-based coordinate system with floating-point velocity
- **Sound**: Pre-loaded clips using `javax.sound.sampled.Clip`
- **Assets**: Loaded from classpath via `ClassLoader.getResourceAsStream()`

## Customization

Game parameters can be tuned in `Constants.java`:
- Gravity, flap impulse, terminal velocity
- Pipe speed, gap size, spawn interval
- Window dimensions
- Font sizes

## Troubleshooting

### Assets Not Loading
If you see error dialogs about missing assets:
1. Ensure `src/main/resources/images/` and `src/main/resources/sounds/` exist
2. Run the asset generators (see Building section)
3. Rebuild the JAR with `mvn clean package`

### Sound Issues
- Sound files are optional - the game will run without them
- If sounds don't play, check that `.wav` files are in `src/main/resources/sounds/`

### High Score Not Saving
- High score is saved to `~/.flappybird/highscore.txt`
- On Windows: `C:\Users\<username>\.flappybird\highscore.txt`
- On Linux/macOS: `~/.flappybird/highscore.txt`
- Ensure the directory is writable

## License

This is a demonstration project. Feel free to use and modify as needed.

## Credits

Built as a pure Java desktop application using only JDK Swing/AWT components.

