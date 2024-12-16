package my.calculator.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CalculatorLogicTest {
    @Test
    void testBasicArithmetic() {
        assertEquals("4", CalculatorLogic.calculate("2+2"));
        assertEquals("6", CalculatorLogic.calculate("2*3"));
        assertEquals("1", CalculatorLogic.calculate("3-2"));
        assertEquals("2", CalculatorLogic.calculate("4/2"));
    }

    @Test
    void testScientificOperations() {
        assertEquals("1", CalculatorLogic.calculate("sin(90)"));
        assertEquals("4", CalculatorLogic.calculate("2^2"));
        assertEquals("6", CalculatorLogic.calculate("3!"));
        assertEquals("2", CalculatorLogic.calculate("√4"));
    }

    @Test
    void testConstants() {
        assertTrue(Math.abs(Double.parseDouble(CalculatorLogic.calculate("π")) - Math.PI) < 0.0001);
        assertTrue(Math.abs(Double.parseDouble(CalculatorLogic.calculate("e")) - Math.E) < 0.0001);
    }

    @Test
    void testErrorHandling() {
        assertThrows(ArithmeticException.class, () -> CalculatorLogic.calculate("1/0"));
        assertThrows(IllegalArgumentException.class, () -> CalculatorLogic.calculate("2++2"));
        assertThrows(ArithmeticException.class, () -> CalculatorLogic.calculate("(-4)!"));
    }
}