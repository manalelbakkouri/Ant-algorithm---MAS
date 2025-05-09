package Agent;

import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import Fourmi.*;
public class NourritureContainer extends Agent {
    @Override
    protected void setup() {
        System.out.println("Initialisation du NourritureContainer...");

        // Créer des nourritures
        ContainerController container = getContainerController();
        try {
            Position[] positionsNourriture = {
                new Position(5, 5),
                new Position(7, 8)
            };

            for (int i = 0; i < positionsNourriture.length; i++) {
                // On crée une Nourriture avec la position et une quantité par défaut (par exemple 100)
                Nourriture nourriture = new Nourriture(positionsNourriture[i], 100);

                // Passer l'instance de Nourriture à l'agent NourritureAgent
                Object[] nourritureArgs = { nourriture };

                // Créer et démarrer l'agent Nourriture
                AgentController nourritureAgent = container.createNewAgent(
                    "Nourriture" + i,
                    "Agent.NourritureAgent",
                    nourritureArgs
                );
                nourritureAgent.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
