import java.awt.*;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Pokemon Match Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(850, 700);
            frame.setLocationRelativeTo(null);

            CardLayout layout = new CardLayout();
            JPanel cardPanel = new JPanel(layout);

            // Game Panels
            GameSelectionPanel selection = new GameSelectionPanel(cardPanel, layout);
            PokemonMatchPanel game = new PokemonMatchPanel();

            cardPanel.add(selection, "Menu");
            cardPanel.add(game, "Pokemon Match");

            frame.add(cardPanel);
            frame.setVisible(true);
        });
    }
}
