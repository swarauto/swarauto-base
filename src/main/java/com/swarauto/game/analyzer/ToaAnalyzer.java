package com.swarauto.game.analyzer;

import com.swarauto.game.GameState;
import org.bytedeco.javacpp.opencv_core;

import static com.swarauto.game.indicator.Indicator.toaDefeatedIndicator;
import static com.swarauto.game.indicator.Indicator.toaNextStageIndicator;

public class ToaAnalyzer extends Analyzer {
    @Override
    protected GameState detectGameState(opencv_core.Mat sourceGrey, String indicatorsDir) {
        GameState gameState;

        if (containIndicators(sourceGrey, indicatorsDir, toaNextStageIndicator)) {
            gameState = GameState.TOA_NEXT_STAGE_CONFIRMATION;
        } else if (containIndicators(sourceGrey, indicatorsDir, toaDefeatedIndicator)) {
            gameState = GameState.TOA_REPLAY_STAGE_CONFIRMATION;
        } else {
            gameState = super.detectGameState(sourceGrey, indicatorsDir);
        }

        return gameState;
    }
}
