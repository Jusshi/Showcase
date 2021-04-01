package sample;

/**
 * Pelikellon määrittävä luokka.
 * @author Jussi Kukkonen
 * @version 1.4.2021
 */
public class Pelikello {
    private String minuutit;
    private String sekunnit;
    private String kymmenosat;

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
     * Palauttaa jäljellä olevan ajan määrän muodossa mm:ss.k
     * @return aikaa jäljellä
     */
    public String toString() {
        return this.minuutit + ":" + this.sekunnit + this.kymmenosat;
    }
}
