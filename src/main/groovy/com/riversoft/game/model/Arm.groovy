package com.riversoft.game.model

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Status
import groovy.transform.ToString

@ToString (includeNames = true)
class Arm {
    List<Card> cards = [] // две карты игрока
    List<Card> allCards = [] // все карты с учетом общих (семь)
    List<Card> comboCards = [] // карты, составляющие кобинацию (пять)
    Card firstCard
    Card secondCard
    Card thirdCard

    Combination combination = Combination.HighCard

    List<Integer> playerCardNumbers = []
    List<Integer> commonCardNumbers = []

    int bet
    int lastRaise
    long bank
    boolean active
    Status status


}
