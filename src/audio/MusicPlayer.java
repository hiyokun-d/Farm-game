package audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class MusicPlayer {
    private Clip clip;
    private FloatControl volumeControl;

    public void playLoop(String resourcepath) {
        stop();

        try {
            InputStream is = getClass().getResourceAsStream(resourcepath);
            if(is == null) {
                System.out.println("The audio " + resourcepath + " is not found! or it's missing try to rename it or do something with it");
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));

            clip = AudioSystem.getClip();
            clip.open(audioStream);

            if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                setVolume(5);
            }

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if(clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    public void setVolume(float db) {
        if(volumeControl != null) {
            volumeControl.setValue(db);
        }
    }
}
