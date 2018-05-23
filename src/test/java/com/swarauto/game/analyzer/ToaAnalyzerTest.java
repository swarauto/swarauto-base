package com.swarauto.game.analyzer;

import com.swarauto.game.GameState;
import org.junit.Assert;
import org.junit.Test;

public class ToaAnalyzerTest {
    private static final String INDICATORS_DIR_PATH = "src/test/resources/profiles/testProfile";
    private Analyzer analyzer = new ToaAnalyzer();

    @Test
    public void testDetectRuneRewardState() {
        Assert.assertEquals(GameState.TOA_NEXT_STAGE_CONFIRMATION,
                analyzer.detectGameState("src/test/resources/sampleTOAVictory.png", INDICATORS_DIR_PATH));
        Assert.assertEquals(GameState.TOA_REPLAY_STAGE_CONFIRMATION,
                analyzer.detectGameState("src/test/resources/sampleTOADefeated.png", INDICATORS_DIR_PATH));
    }
}
