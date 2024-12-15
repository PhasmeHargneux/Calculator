package my.calculator.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;

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
    private final String[] sciButtons = { "sin", "cos", "tan", "exp", "asin", "acos", "atan", "ln",
            "n!", "√", "x²", "π", "log", "10^", "e", "^", "(", ")" };
    
    private final Set<String> FUNCTIONS = new HashSet<>(Arrays.asList(
            "sin", "cos", "tan", "asin", "acos", "atan", "exp", "ln", "log", "√", "x²", "n!", "10^x"
    ));
    private final Set<String> CONSTANTS = new HashSet<>(Arrays.asList(
            "e", "π"
    ));

    public CalculatorFrame() {
        initialize();
    }

    private void initialize() {
        // Initialize the main frame
        frame = new JFrame("Scientific Calculator");
        ImageIcon icon = new ImageIcon("src/resources/icon.png");
        frame.setIconImage(icon.getImage());
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
        textField.setFont(mainFont);
        Caret caret = textField.getCaret();
        caret.setVisible(true);
        textField.requestFocusInWindow();
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                textField.requestFocusInWindow();
            }
        });
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                currentText = textField.getText();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                currentText = textField.getText();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                currentText = textField.getText();
            }
        });
        textField.addActionListener(e -> {
            try {
                String result = CalculatorLogic.calculate(currentText);
                updateDisplay(result);
            } catch (Exception ex) {
                updateDisplay("Error: " + ex.getMessage());
            }
        });
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
            JButton button = new RoundedButtonUI(text);
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
        JButton scientificModeButton = new RoundedButtonUI("Sci");
        scientificModeButton.setBackground(Color.ORANGE); // Orange background for "Sci"
        scientificModeButton.addActionListener(e -> toggleScientificButtons());
        gbcBasic.gridx = 3;
        gbcBasic.gridy = row;
        basicPanel.add(scientificModeButton, gbcBasic); // Add to the basic panel

        frame.add(basicPanel, BorderLayout.CENTER);
    }

    private void initializeScientificPanel() {
        scientificPanel = new JPanel();
        GridBagLayout sciLayout = new GridBagLayout();
        scientificPanel.setLayout(sciLayout);
        scientificPanel.setPreferredSize(new Dimension(400, 0));
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
            JButton button = new RoundedButtonUI(text);
            button.addActionListener(e -> {
                String command = e.getActionCommand();
                if (command.equals("e") || command.equals("π")) {
                    insertConstantIntoTextField(command);
                } else if (FUNCTIONS.contains(command)) {
                    insertFunctionIntoTextField(command);
                } else {
                    int caretPos = textField.getCaretPosition();
                    String before = currentText.substring(0, caretPos);
                    String after = currentText.substring(caretPos);
                    String newText = before + command + after;
                    updateDisplay(newText);
                    textField.setCaretPosition(caretPos + command.length());
                }
            });

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

    // Helper method to update display and keep currentText in sync
    private void updateDisplay(String newText) {
        currentText = newText;
        textField.setText(newText);
        textField.setFont(mainFont); // Ensure default font is always used when displaying new text
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

    private void insertFunctionIntoTextField(String functionName) {
        int caretPos = textField.getCaretPosition();
        String before = currentText.substring(0, caretPos);
        String after = currentText.substring(caretPos);
        String newText = before + functionName + "()" + after;

        updateDisplay(newText);
        // Set the caret position right after the '(' to type inside the parentheses
        textField.setCaretPosition(caretPos + functionName.length() + 1);
    }

    private void insertConstantIntoTextField(String constant) {
        int caretPos = textField.getCaretPosition();
        String before = currentText.substring(0, caretPos);
        String after = currentText.substring(caretPos);
        String newText = before + constant + after;

        updateDisplay(newText);
        textField.setCaretPosition(caretPos + constant.length());
    }

    // ActionListener for button clicks
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            System.out.println("Button clicked: " + command);
            if (command.equals("AC")) {
                updateDisplay("");
            } else if (command.equals("C")) {
                if (!currentText.isEmpty()) {
                    String newText = currentText.substring(0, currentText.length() - 1);
                    updateDisplay(newText.isEmpty() ? "" : newText);
                }
            } else if (command.equals("=")) {
                try {
                    String result = CalculatorLogic.calculate(currentText);
                    updateDisplay(result);
                } catch (Exception ex) {
                    // Reduce the font size for error messages
                    Font smallFont = mainFont.deriveFont(12f); // Set to smaller size
                    textField.setFont(smallFont);
                    updateDisplay(ex.getMessage());
                }
            } else {
                // For digits and operators, insert at caret position
                int caretPos = textField.getCaretPosition();
                String before = currentText.substring(0, caretPos);
                String after = currentText.substring(caretPos);
                String newText = before + command + after;
                updateDisplay(newText);

                // Move caret forward by the length of the inserted command (usually 1 char)
                textField.setCaretPosition(caretPos + command.length());
            }
            textField.requestFocusInWindow();
        }
    }
}