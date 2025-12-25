import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PokemonMatchPanel extends JPanel {
    private static final String[] NAMES = new String[]{
            "darkness","double","fairy","fighting","fire","grass","lightning","metal","psychic","water"
    };
    private static final int ROWS = 4;
    private static final int COLS = 5;
    private static final int CW = 90;
    private static final int CH = 128;

    private ArrayList<Card> deck;
    private ArrayList<JButton> buttons;
    private boolean[] matched;
    private ImageIcon backIcon;
    private Timer initialTimer;
    private Timer mismatchTimer;
    private JButton first;
    private JButton second;
    private boolean ready = false;
    private int errors = 0;
    private int matches = 0;
    private JLabel lblErrors = new JLabel("Errors: 0", JLabel.CENTER);
    private JLabel lblMatches = new JLabel("Matches: 0", JLabel.CENTER);
    private JButton btnRestart = new JButton("Restart Game");

    public PokemonMatchPanel() {
        this.setLayout(new BorderLayout(5, 5));
        this.setBackground(new Color(30, 30, 35));

        // Top info
        JPanel top = new JPanel(new GridLayout(1, 2));
        top.setOpaque(false);
        this.lblErrors.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.lblErrors.setForeground(new Color(255, 100, 100));
        this.lblMatches.setFont(new Font("Segoe UI", Font.BOLD, 18));
        this.lblMatches.setForeground(new Color(100, 255, 100));
        top.add(this.lblErrors);
        top.add(this.lblMatches);
        this.add(top, BorderLayout.NORTH);

        // Grid panel
        JPanel grid = new JPanel(new GridLayout(ROWS, COLS, 5, 5));
        grid.setPreferredSize(new Dimension(COLS * (CW + 2), ROWS * (CH + 2)));
        grid.setBackground(new Color(40, 40, 45));

        // Setup deck and UI structures
        this.setupDeck();
        this.matched = new boolean[this.deck.size()];
        this.buttons = new ArrayList<>();

        // create buttons and add listeners (fix: capture index as final variable)
        for (int i = 0; i < this.deck.size(); i++) {
            final int index = i; // <-- important: effectively-final for lambda capture
            JButton b = new JButton();
            b.setIcon(this.deck.get(i).iconFace); // show face initially; will be hidden by hideAll()/shuffle
            b.setPreferredSize(new Dimension(CW, CH));
            b.setFocusable(false);
            b.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
            b.addActionListener(e -> this.onCardClick(b, index));
            this.buttons.add(b);
            grid.add(b);
        }

        this.add(grid, BorderLayout.CENTER);

        // Bottom: restart button
        this.btnRestart = createStyledButton("Restart Game");
        this.btnRestart.setEnabled(false);
        this.btnRestart.addActionListener(e -> this.resetGame());
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(this.btnRestart);
        this.add(bottom, BorderLayout.SOUTH);

        // Timers
        this.initialTimer = new Timer(3000, e -> {
            this.initialTimer.stop();
            this.hideAll();
            this.ready = true;
            this.btnRestart.setEnabled(true);
        });
        this.initialTimer.setRepeats(false);

        this.mismatchTimer = new Timer(1000, e -> {
            this.mismatchTimer.stop();
            this.hideMismatch();
        });
        this.mismatchTimer.setRepeats(false);

        // Start game
        this.resetGame();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void setupDeck() {
        this.deck = new ArrayList<>();
        for (String nm : NAMES) {
            ImageIcon face = this.loadIcon("/pokemonimage/" + nm + ".jpg");
            if (face == null) {
                face = createPlaceholderIcon(nm);
            }
            this.deck.add(new Card(nm, face));
        }
        // duplicate to make pairs
        this.deck.addAll(new ArrayList<>(this.deck));
        this.backIcon = this.createCardBackImage();
    }

    private ImageIcon createCardBackImage() {
        BufferedImage img = new BufferedImage(CW, CH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        GradientPaint gradient = new GradientPaint(0f, 0f, new Color(70, 70, 120), CW, CH, new Color(30, 30, 60));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, CW, CH);
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(0, 0, CW, CH);
        g2d.drawLine(CW, 0, 0, CH);
        g2d.setColor(Color.WHITE);
        g2d.fillOval(CW/3, CH/2 - 15, 30, 30);
        g2d.setColor(Color.RED);
        g2d.fillArc(CW/3, CH/2 - 15, 30, 30, 0, 180);
        g2d.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon loadIcon(String path) {
        try {
            java.net.URL res = this.getClass().getResource(path);
            if (res == null) return null;
            Image img = new ImageIcon(res).getImage().getScaledInstance(CW, CH, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception ex) {
            return null;
        }
    }

    private ImageIcon createPlaceholderIcon(String name) {
        BufferedImage img = new BufferedImage(CW, CH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(80,80,100));
        g.fillRect(0,0,CW,CH);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString(name, 8, CH/2);
        g.dispose();
        return new ImageIcon(img);
    }

    private void onCardClick(JButton b, int idx) {
        if (!this.ready) return;
        if (this.matched[idx]) return;

        // allow flip only if currently showing back
        if (b.getIcon() == this.backIcon) {
            b.setIcon(this.deck.get(idx).iconFace);
            b.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));

            if (this.first == null) {
                this.first = b;
                return;
            }

            if (this.second == null && b != this.first) {
                this.second = b;
                this.ready = false; // lock until resolution

                int i1 = this.buttons.indexOf(this.first);
                int i2 = idx;
                if (this.deck.get(i1).name.equals(this.deck.get(i2).name)) {
                    // match
                    this.matched[i1] = this.matched[i2] = true;
                    this.matches++;
                    this.lblMatches.setText("Matches: " + this.matches);
                    this.first.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                    this.second.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                    this.first = this.second = null;
                    this.ready = true;

                    if (this.matches == NAMES.length) {
                        this.btnRestart.setEnabled(true);
                        JOptionPane.showMessageDialog(this, "You won with " + this.errors + " errors!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    // mismatch
                    this.errors++;
                    this.lblErrors.setText("Errors: " + this.errors);
                    this.mismatchTimer.restart();
                }
            }
        }
    }

    private void hideAll() {
        for (int i = 0; i < this.buttons.size(); i++) {
            if (!this.matched[i]) {
                JButton jb = this.buttons.get(i);
                jb.setIcon(this.backIcon);
                jb.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
            }
        }
    }

    private void hideMismatch() {
        if (this.first != null && this.second != null) {
            this.first.setIcon(this.backIcon);
            this.second.setIcon(this.backIcon);
            this.first.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
            this.second.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
        }
        this.first = this.second = null;
        this.ready = true;
    }

    private void resetGame() {
        this.shuffleAndDeal();
        this.initialTimer.restart();
    }

    private void shuffleAndDeal() {
        Collections.shuffle(this.deck);
        for (int i = 0; i < this.buttons.size(); i++) {
            JButton jb = this.buttons.get(i);
            jb.setIcon(this.deck.get(i).iconFace);
            jb.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
            this.matched[i] = false;
        }
        this.errors = 0;
        this.matches = 0;
        this.lblErrors.setText("Errors: 0");
        this.lblMatches.setText("Matches: 0");
        this.ready = false;
        this.first = this.second = null;
        this.btnRestart.setEnabled(false);
    }

    private class Card {
        String name;
        ImageIcon iconFace;

        Card(String name, ImageIcon iconFace) {
            this.name = name;
            this.iconFace = iconFace;
        }
    }
}
