package sample;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Pelikellon määrittävä luokka.
 * @author JKukkonen
 * @version
 */
public class Pelikello {
    private String minuutit;
    private String sekunnit;
    private String kymmenosat;
    private Clip audioClip;
    private AudioInputStream audioStream;

    // GET-metodit
    /**
     * Hakee minuuttien määrän.
     * @return minuutit
     */
    public String getMinuutit() {
        return this.minuutit;
    }

    /**
     * Hakee sekunttien määrän.
     * @return sekunnit
     */
    public String getSekunnit() {
        return this.sekunnit;
    }

    /**
     * Hakee sekunttien määrän.
     * @return kymmenosat
     */
    public String getKymmenosat() {
        return this.kymmenosat;
    }

    // SET-metodit
    /**
     * Asettaa minuutit
     * @param min
     */
    public void setMinuutit(int min) {
        if (min < 10) {
            this.minuutit = "0" + min;
        } else {
            this.minuutit = min + "";
        }
    }

    /**
     * Asettaa sekunnit
     * @param sek
     */
    public void setSekunnit(int sek) {
        if (sek < 10) {
            this.sekunnit = "0" + sek;
        } else {
            this.sekunnit = sek + "";
        }
    }

    /**
     * Asettaa kymmenosat
     * @param kym
     */
    public void setKymmenosat(int kym, boolean nelj) {
        if(minuutit.equals("00") && nelj) {
            this.kymmenosat = "." + kym;
        }else{
            this.kymmenosat = "";
        }
    }

    /**
     * Soittaa summerin äänen.
     */
    public void summeri(){
        File audioFile = new File("src/summeri.wav");
        try {
            audioStream = AudioSystem.getAudioInputStream(audioFile);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        try {
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            audioStream.close();
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Palauttaa jäljellä olevan ajan määrän muodossa mm:ss.k
     * @return aikaa jäljellä
     */
    public String toString() {
        return this.minuutit + ":" + this.sekunnit + this.kymmenosat;
    }
}
