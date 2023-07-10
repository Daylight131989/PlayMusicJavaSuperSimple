package org.example;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class MusicPlayerWindow extends JFrame {
    private final JPanel contentPane;
    private final JList<String> playlistList;
    private final JList<String> songList;
    private final Map<String, DefaultListModel<String>> playlistSongs;

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
                File file = new File("");
                // 打开文件选择对话框
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(contentPane);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    file = selectedFile;
                    String filePath = selectedFile.getAbsolutePath();

                    // 获取所选的播放列表
                    String selectedPlaylist = playlistList.getSelectedValue();
                    DefaultListModel<String> selectedPlaylistSongs = playlistSongs.get(selectedPlaylist);

                    // 将歌曲文件路径添加到播放列表
                    selectedPlaylistSongs.addElement(filePath);
                }

                try {
                    AudioFile audioFile = AudioFileIO.read(file);

                    Tag tag = audioFile.getTag();

                    String title = tag.getFirst(FieldKey.TITLE);
                    String artist = tag.getFirst(FieldKey.ARTIST);
                    String album = tag.getFirst(FieldKey.ALBUM);
                    String durationS = String.valueOf(audioFile.getAudioHeader().getTrackLength());
                    int duration = Integer.parseInt(durationS);

                    Connection con;
                    ResultSet rs;
                    con = GetDBConnection.connectionDB("music_db", "root", "Mty030726");
                    if (con == null) return ;

                    String sql = "insert into songs (title, artist, album, duration) values (?, ?, ?, ?)";
                    PreparedStatement statement = con.prepareStatement(sql);
                    statement.setString(1, title);
                    statement.setString(2, artist);
                    statement.setString(3, album);
                    statement.setInt(4, duration);
                    statement.executeUpdate();
                    statement.close();
                }
                catch (IOException | TagException | SQLException e1) {
                    e1.printStackTrace();
                } catch (CannotReadException | InvalidAudioFrameException | ReadOnlyFileException ex) {
                    throw new RuntimeException(ex);
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
                resumeSong();
            }
        });

        // 创建继续按钮
        JButton createPlaylistButton = new JButton("Create");
        createPlaylistButton.setBounds(370, 420, 80, 30);
        contentPane.add(createPlaylistButton);

        createPlaylistButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        PlaylistCreator playlistCreator = new PlaylistCreator();
                        playlistCreator.setVisible(true);
                    }
                });
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
    Player player;
    private boolean isPaused = false;
    private void playSelectedSong() {
        String selectedSong = songList.getSelectedValue();
        if (selectedSong != null && !isPaused) {
            if (player != null) {
                player.close();
            }

            try {
                FileInputStream fis = new FileInputStream(selectedSong);
                BufferedInputStream bis = new BufferedInputStream(fis);
                player = new Player(bis);

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

    private void pauseSong() {
        if (player != null && !isPaused) {
            // 暂停播放
            player.close();
            isPaused = true;
        }
    }

    private void resumeSong() {
        if (player != null && isPaused) {
            // 重新播放
            try {
                FileInputStream fis = new FileInputStream(songList.getSelectedValue());
                BufferedInputStream bis = new BufferedInputStream(fis);
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

    public void addNewPlaylist(String playlistName) {
        DefaultListModel<String> playlistModel = (DefaultListModel<String>) playlistList.getModel(); // 获取主类中的歌单列表模型
        playlistModel.addElement(playlistName); // 将新歌单名称添加到列表模型中
        // 进一步执行你的逻辑，例如创建对应的歌曲列表等
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