<img src="icon.iconset/icon_512x512@2x.png" sizes="" width="200" height="200"/>

# UM Project Golf

This project is a numerical solver for ordinary differential equations using the Runge-Kutta fourth order method.
It is written in Java and built with Maven.

## Getting Started

For running the project, you need to have Maven installed.
You can download it from [here](https://maven.apache.org/download.cgi).
After installing Maven, you can run the project by running the main in the IDE or in the command line.

### Prerequisites

The project runs on java 21.

### Functioning

When opening the main class, you will see the GUI.
You can select the solver in the top left corner,
you can select between the Euler method, the improved Euler method, and the Runge-Kutta method.
You can in the bottom left corner input the step size, the initial value, and the final value.
You can also input the function you want to solve in the text area in the middle

### Function Building

To build a system, you can use the slider to select the number of equations you want (up to 10).
For each equation,
you have a textfield for the function itself and a dropdown menu to select the variable independence of.


### Variables

The variables system is the following:
-When you type a letter in the function, it will be considered as a variable.
    -if the variable doesn't have an attached function it will be treated as a constant
    -if the variable has an attached function, it will be treated as a variable
-If you type e or pi, it will be interpreted as the euler constant and pi respectively
-If you type a number, it will be interpreted as a constant
-if you type cos, sin, tan, exp, log, sqrt, abs, it will be interpreted as the corresponding function and not added to the variables

### Running the tests

For running the system, you need every field to be filled.
If you don't fill them, the system will throw an error.
If you input a function that is not valid, the system will throw an error.
If you input a value for a variable that is not a number, the system will throw an error.

When everything is filled, you can press the run button to get the result.
The result at tFinal will be displayed in the text area in the middle left.
In addition, a graph will be displayed in the middle side of the GUI.

### Notes

The system is not perfect and can be improved.
The system deletes the variable values and the selected item in the dropdown menu when you type an equation or edit one.
The system resets everything when you change the slider for the number of equations.

### Trivia

The system contains a basic API for string recognition into executable function, which can be used for other projects.
It wasn't kept in the final version of the project because it was not working for more complex functions.
Instead, the system uses exp4j, a library that can evaluate strings into functions.

## Authors
Noam Favier
Ole Rotteveel
Beatrice Boccolari
Bruna Calvancati Lauro
Evi Levels
Ahmet Akman
Nehir Sarihan

<img src="icon.iconset/maastricht-logo.png">