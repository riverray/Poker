package com.riversoft.game.logic

import com.riversoft.game.enums.Combination
import com.riversoft.game.enums.Rank
import com.riversoft.game.enums.Stage
import com.riversoft.game.enums.Status
import com.riversoft.game.enums.Suit
import com.riversoft.game.model.Arm
import com.riversoft.game.model.Card
import com.riversoft.game.model.RetModel

class Poker {
    Random rand = new Random()
    List<Card> allCards = []
    List<Card> deck = []

    List<Arm> arms = []
    List<Card> commonCards = []

    int buttonNumber = 0
    int blaindSize = 0

    int hod = 0
    int currentBet = 0

    Stage currentStage

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

    void startGame(List<Long> banks, int blaindSize) {
        arms.clear()
        commonCards.clear()

        for (int i = 0; i < banks.size(); i++) {
            arms.add(new Arm(bank: banks[i], active: true, status: Status.READY))
        }

        buttonNumber = 0
        this.blaindSize = blaindSize
    }

    void changeBank(int armNumber, long amount) {
        if (arms.size() > 0 && armNumber < arms.size()) {
            arms[armNumber].bank += amount
        }
    }

    void fold(int armNumber) {
        if (arms.size() > 0 && armNumber < arms.size()) {
            arms[armNumber].status = Status.FOLD
        }
    }

    // чек, после чека может быть конец раунда
    RetModel check(int armNumber) {
        if (arms.size() > 0 && armNumber < arms.size()) {
            arms[armNumber].status = Status.CHECK
        }

        return checkNextStage()
    }

    // кол, после кола может быть конец раунда
    RetModel call(int armNumber, long amount) {
        if (arms.size() > 0 && armNumber < arms.size()) {
            arms[armNumber].status = Status.CALL
            arms[armNumber].bet += amount

            if (arms[armNumber].bet > currentBet) {
                currentBet = arms[armNumber].bet
            }
        }

        return checkNextStage()
    }

    // рейз, конца раунда быть не может
    RetModel raise(int armNumber, long amount) {
        if (arms.size() > 0 && armNumber < arms.size()) {
            arms[armNumber].status = Status.RAISE
            arms[armNumber].bet += amount
        }

        if (arms[armNumber].bet > currentBet) {
            currentBet = arms[armNumber].bet
        }

        return checkNextStage()
    }

    void allIn(int armNumber) {
        if (arms.size() > 0 && armNumber < arms.size()) {
            arms[armNumber].status = Status.ALL_IN
            arms[armNumber].bet = arms[armNumber].bank
        }
    }

    void changeDealer() {
        if (buttonNumber < arms.size()) {
            buttonNumber++
        }
        else {
            buttonNumber = 0
        }
    }

