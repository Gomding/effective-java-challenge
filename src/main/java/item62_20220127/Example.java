package item62_20220127;

public class Example {

    private final CompoundKey compoundKey;

    public Example(CompoundKey compoundKey) {
        this.compoundKey = compoundKey;
    }

    private static class CompoundKey {
        private final String className;
        private final Object key;

        public CompoundKey(String className, Object key) {
            this.className = className;
            this.key = key;
        }
    }
}
