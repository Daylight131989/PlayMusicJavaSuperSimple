package org.example;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Image;
import java.io.File;

public class DisplayAlbumArtExample {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\Daylight\\Music\\Taylor Swift - Enchanted.mp3");

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            if (tag != null) {
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null) {
                    byte[] imageData = artwork.getBinaryData();
                    if (imageData != null) {
                        Image image = new ImageIcon(imageData).getImage();

                        JFrame frame = new JFrame();
                        JLabel label = new JLabel(new ImageIcon(image));
                        frame.add(label);
                        frame.pack();
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setVisible(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

