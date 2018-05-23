package com.swarauto.game.director;

import com.swarauto.Settings;
import com.swarauto.dependencies.DependenciesRegistry;
import com.swarauto.game.GameState;
import com.swarauto.game.GameStatus;
import com.swarauto.game.director.event.DirectorEvent;
import com.swarauto.game.profile.CommonConfig;
import com.swarauto.game.profile.Profile;
import com.swarauto.util.CommandUtil;
import com.swarauto.util.ImageUtil;
import com.swarauto.util.OcrUtil;
import com.swarauto.util.Point;
import com.swarauto.util.Rectangle;
import lombok.Getter;

import java.io.File;

import static com.swarauto.game.indicator.Indicator.fiveStarRuneIndicator;
import static com.swarauto.game.indicator.Indicator.sixStarRuneIndicator;

public abstract class BattleDirector extends Director {
    protected final CommandUtil commandUtil;
    protected final OcrUtil ocrUtil;
    protected final Settings settings;

    @Getter
    protected int availableRefillTime;
    @Getter
    protected int availableRuns;

    public BattleDirector() {
        this.commandUtil = DependenciesRegistry.commandUtil;
        this.ocrUtil = DependenciesRegistry.ocrUtil;
        this.settings = DependenciesRegistry.settings;
    }

    @Override
    public void setProfile(Profile profile) {
        super.setProfile(profile);
    }

    @Override
    public void setCommonConfig(CommonConfig commonConfig) {
        super.setCommonConfig(commonConfig);
        availableRefillTime = commonConfig.getMaxRefills();
        availableRuns = commonConfig.getMaxRuns();
    }

    @Override
    protected boolean direct(GameStatus gameStatus) {
        final GameState gameState = gameStatus.getGameState();
        if (gameState == GameState.BATTLE_MANUAL) {
            enableAutoAttackMode();
            return true;
        } else if (gameState == GameState.BATTLE_ENDED) {
            ackBattleResult();
            return true;
        } else if (gameState == GameState.BATTLE_ENDED_FAIL) {
            ackBattleResultFailure();
            return true;
        } else if (gameState == GameState.RUNE_REWARD) {
            proceedRuneReward(gameStatus);
            return true;
//    } else if (gameState == GameState.GEM_REWARD) {
//      proceedGemReward(gameStatus);
//      return true;
        } else if (gameState == GameState.OTHER_REWARD) {
            proceedOtherReward();
            return true;
        } else if (gameState == GameState.REPLAY_BATTLE_CONFIRMATION) {
            replayBattle();
            return true;
        } else if (gameState == GameState.START_BATTLE) {
            startBattle();
            return true;
        } else if (gameState == GameState.SELL_RUNE_CONFIRMATION) {
            confirmSellRune();
            return true;
        } else if (gameState == GameState.SELL_STONE_CONFIRMATION) {
            confirmSellStone();
            return true;
        } else if (gameState == GameState.NOT_ENOUGH_ENERGY) {
            proceedNotEnoughEnergy();
            return true;
        } else if (gameState == GameState.NETWORK_DELAY) {
            confirmNetworkDelay();
            return true;
        } else if (gameState == GameState.UNSTABLE_NETWORK) {
            resendBattleInfo();
            return true;
        } else if (gameState == GameState.IN_BATTLE) {
            wait4Battle();
            return true;
        } else if (gameState == GameState.NO_CRYS) {
            notRefillCrys();
            return true;
        } else if (gameState == GameState.UNKNOWN) {
            LOG.info("UNKNOWN state detected. Waiting for a while before retrying...");
            // Log unknown situation where directive can't handle
//      commandUtil.screenLog(gameStatus, new File("unknownStates"));
            sleep(2000);
            return true;
        }
        return false;
    }

    /**
     * Acknowledge the battle result by click somewhere on the screen
     */
    protected void ackBattleResult() {
        progressMessage("Ending battle...");
        commandUtil.tapScreenCenter();
        commandUtil.tapScreenCenter();
        sleep(1000);
        commandUtil.tapScreenCenter();

        availableRuns--;
    }

    protected void ackBattleResultFailure() {
        progressMessage("Battle fail!!! Not revive...");
        tapScreen(profile.getReviveNo());

        sleep(100);
        tapScreen(new Point(400, 900));

        availableRuns--;
    }

//  protected void collectStone(final GameStatus gameStatus) throws IOException {
//    boolean pickRune = commonConfig.isPickAllRunes();
//    if (!pickRune) {
//      pickRune = applyStoneFilter(gameStatus);
//    }
//    if (pickRune) {
//      progressMessage("Collecting stone...");
//      tapScreen(profile.getGetGemLocation());
//      if (commonConfig.isRuneStoneLogging()) {
//        commandUtil.screenLog(gameStatus, new File("runeLog"));
//      }
//    } else {
//      // Rune will be sold if non of rules are matching
//      sellStone(gameStatus);
//    }
//  }

