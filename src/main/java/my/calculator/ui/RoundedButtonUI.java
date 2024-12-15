package my.calculator.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * A custom JButton with rounded corners and enhanced styling.
 */
public class RoundedButtonUI extends JButton {

    private Font mainFont;
    public RoundedButtonUI(String text) {
        super(text);

        try {
            mainFont = Font.createFont(Font.TRUETYPE_FONT,
            new File("src/resources/AfacadFlux-ExtraBold.ttf")).deriveFont(36f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            mainFont = new Font("Arial", Font.PLAIN, 24); // Fallback font
        }

        setPreferredSize(new Dimension(40, 40));
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBorderPainted(false);
        setBackground(Color.DARK_GRAY);
        setForeground(Color.WHITE);
        setFont(mainFont);
    }

    @Override
    protected void paintComponent(Graphics g) {
        final int CORNER_RADIUS = 20;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set button background color
        if (getModel().isArmed()) {
            g2.setColor(getBackground().darker());
        } else {
            g2.setColor(getBackground());
        }

        // Draw rounded rectangle background
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS));

        // Draw button text
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        g2.setColor(getForeground());
        g2.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 3);

        g2.dispose();

        super.paintComponent(g);
    }
}
