package Fourmi;

import java.util.*;

public class SimulationManager {
    private Terrain terrain;
    private List<Fourmi> fourmis;
    private Position fourmiliere;
    private List<Position> cheminOptimal;

    private int nbFourmis;
    private int nbNourriture;
    private int nbIterations;
    private int iterationActuelle;
    private boolean simulationEnCours;

    private Fenetre fenetre; // pour rafraîchir depuis la simulation si besoin

    public SimulationManager() {
        reset();
    }

    // ====== Méthodes principales ======

    public void demarrerSimulation(int nbFourmis, int nbNourriture, int nbIterations, Fenetre fenetre) {
        this.nbFourmis = nbFourmis;
        this.nbNourriture = nbNourriture;
        this.nbIterations = nbIterations;
        this.iterationActuelle = 0;
        this.simulationEnCours = true;
        this.fenetre = fenetre;

        initialiserTerrainEtFourmis();
    }

    public boolean iteration() {
        if (!simulationEnCours || iterationActuelle >= nbIterations) return false;

        faireUnTour();
        iterationActuelle++;

        if (fenetre != null) {
            fenetre.rafraichir();
        }

        return true;
    }

    public void stopSimulation() {
        this.simulationEnCours = false;
    }

    public void reset() {
        terrain = new Terrain();
        fourmiliere = new Position(10, 10);
        terrain.setFourmiliere(fourmiliere);
        fourmis = new ArrayList<>();
        cheminOptimal = null;
        iterationActuelle = 0;
        simulationEnCours = false;
    }

    private void initialiserTerrainEtFourmis() {
        terrain = new Terrain();
        fourmiliere = new Position(10, 10);
        terrain.setFourmiliere(fourmiliere);

        // Ajout nourriture aléatoire
        Random rand = new Random();
        for (int i = 0; i < nbNourriture; i++) {
            int x = rand.nextInt(21);
            int y = rand.nextInt(21);
            terrain.ajouterNourriture(new Position(x, y));
        }

        // Création des fourmis
        fourmis = new ArrayList<>();
        for (int i = 0; i < nbFourmis; i++) {
            fourmis.add(new Fourmi(new Position(10, 10), fourmiliere));
        }
    }

    // ====== Simulation du comportement ======

    public void faireUnTour() {
        for (Fourmi f : fourmis) {
            Position pos = f.getPosition();

            if (!f.porteNourriture()) {
                Position cible = trouverNourritureLaPlusProche(pos, terrain.getNourritures());
                if (cible != null) {
                    List<Position> chemin = PathFinder.trouverChemin(pos, cible, terrain);
                    if (!chemin.isEmpty() && chemin.size() > 1) {
                        f.deplacer(chemin.get(1));
                    }

                    if (terrain.contientNourriture(f.getPosition())) {
                        f.prendreNourriture();
                        terrain.retirerNourriture(f.getPosition());
                    }
                }
            } else {
                List<Position> cheminRetour = PathFinder.trouverChemin(pos, fourmiliere, terrain);
                if (!cheminRetour.isEmpty() && cheminRetour.size() > 1) {
                    f.deplacer(cheminRetour.get(1));
                    terrain.deposerPheromone(f.getPosition(), 10.0);
                }

                if (terrain.estFourmiliere(f.getPosition())) {
                    f.retourFourmiliere();
                }
            }
        }

        terrain.evaporerPheromones();

        // Mise à jour du chemin optimal fictif (pour démo)
        cheminOptimal = PathFinder.trouverChemin(fourmiliere, trouverNourritureLaPlusProche(fourmiliere, terrain.getNourritures()), terrain);
    }

    private Position trouverNourritureLaPlusProche(Position pos, List<Position> nourritures) {
        Position plusProche = null;
        int minDistance = Integer.MAX_VALUE;

        for (Position p : nourritures) {
            int dist = Math.abs(p.getX() - pos.getX()) + Math.abs(p.getY() - pos.getY());
            if (dist < minDistance) {
                minDistance = dist;
                plusProche = p;
            }
        }
        return plusProche;
    }

    // ====== Getters utiles pour l'interface graphique ======

    public Terrain getTerrain() {
        return terrain;
    }

    public List<Fourmi> getFourmis() {
        return fourmis;
    }

    public int getCheminOptimalLongueur() {
        return (cheminOptimal != null) ? cheminOptimal.size() : 0;
    }

    public boolean isOptimalPath(Position p) {
        return cheminOptimal != null && cheminOptimal.contains(p);
    }

    public List<Position> getCheminOptimal() {
        return cheminOptimal;
    }

    public void setCheminOptimal(List<Position> chemin) {
        this.cheminOptimal = chemin;
    }
}
