<img src="icon.iconset/icon_512x512@2x.png" sizes="" width="200" height="200" alt=""/>

# UM Project Golf

This project is a numerical solver for ordinary differential equations using the Runge-Kutta fourth order method.
It is written in Java and built with Maven.

## Getting Started

For running the project, you need to have Maven installed.
You can download it from [here](https://maven.apache.org/download.cgi).
After installing Maven, run the following command in the project directory:
```mvn clean package```
This will create a target directory with the jar file in it.
As well as the compiled classes in the target/classes directory.
But also the java doc in the target/site/apidocs directory.

## JavaDoc

The Java doc website can created by running the following command in the project directory:
```mvn javadoc:javadoc```
Then you can open the index.html file in the target/site/apidocs directory.

## Prerequisites

The project runs on java 21.


## Functioning

For running the game, you will have to run the launcher class in the game directory.
Specification for Mac users:
- When running the class, you will have to pass as java arguments: -XstartOnFirstThread

### DISCLAIMER: 

- The keyboard bindings map out a standard QWERTY keyboard.
  So the keys might be different on a different keyboard layout.
  Numbers should be at the same place, but the special characters might be different.
  So if you are having trouble with the controls, you can change them in the Input class in the GolfGame class.
- The game is not optimized for low-end computers.

## GamePlay

The goal of the game is to get the ball in the hole with the least number of shots.
The ball will move, according to the laws of physics.
If you find the ball to slide too much, you can increase the friction in the Consts class.
When the ball is in the hole, the game will display a message with the number of shots.
As well as a small animation.

## Ball Movement

For moving the ball, the approach is a TextField one.
You will have to input the initial velocity of the ball as a vector
v = (vx, vz), x and z being the horizontal components.
The vertical component is calculated by the HeightFunction.
So as vector works, you can input negative values to make the ball go backward.
Floating point values are accepted.
Then when pressing the Apply button, the ball will move, according to the input.

Also considering the critical difficulty of inputting the right angle with the +/-
a directional Arrow is placed in the game.
It will provide you with the direction of the ball. 
So you can adjust the angle accordingly.

## Bots

The game offers a bot mode.
The bot will calculate the best path to the hole and move the ball accordingly.
The bots will have their entire path calculated before starting the game.
You can play a shot of the AI bot ball with the key 1.
Or you can play a shot of the Rule-Based bot ball with the key 2.
You can also reset the ball with the R key
(note that this will also reset the players' ball if there are more than one).
So if you select the bots in the start menu, the game will start with the bot mode.
Therefore, it will calculate the complete path and move the ball to the hole.
Making it longer to run when pressing the Start button.
(A little info message will be displayed in the console when the bot is done calculating)
Also, The start Button will have a little message to indicate the time it will take to calculate the path.

## Debug Mode

In debug mode, everything is modifiable.
For ease of Examination.
A small issue we have is that if you haven't selected the start and hole position before starting the game.
The path will not be calculated.
And the bot will serve no purpose.
As the start and hole positions are not defined.
Therefore, you can put predefined positions in the Consts class.
Or, if you really want to define the start and hole position in the game.
You will have to return to the start menu by the in-game menu.
And then select the bots again.
Then it will compute the path and move the ball to the hole.

## Multiplayer

The game offers a multiplayer mode.
In this mode, two players will play against each other.
The player who gets the ball in the hole the fastest wins.
The game will alternate between the players.
So the first player will play first, then the second player.
When a user completes the hole, the game will continue on for the other player.
Note that even when a player is done, he can still play (no block yet).
So if he wants, he can explore the map for the fun of it.
An animation will be displayed when a player completes the hole.


## Gui elements

The game offers a start menu with the following options:
- Start: Start the game with the selected options
- Change Terrain: Change the terrain to a new one (randomly generated)
- Exit: Exit the game
- Multiplayer: Start the game in multiplayer mode (two players)
- Bots: Start the game with the bot mode
- Debug: Start the game in debug mode
- Sound: Turn the sound on or off

The game also offers an in-game menu with the following options:
- Resume: Resume the game
- Return to Start Menu: Return to the start menu
- Sound: Turn the sound on or off
- Exit: Exit the game

## Controls

The game offers two control methods:
The first one is the keyboard and mouse:
- The player can move the camera with the WASD keys
- The player can go up and down with the space and shift keys
- The player can rotate the camera with the mouse by clicking and dragging with the right mouse button
- The player can teleport to the ball with the Q key
- The player can teleport to the hole with the down key
- The player can teleport to start with the up key
- The player can reset every ball with the R key (this will only reset the balls if ALL balls aren't actively moving, bots and players)
- The player can get a surprise with the F key
- While in-game, the player can open the in-game menu with the escape key
- For the ball bot movement
  - the player can do a shot of the AI bot ball with the key 1
  - the player can do a shot of the Rule-Based bot ball with the key 2

In Debug mode:
- The player can select the start position with the left key
- The player can select the hole position with the right key

Doing will cause the terrain to regenerate and the ball to be placed at the start position.
And the flag to be placed at the hole position.
Note that the position given is dependent on the camera position.
So you'd have to move the camera to the desired position before pressing the key.
Finally, doing so will create the path with an A* algorithm.
So make sure that the start and hole positions are reachable.
(with avoiding sand and water, which are not reachable by the algorithm)
You can also decide to create the trees automatically or not by changing the boolean in the code.
Otherwise, you can place them manually by pressing the T key.

The second one is the Consts class:
In this class you can change pretty much everything in the game.
- Terrain size
- Simplex noise parameters
- Texture quality (vertex count)
- Maximum height of the terrain
- Number of trees
- The framerate
- The FOV
- The near and far plane
- The specular power
- The player height
- The mouse sensitivity
- The movement speed
- The default color
- The ambient light

And for examination purposes:
- You can change the HeightFunction to a different one
- the gravity
- the mass of the ball
- the target radius
- If you want trees or not
- The distance from the hole (up and down)
- The size of the green
- The maximum speed of the ball
- And the friction of the ball (static and kinetic) for grass and sand
- Also, if you want, you can define the start and hole position in the code directly with setting,
  the USE_PREDEFINED_POSITIONS boolean to true.
  You can still change the positions in the game later with the debug mode.
- The error margin for the bots
- The bot sensitivity (the step size for the bots)

For additional information, you can check the comments in the Consts class.
But be careful, changing some values can cause the game to not work properly.
Those are marked with a warning in the comments.
The comments are there to help you understand what each value does.
So if you are finding the game too hard or too easy, or even too slow, it will guide you to the right value to change.

### UML Diagram

The UML diagram for the entire project can be found in the main directory.
It is a png file with the name umProjectDiagram.png.
It shows the classes and their relationships.
It's rather big, so you will have to zoom in to see everything.

## Known Issues
- 
- The ball movement is not perfect, and sometimes gets stuck mid-shot. But starts moving again after a while.
- The ball slides too much on the grass. Especially in simple terrain. (Recommended to increase the friction in the Consts class)
- Due to small friction, the bots may not be able to reach the hole due to hills. (This will be fixed in the future by improving the AIs by taking the height into account for decision-making.)

## Credits & License
Cartoon Texture terrain image created by upklyak (Free license) —
<a href="https://www.freepik.com/free-vector/game-textures-water-green-lawn-sand-grass-meadow-with-flowers-seamless-patterns-top-view-cartoon-textured-backgrounds-blue-liquid-field-desert-surface-graphic-ui-gui-vector-layers_21267469.htm#query=texture&position=8&from_view=author&uuid=fb39b549-3ad8-46e1-ba38-41fd1a95803a">Cartoon Vectors by Freepik</a>

Buttons Texture image created by Yuliya Pauliukevich (Free license) —
<a href="https://www.vecteezy.com/vector-art/13133856-web-and-game-menu-buttons-with-different-textures">App Vectors from Vecteezy</a>

Directional Arrows object created by Marcel Plagmann (Royalty-free license, no AI) —
<a href="https://www.cgtrader.com/free-3d-models/various/various-models/cc0-arrow-5">Arrow Vectors from cgTrader</a>

Tree object created by printable_models(Personal use license) (Only tree directory, other are propitiatory) —
<a href="https://free3d.com/3d-model/-oak-tree-v1--463277.html">Tree Models from Free3d</a>

Skybox texture created by KIIRA (Free license) —
<a href="https://opengameart.org/content/sky-box-sunny-day">Sky texture from opengameart</a>

Font mighty souly created by graphicsauceco (Freeware, Non-commercial license) —
<a href="https://www.fontspace.com/mighty-souly-font-f111821">Font from fontSpace</a>

Background music created by Fernweh Goldfish (Royalty-free license) —
<a href="https://uppbeat.io/t/fernweh-goldfish/skippy-mr-sunshine">Music from UppBeat</a>

## Authors
- Noam Favier
- Ole Rotteveel
- Beatrice Boccolari
- Bruna Calvancati Lauro
- Evi Levels
- Ahmet Akman
- Nehir Sarihan

<img src="icon.iconset/maastricht-logo.png" alt="">