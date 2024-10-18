package my.calculator.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import my.calculator.core.CalculatorLogic;
import my.calculator.core.ScientificMode;

public class CalculatorFrame {

    private JFrame frame;
    private JTextField textField;
    private CalculatorLogic calculatorLogic = new CalculatorLogic();
    private ScientificMode scientificMode = new ScientificMode();
    private String currentText = "";
    private boolean isScientificMode = false;

    public CalculatorFrame() {
        // Initialize the main frame
        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());

        // Create the display area (text field)
        textField = new JTextField("0", 20);
        textField.setEditable(false);
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        frame.add(textField, BorderLayout.NORTH);

        // Create the panel for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 4));

        // Add number buttons and basic operation buttons
        String[] buttons = {
                "C", "(", ")", "%",
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
                
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.addActionListener(new ButtonClickListener());
            panel.add(button);
        }

        // Add scientific mode button
        JButton scientificModeButton = new JButton("Sci");
        scientificModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isScientificMode = !isScientificMode;
                if (isScientificMode) {
                    panel.add(new JButton("sin"), 4);
                    panel.add(new JButton("cos"), 5);
                    panel.add(new JButton("tan"), 6);
                    panel.add(new JButton("Factorial"), 7);
                } else {
                    // Remove scientific buttons
                }
                frame.repaint();
            }
        });
        panel.add(scientificModeButton);

        frame.add(panel, BorderLayout.CENTER);

        // Make the frame visible
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.charAt(0) == 'C') {
                currentText = "";
                textField.setText("0");
            } else if (command.charAt(0) == '=') {
                try {
                    double result = calculatorLogic.add(Double.parseDouble(currentText), 0); // Replace with actual logic
                    textField.setText(String.valueOf(result));
                } catch (Exception ex) {
                    textField.setText("Error");
                }
            } else {
                currentText += command;
                textField.setText(currentText);
            }
        }
    }
}
