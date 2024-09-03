package Model;

import java.util.*;

import Enum.Rank;
import Enum.Suit;

public class Deck {
    private List<Card> deck;
    private boolean c = true;

    public Deck() {
        deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(rank, suit));
            }
        }

        // Xáo trộn bộ bài
        Collections.shuffle(deck);
    }

    public Card dealCard() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        if (c) {
            c = false;
            return deck.remove(index3());
        } else {
            return deck.remove(deck.size() - 1);
        }
    }

    public int index3() {
        return deck.indexOf(new Card(Rank.THREE, Suit.SPADES)); // Ví dụ, tìm Card(3, Spades)
    }

    public List<Card> getDeck() {
        return deck;
    }
}