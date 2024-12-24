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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import my.calculator.core.CalculatorLogic;

/**
 * Main application frame for the Java Scientific Calculator.
 */
public class CalculatorFrame {
    private JFrame frame;
    private JTextField textField;
    private String currentText = "";
    private List<String> history = new ArrayList<>();
    private int historyIndex = -1;
    private Font mainFont;
    private boolean isScientific = false;
    private JPanel scientificPanel;
    private boolean isError = false;
    private final String[] sciButtons = { 
        "sin", "cos", "tan", "exp", "asin", "acos", "atan", "ln",
        "!", "√", "x²", "π", "log", "10^", "e", "^", "(", ")", "←", "→"
    };
    private final Set<String> functions = new HashSet<>(Arrays.asList(
            "sin", "cos", "tan", "asin", "acos", "atan", "exp", "ln", "log", "√", "10^x"
    ));

    public boolean isScientific() {
        return isScientific;
    }

    public JTextField getTextField() {
        return textField;
    }

    public String getCurrentText() {
        return currentText;
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getScientificPanel() {
        return scientificPanel;
    }

    public int getCaretPosition() {
        return textField.getCaretPosition();
    }

    /** Constructs the calculator frame and initializes UI. */
    public CalculatorFrame() {
        initialize();
    }

    /** Sets up all UI components and event listeners. */
    private void initialize() {
        frame = new JFrame("Java Scientific Calculator");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        try {
            mainFont = Font.createFont(Font.TRUETYPE_FONT,
                    new File("src/main/resources/AfacadFlux-ExtraBold.ttf")).deriveFont(36f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            mainFont = new Font("Arial", Font.PLAIN, 24);
        }

        textField = new RoundedTextFieldUI(4, 60, 60);
        textField.setFont(mainFont);
        Caret caret = textField.getCaret();
        caret.setVisible(true);
        textField.setCaretColor(Color.GRAY);
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
                String expr = currentText;
                String result = CalculatorLogic.calculate(expr);
                addToHistory(expr);
                updateDisplay(result);
            } catch (Exception ex) {
                updateDisplay("Error: " + ex.getMessage());
            }
        });
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    navigateHistory(-1);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    navigateHistory(1);
                }
            }
        });
        frame.add(textField, BorderLayout.NORTH);

        initializeBasicPanel();
        initializeScientificPanel();
        frame.setVisible(true);
    }

    /** Builds and lays out basic (non-scientific) calculator buttons. */
    private void initializeBasicPanel() {
        JPanel basicPanel = new JPanel();
        GridBagLayout basicLayout = new GridBagLayout();
        basicPanel.setLayout(basicLayout);
        basicPanel.setBackground(new Color(51, 51, 51));
        basicPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbcBasic = new GridBagConstraints();
        gbcBasic.fill = GridBagConstraints.BOTH;
        gbcBasic.insets = new Insets(5, 5, 5, 5);
        gbcBasic.anchor = GridBagConstraints.CENTER;
        gbcBasic.weightx = 1.0;
        gbcBasic.weighty = 1.0;

        String[] basicButtons = {
            "AC", "C", "%", "÷",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            ".", "0", "="
        };

        int row = 0;
        int col = 0;
        for (String text : basicButtons) {
            JButton button = new RoundedButtonUI(text);
            button.setFont(mainFont);
            button.addActionListener(new ButtonClickListener());
            gbcBasic.gridx = col;
            gbcBasic.gridy = row;
            basicPanel.add(button, gbcBasic);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }

        JButton scientificModeButton = new RoundedButtonUI("Sci");
        scientificModeButton.setFont(mainFont);
        scientificModeButton.setBackground(Color.ORANGE);
        scientificModeButton.addActionListener(e -> toggleScientificButtons());
        gbcBasic.gridx = 3;
        gbcBasic.gridy = row;
        basicPanel.add(scientificModeButton, gbcBasic);

        frame.add(basicPanel, BorderLayout.CENTER);
    }

    /** Builds and lays out scientific calculator buttons. */
    private void initializeScientificPanel() {
        scientificPanel = new JPanel();
        setupScientificPanelLayout();
        addScientificButtons();
        frame.add(scientificPanel, BorderLayout.EAST);
    }

    private void setupScientificPanelLayout() {
        GridBagLayout sciLayout = new GridBagLayout();
        scientificPanel.setLayout(sciLayout);
        scientificPanel.setPreferredSize(new Dimension(400, 0));
        scientificPanel.setBackground(new Color(51, 51, 51));
        scientificPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        scientificPanel.setVisible(false);
    }

    private void addScientificButtons() {
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
            button.setFont(mainFont);
            button.addActionListener(this::handleScientificButtonClick);
            sciGbc.gridx = sciCol;
            sciGbc.gridy = sciRow;
            scientificPanel.add(button, sciGbc);
            sciCol++;
            if (sciCol == 4) {
                sciCol = 0;
                sciRow++;
            }
        }
    }

    private void handleScientificButtonClick(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("e") || command.equals("π")) {
            insertConstantIntoTextField(command);
        } else if (functions.contains(command)) {
            insertFunctionIntoTextField(command);
        } else if (command.equals("x²")) {
            insertAtCaret("^2");
        } else if (command.equals("←")) {
            int pos = textField.getCaretPosition();
            if (pos > 0) {
                setCurrentText(
                    currentText.substring(0, pos - 1)
                    + currentText.substring(pos)
                );
                textField.setCaretPosition(pos - 1);
            }
        } else if (command.equals("→")) {
            int pos = textField.getCaretPosition();
            if (pos < currentText.length()) {
                textField.setCaretPosition(pos + 1);
            }
        } else if (command.equals("(") || command.equals(")")) {
            insertAtCaret(command);
        } else if (command.equals("^")) {
            insertAtCaret("^");
        }
    }

    private void updateDisplay(String newText) {
        currentText = newText;
        textField.setText(newText);
        textField.setFont(mainFont);
    }

    public void toggleScientificButtons() {
        if (!isScientific) {
            scientificPanel.setVisible(true);
            frame.setSize(frame.getWidth() + scientificPanel.getPreferredSize().width, frame.getHeight());
        } else {
            frame.setSize(frame.getWidth() - scientificPanel.getPreferredSize().width, frame.getHeight());
            scientificPanel.setVisible(false);
        }
        isScientific = !isScientific;
        frame.validate();
        frame.repaint();
    }

    public void insertFunctionIntoTextField(String functionName) {
        int caretPos = textField.getCaretPosition();
        String before = currentText.substring(0, caretPos);
        String after = currentText.substring(caretPos);
        String newText = before + functionName + "()" + after;
        updateDisplay(newText);
        textField.setCaretPosition(caretPos + functionName.length() + 1);
    }

    public void insertConstantIntoTextField(String constant) {
        int caretPos = textField.getCaretPosition();
        String before = currentText.substring(0, caretPos);
        String after = currentText.substring(caretPos);
        String newText = before + constant + after;
        updateDisplay(newText);
        textField.setCaretPosition(caretPos + constant.length());
    }

    public void insertAtCaret(String text) {
        if (isError) {
            isError = false;
            textField.setFont(mainFont);
            updateDisplay("");
        }
        int caretPos = textField.getCaretPosition();
        String before = currentText.substring(0, caretPos);
        String after = currentText.substring(caretPos);
        String newText = before + text + after;
        updateDisplay(newText);
        textField.setCaretPosition(caretPos + text.length());
    }

    public void showError(String errorMessage) {
        isError = true;
        Font smallFont = mainFont.deriveFont(12f);
        textField.setFont(smallFont);
        updateDisplay(errorMessage);
    }

    private void addToHistory(String expression) {
        history.add(expression);
        historyIndex = history.size();
    }

    private void navigateHistory(int direction) {
        if (history.isEmpty()) return;
        historyIndex += direction;
        if (historyIndex < 0) {
            historyIndex = 0;
        } else if (historyIndex >= history.size()) {
            historyIndex = history.size() - 1;
        }
        setCurrentText(history.get(historyIndex));
    }

    public void setCurrentText(String text) {
        updateDisplay(text);
    }

    /**
     * Handles basic calculator button actions.
     */
    public class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch (command) {
                case "AC" -> updateDisplay("");
                case "C" -> {
                    if (!currentText.isEmpty()) {
                        String newText = currentText.substring(0, currentText.length() - 1);
                        updateDisplay(newText.isEmpty() ? "" : newText);
                    }
                }
                case "=" -> {
                    try {
                        String expr = currentText;
                        String result = CalculatorLogic.calculate(expr);
                        addToHistory(expr);
                        updateDisplay(result);
                    } catch (Exception ex) {
                        showError("Error: " + ex.getMessage());
                    }
                }
                default -> insertAtCaret(command);
            }
            textField.requestFocusInWindow();
        }
    }
}