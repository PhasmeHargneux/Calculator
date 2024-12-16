package my.calculator.core;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code CalculatorLogic} class provides methods to parse and evaluate mathematical expressions.
 * It supports basic arithmetic operations, scientific functions, implicit multiplication, and constants.
 * <p>
 * Features:
 * <ul>
 *   <li>Shunting Yard algorithm for infix to RPN conversion</li>
 *   <li>RPN evaluation with arithmetic and scientific functions</li>
 *   <li>Implicit multiplication (e.g., "9sin(90)" → "9 * sin(90)")</li>
 *   <li>Constants: π (pi) and e are handled as numeric constants, not functions.</li>
 * </ul>
 */
public class CalculatorLogic {

    /** Operator precedence mapping. Higher number = higher precedence. */
    private static final Map<String, Integer> OPERATOR_PRECEDENCE = new HashMap<>();
    /** Operator associativity mapping. true = left-associative, false = right-associative. */
    private static final Map<String, Boolean> OPERATOR_ASSOCIATIVITY = new HashMap<>();

    /** Set of supported functions (no longer includes pi or e). */
    private static final Set<String> FUNCTIONS = new HashSet<>(Arrays.asList(
            "sin", "cos", "tan", "asin", "acos", "atan",
            "exp", "ln", "log", "√", "x²", "10^x"
            // Note: "e" and "π" removed from functions
    ));

    /** Set of constants: pi and e. These are treated as numeric constants. */
    private static final Map<String, Double> CONSTANTS = new HashMap<>();

    static {
        // Define operator precedence
        OPERATOR_PRECEDENCE.put("(", 0);
        OPERATOR_PRECEDENCE.put(")", 0);
        OPERATOR_PRECEDENCE.put("^", 3);
        OPERATOR_PRECEDENCE.put("%", 2);
        OPERATOR_PRECEDENCE.put("*",2);
        OPERATOR_PRECEDENCE.put("/", 2);
        OPERATOR_PRECEDENCE.put("+", 1);
        OPERATOR_PRECEDENCE.put("-", 1);
        OPERATOR_PRECEDENCE.put("!", 4);

        // Define operator associativity
        OPERATOR_ASSOCIATIVITY.put("^", false);
        OPERATOR_ASSOCIATIVITY.put("!", false);
        OPERATOR_ASSOCIATIVITY.put("*", true);
        OPERATOR_ASSOCIATIVITY.put("/", true);
        OPERATOR_ASSOCIATIVITY.put("%", true);
        OPERATOR_ASSOCIATIVITY.put("+", true);
        OPERATOR_ASSOCIATIVITY.put("-", true);

        // Initialize constants
        CONSTANTS.put("e", Math.E);
        CONSTANTS.put("π", Math.PI);
    }

