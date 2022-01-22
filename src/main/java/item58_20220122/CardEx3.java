package item58_20220122;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardEx3 {
    public static void main(String[] args) {
        List<Suit> suits = Arrays.asList(Suit.values());
        List<Rank> ranks = Arrays.asList(Rank.values());

        List<Card> deck = new ArrayList<>();
        for (Suit suit : suits)
            for (Rank rank : ranks)
                deck.add(new Card(suit, rank));
    }
}
