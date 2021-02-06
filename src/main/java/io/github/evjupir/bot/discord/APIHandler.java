package io.github.evjupir.bot.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.ArrayList;
import java.util.List;

public class APIHandler {
    private DiscordApi api;
    private List<String> muted = new ArrayList<>();

    public APIHandler(String token) {
        this.api = new DiscordApiBuilder().setToken(token).setAllNonPrivilegedIntents().login().join();
    }

    public void muteChannel(String channelId) {
        muted.add(channelId);
        api.getServerVoiceChannelById(channelId).ifPresent(channel -> {
            channel.getConnectedUsers().forEach(user -> user.mute(channel.getServer()));
            channel.getServer().addUserChangeMutedListener(event -> {
                if (event.isNewMuted() == true) return;
                if (event.getUser().getConnectedVoiceChannel(event.getServer()).get().getIdAsString().equals(channelId) && muted.contains(event.getUser().getConnectedVoiceChannel(event.getServer()).get().getIdAsString())) {
                    event.getUser().mute(event.getServer());
                }
            });
        });
    }

    public void unmuteChannel(String channelId) {
        muted.remove(channelId);
        api.getServerVoiceChannelById(channelId).ifPresent(channel -> {
            channel.getConnectedUsers().forEach(user -> user.unmute(channel.getServer()));
        });
    }
}
