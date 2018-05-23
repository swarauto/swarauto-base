package com.swarauto.game.analyzer;

import com.swarauto.game.GameState;
import org.bytedeco.javacpp.opencv_core;

import static com.swarauto.game.indicator.Indicator.confirmSellRuneIndicator;
import static com.swarauto.game.indicator.Indicator.runeRewardIndicator;

public class DungeonAnalyzer extends Analyzer {
    @Override
    protected GameState detectGameState(opencv_core.Mat sourceGrey, String indicatorsDir) {
        GameState gameState;

        if (containIndicators(sourceGrey, indicatorsDir, runeRewardIndicator)) {
            gameState = GameState.RUNE_REWARD;
        } else if (containIndicators(sourceGrey, indicatorsDir, confirmSellRuneIndicator)) {
            gameState = GameState.SELL_RUNE_CONFIRMATION;
        } else {
            gameState = super.detectGameState(sourceGrey, indicatorsDir);
        }

        return gameState;
    }
}
