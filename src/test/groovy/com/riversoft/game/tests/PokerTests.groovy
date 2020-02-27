package com.riversoft.game.tests

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Rank
import com.riversoft.game.enums.Suit
import com.riversoft.game.logic.Poker
import com.riversoft.game.model.Arm
import com.riversoft.game.model.Card
import spock.lang.Specification

class PokerTests extends Specification {

    def "генерация всех кард" () {
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

    def "генерация изначальной колоды" () {
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

    def "создаем стол" () {
        given:
        List<Long> banks = [100, 200, 300]
        Poker game = new Poker()

        when:
        game.startGame(banks)

        then:
        game.arms.size() == banks.size()
    }

    def "делаем первую раздачу" () {
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

    def "Four" () {
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.DIAMOND, Rank.FIVE), new Card(Suit.SPADE, Rank.FIVE), new Card(Suit.CLUB, Rank.KING)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.NINE)]

        when:
        game.startGame(banks)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.isFour(game.arms[0])

        then:
        game.arms[0].combination == Combination.Four
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard == commonCards[4]
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]
    }

    private void changeCards(Poker game, List<Card> commonCards, List<Card> playerCards) {
        game.commonCards = commonCards
        game.arms[0].cards = playerCards
        game.arms[0].allCards.clear()
        game.arms[0].allCards.addAll(game.arms[0].cards.collect())
        game.arms[0].allCards.addAll(game.commonCards.collect())
        game.arms[0].allCards.sort { t -> t.rank }
    }
}
