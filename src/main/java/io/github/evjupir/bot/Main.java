package io.github.evjupir.bot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    private List<String> mutedChannels = new ArrayList<>();
    private Gson gson = new Gson();
    public DiscordApi api;

    public static void main(String[] args) {
        new Main("token here");
    }

    public Main(String token) {
        api = new DiscordApiBuilder().setToken(token).setAllIntents().login().join();
        api.addUserChangeMutedListener((event) -> {
            if (event.isNewMuted()) return;
            if (event.getUser().getConnectedVoiceChannel(event.getServer()).isPresent()) {
                if (mutedChannels.contains(event.getUser().getConnectedVoiceChannel(event.getServer()).get().getIdAsString()))
                    event.getUser().mute(event.getServer());
            }
        });

        api.addServerVoiceChannelMemberJoinListener(event -> {
            if(mutedChannels.contains(event.getChannel().getIdAsString())){
                event.getUser().mute(event.getServer());
            }
        });

        api.addServerVoiceChannelMemberLeaveListener(event -> {
            if(mutedChannels.contains(event.getChannel().getIdAsString())){
                event.getUser().unmute(event.getServer());
            }
        });

        port(8021);
        System.out.println("\n\n*************************************************************************");
        System.out.println("                          EvjupirBot - Backend\n");
        System.out.println(" Gehe nun auf https://evjupir.github.io/bot, um diesen Bot zu nutzen");
        System.out.println("*************************************************************************");
        get("/status", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            return res;
        });
        get("/channels", (req, res) -> {
            Collection<Server> servers = api.getServers();
            JsonArray ret = new JsonArray();

            servers.forEach(server -> {
                JsonObject serverJson = new JsonObject();
                server.getIcon().ifPresent(icon -> serverJson.addProperty("url", icon.getUrl().toString()));
                serverJson.addProperty("name", server.getName());

                JsonArray channelsJson = new JsonArray();

                server.getVoiceChannels().forEach(vChannel -> {
                    JsonObject channelJson = new JsonObject();
                    if(vChannel.canYouMuteUsers()){
                        channelJson.addProperty("isMuted", mutedChannels.contains(vChannel.getIdAsString()));
                        channelJson.addProperty("channelId", vChannel.getIdAsString());
                        channelJson.addProperty("name", vChannel.getName());
                        JsonArray users = new JsonArray();
                        vChannel.getConnectedUsers().forEach(user -> {
                            JsonObject userJson = new JsonObject();
                            userJson.addProperty("name", user.getDiscriminatedName());
                            userJson.addProperty("url", user.getAvatar().getUrl().toString());
                            users.add(userJson);
                        });
                        channelJson.add("users", users);
                        channelsJson.add(channelJson);
                    }
                });
                serverJson.add("channels", channelsJson);
                ret.add(serverJson);
            });

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS");

            return ret.toString();
        });

        get("/channels/mute/:id", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS");

            Optional<ServerVoiceChannel> channel = api.getServerVoiceChannelById(req.params("id"));
            if (channel.isPresent()) {
                ServerVoiceChannel vChannel = channel.get();
                if (!vChannel.canYouMuteUsers()) {
                    res.status(401);
                    return "No perms";
                }
                vChannel.getConnectedUsers().forEach(user -> user.mute(vChannel.getServer()));
                mutedChannels.add(vChannel.getIdAsString());
                return "muted";
            }
            res.status(404);
            return "Not found";
        });

        get("/channels/unmute/:id", (req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "POST, GET, OPTIONS");

            Optional<ServerVoiceChannel> channel = api.getServerVoiceChannelById(req.params("id"));
            if (channel.isPresent()) {
                ServerVoiceChannel vChannel = channel.get();
                if(!mutedChannels.contains(vChannel.getIdAsString())){
                    res.status(201);
                    return "Not muted";
                }
                if (!vChannel.canYouMuteUsers()) {
                    res.status(401);
                    return "No perms";
                }
                vChannel.getConnectedUsers().forEach(user -> user.unmute(vChannel.getServer()));
                mutedChannels.remove(vChannel.getIdAsString());
                return "unmuted";
            }
            res.status(404);
            return "Not found";

        });
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                mutedChannels.forEach(channelId -> {
                    api.getServerVoiceChannelById(channelId).ifPresent(channel -> {
                        channel.getConnectedUsers().forEach(user -> user.unmute(channel.getServer()));
                    });
                });
            }
        });
    }
}
