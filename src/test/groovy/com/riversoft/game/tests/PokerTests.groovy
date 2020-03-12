package com.riversoft.game.tests

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Rank
import com.riversoft.game.enums.Stage
import com.riversoft.game.enums.Suit
import com.riversoft.game.logic.Poker
import com.riversoft.game.model.Card
import spock.lang.Specification

import javax.net.ssl.HostnameVerifier

class PokerTests extends Specification {
    int blaindSize = 2

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
        game.startGame(banks, blaindSize)

        then:
        game.arms.size() == banks.size()
    }

    def "делаем первую раздачу"() {
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()

        when:
        game.startGame(banks, blaindSize)
        def model = game.firstGame()

        then:
        game.arms.size() == banks.size()
    }

    //# region Combination

    def "RoyalFlush"() {
        // все карты комбинации на столе
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.QUEEN), new Card(Suit.CLUB, Rank.ACE), new Card(Suit.CLUB, Rank.TEN), new Card(Suit.CLUB, Rank.JACK), new Card(Suit.CLUB, Rank.KING)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.NINE)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ROYAL_FLUSH
        game.arms[0].firstCard.rank == Rank.ACE
        game.arms[0].comboCards.count { t -> t.suit == Suit.CLUB } == 5
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // одна карта у игрока
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

        // две карты у игрока
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
        // все карты на столе
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.SEVEN), new Card(Suit.CLUB, Rank.NINE), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.CLUB, Rank.JACK), new Card(Suit.CLUB, Rank.TEN)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.FIVE)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT_FLUSH
        game.arms[0].firstCard.rank == Rank.JACK
        game.arms[0].comboCards.count { t -> t.suit == Suit.CLUB } == 5
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // одна карта у игрока, 7 карт подряд
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

        // две карты у игрока,
        when:
        commonCards = [new Card(Suit.SPADE, Rank.JACK), new Card(Suit.SPADE, Rank.NINE), new Card(Suit.SPADE, Rank.EIGHT), new Card(Suit.SPADE, Rank.SIX), new Card(Suit.HEART, Rank.KING)]
        playerCards = [new Card(Suit.SPADE, Rank.QUEEN), new Card(Suit.SPADE, Rank.TEN)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT_FLUSH
        game.arms[0].firstCard.rank == Rank.QUEEN
        game.arms[0].comboCards.count { t -> t.suit == Suit.SPADE } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 2]

        // две карты у игрока, нижний стрит
        when:
        commonCards = [new Card(Suit.SPADE, Rank.FIVE), new Card(Suit.SPADE, Rank.FOUR), new Card(Suit.SPADE, Rank.THREE), new Card(Suit.SPADE, Rank.KING), new Card(Suit.SPADE, Rank.TEN)]
        playerCards = [new Card(Suit.SPADE, Rank.TWO), new Card(Suit.SPADE, Rank.ACE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT_FLUSH
        game.arms[0].firstCard.rank == Rank.FIVE
        game.arms[0].comboCards.count { t -> t.suit == Suit.SPADE } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 2]

        // одна карта у игрока, 5 подряд и туз, должен определиться стрит по шесть
        when:
        commonCards = [new Card(Suit.SPADE, Rank.FIVE), new Card(Suit.SPADE, Rank.FOUR), new Card(Suit.SPADE, Rank.THREE), new Card(Suit.SPADE, Rank.SIX), new Card(Suit.SPADE, Rank.TEN)]
        playerCards = [new Card(Suit.SPADE, Rank.TWO), new Card(Suit.SPADE, Rank.ACE)]

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
        // все карты на столе
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.DIAMOND, Rank.FIVE), new Card(Suit.SPADE, Rank.FIVE), new Card(Suit.CLUB, Rank.KING)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.NINE)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FOUR
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard.rank == commonCards[4].rank
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // одна карта из четверки у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.CLUB, Rank.QUEEN), new Card(Suit.DIAMOND, Rank.NINE), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.NINE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FOUR
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard.rank == commonCards[2].rank
        game.arms[0].playerCardNumbers == [1]
        game.arms[0].commonCardNumbers == [0, 2, 3, 4]

        // две карты из четверки у игрока. При этом есть еще одна пара, одна карта из которой входит к комбинацию как старшая из остающихся
        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.EIGHT), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.EIGHT)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FOUR
        game.arms[0].firstCard.rank == playerCards[1].rank
        game.arms[0].secondCard.rank == commonCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [2, 3, 4]

        // две карты (одна из четверки и старшая) у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.TWO), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.KING), new Card(Suit.SPADE, Rank.EIGHT)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FOUR
        game.arms[0].firstCard.rank == playerCards[1].rank
        game.arms[0].secondCard.rank == playerCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 2]

        // четыре и три, берется четыре и одна из трех
        when:
        commonCards = [new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.KING), new Card(Suit.SPADE, Rank.KING)]
        playerCards = [new Card(Suit.HEART, Rank.KING), new Card(Suit.SPADE, Rank.EIGHT)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FOUR
        game.arms[0].firstCard.rank == commonCards[1].rank
        game.arms[0].secondCard.rank == playerCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 2]
    }

    def "FullHouse"() {
        // все карты на столе
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.KING), new Card(Suit.DIAMOND, Rank.FIVE), new Card(Suit.SPADE, Rank.FIVE), new Card(Suit.CLUB, Rank.KING)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.NINE)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FULL_HOUSE
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard.rank == commonCards[4].rank
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // две тройки берется старшая и две карты из младшей, две карты у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.NINE), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.EIGHT)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FULL_HOUSE
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard.rank == commonCards[2].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 3, 4]

        // две тройки берется старшая и две карты из младшей, одна карта у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.NINE), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.FIVE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FULL_HOUSE
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard.rank == commonCards[2].rank
        game.arms[0].playerCardNumbers == [0]
        game.arms[0].commonCardNumbers == [0, 1, 3, 4]

        // тройка и пара, одна карта пары у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.EIGHT), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.KING), new Card(Suit.CLUB, Rank.EIGHT)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FULL_HOUSE
        game.arms[0].firstCard.rank == commonCards[2].rank
        game.arms[0].secondCard.rank == commonCards[0].rank
        game.arms[0].playerCardNumbers == [1]
        game.arms[0].commonCardNumbers == [0, 2, 3, 4]

        // тройка и пара, две карты пары у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.HEART, Rank.NINE), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.EIGHT), new Card(Suit.SPADE, Rank.NINE)]
        playerCards = [new Card(Suit.HEART, Rank.KING), new Card(Suit.CLUB, Rank.KING)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FULL_HOUSE
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].secondCard.rank == playerCards[1].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 4]
    }

    def "Flush"() {
        // просто пять карт все на столе
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.CLUB, Rank.JACK), new Card(Suit.CLUB, Rank.KING), new Card(Suit.CLUB, Rank.EIGHT), new Card(Suit.CLUB, Rank.TEN)]
        def playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.DIAMOND, Rank.TWO)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard.rank == commonCards[2].rank
        game.arms[0].secondCard.rank == commonCards[1].rank
        game.arms[0].comboCards.count { t -> t.suit == Suit.CLUB } == 5
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // 5 всего, одна у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.TEN), new Card(Suit.CLUB, Rank.JACK), new Card(Suit.CLUB, Rank.KING)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.TWO)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard == commonCards[4]
        game.arms[0].comboCards.count { t -> t.suit == Suit.CLUB } == 5
        game.arms[0].playerCardNumbers == [1]
        game.arms[0].commonCardNumbers == [0, 2, 3, 4]

        // 5 всего, две у игрока
        when:
        commonCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.TEN), new Card(Suit.HEART, Rank.JACK), new Card(Suit.CLUB, Rank.KING)]
        playerCards = [new Card(Suit.HEART, Rank.TEN), new Card(Suit.HEART, Rank.QUEEN)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard == playerCards[1]
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 3]

        // 6 всего, у игрока младшая и в комбинацию не входит
        when:
        commonCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.HEART, Rank.KING), new Card(Suit.HEART, Rank.TEN), new Card(Suit.HEART, Rank.JACK)]
        playerCards = [new Card(Suit.CLUB, Rank.TEN), new Card(Suit.HEART, Rank.THREE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard.rank == commonCards[2].rank
        game.arms[0].secondCard.rank == commonCards[4].rank
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // 6 всего, у игрока одна и в комбинацию входит
        when:
        commonCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.HEART, Rank.KING), new Card(Suit.HEART, Rank.TEN), new Card(Suit.HEART, Rank.JACK)]
        playerCards = [new Card(Suit.CLUB, Rank.TEN), new Card(Suit.HEART, Rank.QUEEN)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard.rank == commonCards[2].rank
        game.arms[0].secondCard.rank == playerCards[1].rank
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == [1]
        game.arms[0].commonCardNumbers == [1, 2, 3, 4]

        // 6 всего, у игрока две и они в комбинацию входят
        when:
        commonCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.HEART, Rank.KING), new Card(Suit.DIAMOND, Rank.SEVEN), new Card(Suit.HEART, Rank.JACK)]
        playerCards = [new Card(Suit.HEART, Rank.TEN), new Card(Suit.HEART, Rank.QUEEN)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard.rank == commonCards[2].rank
        game.arms[0].secondCard.rank == playerCards[1].rank
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [1, 2, 4]

        // 7 всего, у игрока две и они в комбинацию входят
        when:
        commonCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.HEART, Rank.KING), new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.HEART, Rank.JACK)]
        playerCards = [new Card(Suit.HEART, Rank.TEN), new Card(Suit.HEART, Rank.QUEEN)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard.rank == commonCards[2].rank
        game.arms[0].secondCard.rank == playerCards[1].rank
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [1, 2, 4]

        // 7 всего, у игрока две и они в комбинацию не входят
        when:
        commonCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.HEART, Rank.KING), new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.HEART, Rank.JACK)]
        playerCards = [new Card(Suit.HEART, Rank.TWO), new Card(Suit.HEART, Rank.THREE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.FLUSH
        game.arms[0].firstCard.rank == commonCards[2].rank
        game.arms[0].secondCard.rank == commonCards[4].rank
        game.arms[0].comboCards.count { t -> t.suit == Suit.HEART } == 5
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]


    }

    def "Straight"() {
        // просто пять карт, все на столе, простой стрит
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.DIAMOND, Rank.NINE), new Card(Suit.HEART, Rank.SIX), new Card(Suit.SPADE, Rank.EIGHT), new Card(Suit.CLUB, Rank.SEVEN)]
        def playerCards = [new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.DIAMOND, Rank.TWO)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT
        game.arms[0].firstCard.rank == commonCards[1].rank
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // просто пять карт, все на столе, нижний стрит
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.THREE), new Card(Suit.DIAMOND, Rank.ACE), new Card(Suit.CLUB, Rank.FOUR), new Card(Suit.HEART, Rank.TWO)]
        playerCards = [new Card(Suit.HEART, Rank.EIGHT), new Card(Suit.CLUB, Rank.JACK)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // просто пять карт, одна карта совпадает номиналом на столе и у игрока, должна взяться та, что у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.DIAMOND, Rank.NINE), new Card(Suit.HEART, Rank.SIX), new Card(Suit.SPADE, Rank.EIGHT), new Card(Suit.CLUB, Rank.SEVEN)]
        playerCards = [new Card(Suit.HEART, Rank.NINE), new Card(Suit.DIAMOND, Rank.TWO)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].playerCardNumbers == [0]
        game.arms[0].commonCardNumbers == [0, 2, 3, 4]

        // 7 карт, две у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.SIX), new Card(Suit.DIAMOND, Rank.NINE), new Card(Suit.HEART, Rank.TEN), new Card(Suit.SPADE, Rank.EIGHT), new Card(Suit.CLUB, Rank.SEVEN)]
        playerCards = [new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.DIAMOND, Rank.JACK)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [1, 2, 3]

        // 7 карт, все на столе, возможен нижний стрит
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.THREE), new Card(Suit.DIAMOND, Rank.FOUR), new Card(Suit.CLUB, Rank.ACE), new Card(Suit.HEART, Rank.TWO)]
        playerCards = [new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.CLUB, Rank.SIX)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.STRAIGHT
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 2]
    }

    def "Three"() {
        // тройка, все на столе
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.DIAMOND, Rank.FIVE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.SPADE, Rank.EIGHT), new Card(Suit.CLUB, Rank.SEVEN)]
        def playerCards = [new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.DIAMOND, Rank.TWO)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.THREE
        game.arms[0].firstCard.rank == commonCards[0].rank
        game.arms[0].playerCardNumbers == [0]
        game.arms[0].commonCardNumbers == [0, 1, 2, 3]

        // одна карта у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.DIAMOND, Rank.FOUR), new Card(Suit.CLUB, Rank.SEVEN), new Card(Suit.HEART, Rank.TWO)]
        playerCards = [new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.CLUB, Rank.SIX)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.THREE
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 3]

        // две карты у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.DIAMOND, Rank.ACE), new Card(Suit.CLUB, Rank.QUEEN), new Card(Suit.HEART, Rank.TWO)]
        playerCards = [new Card(Suit.HEART, Rank.ACE), new Card(Suit.CLUB, Rank.ACE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.THREE
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [1, 2, 3]
    }

    def "TwoPair"() {
        // две пары тройка, все на столе
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.DIAMOND, Rank.QUEEN), new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.SPADE, Rank.ACE), new Card(Suit.CLUB, Rank.FIVE)]
        def playerCards = [new Card(Suit.HEART, Rank.KING), new Card(Suit.DIAMOND, Rank.TWO)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.TWO_PAIR
        game.arms[0].firstCard.rank == commonCards[1].rank
        game.arms[0].secondCard.rank == commonCards[0].rank
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // 2 пары, одна пара у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.DIAMOND, Rank.FOUR), new Card(Suit.CLUB, Rank.KING), new Card(Suit.HEART, Rank.TWO)]
        playerCards = [new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.CLUB, Rank.SEVEN)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.TWO_PAIR
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].secondCard.rank == commonCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 3]

        // две карты у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.SEVEN), new Card(Suit.DIAMOND, Rank.ACE), new Card(Suit.CLUB, Rank.QUEEN), new Card(Suit.HEART, Rank.TWO)]
        playerCards = [new Card(Suit.HEART, Rank.ACE), new Card(Suit.CLUB, Rank.QUEEN)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.TWO_PAIR
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].secondCard.rank == playerCards[1].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [1, 2, 3]

        // три пары, все реальные на столе
        when:
        commonCards = [new Card(Suit.CLUB, Rank.FIVE), new Card(Suit.HEART, Rank.FIVE), new Card(Suit.DIAMOND, Rank.ACE), new Card(Suit.CLUB, Rank.QUEEN), new Card(Suit.HEART, Rank.QUEEN)]
        playerCards = [new Card(Suit.HEART, Rank.THREE), new Card(Suit.CLUB, Rank.THREE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.TWO_PAIR
        game.arms[0].firstCard.rank == commonCards[3].rank
        game.arms[0].secondCard.rank == commonCards[0].rank
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]
    }

    def "Pair"() {
        // все на столе
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()
        def commonCards = [new Card(Suit.CLUB, Rank.TEN), new Card(Suit.DIAMOND, Rank.QUEEN), new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.SPADE, Rank.ACE), new Card(Suit.CLUB, Rank.EIGHT)]
        def playerCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.DIAMOND, Rank.TWO)]

        when:
        game.startGame(banks, blaindSize)
        game.firstGame()
        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ONE_PAIR
        game.arms[0].firstCard.rank == commonCards[1].rank
        game.arms[0].secondCard.rank == commonCards[3].rank
        game.arms[0].playerCardNumbers == []
        game.arms[0].commonCardNumbers == [0, 1, 2, 3, 4]

        // пара на столе, одна карта у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.THREE), new Card(Suit.DIAMOND, Rank.QUEEN), new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.SPADE, Rank.ACE), new Card(Suit.CLUB, Rank.EIGHT)]
        playerCards = [new Card(Suit.HEART, Rank.FIVE), new Card(Suit.DIAMOND, Rank.JACK)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ONE_PAIR
        game.arms[0].firstCard.rank == commonCards[1].rank
        game.arms[0].secondCard.rank == commonCards[3].rank
        game.arms[0].playerCardNumbers == [1]
        game.arms[0].commonCardNumbers == [1, 2, 3, 4]

        // пара на столе, две карты у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.THREE), new Card(Suit.DIAMOND, Rank.QUEEN), new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.SPADE, Rank.FOUR), new Card(Suit.CLUB, Rank.EIGHT)]
        playerCards = [new Card(Suit.HEART, Rank.NINE), new Card(Suit.DIAMOND, Rank.JACK)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ONE_PAIR
        game.arms[0].firstCard.rank == commonCards[1].rank
        game.arms[0].secondCard.rank == playerCards[1].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [1, 2, 4]

        // пара у игрока
        when:
        commonCards = [new Card(Suit.CLUB, Rank.THREE), new Card(Suit.DIAMOND, Rank.JACK), new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.SPADE, Rank.FOUR), new Card(Suit.CLUB, Rank.EIGHT)]
        playerCards = [new Card(Suit.HEART, Rank.NINE), new Card(Suit.DIAMOND, Rank.NINE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ONE_PAIR
        game.arms[0].firstCard.rank == playerCards[0].rank
        game.arms[0].secondCard.rank == commonCards[2].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [1, 2, 4]

        // пара пополам
        when:
        commonCards = [new Card(Suit.CLUB, Rank.NINE), new Card(Suit.DIAMOND, Rank.JACK), new Card(Suit.HEART, Rank.QUEEN), new Card(Suit.SPADE, Rank.FOUR), new Card(Suit.CLUB, Rank.EIGHT)]
        playerCards = [new Card(Suit.HEART, Rank.ACE), new Card(Suit.DIAMOND, Rank.NINE)]

        changeCards(game, commonCards, playerCards)
        game.checkCombination(game.arms[0])

        then:
        game.arms[0].combination == Combination.ONE_PAIR
        game.arms[0].firstCard.rank == playerCards[1].rank
        game.arms[0].secondCard.rank == playerCards[0].rank
        game.arms[0].playerCardNumbers == [0, 1]
        game.arms[0].commonCardNumbers == [0, 1, 2]
    }

    private void changeCards(Poker game, List<Card> commonCards, List<Card> playerCards) {
        game.commonCards = commonCards
        game.arms[0].cards = playerCards
        game.arms[0].allCards.clear()
        game.arms[0].allCards.addAll(game.arms[0].cards.collect())
        game.arms[0].allCards.addAll(game.commonCards.collect())
        game.arms[0].allCards.sort { t -> t.rank }
    }

    // endregion

    def "реализация префлопа"() {
        given:
        List<Long> banks = [100, 100, 100]
        Poker game = new Poker()

        when:
        game.startGame(banks, blaindSize)
        def model = game.firstGame()


        while (model.stage != Stage.FLOP) {
            if (model.arms[model.hod].bet < model.currentBet) {
                model = game.call(model.hod, model.currentBet - model.arms[model.hod].bet)
            }
        }

        then:
        game.arms.size() == banks.size()
    }
}
