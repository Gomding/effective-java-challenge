package item37_20211228;

import java.util.*;

public class GardenExample2 {
    public static void main(String[] args) {
        List<Plant> garden = Arrays.asList(
                new Plant("한해살이 식물", Plant.LifeCycle.ANNUAL),
                new Plant("여러해살이 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("두해살이 식물", Plant.LifeCycle.BIENNIAL)
        );

        Map<Plant.LifeCycle, Set<Plant>> plantByLifeCycle =
                new EnumMap<>(Plant.LifeCycle.class);
        for (Plant.LifeCycle lifeCycle : Plant.LifeCycle.values()) {
            plantByLifeCycle.put(lifeCycle, new HashSet<>());
        }
        for (Plant plant : garden) {
            plantByLifeCycle.get(plant.lifeCycle()).add(plant);
        }
        System.out.println(plantByLifeCycle);
    }
}
