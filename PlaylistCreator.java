package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PlaylistCreator extends JFrame {
    private final JTextField playlistNameField;
    private final JButton createButton;
    private final JButton cancelButton;
    private final List<String> playlists;

    public PlaylistCreator() {
        setTitle("Create Playlist");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 创建文本框和按钮面板
        JPanel inputPanel = new JPanel();
        playlistNameField = new JTextField(20);
        createButton = new JButton("Create");
        cancelButton = new JButton("Cancel");
        inputPanel.add(new JLabel("Playlist Name: "));
        inputPanel.add(playlistNameField);
        inputPanel.add(createButton);
        inputPanel.add(cancelButton);

        // 创建按钮监听器
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playlistName = playlistNameField.getText();
                if (!playlistName.isEmpty()) {
                    // 创建新的歌单
                    playlists.add(playlistName);
                    JOptionPane.showMessageDialog(null, "Playlist created: " + playlistName);
                    //addNewPlaylist(playlistName);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a playlist name.");
                }
                closeWindow();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });

        add(inputPanel, BorderLayout.CENTER);
        playlists = new ArrayList<>();
    }

    private void closeWindow() {
        setVisible(false);
        dispose();
    }

    public List<String> getPlaylists() {
        return playlists;
    }


}

