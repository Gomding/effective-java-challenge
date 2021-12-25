package item34_20211225;

public class WeightTable {
    public static void main(String[] args) {
        double mass = 185.0d / Planet.EARTH.getSurfaceGravity();
        for (Planet p : Planet.values()) {
            System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));
        }
    }
}
