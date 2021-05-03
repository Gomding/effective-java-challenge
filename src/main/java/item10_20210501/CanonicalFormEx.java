package item10_20210501;

import java.util.Objects;

public class CanonicalFormEx {
}

class CaseInsenstiveStringEx1 {
    private final String s;

    public CaseInsenstiveStringEx1(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsenstiveStringEx1) {
            return s.equalsIgnoreCase(((CaseInsenstiveStringEx1) o).s);
        }
        return false;
    }
}

class CaseInsenstiveStringEx2 {
    private final String value;
    private final String canonicalFormValue;

    public CaseInsenstiveStringEx2(String value) {
        this.value = Objects.requireNonNull(value);
        this.canonicalFormValue = value.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsenstiveStringEx2) {
            return canonicalFormValue.equals(((CaseInsenstiveStringEx2) o).canonicalFormValue);
        }
        return false;
    }
}


