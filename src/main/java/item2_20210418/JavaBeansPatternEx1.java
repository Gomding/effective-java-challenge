package item2_20210418;

public class JavaBeansPatternEx1 {

    public static void main(String[] args) {
        JavaBeansNutritionFacts nutritionFacts = new JavaBeansNutritionFacts();

        nutritionFacts.setServingSize(240);
        nutritionFacts.setServings(10);
        nutritionFacts.setCalories(390);
        nutritionFacts.setFat(10);
        nutritionFacts.setSodiun(35);
        nutritionFacts.setCarbohydrate(27);
    }
}

class JavaBeansNutritionFacts {
    private int servingSize = -1;      // ml, 1회 제공량   - 필수 기본값이 존재하지 않으므로 -1로 초기화
    private int servings = -1;         // 회, 총 n회 제공량 - 필수 기본값이 존재하지 않으므로 -1로 초기화
    private int calories = 0;         // 1회 제공량당 열량  - 선택
    private int fat = 0;
    private int sodium = 0;
    private int carbohydrate = 0;

    public JavaBeansNutritionFacts() {
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setSodiun(int sodium) {
        this.sodium = sodium;
    }

    public void setCarbohydrate(int carbohydrate) {
        this.carbohydrate = carbohydrate;
    }
}

