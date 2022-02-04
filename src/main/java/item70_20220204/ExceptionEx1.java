package item70_20220204;

public class ExceptionEx1 {
    public static void main(String[] args) throws Exception {
        Car car = new Car(0);
        try {
            car.move();
        } catch (Exception e) {
            car.refuel(10);
            car.move();
        }
    }
}

class Car {

    private int oil;

    public Car(int oil) {
        this.oil = oil;
    }

    public void move() throws Exception {
        if (this.oil <= 0) {
            throw new Exception("연료가 부족합니다!");
        }
    }

    public void refuel(int oil) {
        this.oil += oil;
    }
}
