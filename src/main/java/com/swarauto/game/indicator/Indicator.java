package com.swarauto.game.indicator;

public enum Indicator {
    // Required
    startBattleIndicator,
    networkDelayIndicator,
    networkUnstableIndicator,
    reviveIndicator,
    battleEndIndicator,
    runeRewardIndicator,
    confirmSellRuneIndicator,
    otherRewardIndicator,
    replayBattleIndicator,

    // Rift
    riftBattleEndIndicator,
    stoneRewardIndicator,
    confirmSellStoneIndicator,

    // TOA
    toaNextStageIndicator,
    toaDefeatedIndicator,

    // Auto refill
    noEnergyIndicator,

    // Rune pick
    sixStarRuneIndicator,
    fiveStarRuneIndicator,

    // Optional
    inBattleIndicator,
    manualAttackIndicator,
    noCrysIndicator,
}
