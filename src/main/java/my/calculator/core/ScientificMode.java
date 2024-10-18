package my.calculator.core;

public class ScientificMode {

    // Scientific operations
    public double sin(double angleInDegrees) {
        return Math.sin(Math.toRadians(angleInDegrees));
    }

    public double cos(double angleInDegrees) {
        return Math.cos(Math.toRadians(angleInDegrees));
    }

    public double tan(double angleInDegrees) {
        return Math.tan(Math.toRadians(angleInDegrees));
    }

    public double factorial(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Factorial is not defined for negative numbers");
        }
        if (number == 0 || number == 1) {
            return 1;
        }
        double result = 1;
        for (int i = 2; i <= number; i++) {
            result *= i;
        }
        return result;
    }
}
