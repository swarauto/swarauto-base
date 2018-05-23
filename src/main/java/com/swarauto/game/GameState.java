package com.swarauto.game;

public enum GameState {
    /**
     * Unable to identify the game state.
     */
    UNKNOWN,
    /**
     * Game battle are in manual mode.
     */
    BATTLE_MANUAL,
    IN_BATTLE,
    /**
     * Battle end with successful.
     */
    BATTLE_ENDED,
    /**
     * only Rune are considered in this category. Ideally, it will have get or sell actions.
     */
    RUNE_REWARD,
    /**
     * Grindstone and gem are considered in this category. Ideally, it will have get or sell actions.
     */
    GEM_REWARD,
    /**
     * Other rewards which only have 1 OK (get) action.
     */
    OTHER_REWARD,
    /**
     * Yes or no to replay new battle.
     */
    REPLAY_BATTLE_CONFIRMATION,
    /**
     * Start new battle.
     */
    START_BATTLE,
    /**
     * Sell rune yes, no confirmation with 5* or +9
     */
    SELL_RUNE_CONFIRMATION,
    SELL_STONE_CONFIRMATION,
    /**
     * Not enough energy
     */
    NOT_ENOUGH_ENERGY,
    /**
     * Network delay after the battle.
     */
    NETWORK_DELAY,
    /**
     * Unstable network when start new battle.
     */
    UNSTABLE_NETWORK,
    BATTLE_ENDED_FAIL,
    NO_CRYS,
    /**
     * TOA stage victory, waiting click next stage
     */
    TOA_NEXT_STAGE_CONFIRMATION,
    /**
     * TOA stage failed
     */
    TOA_REPLAY_STAGE_CONFIRMATION,;;
}
