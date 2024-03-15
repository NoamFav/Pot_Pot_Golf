package com.example.um_project_golf;

import java.util.ArrayList;
import java.util.List;

public class InputManagement {

    private final List<String> equations = List.of("21x^2 + 3y", "3x + 4y - (8 + 9x)");
    private final List<String> variables = List.of("x", "y");
    private final List<String> startedValues = List.of("3", "4");

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
        private final String value; //value of token

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
    private List<List<Token>> constructFunctions(List<String> equations)
    {
        List<List<Token>> functions = new ArrayList<>();
        for (String equation : equations)
        {
            List<Token> tokens = tokenize(equation);
            System.out.println(tokens);
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
        double result = 0;

        Token previousToken = null;
        Token nextToken = null;

        for(int order = 0; order < 4; order++) // 0 = parentheses, 1 = exponents, 2 = multiplication and division, 3 = addition and subtraction
        {
            for (Token currentToken : tokens)
            {
                nextToken = tokens.get(tokens.indexOf(currentToken) + 1);
                switch (order)
                {
                    case 0:
                        if (currentToken.type == Type.PARENTHESIS && currentToken.value.equals("("))
                        {
                            List<Token> subTokens = new ArrayList<>();

                            for (int i = tokens.indexOf(currentToken) + 1; i < tokens.size(); i++)
                            {
                                Token subToken = tokens.get(i);
                                if (subToken.type == Type.PARENTHESIS && subToken.value.equals(")"))
                                {
                                    break;
                                }
                                subTokens.add(subToken);
                            }
                            result = doPEMDAS(subTokens);
                        }
                        break;
                    case 1:
                        if (currentToken.type == Type.POWER)
                        {

                        }
                        break;
                    case 2:
                        if (currentToken.type == Type.OPERATOR)
                        {
                            if (currentToken.value.equals("*"))
                            {

                            }
                            else if (currentToken.value.equals("/"))
                            {

                            }
                        }
                        break;
                    case 3:
                        if (currentToken.type == Type.OPERATOR)
                        {
                            if (currentToken.value.equals("+"))
                            {

                            }
                            else if (currentToken.value.equals("-"))
                            {

                            }
                        }
                        break;
                }

                previousToken = currentToken;
            }
        }
        return result;
    }

    public static void main(String[] args)
    {
        InputManagement inputManagement = new InputManagement();
        List<List<Token>> functions = inputManagement.constructFunctions(inputManagement.equations);
    }
}
