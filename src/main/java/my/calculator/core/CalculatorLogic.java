package my.calculator.core;

import java.util.Stack;

public class CalculatorLogic {

    // Basic operations
    public double add(double a, double b) {
        return a + b;
    }

    public double subtract(double a, double b) {
        return a - b;
    }

    public double multiply(double a, double b) {
        return a * b;
    }

    public double divide(double a, double b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return a / b;
    }

    public double modulo(double a, double b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Cannot mod by zero");
        }
        return a % b;
    }

    // Method to calculate the result from the input string
    public double calculate(String input) throws IllegalArgumentException {
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

        return numbers.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private int precedence(char op) {
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

    private double applyOperation(char op, double b, double a) {
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
}