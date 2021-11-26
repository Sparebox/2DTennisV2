package game;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public final class AudioManager {

    public final File BALL_HIT;
    public final File SHOOT;
    public final File EXPLOSION;
    public final File POP;
    public final File CREAK;
    public final File VORTEX;
    public final File WHOOSH;

    private Set<Clip> clips;

    public AudioManager() throws URISyntaxException {
        this.clips = new HashSet<>();
        BALL_HIT = new File(getClass().getResource("/clack.wav").toURI());
        SHOOT = new File(getClass().getResource("/rocket_shoot.wav").toURI());
        EXPLOSION = new File(getClass().getResource("/explosion.wav").toURI());
        POP = new File(getClass().getResource("/pop.wav").toURI());
        CREAK = new File(getClass().getResource("/creak.wav").toURI());
        VORTEX = new File(getClass().getResource("/vortex.wav").toURI());
        WHOOSH= new File(getClass().getResource("/whoosh.wav").toURI());
    }

    public void playSound(File audioFile) {
        if(!clips.isEmpty()) {
            var toRemove = new HashSet<Clip>();
            for(var clip : clips) {
                if(!clip.isRunning()) {
                    clip.close();
                    toRemove.add(clip);
                }
            }
            clips.removeAll(toRemove);
            toRemove.clear();
        }
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clips.add(clip);
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        } 
    }

    public void cleanUp() {
        for(var clip : clips) {
            clip.close();
        }
        clips.clear();
    }

    
}
