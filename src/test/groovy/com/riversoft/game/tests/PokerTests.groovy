package com.riversoft.game.tests

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Rank
import com.riversoft.game.enums.Suit
import com.riversoft.game.logic.Poker
import com.riversoft.game.model.Card
import spock.lang.Specification

class PokerTests extends Specification {

    def "генерация всех кард"() {
        when:
        Poker game = new Poker()

        then:
        game.allCards.size() == 52

        game.allCards.count { t -> t.suit == Suit.SPADE } == 13
        game.allCards.count { t -> t.suit == Suit.CLUB } == 13
        game.allCards.count { t -> t.suit == Suit.DIAMOND } == 13
        game.allCards.count { t -> t.suit == Suit.HEART } == 13

        game.allCards.count { t -> t.rank == Rank.TWO } == 4
        game.allCards.count { t -> t.rank == Rank.THREE } == 4
        game.allCards.count { t -> t.rank == Rank.FOUR } == 4
        game.allCards.count { t -> t.rank == Rank.FIVE } == 4
        game.allCards.count { t -> t.rank == Rank.SIX } == 4
        game.allCards.count { t -> t.rank == Rank.SEVEN } == 4
        game.allCards.count { t -> t.rank == Rank.EIGHT } == 4
        game.allCards.count { t -> t.rank == Rank.NINE } == 4
        game.allCards.count { t -> t.rank == Rank.TEN } == 4
        game.allCards.count { t -> t.rank == Rank.JACK } == 4
        game.allCards.count { t -> t.rank == Rank.QUEEN } == 4
        game.allCards.count { t -> t.rank == Rank.KING } == 4
        game.allCards.count { t -> t.rank == Rank.ACE } == 4
    }

    def "генерация изначальной колоды"() {
        given:
        Poker game = new Poker()

        when:
        game.shuffleDeck()

        then:
        game.deck.size() == 52

        game.deck.count { t -> t.suit == Suit.SPADE } == 13
        game.deck.count { t -> t.suit == Suit.CLUB } == 13
        game.deck.count { t -> t.suit == Suit.DIAMOND } == 13
        game.deck.count { t -> t.suit == Suit.HEART } == 13

        game.deck.count { t -> t.rank == Rank.TWO } == 4
        game.deck.count { t -> t.rank == Rank.THREE } == 4
        game.deck.count { t -> t.rank == Rank.FOUR } == 4
        game.deck.count { t -> t.rank == Rank.FIVE } == 4
        game.deck.count { t -> t.rank == Rank.SIX } == 4
        game.deck.count { t -> t.rank == Rank.SEVEN } == 4
        game.deck.count { t -> t.rank == Rank.EIGHT } == 4
        game.deck.count { t -> t.rank == Rank.NINE } == 4
        game.deck.count { t -> t.rank == Rank.TEN } == 4
        game.deck.count { t -> t.rank == Rank.JACK } == 4
        game.deck.count { t -> t.rank == Rank.QUEEN } == 4
        game.deck.count { t -> t.rank == Rank.KING } == 4
        game.deck.count { t -> t.rank == Rank.ACE } == 4
    }

    def "создаем стол"() {
        given:
        List<Long> banks = [100, 200, 300]
        Poker game = new Poker()

        when:
        game.startGame(banks)

        then:
        game.arms.size() == banks.size()
    }

    def "делаем первую раздачу"() {
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()

        when:
        game.startGame(banks)
        game.firstGame()

        then:
        game.arms.size() == banks.size()
        game.commonCards.size() == 5
    }

    //# region Combination

