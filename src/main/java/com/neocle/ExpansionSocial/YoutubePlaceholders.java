package com.neocle.ExpansionSocial;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class YoutubePlaceholders {
    private final HashMap<String, String> YOUTUBE_API_KEY;
    private final Logger logger = Logger.getLogger("SocialExpansion");

    public YoutubePlaceholders(HashMap<String, String> youtubeAPIKey) {
        this.YOUTUBE_API_KEY = youtubeAPIKey;
    }

    public String getYoutubeChannelId(String username) {
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

    public int getYouTubeFollowersCount(String username) {
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

    public boolean isYouTubeUserStreaming(String username) {
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

    public int getYouTubeViewersCount(String username) {
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

    public String getLastYouTubeStreamDate(String username) {
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
    
    public int getYouTubeVideoCount(String username) {
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
    
    public long getYouTubeTotalViews(String username) {
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
    
    public String getYoutubeMostViewedVideo(String username) {
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
    
    public String getYoutubeLastVideo(String username) {
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
    

    public String getYoutubeChannelUrl(String username) {
        return "https://youtube.com/@" + username;
    }

    public String getApiResponse(String urlString) throws IOException {
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