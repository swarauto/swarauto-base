package com.swarauto.game.profile;

import com.swarauto.game.indicator.Indicator;
import com.swarauto.util.Point;
import com.swarauto.util.Rectangle;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Data
public class Profile {
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    transient private String path;

    @Setter(AccessLevel.PACKAGE)
    private String id;

    private String name;

    // Required
    private Point startBattle;
    private Point confirmNetworkDelay;
    private Point resendBattleInfo;
    private Point reviveNo;
    private Point sellRuneLocation;
    private Point sellRuneConfirmation;
    private Point getRuneLocation;
    private Point getRewardLocation;
    private Point replayBattle;

    // Rift
    private Point sellGemLocation;
    private Point sellStoneConfirmation;
    private Point getGemLocation;

    // Refill
    private Point rechargeEnergyYes;
    private Point rechargeEnergyNo;
    private Point rechargeEnergy;
    private Point confirmRechargeEnergy;
    private Point ackRechargeEnergyOk;
    private Point closeRechargeEnergy;

    // Rune picking
    private Rectangle rareLevelBox;
    private Rectangle grindstoneStatBox;

    // Optional
    private Point enableAutoMode;
    private Point rechargeCrysNo;

    Profile() {

    }

    public File getIndicatorFile(Indicator indicator) {
        return new File(getIndicatorsDir() + "/" + indicator.name());
    }

    public String getIndicatorsDir() {
        if (path == null) throw new IllegalStateException("Profile not initialized. Path not set");
        return new File(path + "/").getAbsolutePath();
    }
}
