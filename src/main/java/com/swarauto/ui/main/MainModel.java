package com.swarauto.ui.main;

import com.swarauto.dependencies.DependenciesRegistry;
import com.swarauto.game.profile.Profile;
import com.swarauto.game.profile.ProfileManager;
import com.swarauto.game.session.SessionType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class MainModel {
    private ProfileManager profileManager;

    private final List<String> profileIds = new ArrayList<String>();
    private final List<String> profileNames = new ArrayList<String>();
    @Getter
    private final List<String> sessionTypeNames = new ArrayList<String>();
    @Getter
    @Setter
    private int selectedProfileNameIndex = -1;
    @Getter
    @Setter
    private int selectedSessionTypeNameIndex = -1;

    public MainModel() {
        profileManager = DependenciesRegistry.profileManager;
    }

    public void loadData() {
        loadSessionTypes();
        loadProfiles();
    }

    private void loadProfiles() {
        profileIds.clear();
        profileNames.clear();
        profileIds.addAll(profileManager.getProfileIds());
        for (String id : profileIds) {
            Profile profile = profileManager.loadProfile(id);
            if (profile != null) {
                profileNames.add(profile.getName());
            }
        }
    }

    private void loadSessionTypes() {
        sessionTypeNames.clear();
        for (SessionType sessionType : SessionType.values()) {
            sessionTypeNames.add(sessionType.name());
        }
    }

    public List<String> getProfileOptions() {
        List<String> options = new ArrayList<String>();
        for (int i = 0; i < profileIds.size(); i++) {
            options.add(profileIds.get(i) + " - " + profileNames.get(i));
        }
        return options;
    }

    public String getSelectedProfileId() {
        if (selectedProfileNameIndex >= 0) return profileIds.get(selectedProfileNameIndex);
        return null;
    }

    public String getSelectedSessionTypeName() {
        if (selectedSessionTypeNameIndex >= 0) return sessionTypeNames.get(selectedSessionTypeNameIndex);
        return null;
    }

    public void setSelectedProfileId(String selectedProfileId) {
        for (int i = 0; i < profileIds.size(); i++) {
            if (profileIds.get(i).contentEquals(selectedProfileId)) setSelectedProfileNameIndex(i);
        }
    }
}
