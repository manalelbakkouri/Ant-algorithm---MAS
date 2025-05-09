package Fourmi;
import java.util.*;


public class Fourmi {
    private Position position;
    private boolean porteNourriture;
    private Position positionFourmiliere;

    public Fourmi(Position position, Position fourmiliere) {
        this.position = position;
        this.positionFourmiliere = fourmiliere;
    }
    
    public void laisserPheromone(Terrain terrain) {
        if (porteNourriture) {
            terrain.deposerPheromone(position, 1.0);
        }
    }
    
    public Position choisirDirectionAvecPheromones(Terrain terrain) {
        List<Position> voisines = new ArrayList<>();
        int x = position.getX();
        int y = position.getY();

        // Déplacements possibles
        Position[] directions = {
            new Position(x + 1, y),
            new Position(x - 1, y),
            new Position(x, y + 1),
            new Position(x, y - 1)
        };

        double maxPheromone = -1.0;
        Position meilleure = null;

        for (Position p : directions) {
            if (terrain.estValide(p)) {
                double val = terrain.getPheromone(p);
                if (val > maxPheromone) {
                    maxPheromone = val;
                    meilleure = p;
                }
                voisines.add(p); // on garde toutes les cases valides
            }
        }

        // Si aucune phéromone, choisir une position aléatoire
        if (meilleure == null && !voisines.isEmpty()) {
            Collections.shuffle(voisines);
            return voisines.get(0);
        }

        return meilleure;
    }
    
    public void chercherNourriture(Terrain terrain) {
        Position prochaine = choisirDirectionAvecPheromones(terrain);
        if (prochaine != null) {
            deplacer(prochaine);

            // Si elle arrive sur une case avec nourriture
            if (terrain.contientNourriture(prochaine)) {
                prendreNourriture();
                terrain.retirerNourriture(prochaine);
            }
        }
    }
    
    public void agir(Terrain terrain) {
        if (porteNourriture) {
            laisserPheromone(terrain);
            // Retourner à la fourmilière
            retourFourmiliere(); // simple version pour l’instant
        } else {
            chercherNourriture(terrain);
        }
    }





    public void deplacer(Position nouvellePos) {
        this.position = nouvellePos;
    }

    public void prendreNourriture() {
        this.porteNourriture = true;
    }

    public Position getPosition() {
        return position;
    }

    public boolean porteNourriture() {
        return porteNourriture;
    }

    public void retourFourmiliere() {
        this.position = new Position(positionFourmiliere.getX(), positionFourmiliere.getY());
        this.porteNourriture = false;
    }

    // Ajout de la méthode getFourmiliere()
    public Position getFourmiliere() {
        return positionFourmiliere;
    }
}
