package com.swarauto.game.analyzer;

import com.swarauto.game.GameState;
import org.bytedeco.javacpp.opencv_core;

import static com.swarauto.game.indicator.Indicator.riftBattleEndIndicator;

public class RiftAnalyzer extends DungeonAnalyzer {
    @Override
    protected GameState detectGameState(opencv_core.Mat sourceGrey, String indicatorsDir) {
        GameState gameState = super.detectGameState(sourceGrey, indicatorsDir);

        // Trick: Detect battle-end last. Since riftBattleEndIndicator always displayed when end.
        if (gameState == GameState.UNKNOWN) {
            if (containIndicators(sourceGrey, indicatorsDir, riftBattleEndIndicator)) {
                gameState = GameState.BATTLE_ENDED;
// Temporary disable stone
//        } else if (containIndicators(sourceGrey, indicatorsDir, confirmSellStoneIndicator)) {
//            gameState = GameState.SELL_STONE_CONFIRMATION;
//        } else if (containIndicators(sourceGrey, indicatorsDir, stoneRewardIndicator)) {
//            gameState = GameState.GEM_REWARD;
            }
        }

        return gameState;
    }
}