    protected void confirmSellRune() {
        progressMessage("Confirm to sell rune...");
        tapScreen(profile.getSellRuneConfirmation());
    }

    protected void confirmSellStone() {
        progressMessage("Confirm to sell stone...");
        tapScreen(profile.getSellStoneConfirmation());
    }

    /**
     * Enable auto attack mode by clicking on play icon (third button) at bottom left
     */
    protected void enableAutoAttackMode() {
        progressMessage("Enabling auto mode...");
        tapScreen(profile.getEnableAutoMode());
    }

    protected void notRefillCrys() {
        progressMessage("Not refill crys, wait for energy instead...");
        tapScreen(profile.getRechargeCrysNo());
        availableRefillTime = 0;
    }

//  protected void proceedGemReward(GameStatus gameStatus) {
//    if (commonConfig.isSellAllRunes()) {
//      sellStone(gameStatus);
//    } else {
//      try {
//        collectStone(gameStatus);
//      } catch (final IOException ex) {
//        throw new RuntimeException("Error when collect stone", ex);
//      }
//    }
//  }

    protected void proceedOtherReward() {
        progressMessage("Collecting rewards...");
        tapScreen(profile.getGetRewardLocation());
    }

    protected void proceedRuneReward(GameStatus gameStatus) {
        boolean shouldKeep = true;

        if (commonConfig.isSellAllRunes()) {
            // SELL ALL
            LOG.info("CommonConfig: SELL ALL RUNES");
            shouldKeep = false;
        } else if (commonConfig.isPickAllRunes()) {
            // GET ALL
            LOG.info("CommonConfig: GET ALL RUNES");
            shouldKeep = true;
        } else if (commonConfig.isSelectivePickRunes()) {
            // SELECTIVE GET
            LOG.info("Selective picking runes...");
            shouldKeep = shouldKeepRune(gameStatus);
        }

        if (shouldKeep) {
            progressMessage("Collecting rune...");
            tapScreen(profile.getGetRuneLocation());
        } else {
            sellRune(gameStatus);
        }
    }

    private boolean shouldKeepRune(final GameStatus gameStatus) {
        // If cant recognize, dont sell
        if (!ocrUtil.isInitialized()) {
            LOG.info("OCR Lib not init. GET RUNE");
            return true;
        }

        boolean shouldKeep = true;

        // Rarity
        Rectangle rarityArea = profile.getRareLevelBox();
        if (rarityArea != null) {
            CommonConfig.RunePickingRarity minRarity = commonConfig.getRunePickingMinRarity();
            LOG.info("RarityArea: {},{},{},{}. Checking for MinRarity: {}",
                    rarityArea.x, rarityArea.y, rarityArea.width, rarityArea.height,
                    minRarity.getText());
            switch (minRarity) {
                case RARITY_ALL:
                    LOG.info("MinRarity == RARITY_ALL. GET RUNE");
                    shouldKeep = true;
                    break;
                default: {
                    final String rareLevel = ocrUtil.text(new File(gameStatus.getScreenFile()), profile.getRareLevelBox());
                    CommonConfig.RunePickingRarity rarity = CommonConfig.RunePickingRarity.byRarity(rareLevel);
                    LOG.info("Rarity OCR text = {}", rareLevel);

                    if (rarity == null) {
                        LOG.info("Unknown RARITY -> GET RUNE");
                        shouldKeep = true;
                    } else {
                        shouldKeep = minRarity.getValue() <= rarity.getValue();
                        LOG.info("Compare value: MinRarity = {}. Actual Rarity = {} -> {}",
                                minRarity.getValue(), rarity.getValue(), shouldKeep ? "GET RUNE" : "SELL RUNE");
                    }
                    break;
                }
            }
        }

        // If keep, then detect grade
        if (shouldKeep && profile.getIndicatorFile(sixStarRuneIndicator).exists() && profile.getIndicatorFile(fiveStarRuneIndicator).exists()) {
            LOG.info("GradeIndicator exists. Checking for rune star...");
            switch (commonConfig.getRunePickingMinGrade()) {
                case GRADE_ALL:
                    shouldKeep = true;
                    break;
                case GRADE_6STAR:
                    shouldKeep = ImageUtil.contains(gameStatus.getScreenFile(), profile.getIndicatorFile(sixStarRuneIndicator).getAbsolutePath(), 98) != null;
                    break;
                case GRADE_5STAR_AND_ABOVE:
                    shouldKeep = ImageUtil.contains(gameStatus.getScreenFile(), profile.getIndicatorFile(fiveStarRuneIndicator).getAbsolutePath(), 98) != null
                            || ImageUtil.contains(gameStatus.getScreenFile(), profile.getIndicatorFile(sixStarRuneIndicator).getAbsolutePath(), 98) != null;
                    break;
            }
        }

        return shouldKeep;
    }

