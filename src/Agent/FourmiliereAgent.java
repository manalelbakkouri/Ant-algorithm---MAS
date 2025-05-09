package Agent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class FourmiliereAgent extends Agent {
    protected void setup() {
        System.out.println("Fourmilière prête à recevoir des messages !");

        // Enregistrement dans le DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("fourmiliere-service");
        sd.setName("FourmiliereReception");

        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            System.out.println("Fourmilière enregistrée dans le DF.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    System.out.println("REÇU: " + msg.getSender().getName() + " > " + msg.getContent());
                }
                block();
            }
        });
    }
}
