package my.calculator.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JTextField;


public class RoundedTextFieldUI extends JTextField {

    private int arcWidth;
    private int arcHeight;

    public RoundedTextFieldUI(int columns, int arcWidth, int arcHeight) {
        super(columns); // This calls the JTextField constructor
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        setOpaque(false); // Make the background transparent for custom painting
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Enable anti-aliasing for smoother corners
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint a rounded rectangle as the background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw a rounded border
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
    }

    @Override
    public Insets getInsets() {
        return new Insets(10, 10, 10, 10); // Padding inside the text field
    }
}

