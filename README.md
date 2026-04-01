# Tank 2025 - JavaFX Arcade Game
Tank 2025 is a retro-style 2D arcade game built with JavaFX. The project demonstrates core game development concepts including game loops, collision detection, and automated enemy AI behavior.


# 🚀 Key Features
* Dynamic Game Loop: Powered by JavaFX AnimationTimer to ensure smooth rendering and state updates.

* Autonomous Enemy AI: Enemy tanks feature randomized movement patterns and automatic firing logic.

* Precise Collision Detection: Uses Rectangle2D bounds to manage interactions between tanks, bullets, and walls.

* Visual Feedback System: Features dual-layered explosion animations for both unit destruction (large) and environmental impacts (small).

* Game State Management: Includes real-time score tracking, a life-based respawn system, and interactive Pause/Game Over screens.

## 🎮 Controls
* Action,Key
* Move Tank,"Arrow Keys (Up, Down, Left, Right)"
* Fire Bullet,X
* Pause / Resume,P
* Restart Game,R (During Pause or Game Over)
* Exit Application,ESC


# 🛠 Technical Stack
* Language: Java

* Graphics: JavaFX (Canvas API, GraphicsContext)

* Architecture: Object-Oriented Programming (OOP)


## 📂 Project Structure
* Main.java: The application entry point that launches the JavaFX stage.

* Game.java: The engine of the game, managing the loop, UI labels, and object collections.

* PlayerTank.java: Handles user input and player-specific movement physics.

* EnemyTank.java: Contains the AI logic for enemy behavior and randomized pathing.

* Bullet.java: Manages projectile trajectory and lifespan.

* Explosion.java: Controls the rendering and timing of visual effects.

* Wall.java: Static obstacles that define the battlefield layout.
