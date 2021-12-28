package item37_20211228;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

public class GardenExample4 {
    public static void main(String[] args) {
        List<Plant> garden = Arrays.asList(
                new Plant("한해살이 식물", Plant.LifeCycle.ANNUAL),
                new Plant("여러해살이 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("두해살이 식물", Plant.LifeCycle.BIENNIAL)
        );

        EnumMap<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = garden.stream()
                .collect(groupingBy(Plant::lifeCycle, () -> new EnumMap<>(Plant.LifeCycle.class), toSet()));
        System.out.println(plantsByLifeCycle);
    }
}
