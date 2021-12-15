package item26_20211215;

import java.util.ArrayList;
import java.util.List;

public class GenericEx1 {
    public static void main(String[] args) {
        List<Stamp> stamps = new ArrayList<>();
        stamps.add(new Stamp());
        //stamps.add(new Coin()); 컴파일 오류 발생
    }
}

class Stamp {

}

class Coin {

}
