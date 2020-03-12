package com.riversoft.game.model

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Status
import groovy.transform.ToString

@ToString (includeNames = true)
class Arm {
    List<Card> cards = [] // две карты игрока
    List<Card> allCards = [] // все карты с учетом общих (семь)
    List<Card> comboCards = [] // карты, составляющие комбинацию (пять)
    Card firstCard // первая карта комбинации
    Card secondCard // вторая карта комбинации
    Card thirdCard // третья карта комбинации

    Combination combination = Combination.HIGH_CARD // комбинация

    List<Integer> playerCardNumbers = [] // номера карт игрока, участвующих в комбинации
    List<Integer> commonCardNumbers = [] // номера общих карт, участвующих в комбинации

    int bet // размер ставки
    int lastRaise
    long bank // банк игрока
    boolean active // активность
    Status status // статус



}
