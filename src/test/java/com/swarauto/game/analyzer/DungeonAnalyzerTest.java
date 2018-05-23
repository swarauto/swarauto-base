package com.swarauto.game.analyzer;

import com.swarauto.game.GameState;
import org.junit.Assert;
import org.junit.Test;

public class DungeonAnalyzerTest {
    private static final String INDICATORS_DIR_PATH = "src/test/resources/profiles/testProfile";

    @Test
    public void testDetectRuneRewardState() {
        Analyzer analyzer = Analyzer.newInstance(DungeonAnalyzer.class);
        Assert.assertEquals(GameState.RUNE_REWARD, analyzer.detectGameState("src/test/resources/sampleRuneReward.png", INDICATORS_DIR_PATH));
        analyzer.onDestroy();
    }
}
