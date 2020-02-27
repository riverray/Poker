package com.riversoft.game.logic

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Rank
import com.riversoft.game.enums.Status
import com.riversoft.game.enums.Suit
import com.riversoft.game.model.Arm
import com.riversoft.game.model.Card

import java.awt.RadialGradientPaint

class Poker {
    Random rand = new Random()
    List<Card> allCards = []
    List<Card> deck = []

    List<Arm> arms = []
    List<Card> commonCards = []

    Poker() {
        def suits = Suit.values()
        def ranks = Rank.values()

        for (def suit : suits) {
            for (def rank : ranks) {
                allCards.add(new Card(suit: suit, rank: rank))
            }
        }
    }

    void shuffleDeck() {
        deck.clear()
        deck = allCards.collect()
    }

    void startGame(List<Long> banks) {
        for (int i = 0; i < banks.size(); i++) {
            arms.add(new Arm(bank: banks[i], active: true, status: Status.READY))
        }
    }

    void firstGame() {
        shuffleDeck()

        // раздаем карты на стол
        getCommonCards()

        // раздаем карты игрокам, которые в статусе "готов"
        for (def arm : arms) {
            if (arm.status != Status.READY) {
                continue
            }

            getArmCards(arm)
            fillAllCards(arm)

            checkCombination(arm)
        }


        111
    }

    private void fillAllCards(Arm arm) {
        arm.allCards.addAll(arm.cards)
        arm.allCards.addAll(commonCards)
        arm.allCards.sort { t -> t.rank }
    }

    void getArmCards(Arm arm) {
        for (int i = 0; i < 2; i++) {
            int index = rand.nextInt(deck.size())
            arm.cards.add(deck[index])
            deck.remove(index)
        }
    }

    void getCommonCards() {
        for (int i = 0; i < 5; i++) {
            int index = rand.nextInt(deck.size())
            commonCards.add(deck[index])
            deck.remove(index)
        }
    }

    void checkCombination(Arm arm) {
        if (isRoyalFlush(arm)) {
            return
        }
        if (isStraightFlush(arm)) {
            return
        }
        if (isFour(arm)) {
            return
        }
        if (isFullHouse(arm)) {
            return
        }
        if (isFlush(arm)) {
            return
        }
        if (isStraight(arm)) {
            return
        }
        if (isThree(arm)) {
            return
        }
        if (isTwoPair(arm)) {
            return
        }
        if (isOnePair(arm)) {
            return
        }


    }

    private void fillNumberLists(Arm arm) {
        for (int i = 0; i < arm.cards.size(); i++) {
            if (arm.comboCards.any { t -> t == arm.cards[i] }) {
                arm.playerCardNumbers.add(i)
            }
        }
        for (int i = 0; i < commonCards.size(); i++) {
            if (arm.comboCards.any { t -> t == commonCards[i] }) {
                arm.commonCardNumbers.add(i)
            }
        }
    }

    boolean isRoyalFlush(Arm arm) {
        return false
    }

    boolean isStraightFlush(Arm arm) {
    }

    boolean isFour(Arm arm) {
        if (arm.allCards.count { t -> t.rank == arm.allCards[3].rank } == 4) {
            arm.combination = Combination.Four
            arm.firstCard = arm.allCards[3]

            // определяем пятую карту комбинации
            // если первая карта принадлежие четверке - тогда это последняя карта (плюс первые четыре)
            if (arm.allCards[0].rank == arm.firstCard.rank) {
                arm.comboCards = arm.allCards.take(4)
                arm.comboCards.add(arm.allCards[6])
                arm.secondCard = arm.allCards[6]
            }
            // если нет - тогда это 3-я карта (плюс следующие)
            else {
                arm.comboCards = arm.allCards.drop(2)
                arm.secondCard = arm.allCards[2]
            }

            fillNumberLists(arm)

            return true
        }
        return false
    }

    boolean isFullHouse(Arm arm) {
        return false
    }

    boolean isFlush(Arm arm) {
        int count
        Suit flushSuit

        count = allCards.count { t -> t.suit == Suit.SPADE }
        if (count >= 5) {
            def comboCards = allCards.findAll { t -> t.suit == Suit.SPADE }.drop(count - 5)

        }
        return false
    }

    boolean isStraight(Arm arm) {
        return false
    }

    boolean isThree(Arm arm) {
        return false
    }

    boolean isTwoPair(Arm arm) {
        return false
    }

    boolean isOnePair(Arm arm) {
        return false
    }


}
