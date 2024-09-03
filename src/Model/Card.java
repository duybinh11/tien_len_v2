package Model;

import java.io.Serializable;

import Enum.Rank;
import Enum.Suit;

public class Card implements Comparable<Card>, Serializable {
    private final Suit suit;
    private final Rank rank;

    public Card(Rank rank, Suit suit) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public int compareTo(Card other) {
        return this.rank.compareTo(other.rank);
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}