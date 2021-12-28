package item37_20211228;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public enum Phase2 {
    SOLID, LIQUID, GAS;

    public enum Transition2 {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase2 from;
        private final Phase2 to;

        Transition2(Phase2 from, Phase2 to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화한다.
        private static final Map<Phase2, Map<Phase2, Phase2.Transition2>> map =
                Arrays.stream(values())
                        .collect(groupingBy(
                                t -> t.from,
                                () -> new EnumMap<>(Phase2.class),
                                toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase2.class))
                                )
                        );

        public static Phase2.Transition2 from(Phase2 from, Phase2 to) {
            return map.get(from).get(to);
        }
    }
}
