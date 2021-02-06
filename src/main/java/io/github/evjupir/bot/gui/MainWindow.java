package io.github.evjupir.bot.gui;

import io.github.evjupir.bot.Main;
import io.github.evjupir.bot.discord.APIHandler;
import kong.unirest.Unirest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MainWindow {
    private List<Main.ChannelInfo> channels;
    private APIHandler api;

    private JFrame window;

    public MainWindow(APIHandler api, List<Main.ChannelInfo> channels) {
        this.api = api;
        this.channels = channels;
        create();
    }

    private void create() {
        window = new JFrame("Evjupir-Bot");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel header = new JPanel();
        header.setLayout(new GridLayout(1,2));
        header.add(new JLabel("Channel"));
        header.add(new JLabel("Muted?"));

        JPanel main = new JPanel();
        main.setLayout(new GridLayout(channels.size() + 2, 1));
        main.setBorder(new EmptyBorder(10, 10, 10, 10));
        main.add(header);

        JPanel empty = new JPanel();
        empty.setSize(0, 20);

        main.add(empty);

        addChannels(main);

        window.getContentPane().add(main);
        window.setSize(600, 700);
        window.pack();
        window.setVisible(true);
    }

    private void addChannels(JPanel main) {
        for(Main.ChannelInfo info : channels){
            main.add(new ChannelEntry(info, api).get());
        }
    }
}
