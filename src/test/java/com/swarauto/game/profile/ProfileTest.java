package com.swarauto.game.profile;

import com.swarauto.game.indicator.Indicator;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class ProfileTest {
    private static final String PROFILES_FOLDER = "src/test/resources/profiles";

    @Test
    public void testGetIndicator() {
        ProfileManager profileManager = new ProfileManager();
        profileManager.setLocation(PROFILES_FOLDER);

        // Create empty profile
        Profile profile = profileManager.createEmptyProfile();
        File file = profile.getIndicatorFile(Indicator.fiveStarRuneIndicator);
        Assert.assertNotNull(file);
        Assert.assertEquals(Indicator.fiveStarRuneIndicator.name(), file.getName());
        profileManager.deleteProfile(profile.getId());

        // Load empty profile
        profile = profileManager.loadProfile("testProfileEmpty");
        file = profile.getIndicatorFile(Indicator.fiveStarRuneIndicator);
        Assert.assertNotNull(file);
        Assert.assertEquals(Indicator.fiveStarRuneIndicator.name(), file.getName());

        // Load profile
        profile = profileManager.loadProfile("testProfile");
        file = profile.getIndicatorFile(Indicator.fiveStarRuneIndicator);
        Assert.assertNotNull(file);
        Assert.assertEquals(Indicator.fiveStarRuneIndicator.name(), file.getName());
    }
}
