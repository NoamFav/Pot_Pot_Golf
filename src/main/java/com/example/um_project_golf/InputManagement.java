package com.example.um_project_golf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InputManagement {

    private final List<String> equations = List.of("21x + 3y", "3x + 4y - (8 + 9x)");

    enum Type
    {
        NUMBER,
        VARIABLE,
        OPERATOR,
        PARENTHESIS
    }

    static class Token
    {
        private final Type type;
        private final String value;

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
    private List<Function<Double, Double>> constructFunctions(List<String> equations)
    {
        List<Function<Double, Double>> functions = new ArrayList<>();
        for (String equation : equations)
        {
            functions.add(functionRecognition(equation));
        }
        return functions;
    }

    private Function<Double, Double> functionRecognition(String equation)
    {
        List<Token> tokens = tokenize(equation);
        System.out.println(tokens);



        return (x) -> 3 * x; // Placeholder
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
        List<Function<Double, Double>> functions = inputManagement.constructFunctions(inputManagement.equations);
        System.out.println(functions);
    }
}
