package com.neocle.ExpansionSocial;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.clip.placeholderapi.PlaceholderAPIPlugin;

public class Config {
    private final SocialExpansion ex;
    private final PlaceholderAPIPlugin plugin;
    private FileConfiguration config;
    private File file;
    public Config(SocialExpansion ex) {
        this.ex = ex;
        plugin = ex.getPlaceholderAPI();
        reload();
    }

    @SuppressWarnings("deprecation")
    public void reload() {
        if (file == null)
            file = new File(
                    plugin.getDataFolder() + File.separator + "expansions" + File.separator + ex.getIdentifier(),
                    "config.yml");
        if (!file.exists())
            plugin.saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
        config.options().header("");
        if (config.getKeys(false).isEmpty()) {
            config.set("Twitch.CLIENT_ID", "YOUR_CLIENT_ID_HERE");
            config.set("Twitch.ACCESS_TOKEN", "YOUR_ACCESS_TOKEN_HERE");
            config.set("Youtube.API_KEY", "YOUR_API_KEY_HERE");
            config.set("Twitter.BEARER_TOKEN", "YOUR_BEARER_TOKEN_HERE");
            save();
        }
    }
    
    public FileConfiguration load() {
        if (config == null)
            reload();
        return config;
    }

    public void save() {
        if ((config == null) || (file == null))
            return;
        try {
            load().save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save to " + file, ex);
        }
    }

    public String getTwitchClientId() {
        return config.getString("Twitch.CLIENT_ID");
    }

    public String getTwitchAccessToken() {
        return config.getString("Twitch.ACCESS_TOKEN");
    }

    public String getYoutubeAPIKey() {
        return config.getString("Youtube.API_KEY");
    }

    public String getTwitterBearerToken() {
        return config.getString("Twitter.BEARER_TOKEN");
    }
}