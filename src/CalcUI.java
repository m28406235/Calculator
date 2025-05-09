import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CalcUI extends JFrame {
    private final String[] BUTTONS = {
            "C", "DEL", "←", "→",
            "√", "^", "(", ")",
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
    };
    private Display display;
    private CalcLogic calcLogic;
    private CalcButtonHandler buttonClickHandler;

    public CalcUI() {
        setUndecorated(true);
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 400, 550, 30, 30));

        display = new Display();
        calcLogic = new CalcLogic();
        buttonClickHandler = new CalcButtonHandler(calcLogic, display);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(86, 100, 107));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel buttonPanel = new JPanel(new GridLayout(6, 4, 5, 5));
        buttonPanel.setOpaque(false);

        for (String label : BUTTONS) {
            JButton button = createButton(label);
            button.addActionListener(e -> buttonClickHandler.handleButtonClick(label));
            buttonPanel.add(button);
        }

        mainPanel.add(display.getDisplayPanel(), BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? getBackground().darker() : new Color(48, 64, 74));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalcUI calculator = new CalcUI();
            calculator.setVisible(true);
        });
    }
}