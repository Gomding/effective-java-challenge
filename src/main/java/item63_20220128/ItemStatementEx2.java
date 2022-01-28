package item63_20220128;

import java.util.ArrayList;
import java.util.List;

public class ItemStatementEx2 {

    private final List<String> items = new ArrayList<>();

    public String statement() {
        StringBuilder result = new StringBuilder();

        for (String item : items) {
            result.append(item);
        }
        return result.toString();
    }
}
