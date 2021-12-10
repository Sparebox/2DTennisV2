package game;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
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

    public final URL BALL_HIT = getClass().getResource("/clack.wav");
    public final URL SHOOT = getClass().getResource("/rocket_shoot.wav");
    public final URL EXPLOSION = getClass().getResource("/explosion.wav");
    public final URL POP = getClass().getResource("/pop.wav");
    public final URL CREAK = getClass().getResource("/creak.wav");
    public final URL VORTEX = getClass().getResource("/vortex.wav");
    public final URL WHOOSH = getClass().getResource("/whoosh.wav");

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
            this.sources.get(audioFile).play();
        } catch (ALException e) {};
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
