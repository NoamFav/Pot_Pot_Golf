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
        Token previousToken = null;
        Token nextToken = null;
        double nextTokenValue3; //addition and subtraction
        double nextTokenValue12; //multiplication and division and exponents
        List<Token> copyTokens = new ArrayList<>(tokens);

        for(int order = 0; order < 4; order++) // 0 = parentheses, 1 = exponents, 2 = multiplication and division, 3 = addition and subtraction
        {
            for (int index = 0; index < tokens.size(); index++)
            {
                Token currentToken = tokens.get(index);
                if (tokens.indexOf(currentToken) == tokens.size() - 1)
                {
                    nextToken = null;
                }
                else
                {
                    nextToken = tokens.get(tokens.indexOf(currentToken) + 1);
                }
                if ((nextToken != null ? nextToken.type : null) == Type.NUMBER)
                {
                    nextTokenValue3 = Double.parseDouble(nextToken.value);
                    nextTokenValue12 = Double.parseDouble(nextToken.value);
                }
                else
                {
                    nextTokenValue3 = 0;
                    nextTokenValue12 = 0;
                }

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
                            Double result = doPEMDAS(subTokens);
                            copyTokens.add(tokens.indexOf(currentToken),new Token(Type.NUMBER, String.valueOf(result)));
                            copyTokens.remove(tokens.indexOf(subTokens.get(0))-1);
                            copyTokens.remove(tokens.get(subTokens.size()));
                            copyTokens.removeAll(subTokens);

                        }
                        break;
                    case 1:
                        if (currentToken.type == Type.POWER)
                        {
                            double powerResult = Math.pow(Double.parseDouble(previousToken.value), nextTokenValue12);
                            copyTokens.add(tokens.indexOf(currentToken),new Token(Type.NUMBER, String.valueOf(powerResult)));
                            copyTokens.remove(previousToken);
                            copyTokens.remove(currentToken);
                            copyTokens.remove(nextToken);

                        }
                        break;
                    case 2:
                        if (currentToken.type == Type.OPERATOR)
                        {
                            if (currentToken.value.equals("*"))
                            {
                                double multiplicationResult = Double.parseDouble(previousToken.value) * nextTokenValue12;
                                copyTokens.add(tokens.indexOf(currentToken),new Token(Type.NUMBER, String.valueOf(multiplicationResult)));
                                copyTokens.remove(previousToken);
                                copyTokens.remove(currentToken);
                                copyTokens.remove(nextToken);
                            }
                            else if (currentToken.value.equals("/"))
                            {
                                double divisionResult = Double.parseDouble(previousToken.value) / nextTokenValue12;
                                copyTokens.add(tokens.indexOf(currentToken),new Token(Type.NUMBER, String.valueOf(divisionResult)));
                                copyTokens.remove(previousToken);
                                copyTokens.remove(currentToken);
                                copyTokens.remove(nextToken);

                            }
                        }
                        break;
                    case 3:
                        if (currentToken.type == Type.OPERATOR)
                        {
                            if (currentToken.value.equals("+"))
                            {
                                double additionResult = Double.parseDouble(previousToken.value) + nextTokenValue3;
                                copyTokens.add(tokens.indexOf(currentToken),new Token(Type.NUMBER, String.valueOf(additionResult)));
                                copyTokens.remove(previousToken);
                                copyTokens.remove(currentToken);
                                copyTokens.remove(nextToken);
                            }
                            else if (currentToken.value.equals("-"))
                            {
                                double subtractionResult = Double.parseDouble(previousToken.value) - nextTokenValue3;
                                copyTokens.add(tokens.indexOf(currentToken),new Token(Type.NUMBER, String.valueOf(subtractionResult)));
                                copyTokens.remove(previousToken);
                                copyTokens.remove(currentToken);
                                copyTokens.remove(nextToken);
                            }
                        }
                        break;
                }

                previousToken = currentToken;
            }
            tokens = new ArrayList<>(copyTokens);
        }
        return Double.parseDouble(tokens.get(0).value);
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
