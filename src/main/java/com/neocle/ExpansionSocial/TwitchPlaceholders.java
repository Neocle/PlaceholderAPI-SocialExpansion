package com.neocle.ExpansionSocial;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

public class TwitchPlaceholders {

    private final HashMap<String, String> TWITCH_CLIENT_ID;
    private final HashMap<String, String> TWITCH_ACCESS_TOKEN;
    private final Logger logger = Logger.getLogger("SocialExpansion");

    public TwitchPlaceholders(HashMap<String, String> twitchClientId, HashMap<String, String> twitchAccessToken) {
        this.TWITCH_CLIENT_ID = twitchClientId;
        this.TWITCH_ACCESS_TOKEN = twitchAccessToken;
    }

    public String getTwitchChannelUrl(String username) {
        return "https://www.twitch.tv/" + username;
    }

    public boolean isTwitchUserStreaming(String username) {
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

    public int getTwitchFollowers(String username) {
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

    public String getTwitchLastStreamDate(String username) {
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

    public String getTwitchUserDisplayName(String username) {
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

    public String getTwitchGameName(String username) {
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

    public int getTwitchViewersCount(String username) {
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

    public String getTwitchStreamUptime(String username) {
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
    
    public String getTwitchUserId(String username) {
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
}