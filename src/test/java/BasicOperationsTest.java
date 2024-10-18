import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import my.calculator.core.CalculatorLogic;

public class BasicOperationsTest {

    private final CalculatorLogic calculatorLogic = new CalculatorLogic();

    @Test
    public void testAddition() {
        assertEquals(5, calculatorLogic.add(2, 3));
    }

    @Test
    public void testSubtraction() {
        assertEquals(1, calculatorLogic.subtract(5, 4));
    }

    @Test
    public void testMultiplication() {
        assertEquals(12, calculatorLogic.multiply(3, 4));
    }

    @Test
    public void testDivision() {
        assertEquals(2, calculatorLogic.divide(6, 3));
    }

    @Test
    public void testModulo() {
        assertEquals(1, calculatorLogic.modulo(10, 3));
    }

    @Test
    public void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculatorLogic.divide(5, 0);
        });
    }

    @Test
    public void testModuloByZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculatorLogic.modulo(5, 0);
        });
    }
}
