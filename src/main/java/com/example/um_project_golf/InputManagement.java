package com.example.um_project_golf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InputManagement {

    private final List<String> equations = List.of("21x + 3y", "3x+4y");

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
        List<Character> equationChars = equation
                .chars()
                .mapToObj(c -> (char) c)
                .filter(c -> c != ' ')
                .toList();

        List<String> fixedList = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();

        for (Character c : equationChars)
        {
            if (Character.isDigit(c))
            {
                currentNumber.append(c);
            } else
            {
                if (!currentNumber.isEmpty())
                {
                    fixedList.add(currentNumber.toString());
                    currentNumber = new StringBuilder();
                }
                fixedList.add(c.toString());
            }
        }

        if (!currentNumber.isEmpty())
        {
            fixedList.add(currentNumber.toString());
        }

        System.out.println(fixedList);

        return (x) -> 3 * x;
    }

    public static void main(String[] args)
    {
        InputManagement inputManagement = new InputManagement();
        List<Function<Double, Double>> functions = inputManagement.constructFunctions(inputManagement.equations);
        System.out.println(functions);
    }
}
