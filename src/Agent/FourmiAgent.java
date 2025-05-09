package Agent;

import Fourmi.Fourmi;
import Fourmi.Position;
import Fourmi.Terrain;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.List;

public class FourmiAgent extends Agent {
    private Fourmi fourmi;
    private Terrain terrain;
    private Position objectif = null;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        terrain = (Terrain) args[0];
        Position posInitiale = (Position) args[1];

        this.fourmi = new Fourmi(posInitiale, terrain.getFourmiliere());

        System.out.println(getLocalName() + " démarrée à " + posInitiale);

        // 🔽 Enregistrement dans le DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("fourmi");
        sd.setName("FourmiCommunication");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🔁 Comportement de réception de messages
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    String contenu = msg.getContent();
                    System.out.println(getLocalName() + " a reçu un message : " + contenu);
                    if (contenu.startsWith("Nourriture trouvée en")) {
                        String[] parts = contenu.split("en")[1].trim().replace("(", "").replace(")", "").split(",");
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        objectif = new Position(x, y);
                    }
                }
                block();
            }
        });

        // 🔁 Comportement de mouvement et action
        addBehaviour(new TickerBehaviour(this, 1000) {
            protected void onTick() {
                if (!fourmi.porteNourriture()) {
                    if (objectif == null || !terrain.contientNourriture(objectif)) {
                        objectif = chercherNourritureLaPlusProche();
                    }
                    seDeplacerVers(objectif);
                } else {
                    seDeplacerVers(fourmi.getFourmiliere());
                }

                Position actuelle = fourmi.getPosition();
                System.out.println(getLocalName() + " est à " + actuelle);

                // Prendre nourriture
                if (terrain.contientNourriture(actuelle) && !fourmi.porteNourriture()) {
                    fourmi.prendreNourriture();
                    System.out.println(getLocalName() + " a trouvé de la nourriture !");
                    envoyerMessage("Nourriture trouvée en " + actuelle);
                    terrain.retirerNourriture(actuelle);
                    objectif = null;
                }

                // Déposer nourriture
                if (fourmi.porteNourriture() && terrain.estFourmiliere(actuelle)) {
                    fourmi.retourFourmiliere();
                    System.out.println(getLocalName() + " a rapporté de la nourriture !");
                    envoyerMessage("Nourriture rapportée à la fourmilière");
                }
            }
        });
    }

    private void seDeplacerVers(Position destination) {
        if (destination == null) return;
        Position actuelle = fourmi.getPosition();

        int dx = Integer.compare(destination.getX(), actuelle.getX());
        int dy = Integer.compare(destination.getY(), actuelle.getY());

        Position nouvellePos = new Position(actuelle.getX() + dx, actuelle.getY() + dy);
        if (terrain.estValide(nouvellePos)) {
            fourmi.deplacer(nouvellePos);
        }
    }

    private Position chercherNourritureLaPlusProche() {
        Position posActuelle = fourmi.getPosition();
        List<Position> sources = terrain.getNourritures();
        Position plusProche = null;
        int minDistance = Integer.MAX_VALUE;

        for (Position n : sources) {
            int dist = Math.abs(posActuelle.getX() - n.getX()) + Math.abs(posActuelle.getY() - n.getY());
            if (dist < minDistance) {
                minDistance = dist;
                plusProche = n;
            }
        }
        return plusProche;
    }

    private void envoyerMessage(String content) {
        try {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("fourmi");
            template.addServices(sd);

            DFAgentDescription[] result = DFService.search(this, template);
            for (DFAgentDescription dfd : result) {
                AID dest = dfd.getName();
                // Ne pas s'envoyer un message à soi-même
                if (!dest.equals(getAID())) {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(dest);
                    msg.setContent(content);
                    send(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