    def "RoyalFlush"() {
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.QUEEN), new Card(Suit.CLUB, Rank.ACE), new Card(Suit.CLUB, Rank.TEN), new Card(Suit.CLUB, Rank.JACK), new Card(Suit.CLUB, Rank.KING)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.NINE)]

        when:
        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ROYAL_FLUSH
        game.arms[0].firstCard.rank == Rank.ACE
        game.arms[0].comboCards.count { t -> t.suit == Suit.CLUB } == 5
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        when:
        commonCards = [new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.HEART, Rank.ACE), new Card(Suit.HEART, Rank.TEN), new Card(Suit.HEART, Rank.JACK), new Card(Suit.HEART, Rank.KING)]
        playerCards = [new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.HEART, Rank.FIVE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ROYAL_FLUSH
        game.arms[0].firstCard.rank == Rank.ACE
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == [0]
        game.arms[0].commonCardNumbers == [1, 2, 3, 4]

        when:
        commonCards = [new Card(Suit.SPADE, Rank.ACE), new Card(Suit.HEART, Rank.ACE), new Card(Suit.SPADE, Rank.JACK), new Card(Suit.SPADE, Rank.KING), new Card(Suit.HEART, Rank.KING)]
        playerCards = [new Card(Suit.SPADE, Rank.QUEEN), new Card(Suit.SPADE, Rank.TEN)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ROYAL_FLUSH
        game.arms[0].firstCard.rank == Rank.ACE
        game.arms[0].comboCards.count { t -> t.suit == Suit.SPADE } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 2, 3]
    }

    def "StraightFlush"() {
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.SEVEN), new Card(Suit.CLUB, Rank.NINE), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.CLUB, Rank.JACK), new Card(Suit.CLUB, Rank.TEN)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.FIVE)]

        when:
        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT_FLUSH
        game.arms[0].firstCard.rank == Rank.JACK
        game.arms[0].comboCards.count { t -> t.suit == Suit.CLUB } == 5
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        when:
        commonCards = [new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.HEART, Rank.TEN), new Card(Suit.HEART, Rank.JACK), new Card(Suit.HEART, Rank.KING)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.HEART, Rank.NINE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT_FLUSH
        game.arms[0].firstCard.rank == Rank.KING
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == [1]
        game.arms[0].commonCardNumbers == [1, 2, 3, 4]

        when:
        commonCards = [new Card(Suit.SPADE, Rank.JACK), new Card(Suit.SPADE, Rank.NINE), new Card(Suit.SPADE, Rank.EIGHT), new Card(Suit.SPADE, Rank.SIX), new Card(Suit.HEART, Rank.KING)]
        playerCards = [new Card(Suit.SPADE, Rank.QUEEN), new Card(Suit.SPADE, Rank.TEN)]

        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT_FLUSH
        game.arms[0].firstCard.rank == Rank.QUEEN
        game.arms[0].comboCards.count { t -> t.suit == Suit.SPADE } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 2]

        when:
        commonCards = [new Card(Suit.SPADE, Rank.FIVE), new Card(Suit.SPADE, Rank.FOUR), new Card(Suit.SPADE, Rank.THREE), new Card(Suit.SPADE, Rank.KING), new Card(Suit.SPADE, Rank.TEN)]
        playerCards = [new Card(Suit.SPADE, Rank.TWO), new Card(Suit.SPADE, Rank.ACE)]

        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT_FLUSH
        game.arms[0].firstCard.rank == Rank.FIVE
        game.arms[0].comboCards.count { t -> t.suit == Suit.SPADE } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 2]

        when:
        commonCards = [new Card(Suit.SPADE, Rank.FIVE), new Card(Suit.SPADE, Rank.FOUR), new Card(Suit.SPADE, Rank.THREE), new Card(Suit.SPADE, Rank.SIX), new Card(Suit.SPADE, Rank.TEN)]
        playerCards = [new Card(Suit.SPADE, Rank.TWO), new Card(Suit.SPADE, Rank.ACE)]

        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT_FLUSH
        game.arms[0].firstCard.rank == Rank.SIX
        game.arms[0].comboCards.count { t -> t.suit == Suit.SPADE } == 5
        game.arms[0].playerCardNumbers == [0]
        game.arms[0].commonCardNumbers == [0, 1, 2, 3]
    }

    def "Four"() {
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.DIAMOND, Rank.FIVE), new Card(Suit.SPADE, Rank.FIVE), new Card(Suit.CLUB, Rank.KING)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.NINE)]

        when:
        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FOUR
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard == commonCards[4]
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.CLUB, Rank.QUEEN), new Card(Suit.DIAMOND, Rank.NINE), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.NINE)]
        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FOUR
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard == commonCards[2]
        game.arms[0].playerCardNumbers == [1]
        game.arms[0].commonCardNumbers == [0, 2, 3, 4]

        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.EIGHT), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.EIGHT)]
        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FOUR
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].secondCard.rank == commonCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 2, 3] || [2, 3, 4]
    }

    def "FullHouse"() {}

    def "Flush"() {
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.CLUB, Rank.TEN), new Card(Suit.CLUB, Rank.JACK), new Card(Suit.CLUB, Rank.KING)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.TWO)]

        when:
        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard == commonCards[4]
        game.arms[0].comboCards.count { t -> t.suit == Suit.CLUB } == 5
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.TEN), new Card(Suit.CLUB, Rank.JACK), new Card(Suit.CLUB, Rank.KING)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.TWO)]

        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard == commonCards[4]
        game.arms[0].comboCards.count { t -> t.suit == Suit.CLUB } == 5
        game.arms[0].playerCardNumbers == [1]
        game.arms[0].commonCardNumbers == [0, 2, 3, 4]

        when:
        commonCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.TEN), new Card(Suit.HEART, Rank.JACK), new Card(Suit.CLUB, Rank.KING)]
        playerCards = [new Card(Suit.HEART, Rank.TEN), new Card(Suit.HEART, Rank.QUEEN)]

        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard == playerCards[1]
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 3]
    }

    def "Straight"() {}

    def "Three"() {}

    def "TwoPair"() {}

    def "Pair"() {}

    private void changeCards(Poker game, List<Card> commonCards, List<Card> playerCards) {
        game.commonCards = commonCards
        game.arms[0].cards = playerCards
        game.arms[0].allCards.clear()
        game.arms[0].allCards.addAll(game.arms[0].cards.collect())
        game.arms[0].allCards.addAll(game.commonCards.collect())
        game.arms[0].allCards.sort { t -> t.rank }
    }

    // endregion
}
