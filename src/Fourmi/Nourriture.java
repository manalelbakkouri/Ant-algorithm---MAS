package Fourmi;

public class Nourriture {
    private Position position;
    private int quantity;

    public Nourriture(Position position, int quantity) {
        this.position = position;
        this.quantity = quantity;
    }

    public Position getPosition() {
        return position;
    }

    public int getQuantity() {
        return quantity;
    }

    public void decreaseQuantity(int amount) {
        quantity = Math.max(0, quantity - amount);
    }
}
