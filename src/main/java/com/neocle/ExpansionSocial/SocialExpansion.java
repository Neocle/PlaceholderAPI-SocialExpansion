package com.neocle.ExpansionSocial;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.logging.Logger;

public class SocialExpansion extends PlaceholderExpansion {

    private final HashMap<String, String> TWITCH_CLIENT_ID = new HashMap<>();
    private final HashMap<String, String> TWITCH_ACCESS_TOKEN = new HashMap<>();
    private final HashMap<String, String> YOUTUBE_API_KEY = new HashMap<>();
    private final Logger logger = Logger.getLogger("SocialExpansion");
    @SuppressWarnings("unused")
    private final Config configHandler;

    private final TwitchPlaceholders twitchPlaceholders;
    private final YoutubePlaceholders youtubePlaceholders;

    public SocialExpansion() {
        this.configHandler = new Config(this);
        this.twitchPlaceholders = new TwitchPlaceholders(TWITCH_CLIENT_ID, TWITCH_ACCESS_TOKEN);
        this.youtubePlaceholders = new YoutubePlaceholders(YOUTUBE_API_KEY);
    }

    @Override
    public boolean register() {
        @SuppressWarnings("unused")
        FileConfiguration config = new Config(this).load();
        TWITCH_CLIENT_ID.put("default", new Config(this).getTwitchClientId());
        TWITCH_ACCESS_TOKEN.put("default", new Config(this).getTwitchAccessToken());
        YOUTUBE_API_KEY.put("default", new Config(this).getYoutubeAPIKey());
        return super.register();
    }

    @Override
    public String getAuthor() {
        return "Neocle";
    }

    @Override
    public String getIdentifier() {
        return "social";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        String[] parts = params.split("_", 4);
        if (parts.length < 4) {
            return null;
        }

        String social = parts[0];
        String category = parts[1];
        String identifier = parts[2];
        String username = parts[3];

        String fullIdentifier = social + "_" + category + "_" + identifier;

        switch (fullIdentifier) {
            // TWITCH PLACEHOLDERS
            case "twitch_streaming_status":
                return twitchPlaceholders.isTwitchUserStreaming(username) ? "streaming" : "offline";
            case "twitch_followers_count":
                return String.valueOf(twitchPlaceholders.getTwitchFollowers(username));
            case "twitch_stream_last":
                return twitchPlaceholders.getTwitchLastStreamDate(username);
            case "twitch_user_displayname":
                return twitchPlaceholders.getTwitchUserDisplayName(username);
            case "twitch_game_name":
                return twitchPlaceholders.getTwitchGameName(username);
            case "twitch_viewers_count":
                return String.valueOf(twitchPlaceholders.getTwitchViewersCount(username));
            case "twitch_stream_uptime":
                return twitchPlaceholders.getTwitchStreamUptime(username);
            case "twitch_channel_url":
                return twitchPlaceholders.getTwitchChannelUrl(username);
            case "twitch_channel_id":
                return twitchPlaceholders.getTwitchUserId(username);
            // YOUTUBE PLACEHOLDERS
            case "youtube_subscribers_count":
                return String.valueOf(youtubePlaceholders.getYouTubeFollowersCount(username));
            case "youtube_stream_status":
                return youtubePlaceholders.isYouTubeUserStreaming(username) ? "streaming" : "offline";
            case "youtube_viewers_count":
                return String.valueOf(youtubePlaceholders.getYouTubeViewersCount(username));
            case "youtube_stream_last":
                return youtubePlaceholders.getLastYouTubeStreamDate(username);
            case "youtube_video_count":
                return String.valueOf(youtubePlaceholders.getYouTubeVideoCount(username));
            case "youtube_total_views":
                return String.valueOf(youtubePlaceholders.getYouTubeTotalViews(username));
            case "youtube_most_viewed":
                return youtubePlaceholders.getYoutubeMostViewedVideo(username);
            case "youtube_last_video":
                return youtubePlaceholders.getYoutubeLastVideo(username);
            case "youtube_channel_id":
                return youtubePlaceholders.getYoutubeChannelId(username);
            case "youtube_channel_url":
                return youtubePlaceholders.getYoutubeChannelUrl(username);

            default:
                return null;
        }
    }
}