    RetModel checkNextStage() {
        // нужно ли переходить к следующему этапу
        boolean nextLevel
        int bet

        // на префлопе должно быть выравнивание ставок
        if (currentStage == Stage.PRE_FLOP) {
            // выбираем первую ставку
            for (def arm : arms) {
                if (arm.status != Status.OUT_GAME && arm.status != Status.PAUSE && arm.status != Status.FOLD) {
                    bet = arm.bet
                    break
                }
            }

            nextLevel = true

            // если игрок на большой блайнде еще не принял решение
            if (arms.any { t -> t.status == Status.BIG_BLIND }) {
                nextLevel = false
            }


            // среди активных рук проверяем равенство ставок
            for (def arm : arms) {
                if (arm.status != Status.OUT_GAME && arm.status != Status.PAUSE && arm.status != Status.FOLD && arm.bet != bet) {
                    nextLevel = false
                }
            }
        }
        // на флопе пока есть активная рука с несовпадающими ставками
        // на флопе пока статус флопа или рейза
        else if (currentStage == Stage.FLOP) {
            // выбираем первую ставку
            for (def arm : arms) {
                if (arm.status == Status.FLOP || arm.status == Status.CHECK || arm.status == Status.CALL) {
                    bet = arm.bet
                    break
                }
            }

            nextLevel = true

            for (def arm : arms) {
                if (arm.status != Status.OUT_GAME && arm.status != Status.PAUSE && arm.status != Status.FOLD && arm.bet != bet) {
                    nextLevel = false
                }
            }

            // если кто-то еще не принял решение
            if (arms.any { t -> t.status == Status.FLOP }) {
                nextLevel = false
            }
        }
        // на терне пока есть активная рука с несовпадающими ставками
        // на терне пока статус терна или рейза
        else if (currentStage == Stage.TURN) {
            // выбираем первую ставку
            for (def arm : arms) {
                if (arm.status == Status.TURN || arm.status == Status.CHECK || arm.status == Status.CALL) {
                    bet = arm.bet
                    break
                }
            }

            nextLevel = true

            // если кто-то еще не принял решение
            if (arms.any { t -> t.status == Status.TURN }) {
                nextLevel = false
            }

            for (def arm : arms) {
                if (arm.status != Status.OUT_GAME && arm.status != Status.PAUSE && arm.status != Status.FOLD && arm.bet != bet) {
                    nextLevel = false
                }
            }
        }
        // на ривере пока есть активная рука с несовпадающими ставками
        // на ривере пока статус ривера
        else if (currentStage == Stage.RIVER) {
            // выбираем первую ставку
            for (def arm : arms) {
                if (arm.status == Status.RIVER || arm.status == Status.CHECK || arm.status == Status.CALL) {
                    bet = arm.bet
                    break
                }
            }

            nextLevel = true

            // если кто-то еще не принял решение
            if (arms.any { t -> t.status == Status.TURN }) {
                nextLevel = false
            }

            for (def arm : arms) {
                if (arm.status != Status.OUT_GAME && arm.status != Status.PAUSE && arm.status != Status.FOLD && arm.bet != bet) {
                    nextLevel = false
                }
            }
        }

        // TODO реализовать проверку на All-In
        // проверка на All-In

        // пока на том же уровне
        if (!nextLevel) {
            hod = hod + 1 < arms.size() ? hod + 1 : 0

            return new RetModel(
                    stage: currentStage,
                    allBank: arms.sum { t -> t.bet } as long,
                    buttonNumber: buttonNumber,
                    arms: arms.collect(),
                    hod: hod,
                    currentBet: currentBet
//                    currentBet: arms.findAll { t -> t.status == currentStatus }.max { n -> n.bet }.bet
            )
        }

        // переход на следующий уровень
        if (currentStage == Stage.PRE_FLOP) {
            // раздаем 3 общие карты
            getCommonCards(currentStage)

            currentStage = Stage.FLOP

            for (def arm : arms) {
                if (arm.status != Status.PAUSE && arm.status != Status.FOLD && arm.status != Status.OUT_GAME) {
                    arm.status = Status.FLOP
                }
            }

            hod = buttonNumber + 1 < arms.size() ? buttonNumber + 1 : 0

            return new RetModel(
                    stage: currentStage,
                    allBank: arms.sum { t -> t.bet } as long,
                    buttonNumber: buttonNumber,
                    arms: arms.collect(),
                    commonCards: commonCards.collect(),
                    hod: hod,
                    currentBet: currentBet
//                    currentBet: arms.findAll { t -> t.status == Status.FLOP }.max { n -> n.bet }.bet
            )
        }
        // переход на следующий уровень
        else if (currentStage == Stage.FLOP) {
            // раздаем 4-ю карту
            getCommonCards(currentStage)

            currentStage = Stage.TURN

            for (def arm : arms) {
                if (arm.status != Status.PAUSE && arm.status != Status.FOLD && arm.status != Status.OUT_GAME) {
                    arm.status = Status.TURN
                }
            }

            hod = buttonNumber + 1 < arms.size() ? buttonNumber + 1 : 0

            return new RetModel(
                    stage: currentStage,
                    allBank: arms.sum { t -> t.bet } as long,
                    buttonNumber: buttonNumber,
                    arms: arms.collect(),
                    commonCards: commonCards.collect(),
                    hod: hod,
                    currentBet: currentBet
//                    currentBet: arms.findAll { t -> t.status == Status.TURN }.max { n -> n.bet }.bet
            )
        }
        // переход на следующий уровень
        else if (currentStage == Stage.TURN) {
            // раздаем 5-ю карту
            getCommonCards(currentStage)

            currentStage = Stage.RIVER

            for (def arm : arms) {
                if (arm.status != Status.PAUSE && arm.status != Status.FOLD && arm.status != Status.OUT_GAME) {
                    arm.status = Status.RIVER
                }
            }

            hod = buttonNumber + 1 < arms.size() ? buttonNumber + 1 : 0

            return new RetModel(
                    stage: currentStage,
                    allBank: arms.sum { t -> t.bet } as long,
                    buttonNumber: buttonNumber,
                    arms: arms.collect(),
                    commonCards: commonCards.collect(),
                    hod: hod,
                    currentBet: currentBet
//                    currentBet: arms.findAll { t -> t.status == Status.TURN }.max { n -> n.bet }.bet
            )
        }
        // переход на следующий уровень
        else if (currentStage == Stage.RIVER) {
            currentStage = Stage.FINISH

            for (def arm : arms) {
                if (arm.status != Status.PAUSE && arm.status != Status.FOLD && arm.status != Status.OUT_GAME) {
                    arm.status = Status.FINISH
                }
            }

            hod = buttonNumber + 1 < arms.size() ? buttonNumber + 1 : 0

            // заполняем карты рук
            // распознаем комбинации
            for (def arm : arms) {
                fillAllCards(arm)
                checkCombination(arm)
                arm.comboCards.sort { t -> t.rank }
            }

            // сравнение комбинаций и определение победителя
            def winCombinationList = arms.findAll { t -> t.status == Status.FINISH }

            return new RetModel(
                    stage: currentStage,
                    allBank: arms.sum { t -> t.bet } as long,
                    buttonNumber: buttonNumber,
                    arms: arms.collect(),
                    commonCards: commonCards.collect(),
                    hod: hod,
                    currentBet: currentBet
//                    currentBet: arms.findAll { t -> t.status == Status.TURN }.max { n -> n.bet }.bet
            )
        }
    }

