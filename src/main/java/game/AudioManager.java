package game;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.urish.openal.ALException;
import org.urish.openal.OpenAL;
import org.urish.openal.Source;

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

    private HashMap<URL, Source> sources;
    private OpenAL openal;

    /**
     * Creates an audio manager
     */
    public AudioManager() {
        this.sources = new HashMap<>();
        try {
            this.openal = new OpenAL();
            this.sources.put(BALL_HIT, openal.createSource(BALL_HIT));
            this.sources.put(SHOOT, openal.createSource(SHOOT));
            this.sources.put(EXPLOSION, openal.createSource(EXPLOSION));
            this.sources.put(POP, openal.createSource(POP));
            this.sources.put(CREAK, openal.createSource(CREAK));
            this.sources.put(VORTEX, openal.createSource(VORTEX));
            this.sources.put(WHOOSH, openal.createSource(WHOOSH));
            this.sources.put(LOST, openal.createSource(LOST));
            this.sources.put(WON, openal.createSource(WON));
        } catch (ALException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        
    }
    /**
     * Plays the specified audio file
     * @param audioFile the URL of an audio file to be played
     */
    public void playSound(URL audioFile) {
        try {
            var source = this.sources.get(audioFile);
            source.stop();
            source.play();
        } catch (ALException e) {e.printStackTrace();}
    }

    /**
     * Closes all audio streams that are left open
     */
    public void cleanUp() {
        for(var entry : sources.entrySet()) {
            entry.getValue().close();
        }
        openal.close();
        sources.clear();
    }

    
}
