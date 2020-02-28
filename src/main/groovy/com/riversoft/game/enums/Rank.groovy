package com.riversoft.game.enums

enum Rank {
    TWO(2), THREE (3), FOUR (4), FIVE (5), SIX (6), SEVEN (7), EIGHT (8), NINE (9), TEN (10), JACK (11), QUEEN (12), KING (13), ACE (14)
//    TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE

    private int number

    Rank(int number) {
        this.number = number
    }

    int getNumber() {
        return number
    }
}