package com.example.um_project_golf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InputManagement {

    private final List<String> equations = List.of("21x + 3y", "3x+4y");

    enum Type
    {
        NUMBER,
        VARIABLE,
        OPERATOR
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

        // Here, you would parse the tokens and build an expression tree or similar structure
        // For simplicity, this example will not include the full parsing logic

        // Example evaluation, to be replaced with actual logic based on parsing
        return (x) -> 3 * x; // Placeholder
    }

    private List<Token> tokenize(String equation)
    {
        List<Token> tokens = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();

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
                        tokens.add(new Token(Type.NUMBER, currentNumber.toString()));
                        currentNumber = new StringBuilder();
                    }
                    if (c == '+' || c == '-' || c == '*' || c == '/')
                    {
                        tokens.add(new Token(Type.OPERATOR, String.valueOf(c)));
                    }
                    else if (Character.isLetter(c))
                    {
                        tokens.add(new Token(Type.VARIABLE, String.valueOf(c)));
                    }
                }
            }
        }

        if (!currentNumber.isEmpty())
        {
            tokens.add(new Token(Type.NUMBER, currentNumber.toString()));
        }

        return tokens;
    }

    public static void main(String[] args)
    {
        InputManagement inputManagement = new InputManagement();
        List<Function<Double, Double>> functions = inputManagement.constructFunctions(inputManagement.equations);
        System.out.println(functions);
    }
}
