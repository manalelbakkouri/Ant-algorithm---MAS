package Agent;

import Fourmi.Terrain;
import Fourmi.Position;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.AgentContainer;

public class FourmiContainer {
    public static void main(String[] args) {
        Terrain terrain = new Terrain();
        terrain.ajouterNourriture(new Position(6, 6));
        terrain.ajouterNourriture(new Position(1, 6));
        terrain.ajouterNourriture(new Position(8, 3));
        terrain.ajouterNourriture(new Position(4, 9));

        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "1099");
        AgentContainer container = rt.createAgentContainer(p);

        try {
            // Lancer la fourmilière
            AgentController fourmiliere = container.createNewAgent(
                "fourmiliere",
                "Agent.FourmiliereAgent",
                null);
            fourmiliere.start();

            // Créer les fourmis
            for (int i = 0; i < 5; i++) {
                AgentController fourmi = container.createNewAgent(
                    "fourmi" + i,
                    "Agent.FourmiAgent",
                    new Object[]{terrain, new Position(i + 1, i + 1)});
                fourmi.start();
            }

            System.out.println("Fourmilière + fourmis lancées !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
