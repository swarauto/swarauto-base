package com.swarauto.game.profile;

import org.junit.Assert;
import org.junit.Test;

public class ProfileCheckerTest {
    private static final String PROFILES_FOLDER = "src/test/resources/profiles";

    @Test
    public void testRefillSetup() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);

        // Not setup
        Profile profile = profileManager.loadProfile("testProfile");
        Assert.assertFalse(ProfileChecker.isAutoRefillSetup(profile));
        Assert.assertTrue(0 == ProfileChecker.checkAll(profile));

        // OK
        profile = profileManager.loadProfile("profileWithRefillSetup");
        Assert.assertTrue(ProfileChecker.isAutoRefillSetup(profile));
        Assert.assertTrue(ProfileChecker.FLAG_AUTO_REFILL == ProfileChecker.checkAll(profile));
    }

    @Test
    public void testNetworkProblemSetup() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);

        // Not setup
        Profile profile = profileManager.loadProfile("testProfile");
        Assert.assertFalse(ProfileChecker.isNetworkProblemSetup(profile));
        Assert.assertTrue(0 == ProfileChecker.checkAll(profile));

        // OK
        profile = profileManager.loadProfile("profileWithNetworkProblemSetup");
        Assert.assertTrue(ProfileChecker.isNetworkProblemSetup(profile));
        Assert.assertTrue(ProfileChecker.FLAG_NETWORK_PROBLEM == ProfileChecker.checkAll(profile));
    }

    @Test
    public void testCairosDungeonSetup() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);

        // OK
        Profile profile = profileManager.loadProfile("profileWithCairosDungeonSetup");
        Assert.assertTrue(ProfileChecker.isRuneFarmingSetup(profile));
        Assert.assertEquals(ProfileChecker.FLAG_NETWORK_PROBLEM | ProfileChecker.FLAG_RUNE_FARMING, ProfileChecker.checkAll(profile));
    }
}
