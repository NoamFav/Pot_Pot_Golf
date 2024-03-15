package com.example.um_project_golf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputManagement {

    private final List<String> equations = List.of("21x^2 + 3y", "3x + 4y - (8 + 9x)");
    private final HashMap<String, Double> variables = new HashMap<>();

    enum Type //defines the type of token
    {
        NUMBER,
        VARIABLE,
        OPERATOR,
        POWER,
        PARENTHESIS
    }

    static class Token //defines the token into a type and a value
    {
        private final Type type; //type of token
        private String value; //value of token

        public Token(Type type, String value)
        {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString()
        {
            return type + ": " + value;
        }
    }
    private List<List<Token>> constructFunctions(List<String> equations, HashMap<String, Double> variables)
    {
        List<List<Token>> functions = new ArrayList<>();
        for (String equation : equations)
        {
            List<Token> tokens = tokenize(equation);
            for (Token token : tokens)
            {
                if (token.type == Type.VARIABLE)
                {
                    if (variables.containsKey(token.value))
                    {
                        token.value = String.valueOf(variables.get(token.value));
                    }
                    else
                    {
                        throw new IllegalArgumentException("Variable not found: " + token.value);
                    }
                }
            }
            functions.add(tokens);
        }
        return functions;
    }

    private List<Token> tokenize(String equation)
    {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();
        Token previousToken = null;

        for (char currentChar : equation.toCharArray())
        {
            if (currentChar != ' ')
            {
                if (Character.isDigit(currentChar) || currentChar == '.')
                {
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
                    }
                    Token newToken;
                    if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/')
                    {
                        newToken = new Token(Type.OPERATOR, String.valueOf(currentChar));
                    }
                    else if (Character.isLetter(currentChar))
                    {
                        newToken = new Token(Type.VARIABLE, String.valueOf(currentChar));
                        checkAndAddImpliedMultiplication(tokens, newToken, previousToken);
                    }
                    else if (currentChar == '(' || currentChar == ')')
                    {
                        newToken = new Token(Type.PARENTHESIS, String.valueOf(currentChar));

                    } else if (currentChar == '^')
                    {
                        newToken = new Token(Type.POWER, String.valueOf(currentChar));
                    }
                    else
                    {
                        throw new IllegalArgumentException("Invalid character: " + currentChar);
                    }
                    tokens.add(newToken);
                    previousToken = newToken;
                }
            }
        }

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
                                (previousToken.type == Type.VARIABLE && currentToken.type == Type.NUMBER)))
        {
            tokens.add(new Token(Type.OPERATOR, "*"));
        }
    }

    private double doPEMDAS(List<Token> tokens)
    {
        for (int i = 0; i < tokens.size(); i++)
        {
            if (tokens.get(i).type == Type.PARENTHESIS && tokens.get(i).value.equals("("))
            {
                int start = i;
                int depth = 1;
                while (i + 1 < tokens.size() && depth > 0)
                {
                    i++;
                    if (tokens.get(i).value.equals("(")) depth++;
                    else if (tokens.get(i).value.equals(")")) depth--;
                }
                int end = i;
                List<Token> subExpression = tokens.subList(start + 1, end);
                double result = doPEMDAS(new ArrayList<>(subExpression));
                if (end >= start)
                {
                    tokens.subList(start, end + 1).clear();
                }
                tokens.add(start, new Token(Type.NUMBER, String.valueOf(result)));
                i = start;
            }
        }

        for (int order = 1; order <= 3; order++)
        {
            for (int i = 1; i < tokens.size() - 1; i++)
            {
                Token currentToken = tokens.get(i);
                if (isCurrentOperation(currentToken, order))
                {
                    Token leftToken = tokens.get(i - 1);
                    Token rightToken = tokens.get(i + 1);
                    double result = calculate(leftToken, rightToken, currentToken.value);
                    tokens.set(i - 1, new Token(Type.NUMBER, String.valueOf(result)));
                    tokens.remove(i);
                    tokens.remove(i);
                    i--;
                }
            }
        }
        return Double.parseDouble(tokens.get(0).value);
    }

    private boolean isCurrentOperation(Token token, int order)
    {
        return switch (order)
        {
            case 1 -> token.type == Type.POWER;
            case 2 -> token.type == Type.OPERATOR && (token.value.equals("*") || token.value.equals("/"));
            case 3 -> token.type == Type.OPERATOR && (token.value.equals("+") || token.value.equals("-"));
            default -> false;
        };
    }

    private double calculate(Token left, Token right, String operator)
    {
        double leftVal = Double.parseDouble(left.value);
        double rightVal = Double.parseDouble(right.value);
        return switch (operator)
        {
            case "^" -> Math.pow(leftVal, rightVal);
            case "*" -> leftVal * rightVal;
            case "/" -> leftVal / rightVal;
            case "+" -> leftVal + rightVal;
            case "-" -> leftVal - rightVal;
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    public static void main(String[] args)
    {
        InputManagement inputManagement = new InputManagement();

        inputManagement.variables.put("x", 3.0);
        inputManagement.variables.put("y", 4.0);

        List<List<Token>> functions = inputManagement.constructFunctions(inputManagement.equations, inputManagement.variables);
        for (List<Token> function : functions)
        {
            System.out.println(inputManagement.doPEMDAS(function));
        }
    }
}
