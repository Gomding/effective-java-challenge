package item63_20220128;

import java.util.ArrayList;
import java.util.List;

public class ItemStatementEx {

    private final List<String> items = new ArrayList<>();

    public String statement() {
        String result = "";

        for (String item : items) {
            result += item;
        }
        return result;
    }
}
