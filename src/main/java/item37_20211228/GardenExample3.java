package item37_20211228;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class GardenExample3 {
    public static void main(String[] args) {
        List<Plant> garden = Arrays.asList(
                new Plant("한해살이 식물", Plant.LifeCycle.ANNUAL),
                new Plant("여러해살이 식물", Plant.LifeCycle.PERENNIAL),
                new Plant("두해살이 식물", Plant.LifeCycle.BIENNIAL)
        );

        Map<Plant.LifeCycle, List<Plant>> plantsByLifeCycle = garden.stream()
                                                                    .collect(groupingBy(Plant::lifeCycle));
        System.out.println(plantsByLifeCycle);
    }
}
