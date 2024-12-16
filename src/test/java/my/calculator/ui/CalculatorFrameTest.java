package my.calculator.ui;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CalculatorFrameTest {
    private final CalculatorFrame calculator = new CalculatorFrame();

    @Test
    void testInitialization() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            assertNotNull(calculator.getTextField(), "Text field should be initialized");
            assertEquals("", calculator.getCurrentText(), "Display should be empty initially");
        });
    }

    @Test
    void testScientificModeToggle() throws InvocationTargetException, InterruptedException {
        assertFalse(calculator.isScientific(), "Scientific mode should be initially off");
        SwingUtilities.invokeAndWait(() -> {
            calculator.toggleScientificButtons();
            assertTrue(calculator.isScientific(), "Scientific mode should be on after toggle");
        });
    }

    @Test
    void testBasicOperations() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            calculator.insertAtCaret("2+2");
            calculator.new ButtonClickListener().actionPerformed(
                new ActionEvent(calculator, ActionEvent.ACTION_PERFORMED, "=")
            );
            assertEquals("4", calculator.getCurrentText(), "Basic addition should work");
        });
    }
}