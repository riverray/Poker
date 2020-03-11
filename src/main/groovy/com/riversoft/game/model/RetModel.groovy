package com.riversoft.game.model

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Stage
import com.riversoft.game.enums.Status
import groovy.transform.ToString

@ToString (includeNames = true)
class RetModel {
    Stage stage // этеп игры

    List<Arm> arms = [] // игроки

    List<Card> commonCards = [] // карты на столе

    long allBank // общая сумма на столе

    int buttonNumber // номер руки дилера

}