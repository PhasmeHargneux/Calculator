package my.calculator.ui;

import my.calculator.core.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
        frame = new JFrame("My Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());

        // Create the display area (text field) with a larger font and styling
        textField = new RoundedTextFieldUI(20, 50, 50);
        textField.setEditable(false);
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        try {
            textField.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/AfacadFlux-ExtraBold.ttf"))
                    .deriveFont(48f)); // Set font size and style
        } catch (FontFormatException | IOException e) {
            textField.setFont(new Font("Arial", Font.PLAIN, 36)); // Fallback font
        }
        textField.setBackground(Color.BLACK); // Set background color
        textField.setForeground(Color.WHITE); // Set text color
        textField.setBorder(new EmptyBorder(40, 40, 5, 10)); // Add padding around text
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
        GridBagLayout gridBagLayout = new GridBagLayout();
        panel.setLayout(gridBagLayout);
        panel.setBackground(new Color(51, 51, 51));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between buttons
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        String[] buttons = { "C", "(", ")", "%", "7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "0", ".",
                "=", "+" };

        int row = 0;
        int col = 0;

        for (String text : buttons) {
            JButton button = new JButton(text);
            styleButton(button);
            button.addActionListener(new ButtonClickListener());

            gbc.gridx = col;
            gbc.gridy = row;

            panel.add(button, gbc);

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
        scientificModeButton.setBorderPainted(false);
        scientificModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Scientific");
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(scientificModeButton, gbc); // Add to the panel

        return panel;
    }

    private JPanel createScientificPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 5, 5)); // Adjusted to fit remaining buttons
        panel.setBackground(new Color(51, 51, 51));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] sciButtons = { "sin", "cos", "tan", "x!" };

        for (String text : sciButtons) {
            JButton button = new JButton(text);
            styleButton(button); // Apply styling to each scientific button
            button.addActionListener(new ButtonClickListener());
            panel.add(button);
        }

        // Add the orange "Sci" button
        JButton scientificModeButton = new JButton("Sci");
        styleButton(scientificModeButton);
        scientificModeButton.setBackground(Color.ORANGE);
        scientificModeButton.setBorderPainted(false);
        scientificModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Basic");
            }
        });
        panel.add(scientificModeButton);

        return panel;
    }

    // Method to style the buttons
    private void styleButton(JButton button) {
        try {
            button.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/AfacadFlux-ExtraBold.ttf"))
                    .deriveFont(36f)); // Set font size and style
        } catch (FontFormatException | IOException e) {
            button.setFont(new Font("Arial", Font.PLAIN, 36)); // Fallback font
            e.printStackTrace();
        }

        button.setFocusPainted(false); // Remove button focus border
        button.setBackground(Color.DARK_GRAY); // Set background color
        button.setForeground(Color.WHITE); // Set text color
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new RoundedButtonUI()); // Apply custom button UI
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("C")) {
                currentText = "";
                textField.setText("0");
            } else if (command.equals("=")) {
                currentText = CalculatorLogic.calculate(currentText);
                textField.setText(currentText);
                System.err.println(currentText);
            } else {
                currentText += command;
                textField.setText(currentText);
            }
        }
    }
}