    // первая раздача
    RetModel firstGame() {
        shuffleDeck()

        // раздаем карты игрокам, которые в статусе "готов"
        for (def arm : arms) {
            if (arm.status != Status.READY) {
                continue
            }

            arm.status = Status.PRE_FLOP

            // получаем карты игроков
            getArmCards(arm)
//            fillAllCards(arm)

//            checkCombination(arm)
        }

        // снимаем блайнды
        int smallBlindIndex = buttonNumber + 1 < arms.size() ? buttonNumber + 1 : 0
        int bigBlindIndex = smallBlindIndex + 1 < arms.size() ? smallBlindIndex + 1 : 0

        hod = bigBlindIndex + 1 < arms.size() - 1 ? bigBlindIndex + 1 : 0
        currentBet = 2 * blaindSize

        arms[smallBlindIndex].bet = blaindSize
        arms[smallBlindIndex].status = Status.SMALL_BLIND

        arms[bigBlindIndex].bet = 2 * blaindSize
        arms[bigBlindIndex].status = Status.BIG_BLIND

        currentStage = Stage.PRE_FLOP

        return new RetModel(
                stage: Stage.PRE_FLOP,
                allBank: 3 * blaindSize,
                buttonNumber: buttonNumber,
                arms: arms.collect(),
                hod: hod,
                currentBet: currentBet
        )
    }

    // заполнение списка всех карт
    private void fillAllCards(Arm arm) {
        arm.allCards.addAll(arm.cards)
        arm.allCards.addAll(commonCards)
        arm.allCards.sort { t -> t.rank }
    }

    // заполнение карт руки
    void getArmCards(Arm arm) {
        for (int i = 0; i < 2; i++) {
            int index = rand.nextInt(deck.size())
            arm.cards.add(deck[index])
            deck.remove(index)
        }
    }

    // заполнение общих карт
    void getCommonCards(Stage stage) {
        if (stage == Stage.PRE_FLOP) {
            for (int i = 0; i < 3; i++) {
                getCard()
            }
        }
        else if (stage == Stage.FLOP || stage == Stage.TURN) {
            getCard()
        }
    }

    // выдача одной карты на стол
    private void getCard() {
        int index = rand.nextInt(deck.size())
        commonCards.add(deck[index])
        deck.remove(index)
    }

