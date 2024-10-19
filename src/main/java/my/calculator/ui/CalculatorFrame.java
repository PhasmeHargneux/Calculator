package my.calculator.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CalculatorFrame {

    private JFrame frame;
    private JTextField textField;
    private String currentText = "";
    private JPanel mainPanel;
    private JPanel basicPanel;
    private JPanel scientificPanel;
    private CardLayout cardLayout;

    public CalculatorFrame() {
        // Initialize the main frame
        frame = new JFrame("Stylish Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());

        // Create the display area (text field) with a larger font and styling
        textField = new JTextField("0", 20);
        textField.setEditable(false);
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setFont(new Font("Arial", Font.BOLD, 36));  // Increase font size
        textField.setBackground(Color.BLACK);                 // Set background color
        textField.setForeground(Color.WHITE);                 // Set text color
        textField.setBorder(new EmptyBorder(40, 40, 5, 40)); // Add padding around text
        frame.add(textField, BorderLayout.NORTH);

        // Create the card layout for switching panels (Basic and Scientific modes)
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create the basic panel with styled buttons
        basicPanel = createBasicPanel();
        scientificPanel = createScientificPanel();

        // Add both panels to the main panel
        mainPanel.add(basicPanel, "Basic");
        mainPanel.add(scientificPanel, "Scientific");

        // Add the main panel to the frame
        frame.add(mainPanel, BorderLayout.CENTER);

        // Make the frame visible
        frame.setVisible(true);
    }

    private JPanel createBasicPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4, 5, 5)); // Add spacing between buttons (5px)

        String[] buttons = {"C", "(", ")", "%", "7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "0", ".", "=", "+"};

        for (String text : buttons) {
            JButton button = new JButton(text);
            styleButton(button); // Apply styling to each button
            button.addActionListener(new ButtonClickListener());
            panel.add(button);
        }

        // Add the scientific mode toggle button
        JButton scientificModeButton = new JButton("Sci");
        styleButton(scientificModeButton);
        scientificModeButton.setBackground(Color.ORANGE);  // Make the "Sci" button stand out
        scientificModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Scientific");
            }
        });
        panel.add(scientificModeButton);

        return panel;
    }

    private JPanel createScientificPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 3, 5, 5)); // Add spacing between scientific buttons

        String[] sciButtons = {"sin", "cos", "tan", "Factorial"};

        for (String text : sciButtons) {
            JButton button = new JButton(text);
            styleButton(button); // Apply styling to each scientific button
            button.addActionListener(new ButtonClickListener());
            panel.add(button);
        }

        // Add button to switch back to basic mode
        JButton basicModeButton = new JButton("Basic");
        styleButton(basicModeButton);
        basicModeButton.setBackground(Color.CYAN);  // Make the "Basic" button stand out
        basicModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Basic");
            }
        });
        panel.add(basicModeButton);

        return panel;
    }

    // Method to style the buttons
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 20));      // Set font size and style
        button.setFocusPainted(false);                          // Remove button focus border
        button.setBackground(Color.DARK_GRAY);                  // Set background color
        button.setForeground(Color.WHITE);                      // Set text color
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new RoundedButtonUI());                    // Apply custom button UI
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.charAt(0) == 'C') {
                currentText = "";
                textField.setText("0");
            } else if (command.charAt(0) == '=') {
                textField.setText(currentText); // Example: replace with actual logic
            } else {
                currentText += command;
                textField.setText(currentText);
            }
        }
    }
}