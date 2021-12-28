package item37_20211228;

public class Plant {
    enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

    private final String name;
    private final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    public String name() {
        return name;
    }

    public LifeCycle lifeCycle() {
        return lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}
