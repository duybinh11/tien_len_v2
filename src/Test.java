import java.util.Arrays;
import java.util.List;

import Enum.Rank;
import Enum.Suit;
import Model.Card;

public class Test {
    public static void main(String[] args) {
        List<Card> cards = Arrays.asList(
                new Card(Rank.BOI, Suit.BICH),
                new Card(Rank.GIA, Suit.CHUONG),
                new Card(Rank.HAI, Suit.BICH));

        Card cardMax = getCardMax(cards);
        System.out.println("Card mạnh nhất là: " + cardMax);
    }

    public static Card getCardMax(List<Card> cards) {
        return cards.stream().max((card1, card2) -> {
            int rankCompare = card1.getRank().ordinal() - card2.getRank().ordinal();
            if (rankCompare == 0) {
                return card1.getSuit().ordinal() - card2.getSuit().ordinal();
            } else {
                return rankCompare;
            }
        }).orElse(null);
    }

}
