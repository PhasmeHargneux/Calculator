import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import my.calculator.core.ScientificMode;

public class MoreComplexTests {

    private final ScientificMode scientificMode = new ScientificMode();

    @Test
    public void testSin() {
        assertEquals(Math.sin(Math.toRadians(30)), scientificMode.sin(30), 0.001);
    }

    @Test
    public void testCos() {
        assertEquals(Math.cos(Math.toRadians(45)), scientificMode.cos(45), 0.001);
    }

    @Test
    public void testTan() {
        assertEquals(Math.tan(Math.toRadians(60)), scientificMode.tan(60), 0.001);
    }

    @Test
    public void testFactorial() {
        assertEquals(120, scientificMode.factorial(5));
    }

    @Test
    public void testFactorialOfZero() {
        assertEquals(1, scientificMode.factorial(0));
    }

    @Test
    public void testFactorialNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            scientificMode.factorial(-1);
        });
    }
}
