package com.swarauto.ui.main;

import com.swarauto.Settings;
import com.swarauto.dependencies.DependenciesRegistry;
import com.swarauto.game.GameState;
import com.swarauto.game.GameStatus;
import com.swarauto.game.analyzer.Analyzer;
import com.swarauto.game.director.Director;
import com.swarauto.game.director.event.AnalyzerEvent;
import com.swarauto.game.director.event.DirectorEvent;
import com.swarauto.game.director.exp.ExpFarmingDirector;
import com.swarauto.game.profile.CommonConfig;
import com.swarauto.game.profile.Profile;
import com.swarauto.game.profile.ProfileChecker;
import com.swarauto.game.profile.ProfileManager;
import com.swarauto.game.session.AutoSession;
import com.swarauto.game.session.SessionType;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class MainPresenter implements AutoSession.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(MainPresenter.class);
    protected MainView view;
    protected MainModel model;
    protected AutoSession autoSession;
    protected ProfileManager profileManager;
    protected Settings settings;

    public MainPresenter() {
        profileManager = DependenciesRegistry.profileManager;
        settings = DependenciesRegistry.settings;
        model = new MainModel();
        model.loadData();
    }

    public void bindView(MainView view) {
        this.view = view;
    }

    public void refreshProfileList() {
        model.loadData();
        view.renderProfileList(model.getProfileOptions());
    }

    public void refreshSessionTypeList() {
        view.renderSessionTypeList(model.getSessionTypeNames());
    }

    public void refreshCommonConfig() {
        view.renderCommonConfig(profileManager.loadCommonConfig());
    }

    public void onBtnStartClicked() {
        // Check version
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (autoSession == null) {
                    if (startAuto()) {
                        view.renderUIForSessionState(autoSession.getState());
                    }
                } else {
                    switch (autoSession.getState()) {
                        case PAUSED:
                            autoSession.resume();
                            break;
                        case RUNNING:
                            autoSession.pause();
                            break;
                    }
                    view.renderUIForSessionState(autoSession.getState());
                }
            }
        }).start();
    }

    protected boolean startAuto() {
        if (model.getSelectedProfileId() == null) {
            view.renderError("Select Device config to start...");
            return false;
        }

        // Profile
        Profile profile = profileManager.loadProfile(model.getSelectedProfileId());
        if (profile == null) {
            view.renderError("Invalid Device config");
            return false;
        }

        // SessionType
        String sessionTypeName = model.getSelectedSessionTypeName();
        SessionType sessionType = null;
        try {
            sessionType = SessionType.valueOf(sessionTypeName);
        } catch (IllegalArgumentException e) {
            sessionType = null;
            view.renderError("Invalid SessionType");
            return false;
        }

        // Common config
        CommonConfig commonConfig = view.getCommonConfig();
        profileManager.saveCommonConfig(commonConfig);

        // Check ready to auto
        if (!checkProfileReadyToAuto(profile, sessionType, commonConfig)) {
            return false;
        }

        // Deleted record sold-runes
        deleteRecordSoldRunes();

        // Start session
        Analyzer analyzer = Analyzer.newInstance(sessionType.getAnalyzerClass());
        Director director = Director.newInstance(sessionType.getDirectorClass());
        autoSession = new AutoSession(this, analyzer, director);
        autoSession.setProfile(profile);
        autoSession.start();
        autoSession.getDirector().registerEventListener(this);
        autoSession.getAnalyzer().registerEventListener(this);

        return true;
    }

    private void deleteRecordSoldRunes() {
        File soldRunesDir = new File(settings.getSoldRunesFolderPath());
        soldRunesDir.mkdirs();

        File[] files = soldRunesDir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : soldRunesDir.listFiles()) {
                file.delete();
            }
        }
    }

    private boolean checkProfileReadyToAuto(Profile profile, SessionType sessionType, CommonConfig commonConfig) {
        int flags = ProfileChecker.checkAll(profile);

        if (!ProfileChecker.flagOn(ProfileChecker.FLAG_RUNE_FARMING, flags)) {
            view.renderProfileCheckerError("You need to setup your device first. Currently missing Auto Battle config");
            return false;
        }

        if (!ProfileChecker.flagOn(ProfileChecker.FLAG_NETWORK_PROBLEM, flags)) {
            view.renderProfileCheckerError("Missing config for Auto retry on network problem. Your runs will stop when network problem.");
        }

        if (sessionType.equals(SessionType.RIFT) && !ProfileChecker.flagOn(ProfileChecker.FLAG_RIFT, flags)) {
            view.renderProfileCheckerError("You need to setup your device first. Currently missing Rift config");
            return false;
        }

        if (sessionType.equals(SessionType.TOA) && !ProfileChecker.flagOn(ProfileChecker.FLAG_TOA, flags)) {
            view.renderProfileCheckerError("You need to setup your device first. Currently missing TOA config");
            return false;
        }

        if (commonConfig.getMaxRefills() > 0 && !ProfileChecker.flagOn(ProfileChecker.FLAG_AUTO_REFILL, flags)) {
            view.renderProfileCheckerError("You need to setup your device first. Currently missing Auto Refill config");
            return false;
        }

        if (commonConfig.isSelectivePickRunes() && !ProfileChecker.flagOn(ProfileChecker.FLAG_RUNE_PICKING, flags)) {
            view.renderProfileCheckerError("For selective picking runes, you need to setup device first. Otherwise, please select Pick all or Sell all.");
            return false;
        }

        return true;
    }

    public void onBtnStopClicked() {
        if (autoSession != null) {
            autoSession.stop();
            autoSession.getDirector().unregisterEventListener(this);
            autoSession.getAnalyzer().unregisterEventListener(this);
            autoSession.getAnalyzer().onDestroy();
        }
    }

    public void onProfileSelected(int selectedIndex) {
        model.setSelectedProfileNameIndex(selectedIndex);

        Profile profile = profileManager.loadProfile(model.getSelectedProfileId());
        if (profile != null) {
            view.renderSelectedProfile(profile);
        }
    }

    public void onProfileSelected(String id) {
        model.setSelectedProfileId(id);

        Profile profile = profileManager.loadProfile(model.getSelectedProfileId());
        if (profile != null) {
            view.renderSelectedProfile(profile);
        }
    }

    public void onSessionTypeSelected(int selectedIndex) {
        model.setSelectedSessionTypeNameIndex(selectedIndex);
        String sessionTypeName = model.getSelectedSessionTypeName();
        SessionType sessionType = SessionType.valueOf(sessionTypeName);
        Director director = Director.newInstance(sessionType.getDirectorClass());
        CommonConfig commonConfig = profileManager.loadCommonConfig();

        // Special update UI for EXP farming (max runs, sell runes)
        if (director instanceof ExpFarmingDirector) {
            commonConfig.setMaxRuns(((ExpFarmingDirector) director).getNeededRuns());
            commonConfig.setRunePickingMinGrade(CommonConfig.RunePickingGrade.GRADE_NONE);
            commonConfig.setRunePickingMinRarity(CommonConfig.RunePickingRarity.RARITY_NONE);
        } else {
            // Default dont sell runes
            commonConfig.setRunePickingMinGrade(CommonConfig.RunePickingGrade.GRADE_ALL);
            commonConfig.setRunePickingMinRarity(CommonConfig.RunePickingRarity.RARITY_ALL);
        }
        view.renderCommonConfig(commonConfig);
    }

    @Override
    public void onSessionStopped() {
        view.renderMessage("Stopped");

        autoSession = null;
        view.renderUIForSessionState(AutoSession.State.INIT);
    }

    @Subscribe
    public void onAnalyzerEvent(AnalyzerEvent event) {
        switch (event.getId()) {
            case AnalyzerEvent.START_DETECTING_GAME_STATE:
                view.renderMessage("Detecting...");
                break;
            case AnalyzerEvent.GAME_STATE_DETECTED: {
                GameState gameState = (GameState) event.getDataAt(0);
                if (gameState != null) {
                    view.renderMessage("Matched: " + gameState.name());
                    view.renderGameState(gameState);
                }
                break;
            }
            case AnalyzerEvent.EXCEPTION_OCCURRED: {
                Exception exception = (Exception) event.getDataAt(0);
                if (exception != null) {
                    view.renderError("Exception: " + exception.getMessage());
                    LOG.error("AnalyzerEvent.EXCEPTION_OCCURRED", exception);
                }
                break;
            }
        }
    }

    @Subscribe
    public void onDirectorEvent(DirectorEvent event) {
        switch (event.getId()) {
            case DirectorEvent.DIRECTION_ISSUED: {
                GameStatus gameStatus = (GameStatus) event.getDataAt(0);
                if (gameStatus != null) {
                    view.renderMessage("Done direct for " + gameStatus.getGameState().name());
                }
                view.renderSessionReport(autoSession.getReport());
                break;
            }
            case DirectorEvent.REFILLING_ENERGY:
                view.renderMessage("Trying to refill energy...");
                break;
            case DirectorEvent.NO_MORE_RUN:
                view.renderMessage("No more run");
                autoSession.stop();
                break;
            case DirectorEvent.EXCEPTION_OCCURRED: {
                Exception exception = (Exception) event.getDataAt(0);
                if (exception != null) {
                    view.renderError("Exception: " + exception.getMessage());
                    LOG.error("DirectorEvent.EXCEPTION_OCCURRED", exception);
                }
                break;
            }
        }
    }

    public void onBtnInfoClicked() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                view.showInfoDialog(autoSession != null ? autoSession.getReport() : null);
            }
        }).start();
    }

    public void onBtnShowSoldRunesClicked() {
        view.showSoldRunes();
    }
}
