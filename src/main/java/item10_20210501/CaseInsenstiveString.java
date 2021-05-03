package item10_20210501;

import java.util.Objects;

public class CaseInsenstiveString {
    private final String s;

    public CaseInsenstiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    //대칭성 위배

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsenstiveString) {
            return s.equalsIgnoreCase(((CaseInsenstiveString) o).s);
        }
        if (o instanceof String) { // 한방향 으로만 equals가 동작한다.
            return s.equalsIgnoreCase((String) o);
        }
        return false;
    }
}
