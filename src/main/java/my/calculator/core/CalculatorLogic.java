package my.calculator.core;

import java.util.Stack;
import java.lang.String;
import java.text.DecimalFormat;

public class CalculatorLogic {

    // Basic operations
    public static double add(double a, double b) {
        return a + b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    }

    public static double modulo(double a, double b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Cannot mod by zero");
        }
        return a % b;
    }

    // Method to calculate the result from the input string
    public static String calculate(String input) throws IllegalArgumentException {
        // Remove spaces from the input
        input = input.replaceAll("\\s+", "");

        // Use a stack to handle the operations and numbers
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operations = new Stack<>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // If the character is a digit, parse the number
            if (Character.isDigit(c) || c == '.') {
                int start = i;
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    i++;
                }
                numbers.push(Double.parseDouble(input.substring(start, i)));
                i--;
            } else if (c == '(') {
                operations.push(c);
            } else if (c == ')') {
                while (operations.peek() != '(') {
                    numbers.push(applyOperation(operations.pop(), numbers.pop(), numbers.pop()));
                }
                operations.pop();
            } else if (isOperator(c)) {
                while (!operations.isEmpty() && precedence(c) <= precedence(operations.peek())) {
                    numbers.push(applyOperation(operations.pop(), numbers.pop(), numbers.pop()));
                }
                operations.push(c);
            }
        }

        while (!operations.isEmpty()) {
            numbers.push(applyOperation(operations.pop(), numbers.pop(), numbers.pop()));
        }

        double result = numbers.pop();
        if (result == (int) result) {
            return String.valueOf((int) result);
        } else {
            DecimalFormat df;
            if (hasTerminatingDecimals(result)) {
                df = new DecimalFormat("#.##########"); // Display all decimals
            } else {
                df = new DecimalFormat("#.##"); // Display two decimals
            }
            return df.format(result);
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return -1;
        }
    }

    private static double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+':
                return add(a, b);
            case '-':
                return subtract(a, b);
            case '*':
                return multiply(a, b);
            case '/':
                return divide(a, b);
            case '%':
                return modulo(a, b);
            default:
                throw new IllegalArgumentException("Invalid operator: " + op);
        }
    }

    private static boolean hasTerminatingDecimals(double number) {
        while (number != Math.floor(number)) {
            number *= 10;
        }
        while (number % 2 == 0) {
            number /= 2;
        }
        while (number % 5 == 0) {
            number /= 5;
        }
        return number == 1;
    }
}