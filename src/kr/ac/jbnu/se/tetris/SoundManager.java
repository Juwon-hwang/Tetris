package src.kr.ac.jbnu.se.tetris;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private Clip clip;

    public SoundManager(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void play() {
        if (clip != null) {
            clip.setFramePosition(0); // 재생 위치를 처음으로 설정
            clip.start();
        }
    }

    public synchronized void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    public synchronized void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public synchronized void setVolume(float volume) {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float range = max - min;
            float adjustedGain = range * volume + min;

            gainControl.setValue(adjustedGain);
        }
    }
}