package item2_20210418;

public class TelescopingConstructorPatternEx1 {

}

class NutritionFacts {
    private final int servingSize;      // ml, 1회 제공량   - 필수
    private final int servings;         // 회, 총 n회 제공량 - 필수
    private final int calories;         // 1회 제공량당 열량  - 선택
    private final int fat;
    private final int sodiun;
    private final int carbohydrate;

    public NutritionFacts(int servingSize, int servings) {
        this(servingSize, servings, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories) {
        this(servingSize, servings, calories, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat) {
        this(servingSize, servings, calories, fat, 0);
    }

    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodiun) {
        this(servingSize, servings, calories, fat, sodiun, 0);
    }


    public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodiun, int carbohydrate) {
        this.servingSize = servingSize;
        this.servings = servings;
        this.calories = calories;
        this.fat = fat;
        this.sodiun = sodiun;
        this.carbohydrate = carbohydrate;
    }
}
