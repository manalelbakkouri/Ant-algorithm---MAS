package Fourmi;

import java.util.*;

public class PathFinder {

    // Algorithme de Dijkstra pour trouver le chemin le plus court
    public static List<Position> trouverChemin(Position start, Position goal, Terrain terrain) {
        // Map pour stocker les positions des nœuds (clé : position, valeur : coût)
        Map<Position, Position> cameFrom = new HashMap<>();
        Map<Position, Integer> gScore = new HashMap<>();
        PriorityQueue<Position> openSet = new PriorityQueue<>(Comparator.comparingInt(gScore::get));
        
        // Initialisation
        gScore.put(start, 0);
        openSet.add(start);
        
        while (!openSet.isEmpty()) {
            Position current = openSet.poll();
            
            // Si on atteint la destination
            if (current.equals(goal)) {
                return reconstruireChemin(cameFrom, current);
            }
            
            // Explore les voisins
            for (Position neighbor : getVoisins(current, terrain)) {
                int tentativeGScore = gScore.get(current) + 1; // distance d'un pas
                
                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    openSet.add(neighbor);
                }
            }
        }
        
        return new ArrayList<>(); // Aucun chemin trouvé
    }
    
    // Fonction pour reconstruire le chemin
    private static List<Position> reconstruireChemin(Map<Position, Position> cameFrom, Position current) {
        List<Position> chemin = new ArrayList<>();
        chemin.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            chemin.add(current);
        }
        Collections.reverse(chemin);
        return chemin;
    }
    
    // Fonction pour obtenir les voisins d'une position (adjacents)
    private static List<Position> getVoisins(Position current, Terrain terrain) {
        List<Position> voisins = new ArrayList<>();
        int[] directions = {-1, 0, 1}; // Déplacements : haut, bas, gauche, droite
        for (int dx : directions) {
            for (int dy : directions) {
                if (Math.abs(dx) + Math.abs(dy) == 1) { // Ne considérer que les voisins adjacents
                    Position voisin = new Position(current.getX() + dx, current.getY() + dy);
                    if (terrain.estValide(voisin)) {
                        voisins.add(voisin);
                    }
                }
            }
        }
        return voisins;
    }

    // Longueur du chemin
    public static int longueurChemin(List<Position> chemin) {
        return chemin.size() - 1;
    }
}
