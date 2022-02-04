package item70_20220204;

public class ExceptionEx3 {

    public static void main(String[] args) throws MyThrowable {

        throw new MyThrowable();

    }
}

class MyThrowable extends Throwable {

}
