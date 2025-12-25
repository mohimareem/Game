import java.awt.*;
import javax.swing.*;

public class GameSelectionPanel extends JPanel {

    public GameSelectionPanel(JPanel cardPanel, CardLayout cardLayout) {

        setLayout(new GridBagLayout());
        setBackground(new Color(25, 25, 30)); // Dark clean background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 20, 0);

        // --- TITLE ---
        JLabel title = new JLabel("Welcome to Pokémon Match Game");
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        title.setForeground(new Color(255, 215, 0)); // Gold color
        add(title, gbc);

        // Space between title and button
        gbc.insets = new Insets(60, 0, 20, 0);

        // --- BUTTON ---
        JButton pokemonBtn = new JButton("Start Pokemon Match ⚡🔥🌊");
        pokemonBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        pokemonBtn.setBackground(new Color(70, 130, 180));
        pokemonBtn.setForeground(Color.WHITE);
        pokemonBtn.setFocusPainted(false);
        pokemonBtn.setPreferredSize(new Dimension(350, 80));
        pokemonBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        pokemonBtn.addActionListener(e -> cardLayout.show(cardPanel, "Pokemon Match"));

        add(pokemonBtn, gbc);

        // Extra spacing below button
        gbc.insets = new Insets(20, 0, 20, 0);
    }
}