    // проверка комбинаций после открытия всех семи карт
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
        setHighCard(arm)
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
            if (count == 5 && checkClassicStraight(allFlushCards, arm) ||
                    count == 6 && checkClassicStraight(allFlushCards.drop(1), arm) ||
                    count == 6 && checkClassicStraight(allFlushCards.take(5), arm) ||
                    count == 7 && checkClassicStraight(allFlushCards.drop(2), arm) ||
                    count == 7 && checkClassicStraight(allFlushCards.drop(1).take(5), arm) ||
                    count == 7 && checkClassicStraight(allFlushCards.take(5), arm)) {
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
            // если последняя карта принадлежие четверке - тогда третья и все следующие (четыре). При этом проверяем, чтобы взяять по возможности карту игрока
            if (arm.allCards[6].rank == arm.firstCard.rank) {
                arm.comboCards = arm.allCards.drop(3)

                int index = arm.cards.findIndexOf { t -> t.rank == arm.allCards[2].rank }
                if (index >= 0) {
                    arm.comboCards.add(arm.cards[index])
                    arm.secondCard = arm.cards[index]
                }
                else {
                    arm.comboCards.add(arm.allCards[2])
                    arm.secondCard = arm.allCards[2]
                }
            }
            // если нет - тогда это последняя карта (плюс вся четверка)
            else {
                arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.firstCard.rank }.toList())

