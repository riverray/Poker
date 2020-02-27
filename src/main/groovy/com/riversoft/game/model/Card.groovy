package com.riversoft.game.model

import com.riversoft.game.enums.Suit
import com.riversoft.game.enums.Rank
import groovy.transform.ToString

@ToString (includeNames = true, includePackage = false)
class Card {
    Suit suit
    Rank rank

    Card() {}

    Card(Suit suit, Rank rank) {
        this.suit = suit
        this.rank = rank
    }
}