    /**
     * Calculates the result from a given mathematical expression.
     *
     * @param input The mathematical expression as a string.
     * @return The result as a string.
     * @throws IllegalArgumentException if the expression is invalid.
     * @throws ArithmeticException      if arithmetic errors occur (e.g., division by zero).
     */
    public static String calculate(String input) throws IllegalArgumentException, ArithmeticException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input is empty");
        }

        // Remove spaces
        input = input.replaceAll("\\s+", "");

        // Check for consecutive operators
        if (hasConsecutiveOperators(input)) {
            throw new IllegalArgumentException("Invalid input: Consecutive operators");
        }

        // Convert to RPN
        List<String> rpn = shuntingYard(input);

        // Evaluate RPN
        double result = evaluateRPN(rpn);
        return formatResult(result);
    }

    /**
     * Inserts a function name with parentheses into an input string.
     * For example, if input is "3+" and functionName is "sin", returns "3+sin()".
     *
     * @param input        the current input string.
     * @param functionName the function to insert (e.g., "sin").
     * @return the new input string with the inserted function.
     */
    public static String insertFunction(String input, String functionName) {
        if (input == null) {
            input = "";
        }
        int caretPos = input.length();
        String newText = functionName + "()";
        return input.substring(0, caretPos) + newText;
    }

    /**
     * Checks if consecutive operators are present in the input, which would make it invalid.
     *
     * @param input the expression to check.
     * @return true if consecutive operators are found, false otherwise.
     */
    private static boolean hasConsecutiveOperators(String input) {
        String operatorPattern = "[-+*/%^]{2,}";
        Pattern pattern = Pattern.compile(operatorPattern);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    /**
     * Converts the input expression to Reverse Polish Notation (RPN) using the Shunting Yard Algorithm.
     *
     * @param input the mathematical expression in infix notation.
     * @return a list of tokens representing the expression in RPN.
     * @throws IllegalArgumentException if parentheses are mismatched or unknown tokens are found.
     */
    private static List<String> shuntingYard(String input) throws IllegalArgumentException {
        List<String> tokens = tokenize(input);

        // Insert implicit multiplication where needed
        tokens = insertImplicitMultiplicationOperators(tokens);

        List<String> outputQueue = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                outputQueue.add(token);
            } else if (CONSTANTS.containsKey(token)) {
                // Convert constants into their numeric values immediately
                double val = (token.equals("π")) ? Math.PI : Math.E;
                outputQueue.add(Double.toString(val));
            } else if (FUNCTIONS.contains(token)) {
                operatorStack.push(token);
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && isOperator(operatorStack.peek())) {
                    String topOp = operatorStack.peek();
                    if ((OPERATOR_ASSOCIATIVITY.get(token) && OPERATOR_PRECEDENCE.get(token) <= OPERATOR_PRECEDENCE.get(topOp))
                            || (!OPERATOR_ASSOCIATIVITY.get(token) && OPERATOR_PRECEDENCE.get(token) < OPERATOR_PRECEDENCE.get(topOp))) {
                        outputQueue.add(operatorStack.pop());
                    } else {
                        break;
                    }
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    outputQueue.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                operatorStack.pop(); // pop "("
                // If the token at the top of the stack is a function, pop it onto the output queue
                if (!operatorStack.isEmpty() && FUNCTIONS.contains(operatorStack.peek())) {
                    outputQueue.add(operatorStack.pop());
                }
            } else {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
        }

        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            outputQueue.add(op);
        }

        return outputQueue;
    }

    /**
     * Tokenizes the input string into numbers, constants, functions, and operators.
     *
     * @param input the expression string to tokenize.
     * @return a list of tokens.
     * @throws IllegalArgumentException if invalid tokens are detected.
     */
    private static List<String> tokenize(String input) throws IllegalArgumentException {
        input = input.replaceAll("÷", "/");

        // Regex patterns for numbers, functions, operators, parentheses, constants
        String numberPattern = "\\d+(\\.\\d+)?";
        String functionPattern = "(sin|cos|tan|asin|acos|atan|exp|ln|log|√|10\\^x)";
        String constantsPattern = "(π|e)";
        String binaryOperatorPattern = "[-+*/%^]";
        String unaryOperatorPattern = "[!]";  // Factorial as unary operator
        String parenthesesPattern = "[()]";

        // Combined pattern: function | number | constants | operator
        String tokenPattern = String.format("(%s)|(%s)|(%s)|(%s)|(%s)|(%s)", 
        functionPattern, 
        numberPattern, 
        constantsPattern, 
        binaryOperatorPattern,
        unaryOperatorPattern,
        parenthesesPattern);

        Pattern pattern = Pattern.compile(tokenPattern);
        Matcher matcher = pattern.matcher(input);

        List<String> tokens = new ArrayList<>();
        int pos = 0;
        while (matcher.find()) {
            if (matcher.start() != pos) {
                throw new IllegalArgumentException("Invalid token at position " + pos);
            }
            String token = matcher.group();
            tokens.add(token);
            pos = matcher.end();
        }

        if (pos != input.length()) {
            throw new IllegalArgumentException("Invalid token at position " + pos);
        }

        tokens = handleUnaryOperators(tokens);
        return tokens;
    }

    /**
     * Inserts implicit multiplication where needed.
     * For example, if we have [ "9", "sin", "(" ] -> [ "9", "*", "sin", "(" ].
     *
     * @param tokens the list of tokens after initial tokenization.
     * @return updated token list with implicit "*" inserted where appropriate.
     */
    private static List<String> insertImplicitMultiplicationOperators(List<String> tokens) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String current = tokens.get(i);
            result.add(current);
            if (i < tokens.size() - 1) {
                String next = tokens.get(i + 1);

                // If current is a number, constant, or ")" and next is a function or "(",
                // insert "*".
                boolean currentTriggers = isNumber(current) || CONSTANTS.containsKey(current) || current.equals(")");
                boolean nextTriggers = FUNCTIONS.contains(next) || next.equals("(") || CONSTANTS.containsKey(next);

                if (currentTriggers && nextTriggers) {
                    result.add("*");
                }
            }
        }
        return result;
    }

    /**
     * Handles unary operators in the token list, specifically the unary minus for negative numbers.
     *
     * @param tokens the original list of tokens
     * @return a processed list with unary operators handled
     */
    private static List<String> handleUnaryOperators(List<String> tokens) {
        List<String> processedTokens = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("-")) {
                if (i == 0 || (isOperator(tokens.get(i - 1)) || tokens.get(i - 1).equals("("))) {
                    // Unary minus detected, treat it as "0 - ..."
                    processedTokens.add("0");
                }
            }
            processedTokens.add(token);
        }
        return processedTokens;
    }

    /**
     * Gets constant value
     */
    private static double getConstantValue(String token) {
        return CONSTANTS.get(token);
    }

    /**
     * Evaluates the Reverse Polish Notation (RPN) expression.
     *
     * @param rpn a list of tokens in RPN form.
     * @return the result as a double.
     * @throws IllegalArgumentException if the RPN expression is invalid.
     * @throws ArithmeticException      if arithmetic errors occur.
     */
    private static double evaluateRPN(List<String> tokens) {
        Stack<Double> numbers = new Stack<>();
        
        for (String token : tokens) {
            if (isNumber(token)) {
                numbers.push(Double.parseDouble(token));
            } else if (isConstant(token)) {
                numbers.push(getConstantValue(token));
            } else if (isFunction(token)) {
                if (numbers.isEmpty()) {
                    throw new IllegalArgumentException("Insufficient values for function " + token);
                }
                double a = numbers.pop();
                numbers.push(applyFunction(a, token));
            } else if (token.equals("!")) {
                // Special case for factorial as postfix operator
                if (numbers.isEmpty()) {
                    throw new IllegalArgumentException("Insufficient values for operation !");
                }
                double a = numbers.pop();
                numbers.push(factorial(a));
            } else if (isOperator(token)) {
                if (numbers.size() < 2) {
                    throw new IllegalArgumentException("Insufficient values for operation " + token);
                }
                double b = numbers.pop();
                double a = numbers.pop();
                numbers.push(applyOperator(a, b, token));
            }
        }
        
        return numbers.pop();
    }

    /**
     * Applies an operator to two operands.
     *
     * @param a        the first operand
     * @param b        the second operand
     * @param operator the operator symbol
     * @return the result of a operator b
     * @throws ArithmeticException if division or modulus by zero occurs
     */
    private static double applyOperator(double a, double b, String operator) throws ArithmeticException {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("Cannot divide by zero");
                }
                return a / b;
            case "%":
                if (b == 0) {
                    throw new ArithmeticException("Cannot mod by zero");
                }
                return a % b;
            case "^":
                return Math.pow(a, b);
            case "!":
                return factorial(a);   
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    /**
     * Applies a mathematical function to an operand.
     *
     * @param a        the operand
     * @param function the function name
     * @return the result of applying the function to a
     * @throws IllegalArgumentException if the function is unknown
     * @throws ArithmeticException      if the function is not defined for the given operand
     */
    private static double applyFunction(double a, String function) throws IllegalArgumentException, ArithmeticException {
        switch (function) {
            case "sin":
                return Math.sin(Math.toRadians(a));
            case "cos":
                return Math.cos(Math.toRadians(a));
            case "tan":
                return Math.tan(Math.toRadians(a));
            case "asin":
                return Math.toDegrees(Math.asin(a));
            case "acos":
                return Math.toDegrees(Math.acos(a));
            case "atan":
                return Math.toDegrees(Math.atan(a));
            case "exp":
                return Math.exp(a);
            case "ln":
                if (a <= 0) {
                    throw new ArithmeticException("ln undefined for non-positive values");
                }
                return Math.log(a);
            case "log":
                if (a <= 0) {
                    throw new ArithmeticException("log undefined for non-positive values");
                }
                return Math.log10(a);
            case "√":
                if (a < 0) {
                    throw new ArithmeticException("Square root of negative number is undefined");
                }
                return Math.sqrt(a);
            case "x²":
                return a * a;
            case "n!":
                return factorial(a);
            case "10^x":
                return Math.pow(10, a);
            default:
                throw new IllegalArgumentException("Unknown function: " + function);
        }
    }

    /**
     * Calculates the factorial of a number (only for non-negative integers).
     *
     * @param a the number to factorial
     * @return factorial of a
     * @throws ArithmeticException if a is negative or non-integer
     */
    private static double factorial(double a) throws ArithmeticException {
        if (a < 0 || a != (int) a) {
            throw new ArithmeticException("Factorial is only defined for non-negative integers");
        }
        int n = (int) a;
        if (n > 20) {
            // to prevent overflow
            throw new ArithmeticException("Factorial result is too large");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * Checks if a string represents a number.
     *
     * @param token the string token
     * @return true if token is a number, false otherwise
     */
    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a token is a constant
     */
    private static boolean isConstant(String token) {
        return CONSTANTS.containsKey(token);
    }

    /**
     * Checks if a string is an operator.
     *
     * @param token the string token
     * @return true if token is an operator, false otherwise
     */
    private static boolean isOperator(String token) {
        return OPERATOR_PRECEDENCE.containsKey(token) && !token.equals("(") && !token.equals(")");
    }

    /**
     * Checks if a string is a function.
     *
     * @param token the string token
     * @return true if token is a function, false otherwise
     */
    private static boolean isFunction(String token) {
        return FUNCTIONS.contains(token);
    }

    /**
     * Formats the result to remove trailing zeros and apply decimal rules.
     *
     * @param result The numerical result to format.
     * @return The formatted result as a string.
     */
    private static String formatResult(double result) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.##########", symbols);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(result);
    }
}
