package Fourmi;

public class Main {
    public static void main(String[] args) {
        SimulationManager manager = new SimulationManager();
        Fenetre fenetre = new Fenetre(manager);

        // Simulation autonome dans un thread
        new Thread(() -> {
            int tours = 500;
            for (int t = 0; t < tours; t++) {
                manager.faireUnTour();
                fenetre.rafraichir();

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
