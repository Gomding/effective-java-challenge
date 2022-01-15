package item52_20220115;

import java.util.Arrays;
import java.util.List;

public class WineEx {
    public static void main(String[] args) {
        List<Wine> wines = Arrays.asList(
                new Wine(),
                new SparklingWine(),
                new Champagne()
        );

        for (Wine wine : wines) {
            System.out.println(wine.name());
        }
    }
}

class Wine {
    String name() {
        return "포도주";
    }
}

class SparklingWine extends Wine {
    @Override
    String name() {
        return "스파클링 포도주";
    }
}

class Champagne extends SparklingWine {
    @Override
    String name() {
        return "샴페인";
    }
}