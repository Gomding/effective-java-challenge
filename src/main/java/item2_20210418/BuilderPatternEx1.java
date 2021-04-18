package item2_20210418;

public class BuilderPatternEx1 {
    public static void main(String[] args) {
        BuilderNutritionFacts nutritionFacts = new BuilderNutritionFacts
                .Builder(240, 10)
                .calrories(80)
                .fat(10)
                .sodium(35)
                .carbohydrate(27)
                .build();
    }
}


class BuilderNutritionFacts {
    private final int servingSize;      // ml, 1회 제공량   - 필수 기본값이 존재하지 않으므로 -1로 초기화
    private final int servings;         // 회, 총 n회 제공량 - 필수 기본값이 존재하지 않으므로 -1로 초기화
    private final int calories;         // 1회 제공량당 열량  - 선택
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수
        private int calories;
        private int fat;
        private int sodium;
        private int carbohydrate;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calrories(int value) {
            this.calories = value;
            return this;
        }

        public Builder fat(int value) {
            this.fat = value;
            return this;
        }

        public Builder sodium(int value) {
            this.sodium = sodium;
            return this;
        }

        public Builder carbohydrate(int value) {
            this.carbohydrate = value;
            return this;
        }

        public BuilderNutritionFacts build() {
            return new BuilderNutritionFacts(this);
        }
    }

    private BuilderNutritionFacts(Builder builder) {
        this.servingSize = builder.servingSize;
        this.servings = builder.servings;
        this.calories = builder.calories;
        this.fat = builder.fat;
        this.sodium = builder.sodium;
        this.carbohydrate = builder.carbohydrate;
    }
}

