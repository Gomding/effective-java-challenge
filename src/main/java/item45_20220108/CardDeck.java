package item45_20220108;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CardDeck {
    private static List<Card> newDeck() {
        List<Card> newDeck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                newDeck.add(new Card(suit, rank));
            }
        }
        return newDeck;
    }

    private static List<Card> newDeckStream() {
        return Arrays.stream(Suit.values())
                .flatMap(suit ->
                        Arrays.stream(Rank.values())
                                .map(rank -> new Card(suit, rank)))
                .collect(Collectors.toList());
    }
}

class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
}

enum Rank {
    ACE(1), TWO(2), THREE(3), FOUR(4),
    FIVE(5), SIX(6), SEVEN(7), EIGHT(8),
    NINE(9), TEN(10), JACK(11), QUEEN(12),
    KING(13);

    private int number;

    Rank(int number) {
        this.number = number;
    }
}

enum Suit {
    DIAMOND, HEART, CLOVER, SPADE
}
