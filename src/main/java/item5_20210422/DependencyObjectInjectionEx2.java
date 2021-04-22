package item5_20210422;

public class DependencyObjectInjectionEx2 {
}

class SpellChecker {
    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = dictionary;
    }

    public boolean isValid(String word) {
        return true;
    }
}

class SpellChecker2 {
    private final Lexicon dictionary;
    private final SpellRespository spellRespository;

    public SpellChecker2(Lexicon dictionary, SpellRespository spellRespository) {
        this.dictionary = dictionary;
        this.spellRespository = spellRespository;
    }

    public boolean isValid(String word) {
        return true;
    }
}

class SpellRespository {
}