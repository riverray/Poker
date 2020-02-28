package com.riversoft.game.logic

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Rank
import com.riversoft.game.enums.Status
import com.riversoft.game.enums.Suit
import com.riversoft.game.model.Arm
import com.riversoft.game.model.Card

class Poker {
    Random rand = new Random(1)
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
        arms.clear()
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
        commonCards.clear()

        for (int i = 0; i < 5; i++) {
            int index = rand.nextInt(deck.size())
            commonCards.add(deck[index])
            deck.remove(index)
        }
    }

    void checkCombination(Arm arm) {
        arm.playerCardNumbers = []
        arm.commonCardNumbers = []

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
        arm.commonCardNumbers = []
        arm.playerCardNumbers = []

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
        for (def suit : Suit.values()) {
            def allFlushCards = arm.allCards.findAll { t -> t.suit == suit }.sort { t -> t.rank }
            int count = allFlushCards.size()

            if (allFlushCards.size() >= 5 && allFlushCards[count - 5].rank == Rank.TEN) {
                arm.comboCards = allFlushCards.drop(count - 5)
                arm.combination = Combination.ROYAL_FLUSH
                arm.firstCard = arm.comboCards.last()

                fillNumberLists(arm)

                return true
            }
        }
        return false
    }

    boolean isStraightFlush(Arm arm) {
        for (def suit : Suit.values()) {
            def allFlushCards = arm.allCards.findAll { t -> t.suit == suit }.sort { t -> t.rank }
            int count = allFlushCards.size()

            // проверка на классический стрит
            if (count == 5 && checkClassicStraight(allFlushCards, arm) || count == 6 && checkClassicStraight(allFlushCards.drop(1), arm) || count == 6 && checkClassicStraight(allFlushCards.take(5), arm) ||
                    count == 7 && checkClassicStraight(allFlushCards.drop(2), arm) || count == 7 && checkClassicStraight(allFlushCards.drop(1).take(5), arm) || count == 7 && checkClassicStraight(allFlushCards.take(5), arm)) {
                arm.combination = Combination.STRAIGHT_FLUSH

                return true
            }
            // проверка на нижний стрит
            if (count >= 5 && checkLowerStraight(allFlushCards, arm)) {
                arm.combination = Combination.STRAIGHT_FLUSH

                return true
            }
        }
        return false
    }

    boolean isFour(Arm arm) {
        if (arm.allCards.count { t -> t.rank == arm.allCards[3].rank } == 4) {
            arm.combination = Combination.FOUR
            arm.firstCard = arm.allCards[3]

            // определяем пятую карту комбинации
            // если последняя карта принадлежие четверке - тогда третья и все следующие (четыре)
            if (arm.allCards[6].rank == arm.firstCard.rank) {
                arm.comboCards = arm.allCards.drop(2)
                arm.secondCard = arm.allCards[2]
            }
            // если нет - тогда это последняя карта (плюс вся четверка)
            else {
                arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.firstCard.rank }.toList())
                arm.comboCards.add(arm.allCards[6])
                arm.secondCard = arm.allCards[6]
            }

            fillNumberLists(arm)

            return true
        }
        return false
    }

    boolean isFullHouse(Arm arm) {
        // проверяем на тройку
        Card firstCard = null
        Card secondCard = null

        if (arm.allCards.count { t -> t.rank == arm.allCards[4].rank } == 3) {
            firstCard = arm.allCards[4]
        }
        else if (arm.allCards.count { t -> t.rank == arm.allCards[1].rank } == 3) {
            firstCard = arm.allCards[1]
        }

        // если нет тройки - выходим
        if (firstCard == null) {
            return false
        }

        // ищем еще пару или тройку
        // опредяем позиции, где может находится вторая тройка или двойка
        List<Integer> threePositions = []
        List<Integer> potentialPositions = [0, 1, 2, 3, 4, 5, 6]
        for (int i = 0; i < arm.allCards.size(); i++) {
            if (arm.allCards[i].rank == firstCard.rank) {
                threePositions.add(i)
                potentialPositions.removeAll { t -> t == i }
            }
        }

        // идем с конца и проверяем
        for (int i = potentialPositions.size() - 1; i > 0; i--) {
            if (arm.allCards.count { t -> t.rank == arm.allCards[i].rank } >= 2) {
                secondCard = arm.allCards[i]
                break
            }
        }

        // если второй пары/тройки нет - выход
        if (secondCard == null) {
            return false
        }

        arm.combination = Combination.FULL_HOUSE

        arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.firstCard.rank }.toList())
        arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.secondCard.rank }.take(2).toList())

        arm.firstCard = firstCard
        arm.secondCard = secondCard

        return true
    }

    boolean isFlush(Arm arm) {
        for (def suit : Suit.values()) {
            def allFlushCards = arm.allCards.findAll { t -> t.suit == suit }.sort { t -> t.rank }
            int count = allFlushCards.size()

            if (count >= 5) {
                arm.combination = Combination.FLUSH
                arm.comboCards = allFlushCards.drop(count - 5)
                arm.firstCard = arm.comboCards[4]
                arm.secondCard = arm.comboCards[3]

                fillNumberLists(arm)

                return true
            }
        }

        return false
    }

    boolean isStraight(Arm arm) {
        // убрать повторные символы и проверить
//        for (def suit : Suit.values()) {
//            def allFlushCards = allCards.findAll { t -> t.suit == suit }.sort()
//            int count = allFlushCards.size()
//
//            if (allFlushCards.size() >= 5) {
//                arm.combination = Combination.Flush
//
//                // проверка на стрит туз-2-3-4-5
//                if (allFlushCards[3].rank == Rank.FIVE && allFlushCards.last().rank == Rank.ACE) {
//                    if (count == 7) {
//                        allFlushCards.remove(4)
//                        allFlushCards.remove(4)
//                    }
//                    else if (count == 6) {
//                        allFlushCards.remove(4)
//                    }
//
//                    arm.firstCard = allFlushCards[3]
//                    arm.comboCards = allFlushCards
//                }
//                else {
//                    arm.comboCards = allFlushCards.drop(count - 5)
//                    arm.firstCard = allFlushCards.last()
//                }
//            }
//        }
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

    // проверка на классический стрит, надо обязательно 5 карт
    private boolean checkClassicStraight(List<Card> allCards, Arm arm) {
        if (allCards.size() != 5) {
            throw new Exception("Poker. Incorrect size for straight")
        }

        if (allCards[4].rank.getNumber() - allCards[3].rank.getNumber() == 1 &&
                allCards[3].rank.getNumber() - allCards[2].rank.getNumber() == 1 &&
                allCards[2].rank.getNumber() - allCards[1].rank.getNumber() == 1 &&
                allCards[1].rank.getNumber() - allCards[0].rank.getNumber() == 1) {
            arm.comboCards = allCards
            arm.firstCard = arm.comboCards.last()

            fillNumberLists(arm)

            return true
        }

        return false
    }

    // проверка на нижний стрит, надо любое число карт
    private boolean checkLowerStraight(List<Card> allCards, Arm arm) {
        if (allCards[0].rank == Rank.TWO && allCards[1].rank == Rank.THREE && allCards[2].rank == Rank.FOUR && allCards[3].rank == Rank.FIVE && allCards.last().rank == Rank.ACE) {
            arm.comboCards = allCards.take(4)
            arm.comboCards.add(allCards.last())
            arm.firstCard = arm.comboCards[3]

            fillNumberLists(arm)

            return true
        }

        return false
    }
}
