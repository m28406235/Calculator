import javax.swing.*;
import java.awt.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Display {
    private JTextField display;
    private JLabel previousExpression;
    private int cursorPosition = 0;
    private boolean cursorVisible = true;
    private Timer blinkTimer;
    private Color cursorColor = new Color(20, 20, 20);

    public Display() {
        display = new JTextField();
        display.setPreferredSize(new Dimension(0, 110));
        display.setFont(new Font("Roboto", Font.PLAIN, 30));
        display.setBackground(new Color(175, 196, 156));
        display.setForeground(Color.BLACK);
        display.setEditable(false);
        display.setBorder(BorderFactory.createEmptyBorder());
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setHighlighter(null);

        previousExpression = new JLabel();
        previousExpression.setFont(new Font("Roboto", Font.PLAIN, 16));
        previousExpression.setForeground(new Color(80, 80, 80));
        previousExpression.setHorizontalAlignment(JLabel.LEFT);

        CustomCaret customCaret = new CustomCaret();
        display.setCaret(customCaret);

        blinkTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cursorVisible = !cursorVisible;
                customCaret.setVisible(cursorVisible);
                display.repaint();
            }
        });
        blinkTimer.start();

        display.setFocusable(true);
        display.getCaret().setVisible(true);
        display.requestFocus();
    }

    private class CustomCaret extends DefaultCaret {
        @Override
        public void paint(Graphics g) {
            if (!isVisible()) {
                return;
            }

            JTextComponent component = getComponent();
            if (component == null) {
                return;
            }

            try {
                Rectangle r = component.modelToView2D(getDot()).getBounds();
                if (r == null)
                    return;

                int height = r.height;
                int y = r.y;
                int x = r.x;

                g.setColor(cursorColor);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(x, y + 2, x, y + height - 2);

            } catch (Exception e) {
            }
        }
    }

    public JTextField getDisplay() {
        return display;
    }

    public void setText(String text) {
        display.setText(text);
        cursorPosition = text.length();
    }

    public String getText() {
        return display.getText();
    }

    public void appendText(String text) {
        String currentText = display.getText();
        display.setText(currentText + text);
        cursorPosition = display.getText().length();
    }

    public void insertTextAtCursor(String text) {
        String currentText = display.getText();
        String newText = currentText.substring(0, cursorPosition) +
                text +
                currentText.substring(cursorPosition);
        display.setText(newText);
        cursorPosition += text.length();
        updateDisplayView();
    }

    public void moveCursorLeft() {
        if (cursorPosition > 0) {
            cursorPosition--;
            updateDisplayView();
        }
    }

    public void moveCursorRight() {
        if (cursorPosition < display.getText().length()) {
            cursorPosition++;
            updateDisplayView();
        }
    }

    private void updateDisplayView() {
        display.setCaretPosition(cursorPosition);
        display.getCaret().setVisible(true);
        display.requestFocus();
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int position) {
        this.cursorPosition = Math.min(Math.max(0, position), display.getText().length());
        updateDisplayView();
    }

    public void setLastExpression(String text) {
        previousExpression.setText(text);
    }

    public String getLastExpression() {
        return previousExpression.getText();
    }

    public void clearLastExpression() {
        previousExpression.setText("");
    }

    public JPanel getDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(53, 61, 67));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        displayPanel.setOpaque(false);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel greenDisplayArea = new JPanel(new BorderLayout());
        greenDisplayArea.setBackground(new Color(175, 196, 156));
        greenDisplayArea.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        greenDisplayArea.setOpaque(true);

        greenDisplayArea.setPreferredSize(new Dimension(0, 135));

        JPanel previousExpressionPanel = new JPanel(new BorderLayout());
        previousExpressionPanel.setOpaque(false);
        previousExpressionPanel.setPreferredSize(new Dimension(0, 22));
        previousExpressionPanel.add(previousExpression, BorderLayout.WEST);

        greenDisplayArea.add(previousExpressionPanel, BorderLayout.NORTH);
        greenDisplayArea.add(display, BorderLayout.CENTER);

        displayPanel.add(greenDisplayArea, BorderLayout.CENTER);

        return displayPanel;
    }

    public void cleanUpResources() {
        if (blinkTimer != null && blinkTimer.isRunning()) {
            blinkTimer.stop();
        }
    }
}