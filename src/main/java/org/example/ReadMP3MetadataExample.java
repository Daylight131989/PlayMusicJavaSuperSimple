package org.example;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class ReadMP3MetadataExample {
    public static void main(String[] args) {
        File file = new File("C:\\Users\\Daylight\\Music\\Taylor Swift - Cruel Summer.flac");

        try {
            AudioFile audioFile = AudioFileIO.read(file);
            Tag tag = audioFile.getTag();

            String title = tag.getFirst(FieldKey.TITLE);
            String artist = tag.getFirst(FieldKey.ARTIST);
            String album = tag.getFirst(FieldKey.ALBUM);
            String year = tag.getFirst(FieldKey.YEAR);
            String durationS = String.valueOf(audioFile.getAudioHeader().getTrackLength());

            int duration = Integer.parseInt(durationS);
            int minutes = duration / 60;
            int seconds = duration % 60;

            System.out.println("Title: " + title);
            System.out.println("Artist: " + artist);
            System.out.println("Album: " + album);
            System.out.println("Duration: " + minutes + ":" + seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
