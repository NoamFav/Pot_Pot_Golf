package com.example.um_project_golf;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class InputManagement
{

    public enum Type //defines the type of token
    {
        NUMBER,
        VARIABLE,
        OPERATOR,
        POWER,
        PARENTHESIS
    }

    /**
     * @param type  type of token
     * @param value value of token
     */
    public record Token(Type type, String value) //defines the token into a type and a value
    {
        @Override
        public String toString() {
            return type + ": " + value;
        }
    }

    public List<Double> solve(List<List<Token>> equations, HashMap<String, Double> variables)
    {
        equations = replaceVar(equations, variables); //replaces the variables with their values
        return equations.stream()
                .map(this::doPEMDAS) //does the PEMDAS operations on each function
                .collect(Collectors.toList());
    }

    public List<List<Token>> getFunctions(List<String> equations)
    {
        return equations.stream()
                .map(this::tokenize) //convert each equation to a list of tokens
                .map(InputManagement::insertImplicitOnes) //inserts implicit ones where necessary
                .collect(Collectors.toList());
    }
    private List<List<Token>> replaceVar(List<List<Token>> equations, HashMap<String, Double> variables)
    {
        return equations.stream()
                .map(tokens -> changeVar(variables, tokens)) //apply variable changes to each list of tokens
                .collect(Collectors.toList());
    }

    //constructs the functions and replaces the variables with their values
    public List<List<Token>> constructCompleteFunctions(List<String> equations, HashMap<String, Double> variables)
    {
        return equations.stream()
                .map(this::tokenize) //convert each equation to a list of tokens
                .map(InputManagement::insertImplicitOnes) //inserts implicit ones where necessary
                .map(tokens -> changeVar(variables, tokens)) //apply variable changes to each list of tokens
                .collect(Collectors.toList());
    }

    private List<Token> changeVar(HashMap<String, Double> variables, List<Token> tokens)
    {
        return tokens.stream()
                .map(token -> {
                    if (token.type == Type.VARIABLE && variables.containsKey(token.value)) { //replace variable with its value
                        return new Token(Type.NUMBER, variables.get(token.value).toString());
                    }
                    return token; //return the token unchanged if it's not a variable or the variable isn't in the map
                })
                .collect(Collectors.toList());
    }

    //tokenizes the equation by grouping the characters into types
    private List<Token> tokenize(String equation)
    {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();
        boolean decimalPointSeen = false; //flag to track if we've seen a decimal point in the current number
        Token previousToken = null;

        for (char currentChar : equation.toCharArray())
        {
            if (currentChar != ' ')
            {
                if (Character.isDigit(currentChar) || (currentChar == '-' && (previousToken == null || previousToken.type() == Type.OPERATOR || previousToken.type() == Type.PARENTHESIS && previousToken.value().equals("("))) || (currentChar == '.' && !decimalPointSeen))
                {
                    if (currentChar == '.')
                    {
                        decimalPointSeen = true; //set flag to true when decimal point is encountered
                    }
                    currentNumber.append(currentChar);
                }
                else
                {
                    if (!currentNumber.isEmpty())
                    {
                        Token numberToken = new Token(Type.NUMBER, currentNumber.toString());
                        checkAndAddImpliedMultiplication(tokens, numberToken, previousToken);
                        tokens.add(numberToken);
                        previousToken = numberToken;
                        currentNumber = new StringBuilder();
                        decimalPointSeen = false; //reset the decimal point seen flag
                    }

                    Token newToken;
                    if (currentChar == '+' || currentChar == '*' || currentChar == '/')
                    {
                        newToken = new Token(Type.OPERATOR, String.valueOf(currentChar)); //creates a new token for the operator
                    }
                    else if (Character.isLetter(currentChar))
                    {
                        newToken = new Token(Type.VARIABLE, String.valueOf(currentChar)); //creates a new token for the variable
                        checkAndAddImpliedMultiplication(tokens, newToken, previousToken);
                    }
                    else if (currentChar == '(' || currentChar == ')')
                    {
                        newToken = new Token(Type.PARENTHESIS, String.valueOf(currentChar)); //creates a new token for the parenthesis
                        if (currentChar == '(')
                        {
                            checkAndAddImpliedMultiplication(tokens, newToken, previousToken);  //adds implied multiplication if the previous token is a number or a variable
                        }
                    }
                    else if (currentChar == '^')
                    {
                        newToken = new Token(Type.POWER, String.valueOf(currentChar)); //creates a new token for the power
                    }
                    else if (currentChar == '-')
                    {
                        // This case is already handled with negative numbers; might be subtraction
                        newToken = new Token(Type.OPERATOR, String.valueOf(currentChar)); //creates a new token for the operator
                    }
                    else
                    {
                        throw new IllegalArgumentException("Invalid character: " + currentChar); //throws an exception if the character is invalid
                    }

                    tokens.add(newToken);
                    previousToken = newToken;
                }
            }
        }

        // Check and add the last number if there is one
        if (!currentNumber.isEmpty())
        {
            Token numberToken = new Token(Type.NUMBER, currentNumber.toString());
            checkAndAddImpliedMultiplication(tokens, numberToken, previousToken);
            tokens.add(numberToken);
        }

        return tokens;
    }

    private void checkAndAddImpliedMultiplication(List<Token> tokens, Token currentToken, Token previousToken)
    {
        if (previousToken != null &&
                ((previousToken.type == Type.NUMBER && currentToken.type == Type.VARIABLE) ||
                        (previousToken.type == Type.VARIABLE && currentToken.type == Type.VARIABLE) ||
                        (previousToken.type == Type.VARIABLE && currentToken.type == Type.NUMBER) ||
                        ((previousToken.type == Type.VARIABLE || previousToken.type == Type.NUMBER) && currentToken.type == Type.PARENTHESIS && currentToken.value.equals("("))))//determines if there is an implied multiplication between the previous token and the current token
        {
            tokens.add(new Token(Type.OPERATOR, "*")); //adds the implied multiplication to the list of tokens
        }
    }

    public static List<Token> insertImplicitOnes(List<Token> tokens)
    {
        List<Token> modifiedTokens = new ArrayList<>();
        for (Token currentToken : tokens)
        {
            //check for unitary minus without a number
            if (currentToken.type() == Type.NUMBER && currentToken.value().equals("-"))
            {
                modifiedTokens.add(new Token(Type.NUMBER, "-1"));
                continue;
            }

            //add current token if not part of unary minus handling
            modifiedTokens.add(currentToken);
        }
        return modifiedTokens;
    }

    public double doPEMDAS(List<Token> tokens) //does the PEMDAS (Parentheses, Exponents, Multiplication and Division, Addition and Subtraction) operations in order
    {
        for (int i = 0; i < tokens.size(); i++) //iterates through the list of tokens to find parentheses
        {
            if (tokens.get(i).type == Type.PARENTHESIS && tokens.get(i).value.equals("(")) //checks if the current token is an open parenthesis
            {
                int start = i; //initializes the start of the parentheses
                int depth = 1; //initializes the depth of the parentheses
                while (i + 1 < tokens.size() && depth > 0) //iterates through the remaining list of tokens to find the end of the parentheses
                {
                    i++;
                    if (tokens.get(i).value.equals("(")) depth++;
                    else if (tokens.get(i).value.equals(")")) depth--;
                }
                int end = i; //initializes the end of the parentheses
                List<Token> subExpression = tokens.subList(start + 1, end);
                double result = doPEMDAS(new ArrayList<>(subExpression)); //recursively does the PEMDAS operations on the sub-expression
                if (end >= start)
                {
                    tokens.subList(start, end + 1).clear(); //removes the parentheses and the sub-expression from the list of tokens
                }
                tokens.add(start, new Token(Type.NUMBER, String.valueOf(result))); //adds the result of the sub-expression to the list of tokens
                i = start; //updates the current index
            }
        }

        for (int order = 1; order <= 3; order++) //iterates through the orders of operations (1: power, 2: multiplication and division, 3: addition and subtraction)
        {
            for (int i = 1; i < tokens.size() - 1; i++) //iterates through the list of tokens to find the current operation
            {
                Token currentToken = tokens.get(i); //initializes the current token
                if (isCurrentOperation(currentToken, order)) //checks if the current token is the current operation
                {
                    Token previousToken = tokens.get(i - 1);
                    Token nextToken = tokens.get(i + 1);
                    double result = calculate(previousToken, nextToken, currentToken.value); //calculates the result of the current operation
                    tokens.set(i - 1, new Token(Type.NUMBER, String.valueOf(result))); //updates the previous token with the result of the current operation
                    tokens.remove(i); //removes the current token
                    tokens.remove(i); //removes the next token (because i is now the next token)
                    i--;
                }
            }
        }
        return Double.parseDouble(tokens.get(0).value); //returns the result of the PEMDAS operations
    }

    private boolean isCurrentOperation(Token token, int order) //checks if the current token is the current operation based on the order of operations
    {
        return switch (order)
        {
            case 1 -> token.type == Type.POWER;
            case 2 -> token.type == Type.OPERATOR && (token.value.equals("*") || token.value.equals("/"));
            case 3 -> token.type == Type.OPERATOR && (token.value.equals("+") || token.value.equals("-"));
            default -> false;
        };
    }

    private double calculate(Token previousToken, Token nextToken, String operator) //calculates the result of the operation based on the operator
    {
        double previousValue = Double.parseDouble(previousToken.value);
        double nextValue = Double.parseDouble(nextToken.value);
        return switch (operator) //calculates the result of the operation based on the operator
        {
            case "^" -> Math.pow(previousValue, nextValue);
            case "*" -> previousValue * nextValue;
            case "/" -> previousValue / nextValue;
            case "+" -> previousValue + nextValue;
            case "-" -> previousValue - nextValue;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    public List<Expression> constructExpression(List<String> equationsString, HashMap<String,Double> variables) //constructs the expression from the list of tokens
    {
        return equationsString.stream()
                .map(equation -> {
                    ExpressionBuilder builder = new ExpressionBuilder(equation); //initializes the expression builder
                    for (var e : variables.entrySet()) //iterates through the variables
                    {
                        builder.variable(e.getKey()); //adds the variable to the expression builder
                    }
                    return builder.build(); //builds the expression
                })
                .collect(Collectors.toList());
    }

    public List<Expression> addVariables(List<Expression> equations, HashMap<String, Double> variables) //adds the variables to the expression
    {
        for (var e : variables.entrySet()) //iterates through the variables
        {
            for (Expression equation : equations) //iterates through the equations
            {
                equation.setVariable(e.getKey(), e.getValue()); //sets the variable in the equation
            }
        }
        return equations; //returns the equations with the variables
    }

    public List<Double> solveHard(List<Expression> equations, HashMap<String, Double> variables) //solves the equations
    {
        return equations.stream()
                .map(equation -> {
                    for (var e : variables.entrySet()) //iterates through the variables
                    {
                        equation.setVariable(e.getKey(), e.getValue()); //sets the variable in the equation
                    }
                    return equation.evaluate(); //returns the result of the equation
                })
                .collect(Collectors.toList());
    }

    public static void main(String[] args)
    {
        List<String> equations = List.of("21.2x^2 + 3y", "-3x + 4y - (8 + 9x) * -x", "2(x)"); //initializes the equations
        HashMap<String, Double> variables = new HashMap<>(); //initializes the variables

        variables.put("x", 3.0); //placeholders for the variable x
        variables.put("y", 4.0); //placeholders for the variable y

        InputManagement inputManagement = new InputManagement(); //initializes the input management

        boolean simple = true; //flag to check if the equation is supported by the simple solver
        for (String equation : equations)
        {
            if (equation.contains("cos") || equation.contains("sin") || equation.contains("tan") || equation.contains("log") || equation.contains("sqrt") || equation.contains("!") || equation.contains("%") || equation.contains("abs") || equation.contains("e")) {
                simple = false; //sets the flag to false if the equation contains a function that is not supported by the simple solver
                break;
            }
        }

        for (String equation : equations)
        {
            if(simple) //checks if all the equations are supported by the simple solver
            {
                List<List<Token>> functions = inputManagement.getFunctions(equations); //gets the functions
                List<Double> solutions = inputManagement.solve(functions, variables); //solves the equations
                System.out.println("Simple solver:");
                System.out.println("The value of " + equation + " is: " + solutions.get(equations.indexOf(equation))); //prints the result of the equation
            }
            else
            {
                List<Expression> expressions = inputManagement.constructExpression(equations, variables); //constructs the expressions
                List<Double> solutions = inputManagement.solveHard(expressions, variables); //solves the equations
                System.out.println("Hard solver:");
                System.out.println("The value of " + equation + " is: " + solutions.get(equations.indexOf(equation))); //prints the result of the equation
            }
        }
    }
}
