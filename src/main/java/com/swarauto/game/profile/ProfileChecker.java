package com.swarauto.game.profile;

import static com.swarauto.game.indicator.Indicator.battleEndIndicator;
import static com.swarauto.game.indicator.Indicator.confirmSellRuneIndicator;
import static com.swarauto.game.indicator.Indicator.networkDelayIndicator;
import static com.swarauto.game.indicator.Indicator.networkUnstableIndicator;
import static com.swarauto.game.indicator.Indicator.otherRewardIndicator;
import static com.swarauto.game.indicator.Indicator.replayBattleIndicator;
import static com.swarauto.game.indicator.Indicator.reviveIndicator;
import static com.swarauto.game.indicator.Indicator.riftBattleEndIndicator;
import static com.swarauto.game.indicator.Indicator.runeRewardIndicator;
import static com.swarauto.game.indicator.Indicator.startBattleIndicator;
import static com.swarauto.game.indicator.Indicator.toaDefeatedIndicator;
import static com.swarauto.game.indicator.Indicator.toaNextStageIndicator;

public class ProfileChecker {
    public static final int FLAG_NETWORK_PROBLEM = 0x00000001;
    public static final int FLAG_AUTO_REFILL = 0x00000001 << 1;
    public static final int FLAG_RUNE_PICKING = 0x00000001 << 2;
    public static final int FLAG_RUNE_FARMING = 0x00000001 << 3;
    public static final int FLAG_TOA = 0x00000001 << 4;
    public static final int FLAG_RIFT = 0x00000001 << 5;

    public static boolean flagOn(int flag, int flags) {
        return (flag & flags) > 0;
    }

    public static int checkAll(Profile profile) {
        int ret = 0x00000000;

        if (isNetworkProblemSetup(profile)) ret |= FLAG_NETWORK_PROBLEM;
        if (isAutoRefillSetup(profile)) ret |= FLAG_AUTO_REFILL;
        if (isRunePickingSetup(profile)) ret |= FLAG_RUNE_PICKING;
        if (isRuneFarmingSetup(profile)) ret |= FLAG_RUNE_FARMING;
        if (isTOASetup(profile)) ret |= FLAG_TOA;
        if (isRiftSetup(profile)) ret |= FLAG_RIFT;

        return ret;
    }

    public static boolean isNetworkProblemSetup(Profile profile) {
        if (profile.getConfirmNetworkDelay() == null
                || profile.getResendBattleInfo() == null
                || !profile.getIndicatorFile(networkDelayIndicator).exists()
                || !profile.getIndicatorFile(networkUnstableIndicator).exists()) {
            return false;
        }

        return true;
    }

    public static boolean isAutoRefillSetup(Profile profile) {
        if (profile.getRechargeEnergyYes() == null
                || profile.getRechargeEnergyNo() == null
                || profile.getRechargeEnergy() == null
                || profile.getConfirmRechargeEnergy() == null
                || profile.getAckRechargeEnergyOk() == null
                || profile.getCloseRechargeEnergy() == null) {
            return false;
        }

        return true;
    }

    public static boolean isRunePickingSetup(Profile profile) {
        if (profile.getRareLevelBox() == null
//                || !profile.getIndicatorFile(sixStarRuneIndicator).exists()
//                || !profile.getIndicatorFile(fiveStarRuneIndicator).exists()
                ) {
            return false;
        }

        return true;
    }

    public static boolean isRuneFarmingSetup(Profile profile) {
        if (!isNetworkProblemSetup(profile)) return false;

        // Command engine
        if (profile.getStartBattle() == null
                || profile.getReviveNo() == null
                || profile.getSellRuneLocation() == null
                || profile.getSellRuneConfirmation() == null
                || profile.getGetRuneLocation() == null
                || profile.getGetRewardLocation() == null
                || profile.getReplayBattle() == null) {
            return false;
        }

        // Indicators
        if (!profile.getIndicatorFile(startBattleIndicator).exists()
                || !profile.getIndicatorFile(reviveIndicator).exists()
                || !profile.getIndicatorFile(battleEndIndicator).exists()
                || !profile.getIndicatorFile(runeRewardIndicator).exists()
                || !profile.getIndicatorFile(confirmSellRuneIndicator).exists()
                || !profile.getIndicatorFile(otherRewardIndicator).exists()
                || !profile.getIndicatorFile(replayBattleIndicator).exists()) {
            return false;
        }

        return true;
    }

    public static boolean isTOASetup(Profile profile) {
        if (!isRuneFarmingSetup(profile)) return false;

        if (!profile.getIndicatorFile(toaDefeatedIndicator).exists()
                || !profile.getIndicatorFile(toaNextStageIndicator).exists()) {
            return false;
        }

        return true;
    }

    public static boolean isRiftSetup(Profile profile) {
        if (!isRuneFarmingSetup(profile)) return false;

        if (!profile.getIndicatorFile(riftBattleEndIndicator).exists()) {
            return false;
        }

        return true;
    }
}
