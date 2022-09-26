package assignment5;

// this critter likes to fight
public class Jay extends Critter {

    @Override
    public CritterShape viewShape() {
        return CritterShape.TRIANGLE;
    }

    @Override
    public void doTimeStep() {
        for (int i = 0; i<8 && getEnergy()>4; i++) {
            if (look(i, false) != null) {
                walk(i);
                break;
            }
        }
    }

    @Override
    public boolean fight(String oponent) {
        // TODO Auto-generated method stub
        return getEnergy()>5;
    }

    @Override
    public String toString() {
        return "J";
    }
}
