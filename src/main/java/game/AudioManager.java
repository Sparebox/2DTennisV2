package game;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Responsible for the game sound effects
 */
public final class AudioManager {

    public static final URL BALL_HIT = AudioManager.class.getResource("/clack.wav");
    public static final URL SHOOT = AudioManager.class.getResource("/rocket_shoot.wav");
    public static final URL EXPLOSION = AudioManager.class.getResource("/explosion.wav");
    public static final URL POP = AudioManager.class.getResource("/pop.wav");
    public static final URL CREAK = AudioManager.class.getResource("/creak.wav");
    public static final URL VORTEX = AudioManager.class.getResource("/vortex.wav");
    public static final URL WHOOSH = AudioManager.class.getResource("/whoosh.wav");
    public static final URL LOST = AudioManager.class.getResource("/game_over.wav");
    public static final URL WON = AudioManager.class.getResource("/game_win.wav");

    private Set<Clip> clips;

    /**
     * Creates an audio manager
     */
    public AudioManager() {
        this.clips = new HashSet<>();
        
        
    }
    /**
     * Plays the specified audio file and closes previously opened clips
     * @param audioFile the URL of an audio file to be played
     */
    public void playSound(URL audioFile) {
        if(clips.size() > 10) {
            var clipsToRemove = new HashSet<Clip>();
            for(var clip : clips) {
                if(!clip.isRunning()) {
                    clip.close();
                    clipsToRemove.add(clip);
                }
            }
            clips.removeAll(clipsToRemove);
        }
        try {
            var in = AudioSystem.getAudioInputStream(audioFile);
            var clip = AudioSystem.getClip();
            clips.add(clip);
            clip.open(in);
            clip.start();
            in.close();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes all clips that are left open
     */
    public void cleanUp() {
        for(var clip : clips) {
            clip.close();
        }
        clips.clear();
    }

    
}
