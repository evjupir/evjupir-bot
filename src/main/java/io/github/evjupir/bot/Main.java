package io.github.evjupir.bot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.evjupir.bot.discord.APIHandler;
import io.github.evjupir.bot.gui.MainWindow;
import io.github.evjupir.bot.gui.TokenRequester;
import jdk.nashorn.internal.parser.Token;
import kong.unirest.Unirest;

import java.util.List;

public class Main {
    private List<ChannelInfo> channels;
    private Gson gson = new Gson();
    private APIHandler api;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        new TokenRequester(this);
        loadVoiceChannels();
    }

    public void start(){
        new MainWindow(api, channels);
    }

    private void loadVoiceChannels() {
        channels = gson.fromJson(Unirest.get("https://evjupir.github.io/data/bot/channels/voice/index.json").asString().getBody(), new TypeToken<List<ChannelInfo>>() {
        }.getType());
    }

    public class ChannelInfo {
        public String displayName;
        public String guildId;
        public String channelId;
    }

    public void setApi(APIHandler api) {
        this.api = api;
    }
}
