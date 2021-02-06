package io.github.evjupir.bot.gui;

import io.github.evjupir.bot.Main;
import io.github.evjupir.bot.discord.APIHandler;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChannelEntry {
    private Main.ChannelInfo channel;
    private APIHandler handler;
    public ChannelEntry(Main.ChannelInfo channel, APIHandler handler){
        this.channel = channel;
        this.handler = handler;
    }

    public JPanel get(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1,2));
        panel.add(new JLabel(channel.displayName));
        JCheckBox muted = new JCheckBox();
        muted.setSelected(false);
        muted.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(muted.isSelected())
                    handler.muteChannel(channel.channelId);
                else
                    handler.unmuteChannel(channel.channelId);
            }
        });

        panel.add(muted);
        return panel;
    }
}
