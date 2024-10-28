import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import my.calculator.core.CalculatorLogic;

public class BasicOperationsTest {

    @Test
    public void testAddition() {
        assertEquals(5, CalculatorLogic.add(2, 3));
    }

    @Test
    public void testSubtraction() {
        assertEquals(1, CalculatorLogic.subtract(5, 4));
    }

    @Test
    public void testMultiplication() {
        assertEquals(12, CalculatorLogic.multiply(3, 4));
    }

    @Test
    public void testDivision() {
        assertEquals(2, CalculatorLogic.divide(6, 3));
    }

    @Test
    public void testModulo() {
        assertEquals(1, CalculatorLogic.modulo(10, 3));
    }

    @Test
    public void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            CalculatorLogic.divide(5, 0);
        });
    }

    @Test
    public void testModuloByZero() {
        assertThrows(ArithmeticException.class, () -> {
            CalculatorLogic.modulo(5, 0);
        });
    }
}
