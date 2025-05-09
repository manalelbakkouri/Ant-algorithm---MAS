package Fourmi;

import java.util.*;

public class Terrain {
    private List<Fourmi> fourmis; // Liste des fourmis

    private List<Position> nourritures;
    private Set<Position> obstacles;
    private Position fourmiliere;
    private double[][] pheromones;
    private final double TAUX_Evaporation = 0.01;
    private final double PHEROMONE_DEPOT = 1.0;

    private final int TAILLE = 21;
    // Méthode pour récupérer la liste des fourmis
    public List<Fourmi> getFourmis() {
        return fourmis;
    }

    public Terrain() {
        nourritures = new ArrayList<>();
        obstacles = new HashSet<>();
        fourmiliere = new Position(0, 0);
        pheromones = new double[TAILLE][TAILLE];
    }

    public void deposerPheromone(Position p, double quantite) {
        if (estValide(p)) {
            pheromones[p.getX()][p.getY()] += quantite;
        }
    }

    public double getPheromone(Position p) {
        if (estValide(p)) {
            return pheromones[p.getX()][p.getY()];
        }
        return 0.0;
    }

    public void evaporerPheromones() {
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                pheromones[i][j] *= (1.0 - TAUX_Evaporation);
            }
        }
    }

    public void afficher(List<Fourmi> fourmis) {
        char[][] grille = new char[TAILLE][TAILLE];

        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                grille[i][j] = '.';
            }
        }

        grille[fourmiliere.getX()][fourmiliere.getY()] = 'F';

        for (Position n : nourritures) {
            grille[n.getX()][n.getY()] = 'N';
        }

        for (Fourmi f : fourmis) {
            Position pos = f.getPosition();
            if (estValide(pos)) {
                grille[pos.getX()][pos.getY()] = 'A';
            }
        }

        // Afficher les phéromones (en + si aucune fourmi ou nourriture)
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                if (pheromones[i][j] > 0.1 && grille[i][j] == '.') {
                    grille[i][j] = '+';
                }
            }
        }

        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                System.out.print(grille[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void ajouterNourriture(Position p) {
        nourritures.add(p);
    }

    public List<Position> getNourritures() {
        return nourritures;
    }

    public void ajouterObstacle(Position p) {
        obstacles.add(p);
    }

    public boolean estAccessible(Position p) {
        return !obstacles.contains(p);
    }

    public boolean estValide(Position p) {
        return p.getX() >= 0 && p.getX() < TAILLE &&
               p.getY() >= 0 && p.getY() < TAILLE &&
               estAccessible(p);
    }

    public Position getFourmiliere() {
        return fourmiliere;
    }

    public void setFourmiliere(Position p) {
        this.fourmiliere = p;
    }

    public boolean contientNourriture(Position p) {
        return nourritures.contains(p);
    }

    public void retirerNourriture(Position p) {
        nourritures.remove(p);
    }

    public boolean estFourmiliere(Position p) {
        return p.equals(fourmiliere);
    }
    
    
    public char getSymboleCase(Position pos, List<Fourmi> fourmis) {
        // Si c'est la fourmilière
        if (estFourmiliere(pos)) return 'F';

        // Si une fourmi est sur cette case
        for (Fourmi f : fourmis) {
            if (f.getPosition().equals(pos)) return 'A'; // A pour "Ant"
        }

        // Si c’est une nourriture
        if (contientNourriture(pos)) return 'N';

        // Si phéromone présente
        if (estValide(pos) && pheromones[pos.getX()][pos.getY()] > 0.1) return '+';

        return ' '; // Case vide
    }

}