                int index = arm.cards.findIndexOf { t -> t.rank == arm.allCards[6].rank }
                if (index >= 0) {
                    arm.comboCards.add(arm.cards[index])
                    arm.secondCard = arm.cards[index]
                }
                else {
                    arm.comboCards.add(arm.allCards[6])
                    arm.secondCard = arm.allCards[6]
                }

//                arm.comboCards.add(arm.allCards[6])
//                arm.secondCard = arm.allCards[6]
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
            if (arm.allCards.count { t -> t.rank == arm.allCards[potentialPositions[i]].rank } >= 2) {
                // проверим, возможно пара из второй тройки есть у игрока
                secondCard = arm.allCards[potentialPositions[i]]
                break
            }
        }

        // если второй пары/тройки нет - выход
        if (secondCard == null) {
            return false
        }

        arm.combination = Combination.FULL_HOUSE
        arm.firstCard = firstCard
        arm.secondCard = secondCard

        arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.firstCard.rank }.toList())
        // проверим сколько у игрока и в зависимости от этого, берем карты в комбинацию
        int count = arm.cards.count { t -> t.rank == arm.secondCard.rank }
        if (count == 2) {
            arm.comboCards.addAll(arm.cards.toList())
        }
        else if (count == 1) {
            arm.comboCards.add(arm.cards.find { t -> t.rank == arm.secondCard.rank })
            arm.comboCards.addAll(commonCards.findAll { t -> t.rank == arm.secondCard.rank }.take(1))
        }
        else {
            arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.secondCard.rank }.take(2).toList())
        }

        fillNumberLists(arm)

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
        List<Card> allFlushCards = []
        for (def card : arm.allCards) {
            if (!allFlushCards.any { t -> t.rank == card.rank }) {
                allFlushCards.add(card)
            }
        }

        int count = allFlushCards.size()
        boolean straight = false

        // проверка на классический стрит
        if (count == 5 && checkClassicStraight(allFlushCards, arm) ||
                count == 6 && checkClassicStraight(allFlushCards.drop(1), arm) ||
                count == 6 && checkClassicStraight(allFlushCards.take(5), arm) ||
                count == 7 && checkClassicStraight(allFlushCards.drop(2), arm) ||
                count == 7 && checkClassicStraight(allFlushCards.drop(1).take(5), arm) ||
                count == 7 && checkClassicStraight(allFlushCards.take(5), arm)) {
            arm.combination = Combination.STRAIGHT

            straight = true
        }
        // проверка на нижний стрит
        if (!straight && count >= 5 && checkLowerStraight(allFlushCards, arm)) {
            arm.combination = Combination.STRAIGHT

            straight = true
        }

        // перезаполнение карт комбинации с преимуществом карт игрока
        if (straight) {
            for (int i = 0; i < arm.comboCards.size(); i++) {
                int index = arm.cards.findIndexOf { t -> t.rank == arm.comboCards[i].rank }
                if (index >= 0 && arm.cards[index].suit != arm.comboCards[i].suit) {
                    arm.comboCards[i] = arm.cards[index]
                }
            }
            111
        }

        return straight
    }

    boolean isThree(Arm arm) {
        // проверяем на тройку
        Card firstCard = null

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

        // ищем две старшие карты из оставшихся
        def twoCards = arm.allCards.findAll { t -> t.rank != firstCard.rank }.sort { t -> t.rank }.drop(2)

        arm.combination = Combination.THREE
        arm.firstCard = firstCard
        arm.secondCard = twoCards.last()

        arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.firstCard.rank }.toList())
        arm.comboCards.addAll(twoCards)

        fillNumberLists(arm)

        return true
    }

    boolean isTwoPair(Arm arm) {
        // проверяем на старшую пару
        Card firstCard = null
        Card secondCard = null

        if (arm.allCards.count { t -> t.rank == arm.allCards[5].rank } == 2) {
            firstCard = arm.allCards[5]
        }
        else if (arm.allCards.count { t -> t.rank == arm.allCards[3].rank } == 2) {
            firstCard = arm.allCards[3]
        }

        // если нет первой пары - выходим
        if (firstCard == null) {
            return false
        }

        // отбираем оставшиеся карты
        def otherCards = arm.allCards.findAll { t -> t.rank != firstCard.rank }.sort { t -> t.rank }

        // ищем вторую пару
        if (otherCards.count { t -> t.rank == arm.allCards[3].rank } == 2) {
            secondCard = arm.allCards[3]
        }
        else if (otherCards.count { t -> t.rank == arm.allCards[1].rank } == 2) {
            secondCard = arm.allCards[1]
        }

        // если нет второй пары - выходим
        if (secondCard == null) {
            return false
        }

        arm.combination = Combination.TWO_PAIR
        arm.firstCard = firstCard
        arm.secondCard = secondCard

        // оставляем карты
        otherCards = otherCards.findAll { t -> t.rank != secondCard.rank }.sort { t -> t.rank }

        // заносим пары в комбинацию
        arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.firstCard.rank }.toList())
        arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.secondCard.rank }.toList())

        // добавляем пятую карту (может быть третья пара, тогда надо проверить и взять ту, что у игрока)
        // нет третьей пары или есть третья пара, но у игрока ее нет
        if (otherCards[2].rank != otherCards[1].rank || otherCards[2].rank == otherCards[1].rank && !arm.cards.any { t -> t.rank == otherCards[2].rank }) {
            arm.comboCards.add(otherCards[2])
        }
        // есть третья пара, и хотя бы одна карта у игрока
        else {
            int index = arm.cards.findIndexOf { t -> t.rank == otherCards[2].rank }
            if (index >= 0) {
                arm.comboCards.add(arm.cards[index])
            }

        }

        fillNumberLists(arm)

        return true
    }

    boolean isOnePair(Arm arm) {
        // проверяем на пару
        Card firstCard = null
        Card secondCard = null

        if (arm.allCards.count { t -> t.rank == arm.allCards[5].rank } == 2) {
            firstCard = arm.allCards[5]
        }
        else if (arm.allCards.count { t -> t.rank == arm.allCards[3].rank } == 2) {
            firstCard = arm.allCards[3]
        }
        else if (arm.allCards.count { t -> t.rank == arm.allCards[1].rank } == 2) {
            firstCard = arm.allCards[1]
        }

        // если нет пары - выходим
        if (firstCard == null) {
            return false
        }

        // отбираем оставшиеся карты
        def otherCards = arm.allCards.findAll { t -> t.rank != firstCard.rank }.sort { t -> t.rank }.drop(2)

        arm.combination = Combination.ONE_PAIR
        arm.firstCard = firstCard
        arm.secondCard = otherCards.last()

        // заносим пары в комбинацию
        arm.comboCards.addAll(arm.allCards.findAll { t -> t.rank == arm.firstCard.rank }.toList())
        arm.comboCards.addAll(otherCards.toList())

        fillNumberLists(arm)

        return true
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

    void setHighCard(Arm arm) {
        arm.comboCards = arm.allCards.sort { t -> t.rank }.drop(2)

        arm.combination = Combination.HIGH_CARD
        arm.firstCard = arm.comboCards.last()
        arm.secondCard = arm.comboCards[3]

        fillNumberLists(arm)
    }
}
