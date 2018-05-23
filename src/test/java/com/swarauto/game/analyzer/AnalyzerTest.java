package com.swarauto.game.analyzer;

import com.swarauto.game.GameState;
import com.swarauto.game.director.event.AnalyzerEvent;
import org.bytedeco.javacpp.opencv_core;
import org.greenrobot.eventbus.Subscribe;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AnalyzerTest {
    @Test
    public void testNewInstanceFailed() {
        try {
            Analyzer analyzer = Analyzer.newInstance(null);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }

        try {
            Analyzer analyzer = Analyzer.newInstance(Analyzer.class);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testDetectFailedInvalidIndicatorsDir() {
        DummySubscriber subscriber = mock(DummySubscriber.class);
        // Dummy analyzer
        Analyzer analyzer = new Analyzer() {
            @Override
            protected GameState detectGameState(opencv_core.Mat sourceGrey, String indicatorsDir) {
                return super.detectGameState(sourceGrey, indicatorsDir);
            }
        };
        analyzer.registerEventListener(subscriber);

        // Non-exist indicators path
        GameState gameState = analyzer.detectGameState("src/test/resources/sampleRuneReward.png", "/NOT_EXIST_DIR");
        ArgumentCaptor<AnalyzerEvent> argument = ArgumentCaptor.forClass(AnalyzerEvent.class);
        verify(subscriber, atLeastOnce()).onDirectorEvent(argument.capture());

        Assert.assertEquals(GameState.UNKNOWN, gameState);

        List<AnalyzerEvent> events = argument.getAllValues();
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(AnalyzerEvent.START_DETECTING_GAME_STATE, events.get(0).getId());
        Assert.assertEquals(AnalyzerEvent.EXCEPTION_OCCURRED, events.get(1).getId());

        analyzer.unregisterEventListener(subscriber);
        analyzer.onDestroy();
    }

    @Test
    public void testDetectFailedInvalidScreenShot() {
        DummySubscriber subscriber = mock(DummySubscriber.class);
        // Dummy analyzer
        Analyzer analyzer = new Analyzer() {
            @Override
            protected GameState detectGameState(opencv_core.Mat sourceGrey, String indicatorsDir) {
                return super.detectGameState(sourceGrey, indicatorsDir);
            }
        };
        analyzer.registerEventListener(subscriber);

        // Non exist screen shot file
        GameState gameState = analyzer.detectGameState("src/test/resources/NOT_EXIST_FILE", "src/test/resources/profiles/testProfile");
        ArgumentCaptor<AnalyzerEvent> argument = ArgumentCaptor.forClass(AnalyzerEvent.class);
        verify(subscriber, atLeastOnce()).onDirectorEvent(argument.capture());

        Assert.assertEquals(GameState.UNKNOWN, gameState);

        List<AnalyzerEvent> events = argument.getAllValues();
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(AnalyzerEvent.START_DETECTING_GAME_STATE, events.get(0).getId());
        Assert.assertEquals(AnalyzerEvent.GAME_STATE_DETECTED, events.get(1).getId());

        analyzer.unregisterEventListener(subscriber);
        analyzer.onDestroy();
    }

    @Test
    public void testDetectOK() {
        DummySubscriber subscriber = mock(DummySubscriber.class);
        // Dummy analyzer
        Analyzer analyzer = new Analyzer() {
            @Override
            protected GameState detectGameState(opencv_core.Mat sourceGrey, String indicatorsDir) {
                return GameState.RUNE_REWARD;
            }
        };
        analyzer.registerEventListener(subscriber);

        GameState gameState = analyzer.detectGameState("src/test/resources/sampleRuneReward.png", "src/test/resources/profiles/testProfile");
        ArgumentCaptor<AnalyzerEvent> argument = ArgumentCaptor.forClass(AnalyzerEvent.class);
        verify(subscriber, atLeastOnce()).onDirectorEvent(argument.capture());

        Assert.assertEquals(GameState.RUNE_REWARD, gameState);

        List<AnalyzerEvent> events = argument.getAllValues();
        Assert.assertEquals(2, events.size());
        Assert.assertEquals(AnalyzerEvent.START_DETECTING_GAME_STATE, events.get(0).getId());
        Assert.assertEquals(AnalyzerEvent.GAME_STATE_DETECTED, events.get(1).getId());

        analyzer.unregisterEventListener(subscriber);
        analyzer.onDestroy();
    }

    public class DummySubscriber {
        @Subscribe
        public void onDirectorEvent(AnalyzerEvent event) {

        }
    }
}
