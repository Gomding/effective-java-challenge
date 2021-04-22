package item5_20210422;

import java.util.ArrayList;
import java.util.List;

public class DependencyObjectInjectionEx1 {

}

// 맞춤법 검사기 클래스는 사전(dictionary)에 의존
// 아래는 각각
// 유틸리티로 구현한 맞춤법 검사기
class SpellCheckTypeUtilClass {
    private Lexicon dictionary = new KoreanDictionary();

    private SpellCheckTypeUtilClass() {
    }

    // ...

    public void changeDictionary(Lexicon dictionary) {
        this.dictionary = dictionary;
    }
}

// 싱글턴으로 구현한 맞춤법 검사기
class SpellCheckTypeSingleton {
    private Lexicon dictionary = new EnglishDictionary();

    private SpellCheckTypeSingleton() {
    }

    private static final SpellCheckTypeSingleton INSTANCE = new SpellCheckTypeSingleton();

    public SpellCheckTypeSingleton getInstance() {
        return INSTANCE;
    }

    // ...

    public void changeDictionary(Lexicon dictionary) {
        this.dictionary = dictionary;
    }
}

interface Lexicon {
    String hello();
}

class EnglishDictionary implements Lexicon {
    private final String HELLO = "hello";

    public String hello() {
        return HELLO;
    }
}

class KoreanDictionary implements Lexicon {
    private final String HELLO = "안녕하세요";

    public String hello() {
        return HELLO;
    }
}
