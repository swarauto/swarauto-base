package com.swarauto.game.analyzer;

import com.swarauto.game.GameState;
import com.swarauto.game.director.event.AnalyzerEvent;
import com.swarauto.game.indicator.Indicator;
import com.swarauto.util.ImageUtil;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static com.swarauto.game.indicator.Indicator.battleEndIndicator;
import static com.swarauto.game.indicator.Indicator.networkDelayIndicator;
import static com.swarauto.game.indicator.Indicator.networkUnstableIndicator;
import static com.swarauto.game.indicator.Indicator.noEnergyIndicator;
import static com.swarauto.game.indicator.Indicator.otherRewardIndicator;
import static com.swarauto.game.indicator.Indicator.replayBattleIndicator;
import static com.swarauto.game.indicator.Indicator.reviveIndicator;
import static com.swarauto.game.indicator.Indicator.startBattleIndicator;
import static com.swarauto.util.ImageUtil.loadImageInGrey;

public abstract class Analyzer {
    protected static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);
    protected final EventBus eventBus;
    protected Map<String, Mat> cachedIndicatorImages = new HashMap<String, Mat>();

    public Analyzer() {
        this.eventBus = EventBus.builder()
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .build();
    }

    public static Analyzer newInstance(Class<? extends Analyzer> clazz) {
        Analyzer instance = null;
        try {
            Constructor<? extends Analyzer> constructor = clazz.getConstructor();
            instance = constructor.newInstance();
        } catch (Exception e) {
            if (clazz == null) {
                throw new IllegalArgumentException("Null Analyzer class");
            } else {
                throw new IllegalArgumentException("Can't construct Analyzer class: " + clazz.getSimpleName()
                        + " Exception: " + e.getMessage());
            }
        }
        return instance;
    }

    public void onDestroy() {
        for (Mat item : cachedIndicatorImages.values()) {
            item.release();
        }
    }

    public void registerEventListener(Object listener) {
        eventBus.register(listener);
    }

    public void unregisterEventListener(Object listener) {
        eventBus.unregister(listener);
    }

    public GameState detectGameState(String screenFilePath, String indicatorsDir) {
        eventBus.post(AnalyzerEvent.builder().id(AnalyzerEvent.START_DETECTING_GAME_STATE).build());

        // Check indicators dir exist
        if (indicatorsDir == null || !new File(indicatorsDir).exists()) {
            eventBus.post(AnalyzerEvent.builder()
                    .id(AnalyzerEvent.EXCEPTION_OCCURRED)
                    .data(new IllegalArgumentException("Indicators dir not exist"))
                    .build());
            return GameState.UNKNOWN;
        }

        GameState gameState = GameState.UNKNOWN;

        long startTime = System.currentTimeMillis();
        // Load screen shot one time
        Mat sourceGrey = loadImageInGrey(screenFilePath);
        LOG.info("detectGameState() load screen shot. (cost {}ms)", System.currentTimeMillis() - startTime);
        if (sourceGrey != null) {
            startTime = System.currentTimeMillis();
            gameState = detectGameState(sourceGrey, indicatorsDir);
            LOG.info("detectGameState() matching indicators. (cost {}ms)", System.currentTimeMillis() - startTime);
            sourceGrey.release();
        }

        eventBus.post(AnalyzerEvent.builder()
                .id(AnalyzerEvent.GAME_STATE_DETECTED)
                .data(gameState)
                .build());

        return gameState;
    }

    protected GameState detectGameState(Mat sourceGrey, String indicatorsDir) {
        GameState gameState = GameState.UNKNOWN;

        if (containIndicators(sourceGrey, indicatorsDir, startBattleIndicator)) {
            gameState = GameState.START_BATTLE;
        } else if (containIndicators(sourceGrey, indicatorsDir, replayBattleIndicator)) {
            gameState = GameState.REPLAY_BATTLE_CONFIRMATION;
        } else if (containIndicators(sourceGrey, indicatorsDir, otherRewardIndicator)) {
            gameState = GameState.OTHER_REWARD;
        } else if (containIndicators(sourceGrey, indicatorsDir, noEnergyIndicator)) {
            gameState = GameState.NOT_ENOUGH_ENERGY;
        } else if (containIndicators(sourceGrey, indicatorsDir, battleEndIndicator)) {
            gameState = GameState.BATTLE_ENDED;
        } else if (containIndicators(sourceGrey, indicatorsDir, reviveIndicator)) {
            gameState = GameState.BATTLE_ENDED_FAIL;
        } else if (containIndicators(sourceGrey, indicatorsDir, networkDelayIndicator)) {
            gameState = GameState.NETWORK_DELAY;
        } else if (containIndicators(sourceGrey, indicatorsDir, networkUnstableIndicator)) {
            gameState = GameState.UNSTABLE_NETWORK;
// Temporary disable auto/manual attack
//        } else if (containIndicators(sourceGrey, indicatorsDir, inBattleIndicator)) {
//            gameState = GameState.IN_BATTLE;
//        } else if (containIndicators(sourceGrey, indicatorsDir, manualAttackIndicator)) {
//            gameState = GameState.BATTLE_MANUAL;
        }

        return gameState;
    }

    protected final boolean containIndicators(Mat sourceGrey, String indicatorsDir, Indicator... indicators) {
        if (indicators == null || indicators.length == 0) return false;

        boolean contained = false;
        for (Indicator indicator : indicators) {
            String filePath = getIndicatorFilePath(indicator, indicatorsDir);
            if (filePath != null) {

                // Get from cache
                Mat indicatorImage;
                if (cachedIndicatorImages.containsKey(filePath)) {
                    indicatorImage = cachedIndicatorImages.get(filePath);
                } else {
                    indicatorImage = loadImageInGrey(filePath);
                    cachedIndicatorImages.put(filePath, indicatorImage);
                }

                if (ImageUtil.contains(sourceGrey, indicatorImage, 98) != null) {
                    contained = true;
                    break;
                }
            }
        }

        return contained;
    }

    private String getIndicatorFilePath(Indicator indicator, String indicatorsDir) {
        File file = new File(indicatorsDir + "/" + indicator.name());
        if (file.exists()) return file.getAbsolutePath();
        else return null;
    }
}
