package com.swarauto.ui.main;

import com.swarauto.game.GameState;
import com.swarauto.game.profile.CommonConfig;
import com.swarauto.game.profile.Profile;
import com.swarauto.game.session.AutoSession;
import com.swarauto.ui.BaseView;

import java.util.List;

public interface MainView extends BaseView {
    void renderProfileList(List<String> profileList);

    void renderSessionTypeList(List<String> sessionTypeList);

    void renderCommonConfig(CommonConfig commonConfig);

    void renderUIForSessionState(AutoSession.State sessionState);

    void renderSessionReport(AutoSession.Report report);

    void renderGameState(GameState gameState);

    void renderError(String error);

    void renderMessage(String message);

    CommonConfig getCommonConfig();

    void renderProfileCheckerError(String error);

    void showInfoDialog(AutoSession.Report sessionReport);

    void renderSelectedProfile(Profile profile);

    void showSoldRunes();
}
