package com.riversoft.game.enums

enum Combination {
    HIGH_CARD(1), ONE_PAIR(2), TWO_PAIR(3), THREE(4), STRAIGHT(5), FLUSH(6), FULL_HOUSE(7), FOUR(8), STRAIGHT_FLUSH(9), ROYAL_FLUSH(10)

    private int number

    Combination(int number) {
        this.number = number
    }

    int getNumber() {
        return number
    }
}