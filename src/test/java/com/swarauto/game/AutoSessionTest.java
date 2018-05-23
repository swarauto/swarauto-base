package com.swarauto.game;

import com.swarauto.dependencies.DependenciesRegistry;
import com.swarauto.game.analyzer.Analyzer;
import com.swarauto.game.director.Director;
import com.swarauto.game.profile.Profile;
import com.swarauto.game.profile.ProfileManager;
import com.swarauto.game.session.AutoSession;
import com.swarauto.game.session.SessionType;
import com.swarauto.util.CommandUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AutoSessionTest {
    private static final String PROFILES_FOLDER = "src/test/resources/profiles";
    ProfileManager profileManager = new ProfileManager();

    private AutoSession.Listener listener = new AutoSession.Listener() {
        @Override
        public void onSessionStopped() {

        }
    };

    @Before
    public void setup() {
        DependenciesRegistry.commandUtil = mock(CommandUtil.class);
        profileManager.setLocation(PROFILES_FOLDER);
        DependenciesRegistry.profileManager = profileManager;
    }

    @Test
    public void testInit() {
        // FAILED: null SessionType
        try {
            new AutoSession(listener, null, null);
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }

        // Success
        Analyzer analyzer = Analyzer.newInstance(SessionType.CAIROS.getAnalyzerClass());
        Director director = Director.newInstance(SessionType.CAIROS.getDirectorClass());
        AutoSession autoSession = new AutoSession(listener, analyzer, director);
        Assert.assertNotNull(autoSession);

        analyzer.onDestroy();
    }

    @Test
    public void testStates() throws InterruptedException {
        DependenciesRegistry.commandUtil = mock(CommandUtil.class);

        Analyzer analyzer = Analyzer.newInstance(SessionType.CAIROS.getAnalyzerClass());
        Director director = Director.newInstance(SessionType.CAIROS.getDirectorClass());
        AutoSession autoSession = new AutoSession(listener, analyzer, director);

        // init
        Assert.assertEquals(AutoSession.State.INIT, autoSession.getState());

        // not started
        try {
            autoSession.pause();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }

        try {
            autoSession.resume();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }

        // start
        autoSession.start();
        Assert.assertEquals(AutoSession.State.RUNNING, autoSession.getState());

        Thread loopThread = autoSession.getLoop();
        Assert.assertTrue(loopThread.isAlive());

        // pause
        autoSession.pause();
        Assert.assertEquals(AutoSession.State.PAUSED, autoSession.getState());
        Assert.assertTrue(loopThread.isAlive());

        // resume
        autoSession.resume();
        Assert.assertEquals(AutoSession.State.RUNNING, autoSession.getState());
        Assert.assertTrue(loopThread.isAlive());

        // start again failed
        try {
            autoSession.start();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }

        // stop
        autoSession.stop();
        Assert.assertEquals(AutoSession.State.STOPPED, autoSession.getState());

        // start again failed
        try {
            autoSession.start();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }

        // restart
        autoSession.restart();
        Assert.assertEquals(AutoSession.State.RUNNING, autoSession.getState());

        analyzer.onDestroy();
    }

    @Test
    public void testSampleSequence() throws InterruptedException {
        String screenFilePath = "src/test/resources/sampleRuneReward.png";

        // Setup - CommandUtil
        CommandUtil commandUtil = mock(CommandUtil.class);
        when(commandUtil.capturePhoneScreen()).thenReturn(screenFilePath);
        DependenciesRegistry.commandUtil = commandUtil;

        Analyzer analyzer = mock(Analyzer.class);
        when(analyzer.detectGameState(anyString(), anyString())).thenReturn(GameState.IN_BATTLE);
        Director director = mock(Director.class);

        Profile profile = profileManager.loadProfile("testProfile");

        // Start session
        AutoSession autoSession = spy(new AutoSession(listener, analyzer, director));
        autoSession.setProfile(profile);
        autoSession.start();
        Thread.sleep(1000);

        verify(commandUtil, atLeastOnce()).capturePhoneScreen();
        verify(analyzer, atLeastOnce()).detectGameState(anyString(), anyString());
        verify(director, atLeastOnce()).act(any(GameStatus.class));

        analyzer.onDestroy();
    }
}
