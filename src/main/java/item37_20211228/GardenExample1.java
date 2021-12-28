package item37_20211228;

import java.util.*;

public class GardenExample1 {
    public static void main(String[] args) {
        List<Plant> garden = Arrays.asList(
                new Plant("한해살이 식물", Plant.LifeCycle.ANNUAL),
                new Plant("여러해살이 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("두해살이 식물", Plant.LifeCycle.BIENNIAL)
        );

        @SuppressWarnings("unchecked") Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }
        for (Plant plant : garden) {
            plantsByLifeCycle[plant.lifeCycle().ordinal()].add(plant);
        }

        // 결과 출력
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
        }
    }
}
