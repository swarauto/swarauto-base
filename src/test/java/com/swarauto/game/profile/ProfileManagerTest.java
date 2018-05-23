package com.swarauto.game.profile;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class ProfileManagerTest {
    private static final String PROFILES_FOLDER = "src/test/resources/profiles";

    @Test
    public void testSetLocation() {
        ProfileManager profileManager = new ProfileManager();
        try {
            profileManager.setLocation(null);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }

        try {
            profileManager.setLocation("");
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }

        profileManager.setLocation(PROFILES_FOLDER);
    }

    @Test
    public void testCreateEmptyProfile() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);
        Profile profile = profileManager.createEmptyProfile();
        String profilePath = profile.getPath();
        Assert.assertNotNull(profile.getId());
        Assert.assertNotNull(profile.getName());
        Assert.assertTrue(profile.getId().equals(profile.getName()));

        Assert.assertNotNull(profilePath);
        File profileDir = new File(profilePath);
        Assert.assertTrue(profileDir.exists());

        Assert.assertNull(profile.getSellGemLocation());
        Assert.assertNull(profile.getRareLevelBox());

        profileManager.deleteProfile(profile.getId());
        Assert.assertFalse(profileDir.exists());
    }

    @Test
    public void testStates() {
        ProfileManager profileManager = new ProfileManager();

        // location not set
        try {
            profileManager.loadProfile("test");
            Assert.fail("Should throw IllegalStateException");
        } catch (IllegalStateException ignored) {
        }

        profileManager.setLocation(".");
        profileManager.loadProfile("test");
    }

    @Test
    public void testGetListProfileIds() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);

        File profilesDir = new File(PROFILES_FOLDER);
        List<String> ids = profileManager.getProfileIds();
        Assert.assertEquals(profilesDir.listFiles().length, ids.size());
    }

    @Test
    public void testLoad() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);

        // Load invalid name
        try {
            profileManager.loadProfile(null);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            profileManager.loadProfile("");
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }

        // Load non exist profile
        Assert.assertNull(profileManager.loadProfile("blahblah"));

        // Load test profile (empty)
        Profile profile = profileManager.loadProfile("testProfileEmpty");
        Assert.assertNotNull(profile);
        Assert.assertEquals("testProfileEmpty", profile.getId());
        Assert.assertEquals(new File(PROFILES_FOLDER + "/testProfileEmpty").getAbsolutePath(), profile.getPath());

        // Load test profile
        profile = profileManager.loadProfile("testProfile");
        Assert.assertNotNull(profile);
        Assert.assertEquals("testProfile", profile.getName());
        Assert.assertEquals(new File(PROFILES_FOLDER + "/testProfile").getAbsolutePath(), profile.getPath());
    }

    @Test
    public void testSave() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);

        // Null check
        try {
            profileManager.saveProfile(null);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }

        Profile profile = profileManager.createEmptyProfile();

        // Valid infos
        String profileName = "testNewProfile";
        profile.setName(profileName);

        profileManager.saveProfile(profile);

        // Saved profile has path
        Assert.assertEquals(new File(PROFILES_FOLDER + "/" + profile.getId()).getAbsolutePath(), profile.getPath());

        // Load profiles
        Profile loadedProfile = profileManager.loadProfile(profile.getId());
        Assert.assertEquals(profileName, loadedProfile.getName());

        // Clean up
        profileManager.deleteProfile(profile.getId());
    }

    @Test
    public void testCommonConfig() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);

        // Load non-exist
        CommonConfig commonConfig = profileManager.loadCommonConfig();
        Assert.assertNotNull(commonConfig);
        Assert.assertEquals(0, commonConfig.getMaxRefills());
        Assert.assertEquals(Integer.MAX_VALUE, commonConfig.getMaxRuns());
        Assert.assertEquals(true, commonConfig.isPickAllRunes());
        Assert.assertEquals(false, commonConfig.isSelectivePickRunes());
        Assert.assertEquals(false, commonConfig.isSellAllRunes());
        Assert.assertEquals(true, commonConfig.isRecordSoldRunes());

        // Save
        commonConfig.setMaxRefills(2);
        commonConfig.setRecordSoldRunes(false);
        profileManager.saveCommonConfig(commonConfig);
        File commonConfigFile = profileManager.getCommonConfigFile();
        Assert.assertTrue(commonConfigFile.exists());

        // Load from file
        commonConfig = profileManager.loadCommonConfig();
        Assert.assertNotNull(commonConfig);
        Assert.assertEquals(2, commonConfig.getMaxRefills());
        Assert.assertEquals(Integer.MAX_VALUE, commonConfig.getMaxRuns());
        Assert.assertEquals(true, commonConfig.isPickAllRunes());
        Assert.assertEquals(false, commonConfig.isSelectivePickRunes());
        Assert.assertEquals(false, commonConfig.isSellAllRunes());
        Assert.assertEquals(false, commonConfig.isRecordSoldRunes());

        commonConfigFile.delete();
    }
}
