package com.swarauto.game.director.toa;

import com.swarauto.game.GameState;
import com.swarauto.game.GameStatus;
import com.swarauto.game.director.BattleDirector;
import com.swarauto.game.director.event.DirectorEvent;
import com.swarauto.game.profile.Profile;

public class AutoToa extends BattleDirector {
    @Override
    public void setProfile(Profile profile) {
        super.setProfile(profile);

        // Override profile config
        availableRuns = Integer.MAX_VALUE;
    }

    protected boolean direct(GameStatus gameStatus) {
        final GameState gameState = gameStatus.getGameState();

        switch (gameState) {
            case TOA_NEXT_STAGE_CONFIRMATION:
                tapScreen(profile.getReplayBattle()); // Use same coordinate as dungeon
                return true;
            case TOA_REPLAY_STAGE_CONFIRMATION:
                // When failed, stop
                eventBus.post(DirectorEvent.builder()
                        .id(DirectorEvent.NO_MORE_RUN)
                        .build());
                availableRuns = 0;
                return true;
            default:
                return super.direct(gameStatus);
        }
    }
}
