package item58_20220122;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CardEx {
    public static void main(String[] args) {
        List<Suit> suits = Arrays.asList(Suit.values());
        List<Rank> ranks = Arrays.asList(Rank.values());

        List<Card> deck = new ArrayList<>();
        for (Iterator<Suit> i = suits.iterator(); i.hasNext();)
            for (Iterator<Rank> j = ranks.iterator(); j.hasNext();)
                deck.add(new Card(i.next(), j.next()));
    }
}
