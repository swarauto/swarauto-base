package com.swarauto.game.session;

import com.swarauto.dependencies.DependenciesRegistry;
import com.swarauto.game.GameState;
import com.swarauto.game.GameStatus;
import com.swarauto.game.analyzer.Analyzer;
import com.swarauto.game.director.Director;
import com.swarauto.game.director.event.AnalyzerEvent;
import com.swarauto.game.director.event.DirectorEvent;
import com.swarauto.game.profile.Profile;
import com.swarauto.game.profile.ProfileManager;
import com.swarauto.util.CommandUtil;
import lombok.Data;
import lombok.Getter;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoSession {
    protected static final Logger LOG = LoggerFactory.getLogger(AutoSession.class);

    private final Listener listener;
    @Getter
    private final Analyzer analyzer;
    @Getter
    private final Director director;
    private Profile profile;

    protected final CommandUtil commandUtil;
    protected final ProfileManager profileManager;

    @Getter
    private Report report;

    @Getter
    private State state;
    @Getter
    private Loop loop;

    public AutoSession(Listener listener, Analyzer analyzer, Director director) {
        this.listener = listener;

        if (analyzer == null) throw new IllegalArgumentException("Analyzer is null");
        if (director == null) throw new IllegalArgumentException("Director is null");

        this.analyzer = analyzer;
        this.director = director;
        this.commandUtil = DependenciesRegistry.commandUtil;
        this.profileManager = DependenciesRegistry.profileManager;
        this.director.setCommonConfig(this.profileManager.loadCommonConfig());

        init();
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        this.director.setProfile(profile);
    }

    @Subscribe
    public void onAnalyzerEvent(AnalyzerEvent event) {
        switch (event.getId()) {
            case AnalyzerEvent.GAME_STATE_DETECTED: {
                GameState gameState = (GameState) event.getDataAt(0);
                switch (gameState) {
                    case BATTLE_ENDED:
                    case BATTLE_ENDED_FAIL: {
                        report.setCompletedRuns(report.getCompletedRuns() + 1);
                        if (GameState.BATTLE_ENDED.equals(gameState))
                            report.setSuccessRuns(report.getSuccessRuns() + 1);
                        break;
                    }
                    case NO_CRYS:
                        report.setRefillTimes(report.getRefillTimes() - 1);
                        break;
                }
                break;
            }
        }
    }

    @Subscribe
    public void onDirectorEvent(DirectorEvent event) {
        switch (event.getId()) {
            case DirectorEvent.REFILLING_ENERGY:
                report.setRefillTimes(report.getRefillTimes() + 1);
                break;
        }
    }

    private void init() {
        this.report = new Report();
        this.state = State.INIT;
    }

    public void start() {
        if (!state.equals(State.INIT)) throw new IllegalStateException("Session already started");

        LOG.info("AutoSession start()");
        this.loop = new Loop();
        state = State.RUNNING;
        loop.start();
        if (analyzer != null) analyzer.registerEventListener(this);
        if (director != null) director.registerEventListener(this);
    }

    public void pause() {
        if (!state.equals(State.RUNNING)) throw new IllegalStateException("Session not currently running");

        LOG.info("AutoSession pause()");
        state = State.PAUSED;
    }

    public void resume() {
        if (!state.equals(State.PAUSED)) throw new IllegalStateException("Session not currently paused");

        LOG.info("AutoSession resume()");
        state = State.RUNNING;
    }

    public void stop() {
        state = State.STOPPED;
        LOG.info("AutoSession stopping...");
        if (analyzer != null) {
            analyzer.unregisterEventListener(this);
            analyzer.onDestroy();
        }
        if (director != null) director.unregisterEventListener(this);
    }

    public void restart() {
        if (!state.equals(State.INIT) && !state.equals(State.STOPPED))
            throw new IllegalStateException("Session already running");

        init();
        start();
    }

    private class Loop extends Thread {
        @Override
        public void run() {
            while (true) {

                switch (state) {
                    case INIT: // Should never be here
                    case PAUSED:
                        break;
                    case RUNNING: {
                        // Capture screen
                        String screenFilePath = commandUtil.capturePhoneScreen();
                        if (!AutoSession.State.RUNNING.equals(state)) break; // User request stop when processing

                        // Analyze screen
                        String indicatorsDir = null;
                        if (profile != null) indicatorsDir = profile.getIndicatorsDir();
                        GameState gameState = analyzer.detectGameState(screenFilePath, indicatorsDir);
                        if (!AutoSession.State.RUNNING.equals(state)) break; // User request stop when processing

                        // Direct
                        director.act(GameStatus.create(gameState, screenFilePath));
                        break;
                    }
                    case STOPPED:
                        LOG.info("AutoSession stopped!");
                        listener.onSessionStopped();
                        return; // End thread
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public enum State {
        INIT,
        RUNNING,
        PAUSED,
        STOPPED
    }

    @Data
    public static class Report {
        private int completedRuns;
        private int successRuns;
        private int refillTimes;
    }

    public interface Listener {
        void onSessionStopped();
    }
}
