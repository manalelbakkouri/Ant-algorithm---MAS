package Fourmi;

import javax.swing.*;
import java.awt.*;

public class Fenetre extends JFrame {
    private final SimulationManager simulation;
    private final int tailleCase = 25;

    private final JButton startBtn = new JButton("▶ Lancer");
    private final JButton stopBtn = new JButton("⏹ Arrêter");
    private final JButton resetBtn = new JButton("🔄 Réinitialiser");
    private final JButton showPathBtn = new JButton("🧭 Chemin Optimal");

    private final JTextField nbrFourmisField = new JTextField("10", 5);
    private final JTextField nbrNourritureField = new JTextField("3", 5);
    private final JTextField iterationsField = new JTextField("100", 5);

    private final JLabel cheminLabel = new JLabel("Longueur du chemin optimal : -");
    private boolean afficherChemin = false;

    public Fenetre(SimulationManager simulation) {
        this.simulation = simulation;
        setTitle("Simulation de Fourmis 🐜");
        setSize(700, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panneauGrille = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerGrille(g);
            }
        };
        panneauGrille.setPreferredSize(new Dimension(525, 525));
        panneauGrille.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // ========== PANEL CONTRÔLES ========== //
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new GridLayout(3, 2, 10, 10));
        configPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
        configPanel.setFont(new Font("Arial", Font.PLAIN, 14));

        configPanel.add(new JLabel("Nombre de fourmis :"));
        configPanel.add(nbrFourmisField);
        configPanel.add(new JLabel("Nombre de nourritures :"));
        configPanel.add(nbrNourritureField);
        configPanel.add(new JLabel("Nombre d’itérations :"));
        configPanel.add(iterationsField);

        Font buttonFont = new Font("Arial", Font.BOLD, 13);
        startBtn.setFont(buttonFont);
        stopBtn.setFont(buttonFont);
        resetBtn.setFont(buttonFont);
        showPathBtn.setFont(buttonFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.add(startBtn);
        buttonPanel.add(stopBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(showPathBtn);

        cheminLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cheminLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel basPanel = new JPanel(new BorderLayout(10, 10));
        basPanel.add(configPanel, BorderLayout.NORTH);
        basPanel.add(buttonPanel, BorderLayout.CENTER);
        basPanel.add(cheminLabel, BorderLayout.SOUTH);

        // ========== AJOUTS ========= //
        add(panneauGrille, BorderLayout.CENTER);
        add(basPanel, BorderLayout.SOUTH);
        setVisible(true);

        // ========== ACTIONS ========= //
        startBtn.addActionListener(e -> simulation.demarrerSimulation(
                Integer.parseInt(nbrFourmisField.getText()),
                Integer.parseInt(nbrNourritureField.getText()),
                Integer.parseInt(iterationsField.getText()),
                this
        ));

        stopBtn.addActionListener(e -> simulation.stopSimulation());

        resetBtn.addActionListener(e -> {
            simulation.reset();
            rafraichir();
            cheminLabel.setText("Longueur du chemin optimal : -");
        });

        showPathBtn.addActionListener(e -> {
            afficherChemin = !afficherChemin;
            rafraichir();
        });
    }

    private void dessinerGrille(Graphics g) {
        Terrain terrain = simulation.getTerrain();
        for (int y = 0; y < 21; y++) {
            for (int x = 0; x < 21; x++) {
                Position p = new Position(x, y);
                char symbole = terrain.getSymboleCase(p, simulation.getFourmis());

                if (afficherChemin && simulation.isOptimalPath(p)) {
                    g.setColor(Color.YELLOW);
                } else {
                    switch (symbole) {
                        case 'F' -> g.setColor(Color.GREEN);
                        case 'N' -> g.setColor(Color.RED);
                        case 'A' -> g.setColor(Color.BLACK);
                        case '+' -> g.setColor(Color.BLUE);
                        default -> g.setColor(new Color(245, 245, 245));
                    }
                }

                g.fillRect(x * tailleCase, y * tailleCase, tailleCase, tailleCase);
                g.setColor(Color.GRAY);
                g.drawRect(x * tailleCase, y * tailleCase, tailleCase, tailleCase);
            }
        }
    }

    public void rafraichir() {
        cheminLabel.setText("Longueur du chemin optimal : " + simulation.getCheminOptimalLongueur());
        repaint();
    }
}
