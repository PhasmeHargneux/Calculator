package my.calculator.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import my.calculator.core.CalculatorLogic;

public class CalculatorFrame {
    private JFrame frame;
    private JTextField textField;
    private String currentText = "";
    private Font mainFont;
    private boolean isScientific = false;
    private JPanel basicPanel;
    private JPanel scientificPanel;

    // List to store scientific buttons
    private String[] sciButtons = { "sin", "cos", "tan", "exp", "asin", "acos", "atan", "ln",
            "n!", "√", "x²", "π", "log", "10^x", "e", "x^y", "(", ")" };

    public CalculatorFrame() {
        initialize();
    }

    private void initialize() {
        // Initialize the main frame
        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600); // Adjusted size to accommodate both panels
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Initialize the mainFont
        try {
            mainFont = Font.createFont(Font.TRUETYPE_FONT,
                    new File("src/resources/AfacadFlux-ExtraBold.ttf")).deriveFont(36f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            mainFont = new Font("Arial", Font.PLAIN, 24); // Fallback font
        }

        // Initialize the display field
        textField = new RoundedTextFieldUI(4, 60, 60);
        textField.setPreferredSize(new Dimension(400, 100));        
        textField.setFont(mainFont);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setEditable(false);
        textField.setBackground(new Color(51, 51, 51));
        textField.setForeground(Color.WHITE);
        textField.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding around the text field
        frame.add(textField, BorderLayout.NORTH);

        // Initialize the button panels
        initializeBasicPanel();
        initializeScientificPanel();

        // Make the frame visible
        frame.setVisible(true);
    }

    private void initializeBasicPanel() {
        basicPanel = new JPanel();
        GridBagLayout basicLayout = new GridBagLayout();
        basicPanel.setLayout(basicLayout);
        basicPanel.setBackground(new Color(51, 51, 51));
        basicPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbcBasic = new GridBagConstraints();
        gbcBasic.fill = GridBagConstraints.BOTH;
        gbcBasic.insets = new Insets(5, 5, 5, 5); // Spacing between buttons
        gbcBasic.anchor = GridBagConstraints.CENTER;
        gbcBasic.weightx = 1.0;
        gbcBasic.weighty = 1.0;

        // Create and add basic buttons
        String[] basicButtons = { "AC", "C", "%", "÷", "7", "8", "9", "*",
                "4", "5", "6", "-", "1", "2", "3", "+", ".", "0", "=" };

        int row = 0;
        int col = 0;

        for (String text : basicButtons) {
            JButton button = new JButton(text);
            styleButton(button);
            button.addActionListener(new ButtonClickListener());

            gbcBasic.gridx = col;
            gbcBasic.gridy = row;

            basicPanel.add(button, gbcBasic);

            col++;
            if (col == 4) { // Move to the next row after every 4 buttons
                col = 0;
                row++;
            }
        }

        // Add the orange "Sci" button
        JButton scientificModeButton = new JButton("Sci");
        styleButton(scientificModeButton);
        scientificModeButton.setBackground(Color.ORANGE); // Orange background for "Sci"
        scientificModeButton.setBorderPainted(false); // Remove border
        scientificModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleScientificButtons();
            }
        });
        gbcBasic.gridx = 3;
        gbcBasic.gridy = row;
        basicPanel.add(scientificModeButton, gbcBasic); // Add to the basic panel

        frame.add(basicPanel, BorderLayout.CENTER);
    }

    private void initializeScientificPanel() {
        scientificPanel = new JPanel();
        GridBagLayout sciLayout = new GridBagLayout();
        scientificPanel.setLayout(sciLayout);
        scientificPanel.setBackground(new Color(51, 51, 51));
        scientificPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        scientificPanel.setVisible(false); // Initially hidden

        GridBagConstraints sciGbc = new GridBagConstraints();
        sciGbc.fill = GridBagConstraints.BOTH;
        sciGbc.insets = new Insets(5, 5, 5, 5);
        sciGbc.anchor = GridBagConstraints.CENTER;
        sciGbc.weightx = 1.0;
        sciGbc.weighty = 1.0;

        int sciRow = 0;
        int sciCol = 0;

        for (String text : sciButtons) {
            JButton button = new JButton(text);
            styleButton(button);
            button.addActionListener(new ButtonClickListener());

            sciGbc.gridx = sciCol;
            sciGbc.gridy = sciRow;

            scientificPanel.add(button, sciGbc);

            sciCol++;
            if (sciCol == 4) { // Adjust columns as needed
                sciCol = 0;
                sciRow++;
            }
        }

        frame.add(scientificPanel, BorderLayout.EAST);
    }

    // Method to toggle scientific buttons
    private void toggleScientificButtons() {
        if (!isScientific) {
            scientificPanel.setVisible(true);
            frame.setSize(frame.getWidth() + scientificPanel.getPreferredSize().width, frame.getHeight()); // Adjust width as needed
        } else {
            frame.setSize(frame.getWidth() - scientificPanel.getPreferredSize().width, frame.getHeight()); // Adjust width as needed
            scientificPanel.setVisible(false);
        }
        isScientific = !isScientific;
        frame.validate();
        frame.repaint();
    }

    // Apply styling to the buttons
    private void styleButton(JButton button) {
        button.setFont(mainFont);
        button.setPreferredSize(new Dimension(80, 80)); // Set preferred size
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // Disable focus painting for cleaner look
        button.setUI(new RoundedButtonUI());
    }

    // ActionListener for button clicks
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("AC")) {
                currentText = "";
                textField.setText("0");
                textField.setFont(mainFont); // Reset to default font
            } else if (command.equals("C")) {
                if (currentText.length() > 0) {
                    currentText = currentText.substring(0, currentText.length() - 1);
                    textField.setText(currentText.isEmpty() ? "0" : currentText);
                    textField.setFont(mainFont); // Ensure default font is used
                }
            } else if (command.equals("=")) {
                try {
                    String result = CalculatorLogic.calculate(currentText);
                    currentText = result;
                    textField.setText(result);
                    textField.setFont(mainFont); // Reset to default font
                } catch (Exception ex) {
                    // Reduce the font size for error messages
                    Font smallFont = mainFont.deriveFont(12f); // Set to smaller size
                    textField.setFont(smallFont);
                    textField.setText(ex.getMessage());
                    currentText = "";
                }
            } else {
                currentText += command;
                textField.setText(currentText);
                textField.setFont(mainFont); // Ensure default font is used
            }
        }
    }
}