    protected void progressMessage(final String message, final Object... args) {
        eventBus.post(DirectorEvent.builder()
                .id(DirectorEvent.LOGGING)
                .data(String.format(message, args))
                .build());
    }

    protected void refillEnergy() {
        eventBus.post(DirectorEvent.builder()
                .id(DirectorEvent.REFILLING_ENERGY)
                .build());
        progressMessage("Refilling energy...");
        // On screen of not enough energy, select YES on to recharges energy
        tapScreen(profile.getRechargeEnergyYes());
        sleep(500);
        // On shop screen, select energy
        tapScreen(profile.getRechargeEnergy());
        sleep(500);
        // On shop screen, confirm to purchase energy with 30 crystals
        tapScreen(profile.getConfirmRechargeEnergy());
        sleep(2000);
        // On shop screen, click OK confirm purchase successful
        tapScreen(profile.getAckRechargeEnergyOk());
        sleep(500);
        // Close the shop screen
        tapScreen(profile.getCloseRechargeEnergy());
    }

    protected void replayBattle() {
        if (availableRuns <= 0) {
            eventBus.post(DirectorEvent.builder()
                    .id(DirectorEvent.NO_MORE_RUN)
                    .build());
        } else {
            progressMessage("Replaying battle...");
            tapScreen(profile.getReplayBattle());
        }
    }

    /**
     * Sell the rune on battle result screen.
     */
    protected void sellRune(final GameStatus gameStatus) {
        progressMessage("Selling rune...");
        tapScreen(profile.getSellRuneLocation());

        if (commonConfig.isRecordSoldRunes()) {
            commandUtil.screenLog(gameStatus, new File(settings.getSoldRunesFolderPath()));
        }
    }

//  protected void sellStone(final GameStatus gameStatus) {
//    progressMessage("Selling stone...");
//    tapScreen(profile.getSellGemLocation());
//
//    if (commonConfig.isRuneStoneLogging()) {
//      commandUtil.screenLog(gameStatus, new File("runeLog", "sold"));
//    }
//  }

    protected void sleep(final long sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (final InterruptedException ex) {
            LOG.error("Could not sleep!!!", ex);
        }
    }

    protected void startBattle() {
        progressMessage("Starting new battle...");
        tapScreen(profile.getStartBattle());
        sleep(5000);
    }

    protected void wait4Battle() {
        sleep(10000);
    }

    protected void waitForEnergy() {
        progressMessage("Insuffience energy and waiting...");
        tapScreen(profile.getRechargeEnergyNo());

        // Wait for 10 minutes
        final long time = System.currentTimeMillis();
        final long timeToWait = time + 30 * 60 * 1000;
        while (true) {
            sleep(1000);
            final long remainingTime = timeToWait - System.currentTimeMillis();
            if (remainingTime <= 0) {
                break;
            }

            progressMessage(String.format("No enough energy, resume in % seconds", remainingTime));
        }
    }

//  private boolean applyStoneFilter(final GameStatus gameStatus) throws IOException {
//    if (!ocrUtil.isInitialized()) return true;
//
//    if (commonConfig.isPickSpdPercentGrind()) {
//      final String grindOptions = ocrUtil.text(new File(gameStatus.getScreenFile()), profile.getGrindstoneStatBox());
//      final boolean percentOption = grindOptions.contains("°/o") || grindOptions.contains("%");
//      final boolean spdOption = grindOptions.contains("SPD");
//      if (percentOption || spdOption) {
//        return true;
//      }
//    }
//    return false;
//  }

    private void confirmNetworkDelay() {
        progressMessage("Network delay!");
        tapScreen(profile.getConfirmNetworkDelay());
    }

    private void proceedNotEnoughEnergy() {
        if (availableRefillTime <= 0) {
            waitForEnergy();
        } else {
            refillEnergy();
            availableRefillTime--;
        }
    }

    private void resendBattleInfo() {
        progressMessage("Network unstable! resending information...");
        tapScreen(profile.getResendBattleInfo());
    }

    protected void tapScreen(Point point) {
        String tapX = String.valueOf(point.x + (int) (10 * (Math.random() - Math.random())));
        String tapY = String.valueOf(point.y + (int) (10 * (Math.random() - Math.random())));
        commandUtil.tapScreen(tapX, tapY);
    }
}
