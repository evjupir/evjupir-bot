package io.github.evjupir.bot.gui;

import io.github.evjupir.bot.Main;
import io.github.evjupir.bot.discord.APIHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TokenRequester {
    public TokenRequester(Main main){
        final JFrame[] frame = {new JFrame("Bitte Token eingeben...")};
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1,2));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField input = new JTextField();
        input.setText("NzgxOTUxNTYzNDA4NDc0MTUz.X8FHJA.8sP1Xc8K5eEMDleGaTN9dbzSsxA");
        input.setToolTipText("Discord Token here");

        panel.add(input);

        JButton submit = new JButton("Fertig!");
        submit.addActionListener(e -> {
            main.setApi(new APIHandler(input.getText()));
            main.start();
            frame[0].setVisible(false);
            frame[0] = null;
        });

        panel.add(submit);

        frame[0].getContentPane().add(panel);
        frame[0].setSize(600, 150);
        frame[0].setVisible(true);
    }
}
