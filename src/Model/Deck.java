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

        System.out.println(deck);
        // Xáo trộn bộ bài
        Collections.shuffle(deck);
    }

    public Card dealCard() {
        return deck.remove(deck.size() - 1);
        // if (deck.isEmpty()) {
        // throw new IllegalStateException("Deck is empty");
        // }
        // if (c) {
        // c = false;
        // return deck.remove(index3());
        // } else {
        // return deck.remove(deck.size() - 1);
        // }
    }

    public List<Card> getCarts() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            cards.add(dealCard());
        }
        return cards;
    }

    public int index3() {
        return deck.indexOf(new Card(Rank.BA, Suit.BICH));
    }

    public List<Card> getDeck() {
        return deck;
    }
}