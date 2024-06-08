package com.neocle.ExpansionSocial;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class SocialExpansion extends PlaceholderExpansion {

    private final HashMap<String, String> TWITCH_CLIENT_ID = new HashMap<>();
    private final HashMap<String, String> TWITCH_ACCESS_TOKEN = new HashMap<>();
    private final HashMap<String, String> YOUTUBE_API_KEY = new HashMap<>();
    private final Logger logger = Logger.getLogger("SocialExpansion");
    private final Config configHandler;

    public SocialExpansion() {
        this.configHandler = new Config(this);
    }

    @Override
    public boolean register() {
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
                return isTwitchUserStreaming(username) ? "streaming" : "offline";
            case "twitch_followers_count":
                return String.valueOf(getTwitchFollowers(username));
            case "twitch_stream_last":
                return getTwitchLastStreamDate(username);
            case "twitch_user_displayname":
                return getTwitchUserDisplayName(username);
            case "twitch_game_name":
                return getTwitchGameName(username);
            case "twitch_viewers_count":
                return String.valueOf(getTwitchViewersCount(username));
            case "twitch_stream_uptime":
                return getTwitchStreamUptime(username);
            case "twitch_channel_url":
                return getTwitchChannelUrl(username);
            case "twitch_channel_id":
                return getTwitchUserId(username);
            // YOUTUBE PLACEHOLDERS 
            case "youtube_followers_count":
                return String.valueOf(getYouTubeFollowersCount(username));
            case "youtube_stream_status":
                return isYouTubeUserStreaming(username) ? "streaming" : "offline";
            case "youtube_viewers_count":
                return String.valueOf(getYouTubeViewersCount(username));
            case "youtube_stream_last":
                return getLastYouTubeStreamDate(username);
            case "youtube_video_count":
                return String.valueOf(getYouTubeVideoCount(username));
            case "youtube_total_views":
                return String.valueOf(getYouTubeTotalViews(username));
            case "youtube_most_viewed":
                return getYoutubeMostViewedVideo(username);
            case "youtube_last_video":
                return getYoutubeLastVideo(username);
            case "youtube_channel_id":
                return getYoutubeChannelId(username);
            case "youtube_channel_url":
                return getYoutubeChannelUrl(username);

            default:
                return null;
        }
    }

    // TWITCH FUNCTIONS

    private String getTwitchChannelUrl(String username) {
        return "https://www.twitch.tv/" + username;
    }

    private boolean isTwitchUserStreaming(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/streams?user_login=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", TWITCH_CLIENT_ID.get("default"));
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_ACCESS_TOKEN.get("default"));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();
                return !response.contains("\"data\":[]");
            }
        } catch (IOException e) {
            logger.severe("Error checking if user is streaming: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private int getTwitchFollowers(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/channels/followers?broadcaster_id=" + getTwitchUserId(username));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", TWITCH_CLIENT_ID.get("default"));
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_ACCESS_TOKEN.get("default"));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

                if (jsonObject.has("total")) {
                    return jsonObject.get("total").getAsInt();
                } else {
                    logger.warning("Response does not contain 'total' field.");
                }
            } else {
                logger.warning("Error response code: " + responseCode);
            }
        } catch (IOException e) {
            logger.severe("Error getting followers: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    private String getTwitchLastStreamDate(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/videos?user_id=" + getTwitchUserId(username) + "&type=archive");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", TWITCH_CLIENT_ID.get("default"));
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_ACCESS_TOKEN.get("default"));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                JsonElement element = JsonParser.parseString(response);
                JsonObject jsonObject = element.getAsJsonObject();

                JsonArray data = jsonObject.getAsJsonArray("data");
                if (data.size() > 0) {
                    return data.get(0).getAsJsonObject().get("created_at").getAsString();
                }
            }
        } catch (IOException e) {
            logger.severe("Error getting last stream date: " + e.getMessage());
            e.printStackTrace();
        }
        return "N/A";
    }

    private String getTwitchUserDisplayName(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/users?login=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", TWITCH_CLIENT_ID.get("default"));
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_ACCESS_TOKEN.get("default"));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

                JsonArray data = jsonObject.getAsJsonArray("data");
                if (data.size() > 0) {
                    JsonElement firstItem = data.get(0);
                    return firstItem.getAsJsonObject().get("display_name").getAsString();
                }
            }
        } catch (IOException e) {
            logger.severe("Error getting user display name: " + e.getMessage());
            e.printStackTrace();
        }
        return "N/A";
    }   

    private String getTwitchGameName(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/streams?user_login=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", TWITCH_CLIENT_ID.get("default"));
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_ACCESS_TOKEN.get("default"));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

                JsonArray data = jsonObject.getAsJsonArray("data");
                if (data.size() > 0) {
                    JsonElement firstItem = data.get(0);
                    return firstItem.getAsJsonObject().get("game_name").getAsString();
                }
            }
        } catch (IOException e) {
            logger.severe("Error getting game name: " + e.getMessage());
            e.printStackTrace();
        }
        return "N/A";
    }

    private int getTwitchViewersCount(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/streams?user_login=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", TWITCH_CLIENT_ID.get("default"));
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_ACCESS_TOKEN.get("default"));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();

                JsonArray data = jsonObject.getAsJsonArray("data");
                if (data.size() > 0) {
                    JsonElement firstItem = data.get(0);
                    return firstItem.getAsJsonObject().get("viewer_count").getAsInt();
                }
            }
        } catch (IOException e) {
            logger.severe("Error getting viewers count: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    private String getTwitchStreamUptime(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/streams?user_login=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", TWITCH_CLIENT_ID.get("default"));
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_ACCESS_TOKEN.get("default"));

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                JsonArray dataArray = jsonObject.getAsJsonArray("data");

                if (dataArray.size() > 0) {
                    JsonObject streamObject = dataArray.get(0).getAsJsonObject();
                    String startTimeString = streamObject.get("started_at").getAsString();
                    LocalDateTime startTime = LocalDateTime.parse(startTimeString, DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime now = LocalDateTime.now();
                    Duration duration = Duration.between(startTime, now);

                    long hours = duration.toHours();
                    long minutes = duration.toMinutesPart();
                    return String.format("%d hours %d minutes", hours, minutes);
                } else {
                    return "Stream not currently live";
                }
            } else {
                logger.warning("Error response code: " + responseCode);
            }
        } catch (IOException e) {
            logger.severe("Error getting stream uptime: " + e.getMessage());
            e.printStackTrace();
        }
        return "N/A";
    }
    
    private String getTwitchUserId(String username) {
        try {
            URL url = new URL("https://api.twitch.tv/helix/users?login=" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", TWITCH_CLIENT_ID.get("default"));
            connection.setRequestProperty("Authorization", "Bearer " + TWITCH_ACCESS_TOKEN.get("default"));
    
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
    
                JsonArray data = jsonObject.getAsJsonArray("data");
                if (data.size() > 0) {
                    JsonElement firstItem = data.get(0);
                    return firstItem.getAsJsonObject().get("id").getAsString();
                }
            }
        } catch (IOException e) {
            logger.severe("Error getting user ID: " + e.getMessage());
            e.printStackTrace();
        }
    
        return null;
    } 
    
    // YOUTUBE FUNCTIONS

    private String getYoutubeChannelId(String username) {
        try {
            String urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + username + "&type=channel&key=" + YOUTUBE_API_KEY.get("default");
            String response = getApiResponse(urlString);
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonArray items = jsonObject.getAsJsonArray("items");
            if (items.size() > 0) {
                return items.get(0).getAsJsonObject().getAsJsonObject("id").get("channelId").getAsString();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.warning("Failed to get YouTube channel ID for " + username + ": " + e.getMessage());
            return null;
        }
    }

    private int getYouTubeFollowersCount(String username) {
        try {
            String channelId = getYoutubeChannelId(username);
            if (channelId != null) {
                String urlString = "https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + channelId + "&key=" + YOUTUBE_API_KEY.get("default");
                String response = getApiResponse(urlString);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                return jsonObject.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("statistics").get("subscriberCount").getAsInt();
            } else {
                return 0;
            }
        } catch (Exception e) {
            logger.warning("Failed to get YouTube followers count for " + username + ": " + e.getMessage());
            return 0;
        }
    }

    private boolean isYouTubeUserStreaming(String username) {
        try {
            String channelId = getYoutubeChannelId(username);
            if (channelId != null) {
                String urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&type=video&eventType=live&key=" + YOUTUBE_API_KEY.get("default");
                String response = getApiResponse(urlString);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                return jsonObject.getAsJsonArray("items").size() > 0;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.warning("Failed to get YouTube streaming status for " + username + ": " + e.getMessage());
            return false;
        }
    }

    private int getYouTubeViewersCount(String username) {
        try {
            String channelId = getYoutubeChannelId(username);
            if (channelId != null) {
                String urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&type=video&eventType=live&key=" + YOUTUBE_API_KEY.get("default");
                String response = getApiResponse(urlString);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                if (jsonObject.getAsJsonArray("items").size() > 0) {
                    String videoId = jsonObject.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString();
                    urlString = "https://www.googleapis.com/youtube/v3/videos?part=liveStreamingDetails&id=" + videoId + "&key=" + YOUTUBE_API_KEY.get("default");
                    response = getApiResponse(urlString);
                    jsonObject = JsonParser.parseString(response).getAsJsonObject();
                    return jsonObject.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("liveStreamingDetails").get("concurrentViewers").getAsInt();
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            logger.warning("Failed to get YouTube viewers count for " + username + ": " + e.getMessage());
            return 0;
        }
    }

    private String getLastYouTubeStreamDate(String username) {
        try {
            String channelId = getYoutubeChannelId(username);
            if (channelId != null) {
                String urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&type=video&eventType=completed&key=" + YOUTUBE_API_KEY.get("default");
                String response = getApiResponse(urlString);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                JsonArray items = jsonObject.getAsJsonArray("items");
                if (items.size() > 0) {
                    String dateTime = items.get(0).getAsJsonObject().getAsJsonObject("snippet").get("publishedAt").getAsString();
                    return dateTime;
                } else {
                    return "No recent streams found";
                }
            } else {
                return "Channel ID not found";
            }
        } catch (Exception e) {
            logger.warning("Failed to get last YouTube stream date for " + username + ": " + e.getMessage());
            return "Error fetching data";
        }
    }
    
    private int getYouTubeVideoCount(String username) {
        try {
            String channelId = getYoutubeChannelId(username);
            if (channelId != null) {
                String urlString = "https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + channelId + "&key=" + YOUTUBE_API_KEY.get("default");
                String response = getApiResponse(urlString);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                return jsonObject.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("statistics").get("videoCount").getAsInt();
            } else {
                return 0;
            }
        } catch (Exception e) {
            logger.warning("Failed to get YouTube video count for " + username + ": " + e.getMessage());
            return 0;
        }
    }
    
    private long getYouTubeTotalViews(String username) {
        try {
            String channelId = getYoutubeChannelId(username);
            if (channelId != null) {
                String urlString = "https://www.googleapis.com/youtube/v3/channels?part=statistics&id=" + channelId + "&key=" + YOUTUBE_API_KEY.get("default");
                String response = getApiResponse(urlString);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                JsonObject statistics = jsonObject.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("statistics");
                String viewCount = statistics.get("viewCount").getAsString();
                return Long.parseLong(viewCount);
            }
        } catch (Exception e) {
            logger.warning("Failed to get YouTube total views for " + username + ": " + e.getMessage());
        }
        return -1;
    }
    
    private String getYoutubeMostViewedVideo(String username) {
        try {
            String channelId = getYoutubeChannelId(username);
            if (channelId != null) {
                String urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&order=viewCount&maxResults=1&type=video&key=" + YOUTUBE_API_KEY.get("default");
                String response = getApiResponse(urlString);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                JsonArray items = jsonObject.getAsJsonArray("items");
                if (items.size() > 0) {
                    return "https://www.youtube.com/watch?v=" + items.get(0).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString();
                }
            }
        } catch (Exception e) {
            logger.warning("Failed to get most viewed video for " + username + ": " + e.getMessage());
        }
        return "";
    }
    
    private String getYoutubeLastVideo(String username) {
        try {
            String channelId = getYoutubeChannelId(username);
            if (channelId != null) {
                String urlString = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&order=date&maxResults=1&type=video&key=" + YOUTUBE_API_KEY.get("default");
                String response = getApiResponse(urlString);
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                JsonArray items = jsonObject.getAsJsonArray("items");
                if (items.size() > 0) {
                    return "https://www.youtube.com/watch?v=" + items.get(0).getAsJsonObject().getAsJsonObject("id").get("videoId").getAsString();
                }
            }
        } catch (Exception e) {
            logger.warning("Failed to get last video for " + username + ": " + e.getMessage());
        }
        return "";
    }
    

    private String getYoutubeChannelUrl(String username) {
        return "https://youtube.com/@" + username;
    }

    private String getApiResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to get response from API: " + responseCode);
        }
        Scanner scanner = new Scanner(url.openStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();
        return response.toString();
    }
}