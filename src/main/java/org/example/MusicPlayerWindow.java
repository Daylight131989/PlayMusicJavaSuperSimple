package org.example;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicPlayerWindow extends JFrame {
    private JPanel contentPane;
    private JList<String> playlistList;
    private JList<String> songList;
    private Map<String, DefaultListModel<String>> playlistSongs;
    private SwingWorker<Void, Void> musicPlayerWorker;

    public MusicPlayerWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Music Player");
        setSize(800, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // 创建播放列表
        String[] playlists = {"Playlist 1", "Playlist 2", "Playlist 3"};
        playlistList = new JList<>(playlists);
        playlistList.setBounds(10, 10, 200, 400);
        contentPane.add(playlistList);

        // 创建歌曲列表
        songList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBounds(250, 10, 500, 400);
        contentPane.add(scrollPane);

        // 初始化播放列表和歌曲映射
        playlistSongs = new HashMap<>();
        playlistSongs.put("Playlist 1", new DefaultListModel<>());
        playlistSongs.put("Playlist 2", new DefaultListModel<>());
        playlistSongs.put("Playlist 3", new DefaultListModel<>());

        // 播放列表选择事件监听器
        playlistList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateSongList();
                }
            }
        });

        // 添加鼠标双击事件监听器
        songList.addMouseListener(new MouseInputAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击事件
                    // 在这里添加播放音乐的逻辑
                    playSelectedSong();
                }
            }
        });

        JButton addButton = new JButton("Add");
        addButton.setBounds(280, 420, 80, 30);
        contentPane.add(addButton);

        // 添加按钮的动作监听器
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 打开文件选择对话框
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(contentPane);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();

                    // 获取所选的播放列表
                    String selectedPlaylist = playlistList.getSelectedValue();
                    DefaultListModel<String> selectedPlaylistSongs = playlistSongs.get(selectedPlaylist);

                    // 将歌曲文件路径添加到播放列表
                    selectedPlaylistSongs.addElement(filePath);
                }
            }
        });

        // 创建播放按钮
        JButton playButton = new JButton("Play");
        playButton.setBounds(10, 420, 80, 30);
        contentPane.add(playButton);

        // 创建暂停按钮
        JButton pauseButton = new JButton("Pause");
        pauseButton.setBounds(100, 420, 80, 30);
        contentPane.add(pauseButton);

        // 创建继续按钮
        JButton resumeButton = new JButton("Resume");
        resumeButton.setBounds(190, 420, 80, 30);
        contentPane.add(resumeButton);

        // 播放按钮的动作监听器
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playSelectedSong();
            }
        });

        // 暂停按钮的动作监听器
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseSong();
            }
        });

        resumeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // 调用 resumeSong() 并传递 FileInputStream 和位置参数
                resumeSong();
            }
        });

        setVisible(true);
    }

    private void updateSongList() {
        // 清空歌曲列表
        DefaultListModel<String> songListModel = new DefaultListModel<>();
        songList.setModel(songListModel);

        // 获取所选播放列表的歌曲列表
        String selectedPlaylist = playlistList.getSelectedValue();
        DefaultListModel<String> selectedPlaylistSongs = playlistSongs.get(selectedPlaylist);

        // 添加歌曲到歌曲列表模型
        for (int i = 0; i < selectedPlaylistSongs.size(); i++) {
            songListModel.addElement(selectedPlaylistSongs.get(i));
        }
    }

    // 添加测试数据的方法
    private void addTestData() {
        DefaultListModel<String> playlist1 = playlistSongs.get("Playlist 1");
        playlist1.addElement("Taylor Swift - Delicate.mp3");
        playlist1.addElement("Song 2");

        DefaultListModel<String> playlist2 = playlistSongs.get("Playlist 2");
        playlist2.addElement("Song 3");
        playlist2.addElement("Song 4");

        DefaultListModel<String> playlist3 = playlistSongs.get("Playlist 3");
        playlist3.addElement("Song 5");
        playlist3.addElement("Song 6");
    }

    private boolean isPlaying; // 播放状态标识
    private boolean isPaused = false;
    private FileInputStream fis; // 声明输入流变量
    private BufferedInputStream bis; // 声明缓冲输入流变量
    private void playSelectedSong() {
        String selectedSong = songList.getSelectedValue();
        if (selectedSong != null && !isPaused) {
            if (player != null) {
                player.close();
            }

            try {
                fis = new FileInputStream(selectedSong);
                bis = new BufferedInputStream(fis);
                player = new Player(bis);
                isPaused = false;

                new Thread(() -> {
                    try {
                        player.play();
                    } catch (JavaLayerException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } catch (FileNotFoundException | JavaLayerException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Player player; // 声明 Player 变量
    private Thread playerThread; // 声明 Thread 变量
    long resumePosition;
    // 初始化 player 和 playerThread

    // 在需要使用 pauseSong 和 resumeSong 的地方调用这些方法
    private void pauseSong() {
        if (player != null && !isPaused) {
            // 保存当前播放位置
            resumePosition = player.getPosition();

            try {
                player.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            isPaused = true;
        }
    }

    private void resumeSong() {
        if (player != null && isPaused) {
            try {
                // 将输入流跳到上次暂停的位置
                fis.skip(resumePosition);

                isPaused = false;

                new Thread(() -> {
                    try {
                        player.play();
                    } catch (JavaLayerException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getFilePath(String playlist, int songIndex) {
        // 在实际应用中，根据所选的播放列表和歌曲索引获取歌曲文件的路径
        // 这里简单地返回一个示例路径
        String basePath = "C:/Music/";
        String playlistFolder = playlist.toLowerCase().replace(" ", "_");
        String songName = playlistSongs.get(playlist).getElementAt(songIndex);
        String filePath = basePath + playlistFolder + "/" + songName + ".mp3";
        return filePath;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MusicPlayerWindow window = new MusicPlayerWindow();
            window.addTestData(); // 添加测试数据
        });
    }
}