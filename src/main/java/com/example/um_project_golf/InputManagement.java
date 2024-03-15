package com.example.um_project_golf;

import java.util.ArrayList;
import java.util.List;

public class InputManagement {

    private final List<String> equations = List.of("21x^2 + 3y", "3x + 4y - (8 + 9x)");

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

        for (char c : equation.toCharArray())
        {
            if (c != ' ')
            {
                if (Character.isDigit(c) || c == '.')
                {
                    currentNumber.append(c);
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
                    if (c == '+' || c == '-' || c == '*' || c == '/')
                    {
                        newToken = new Token(Type.OPERATOR, String.valueOf(c));
                    }
                    else if (Character.isLetter(c))
                    {
                        newToken = new Token(Type.VARIABLE, String.valueOf(c));
                        checkAndAddImpliedMultiplication(tokens, newToken, previousToken);
                    }
                    else if (c == '(' || c == ')')
                    {
                        newToken = new Token(Type.PARENTHESIS, String.valueOf(c));

                    } else if (c == '^')
                    {
                        newToken = new Token(Type.POWER, String.valueOf(c));
                    }
                    else
                    {
                        throw new IllegalArgumentException("Invalid character: " + c);
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

    public static void main(String[] args)
    {
        InputManagement inputManagement = new InputManagement();
        List<List<Token>> functions = inputManagement.constructFunctions(inputManagement.equations);
    }
}
