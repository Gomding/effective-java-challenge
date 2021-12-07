package item17_20211207;

import java.util.Objects;

public class Money {

    private final int value;

    public Money(int value) {
        this.value = value;
    }

    public Money plus(Money m) {
        return new Money(this.value + m.value);
    }

    public Money minus(Money m) {
        return new Money(this.value - m.value);
    }

    public Money times(Money m) {
        return new Money(this.value * m.value);
    }

    public Money dividedBy(Money m) {
        return new Money(this.value / m.value);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return value == money.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Money{" +
                "value=" + value +
                '}';
    }
}
