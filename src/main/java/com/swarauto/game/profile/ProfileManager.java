package com.swarauto.game.profile;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.swarauto.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.swarauto.util.FileUtil.readTextFile;
import static com.swarauto.util.FileUtil.writeTextFile;

public class ProfileManager {
    public static final String PROFILE_PROPS_FILE_NAME = "props.json";
    public static final String COMMON_CONFIG_FILE_NAME = "commonConfig.json";
    private String profilesLocation;

    public void setLocation(String path) {
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException("Invalid profiles folder: " + path);
        }

        this.profilesLocation = path;
    }

    private File getProfileDir(String id) {
        if (id == null || id.length() == 0) {
            throw new IllegalArgumentException("Invalid profile id: " + id);
        }

        File containerFolder = getProfilesDir();
        return new File(containerFolder, id);
    }

    private File getProfilesDir() {
        if (profilesLocation == null || profilesLocation.length() == 0) {
            throw new IllegalStateException("Must set profiles folder first");
        }

        return new File(profilesLocation);
    }

    public Profile createEmptyProfile() {
        String id = String.valueOf(System.currentTimeMillis());
        return createEmptyProfile(id);
    }

    private Profile createEmptyProfile(String id) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setName(id);

        File profileDir = getProfileDir(id);
        profileDir.mkdirs();
        profile.setPath(profileDir.getAbsolutePath());

        return profile;
    }

    public List<String> getProfileIds() {
        List<String> ids = new ArrayList<String>();
        File[] profilesFolderContent = getProfilesDir().listFiles();
        if (profilesFolderContent != null) {
            for (File folder : profilesFolderContent) {
                if (folder.isDirectory()) {
                    if (loadProfile(folder.getName()) != null) {
                        ids.add(folder.getName());
                    }
                }
            }
        }

        return ids;
    }

    public Profile loadProfile(String id) {
        File profileDir = getProfileDir(id);
        if (!profileDir.exists()) return null;

        Profile profile = null;

        // Load props
        File propFile = new File(profileDir.getAbsolutePath() + "/" + PROFILE_PROPS_FILE_NAME);
        if (propFile.exists()) {
            String content = readTextFile(propFile.getAbsolutePath());
            Gson gson = new Gson();
            try {
                profile = gson.fromJson(content, Profile.class);
                if (profile.getId() == null || profile.getId().length() == 0) {
                    return null;
                }
            } catch (JsonSyntaxException ignored) {
            }
        }
        // Failed to load props file, create empty profile with id
        if (profile == null) {
            profile = createEmptyProfile(id);
        }
        profile.setPath(getProfileDir(profile.getId()).getAbsolutePath());
        return profile;
    }

    public void saveProfile(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Null profile");
        }

        if (profile.getId() == null || profile.getId().length() == 0) {
            throw new IllegalStateException("Profile id is empty");
        }

        String json = new Gson().toJson(profile);

        writeTextFile(profile.getPath() + "/" + PROFILE_PROPS_FILE_NAME, json);
    }

    public void deleteProfile(String id) {
        try {
            File folder = getProfileDir(id);
            FileUtil.deleteFolder(folder);
        } catch (IllegalStateException ignored) {

        }
    }

    public File getCommonConfigFile() {
        File profilesPath = new File(profilesLocation);
        return new File(profilesPath.getParent() + "/" + COMMON_CONFIG_FILE_NAME);
    }

    public CommonConfig loadCommonConfig() {
        CommonConfig commonConfig = null;
        File file = getCommonConfigFile();
        if (file.exists()) {
            String content = readTextFile(file.getAbsolutePath());
            Gson gson = new Gson();
            try {
                commonConfig = gson.fromJson(content, CommonConfig.class);
            } catch (JsonSyntaxException ignored) {
            }
        }
        if (commonConfig == null) {
            commonConfig = new CommonConfig();
        }
        return commonConfig;
    }

    public void saveCommonConfig(CommonConfig commonConfig) {
        if (commonConfig == null) {
            throw new IllegalArgumentException("Null commonConfig");
        }

        String json = new Gson().toJson(commonConfig);

        writeTextFile(getCommonConfigFile().getAbsolutePath(), json);
    }
}
