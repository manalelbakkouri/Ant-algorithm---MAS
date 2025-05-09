package Agent;

import jade.core.Agent;
import Fourmi.*;
public class NourritureAgent extends Agent {
    private Nourriture nourriture;

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " (Nourriture) prêt.");

        Object[] args = getArguments();
        if (args != null && args.length == 1) {
            nourriture = (Nourriture) args[0];
        } else {
            System.out.println("Erreur d'initialisation de " + getLocalName());
            doDelete();
        }
    }

    public Nourriture getNourriture() {
        return nourriture;
    }
}

