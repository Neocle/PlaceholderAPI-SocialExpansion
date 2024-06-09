# PlaceholderAPI-SocialExpansion

## How to setup ?

- Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) and restart your server.
- Download latest [Release](https://github.com/Neocle/PlaceholderAPI-SocialExpansion/releases) and place it in `Plugins/PlaceholderAPi/expansions/`
- Run `/papi reload`
- Edit `Plugins/PlaceholderAPi/expansions/social/config.yml` and set your Twitch Application [Twitch Client ID](https://dev.twitch.tv/docs/cli/mock-api-command/#getting-your-client-id-and-secret), [Twitch Access Token](https://dev.twitch.tv/docs/cli/token-command/#get-an-access-token) and [Youtube API Key](https://support.google.com/googleapi/answer/6158862?hl=en)
- Run `/papi reload` again
- Done!

### ⚠️ __IMPORTANT:__ It is recommended not to use these placeholders on Holograms, it will lead into APIs quota exceeded, and therefore, placeholders not working as expected!
(If you still want to use Holograms, you should reduce the update interval)
## Youtube Placeholders

`%social_youtube_subscribers_count_<username>%`-> Returns subscribers count of <username>

`%social_youtube_stream_status_<username>%` -> Returns <username> status: "streaming" or "offline"

`%social_youtube_viewers_count_<username>%` -> Returns the number of viewers of <username> (if streaming)

`%social_youtube_stream_last_<username>%` -> Returns the date of <username>'s last stream

`%social_youtube_video_count_<username>%` -> Returns the number of published videos of <username>

`%social_youtube_total_views_<username>%` -> Returns the total views of <username>'s channel

`%social_youtube_most_viewed_<username>%` -> Returns the link to the most viewed video of <username>

`%social_youtube_last_video_<username>%` -> Returns the link to the latest video of <username>

`%social_youtube_channel_id_<username>%` -> Returns youtube <username>'s channel id

`%social_youtube_channel_url_<username>%` -> Returns youtube <username>'s channel url

## Twitch Placeholders

`%social_twitch_streaming_status_<username>%`-> Returns <username> status: "streaming" or "offline"

`%social_twitch_stream_last_<username>%` -> Returns the date of the last stream of <username>

`%social_twitch_followers_count_<username>%` -> Returns the number of followers of <username>

`%social_twitch_user_displayname_<username>%` -> Returns the twitch display name of <username>

`%social_twitch_game_name_<username>%` -> Returns the game <username> is streaming on

`%social_twitch_viewers_count_<username>%` -> Returns the viewers count of <username>'s current stream

`%social_twitch_stream_uptime_<username>%` -> Returns the time since <username> has started their current live

`%social_twitch_channel_url_<username>%` -> Returns twitch <username>'s channel url

`%social_twitch_channel_id_<username>%` -> Returns twitch <username>'s channel id
