package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Tulostaulun ohjauspaneelin määrittävä ja sitä ohjaava Controller
 * @author Jussi Kukkonen
 * @version 1.4.2021
 */
public class Controller implements Initializable {

    ObservableList<Screen> screens = Screen.getScreens();
    Tulostaulu TulosT;
    private Pelikello pelikello = new Pelikello();
    private Timeline kelloAnim;
    private int sekunnit = 10;
    public int minuutit = 0;
    private int kymmenosat;
    private boolean tikittaa = false;
    private boolean aikaLoppu = false;
    private boolean neljannes = false;
    private int eraNro = 0;
    private int aikalisaAika = 60;
    private int aikalisatKoti;
    private int aikalisatVieras;
    private boolean aikalisatLoppuKoti = false;
    private boolean aikalisatLoppuVieras = false;
    private AudioInputStream audioStream;

    @FXML
    public Button btnNeljannes;
    @FXML
    public Label lbOhjKello;
    @FXML
    private Button btnKelloPainike;
    @FXML
    private TextField tfLähtölaskentaMin;
    @FXML
    public AnchorPane lahtolaskuPane;
    @FXML
    public AnchorPane ajanmuutosPane;
    @FXML
    public TextField tfMuutosMin;
    @FXML
    public TextField tfMuutosSek;
    @FXML
    public TextField tfMuutosKym;
    @FXML
    public Button btnMuutaAika;
    @FXML
    public Label lbAikalisaKlo;
    @FXML
    public Label lbAikalisatKoti;
    @FXML
    public Label lbAikalisatVieras;
    @FXML
    public Button btnAikalisaKoti;
    @FXML
    public Button btnAikalisaVieras;
    @FXML
    public Button btnAikalisaKotiPlus;
    @FXML
    public Button btnAikalisaKotiMiinus;
    @FXML
    public Button btnAikalisaVierasPlus;
    @FXML
    public Button btnAikalisaVierasMiinus;
    @FXML
    public Button btnOmaAikalisa;
    @FXML
    public TextField tfAikalisaPituus;
    @FXML
    public Polygon pgKoti;
    @FXML
    public Polygon pgVieras;

    /**
     * Soittaa summeria painettessa sen painiketta
     */
    @FXML
    void soitaSummeri(ActionEvent event){
        summeri();
    }

    /**
     * Käynnistää tai pysäyttää kellon tarpeen mukaan kutsumalla sille tarkoitettua tulostaulun metodia
     * @param event
     * @throws IOException
     * @throws LineUnavailableException
     */
    @FXML
    void kelloPainike(ActionEvent event) throws IOException, LineUnavailableException {
        if(!aikaLoppu){
            kellonKaytto();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Tauko");
            alert.setContentText("Mennäänkö tauolle?");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        tauko(eraNro);
                    } catch (IOException | LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }else{
                    paivitaMuutosPane();
                    aikaLoppu = false;
                }
            });
        }
    }

    @FXML
    public void aikalisaKoti(ActionEvent actionEvent) {
        aikalisa(true);
    }

    @FXML
    public void aikalisaVieras(ActionEvent actionEvent) {
        aikalisa(false);
    }

    @FXML
    public void aikalisaKotiPlus(ActionEvent actionEvent) {
        lisaaAikalisa(true);
        btnAikalisaKotiMiinus.setDisable(false);
    }

    @FXML
    public void aikalisaKotiMiinus(ActionEvent actionEvent) {
        vahennaAikalisa(true);
    }

    @FXML
    public void aikalisaVierasPlus(ActionEvent actionEvent) {
        lisaaAikalisa(false);
        btnAikalisaVierasMiinus.setDisable(false);
    }

    @FXML
    public void aikalisaVierasMiinus(ActionEvent actionEvent) {
        vahennaAikalisa(false);
    }

    /**
     * Tarkistaa käyttäjän syötteen ja asettaa aikalisälle uuden pituuden sen mukaan seuraavalle aikalisälle
     * @param actionEvent
     */
    @FXML
    public void maaritaAikalisa(ActionEvent actionEvent) {
        Integer syote = null;
        try{
            syote = Integer.parseInt(tfAikalisaPituus.getText());
        } catch (NumberFormatException e){          //Virhe jos syöte ei ole kokonaisluku
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Aikalisän pituus");
            alert.setHeaderText("Virheellinen syöte!");
            alert.setContentText("Aika ilmoitettava kokonaislukuna!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
        if(syote!= null) {
            if (syote < 1 || syote > 60) {  //Syötteen oltava sopivan arvoinen
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Aikalisän pituus");
                alert.setHeaderText("Virheellinen syöte!");
                alert.setContentText("Aika on oltava välillä 1-60 sekuntia.");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();
            } else {
                aikalisaAika = syote;
                lbAikalisaKlo.setText(Integer.toString(aikalisaAika));
            }
        }
    }
    /**
     * Soittaa summerin äänen ja värjää tulostaulun kehykset punaiseksi soinnin ajaksi.
     */
    public void summeri() {
        TulosT.BpPohjapane.setStyle("-fx-border-width: 20px ; -fx-border-color: red ;");
        Timeline punaKehys = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            TulosT.BpPohjapane.setStyle("-fx-border-width: 20px ; -fx-border-color: Black ;");
        }));
        punaKehys.setCycleCount(1);
        punaKehys.play();
        File audioFile = new File("src/summeri.wav");
        try {
            audioStream = AudioSystem.getAudioInputStream(audioFile);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        try {
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            audioClip.start();
            audioStream.close();
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tarpeen mukaan käynnistää tai pysäyttää kellon animaation. Kutsuu joka framella ajanlaskenta metodia.
     * @throws IOException
     * @throws LineUnavailableException
     */
    public void kellonKaytto() throws IOException, LineUnavailableException {
        if(!tikittaa) { //jos kello ei ole päällä, käynnistetään se
            kelloAnim = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                try {
                    ajanlaskenta(); //kutsutaan joka framella aikaa laskevaa metodia
                } catch (IOException | LineUnavailableException ioException) {
                    ioException.printStackTrace();
                }
            }));
            kelloAnim.setCycleCount(Timeline.INDEFINITE);
            tikittaa = true;
            kelloAnim.play();
            btnKelloPainike.setText("SEIS");
            btnAikalisaKoti.setDisable(true);
            btnAikalisaVieras.setDisable(true);
            btnOmaAikalisa.setDisable(true);
        }else if(aikaLoppu) {   //jos ajanlaskenta ilmoittaa että kello on nollilla, soitetaan summeria
            tikittaa = false;
            summeri();
            kelloAnim.stop();
            if(!neljannes) {                        //jos lähtölaskenta päättyy, vaihdetaan neljännekseen automaattisesti,
                btnKelloPainike.setText("PÄÄLLE");  //muuten odotetaan käyttäjältä varmistus tauolle menosta.
                vaihdaNeljannes();
            }else{
                btnKelloPainike.setText("TAUKO");
            }
        }else{  //pysäytetään kello
            tikittaa = false;
            kelloAnim.stop();
            btnKelloPainike.setText("PÄÄLLE");
            if(!aikalisatLoppuKoti && neljannes) {
                btnAikalisaKoti.setDisable(false);      //jos aikalisiä jäljellä ja jokin  neljännes on käynnissä,
                btnAikalisaKotiPlus.setDisable(false);  //on mahdollista ottaa aikalisä.
                btnOmaAikalisa.setDisable(false);
            }
            if(!aikalisatLoppuVieras && neljannes) {
                btnAikalisaVieras.setDisable(false);
                btnAikalisaVierasPlus.setDisable(false);
                btnOmaAikalisa.setDisable(false);
            }
        }
        paivitaMuutosPane();
    }

    /**
     * Laskee kellonaikaa joka animaation framella ja päivittää oikeat luvut tulostaululle sitä mukaa.
     * @throws IOException
     * @throws LineUnavailableException
     */
    private void ajanlaskenta() throws IOException, LineUnavailableException {
        if(kymmenosat == 0){
            if(sekunnit == 0){
                if(minuutit == 0){  //jos jokainen ajanmääre nollilla, pysäytetään kello ja tiedotetaan siitä eteenpäin
                    aikaLoppu = true;
                    kellonKaytto();
                }else{  //jos minuutteja jäljellä
                    if(minuutit == 2 && eraNro == 4){ //Jos on 4. neljännes, aikaa 2 min ja jommalla kummalla aikalisät nollilla,
                        if(aikalisatKoti == 0){       //lisätään automaattisesti yksi aikalisä.
                            aikalisatKoti++;
                            btnAikalisaKotiMiinus.setDisable(false);
                        }
                        if(aikalisatVieras == 0){
                            aikalisatVieras++;
                            btnAikalisaVierasMiinus.setDisable(false);
                        }
                        paivitaAikalisat();
                    }
                    minuutit--;
                    sekunnit = 59;
                    kymmenosat = 9;
                }
            }else{  //jos sekuntteja jäljellä
                sekunnit--;
                kymmenosat = 9;
            }
        }else{  //jos kymmenesosia jäljellä
            kymmenosat--;
        }
        paivitaKello();   //päivitetään kellonaika
    }

    /**
     * Avaa popup ikkunan joka kysyy tauon pituutta, ja vastauksen perusteella käynnistää tauon.
     * @param era
     * @throws IOException
     * @throws LineUnavailableException
     */
    public void tauko(int era) throws IOException, LineUnavailableException {
        PopUp popUp;
        int taukoaika;
        if(era == 2){
            taukoaika = 20;
        }else{
            taukoaika = 2;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("popUp.fxml"));
        Parent root = loader.load();
        popUp = loader.getController();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Taukoaika");
        popUp.apTaukoajanValinta.setVisible(true);
        popUp.rbAsetus.setText("Asetusten mukainen (" + taukoaika + " minuuttia)");
        stage.showAndWait();
        if(popUp.rbOma.isSelected() && popUp.omaAika != null){
            taukoaika = popUp.omaAika;
        }
        if(popUp.aikaValittu) {
            minuutit = taukoaika;
            neljannes = false;
            aikaLoppu = false;
            paivitaKello();
            kellonKaytto();
        }
    }

    /**
     * Avaa popup ikkunan jossa voi vaihtaa neljännesta ja vaihtaa sen.
     * @param actionEvent
     * @throws IOException
     */
    public void neljänneksenMuutos(ActionEvent actionEvent) throws IOException {
        PopUp popUp;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("popUp.fxml"));
        Parent root = loader.load();
        popUp = loader.getController();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Neljanneksen vaihto");
        popUp.apEranVaihto.setVisible(true);
        stage.showAndWait();

        if(popUp.syotettyEra != null) {
            eraNro = popUp.syotettyEra;
            if (popUp.rbVarsinainen.isSelected()) {
                eraNro--;
            } else {
                eraNro += 3;
            }
            vaihdaNeljannes();
        }
    }

    /**
     * Asettaa pelikellolle käyttäjän syöttämän lähtölaskenta-ajan.
     * @param actionEvent
     */
    public void syotaLahtoAika(ActionEvent actionEvent) {
        Integer syote = null;
        try{
            syote = Integer.parseInt(tfLahtolaskentaMin.getText());
        } catch (NumberFormatException e){          //Virhe jos syöte ei ole kokonaisluku
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virheellinen syöte");
            alert.setHeaderText("Syötteen luku ei onnistu!");
            alert.setContentText("Aika ilmoitettava kokonaislukuna!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
        if(syote!= null) {
            if (syote < 1 || syote > 99) {  //Syötteen oltava sopivan arvoinen
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Virheellinen syöte");
                alert.setHeaderText("Syötetty aika ei kelpaa!");
                alert.setContentText("Aika on oltava välillä 1-99 minuuttia.");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();
            } else {
                minuutit = syote;
                paivitaKello();
            }
        }
    }

    /**
     * Paivittaa kelloon neljänneksen lähtötilanteen.
     */
    public void vaihdaNeljannes(){
        neljannes = true;       //paivittaa ohjauspaneelin näkymän ja viimeisen minuutin esittämisen lähtölaskennan jäljiltä
        lahtolaskuPane.setVisible(false);
        ajanmuutosPane.setVisible(true);
        eraNro++;
        if(eraNro < 5) {    //jos varsinainen neljännes
            btnNeljannes.setText(eraNro + "");
            TulosT.lbEra.setText(eraNro + "");
            minuutit = 0;
            sekunnit = 5;
            aikaLoppu = false;
            paivitaKello();
            if(eraNro >= 2 && eraNro <= 4) {    //nollataan virheet tarvittaessa
                TulosT.lbKotijoukkueVirheet.setText("0");
                TulosT.lbVierasjoukkueVirheet.setText("0");
                btnKotijoukkueVirheet.setText("0");
                btnVierasjoukkueVirheet.setText("0");
                if(eraNro == 3){    //nollataan aikalisät
                    aikalisatKoti = 0;
                    aikalisatVieras = 0;
                    aikalisatLoppuKoti = false;
                    aikalisatLoppuVieras = false;
                    btnAikalisaKotiPlus.setDisable(false);
                    btnAikalisaKotiMiinus.setDisable(true);
                    btnAikalisaVierasPlus.setDisable(false);
                    btnAikalisaVierasMiinus.setDisable(true);
                    paivitaAikalisat();
                }
            }
        }else{  //jos jatkoerä
            btnNeljannes.setText("E" + (eraNro - 4));
            TulosT.lbEra.setText("E" + (eraNro - 4));
            minuutit = 0;
            sekunnit = 5;
            aikaLoppu = false;
            paivitaKello();
            aikalisatKoti = 0;  //nollataan aikalisät
            aikalisatVieras = 0;
            btnAikalisaKotiPlus.setDisable(false);
            btnAikalisaKotiMiinus.setDisable(true);
            btnAikalisaVierasPlus.setDisable(false);
            btnAikalisaVierasMiinus.setDisable(true);
            paivitaAikalisat();
        }
    }

    /**
     * Kellon pysäyttäessä päivittää sen hetkisen ajan tekstikenttiin joilla aikaa muokataan ja asettaa muutospainikkeen
     * käytettäväksi. Myös tyhjentää kentät ja asettaa painikkeen pois käytöstä kun kello on käynnissä.
     */
    public void paivitaMuutosPane(){
        if(!tikittaa) {     //Jos kello pysäytetty
            tfMuutosMin.setText(pelikello.getMinuutit());
            tfMuutosSek.setText(pelikello.getSekunnit());
            tfMuutosKym.setText(Integer.toString(kymmenosat));
            btnMuutaAika.setDisable(false);
        }else{              //jos kello käynnistetään
            tfMuutosMin.setText("");
            tfMuutosSek.setText("");
            tfMuutosKym.setText("");
            btnMuutaAika.setDisable(true);
        }
    }

    /**
     * Asettaa käyttäjän syöttämän ajan kelloon.
     * @param actionEvent
     */
    /**
     * Tarkistaa käyttäjän syötteet, ja asettaa syöttämän ajan kelloon.
     * @param actionEvent
     */
    public void muutaAika(ActionEvent actionEvent) {
        Integer min = null;
        Integer sek = null;
        Integer kym = null;
        try {
            min = Integer.parseInt(tfMuutosMin.getText());
            sek = Integer.parseInt(tfMuutosSek.getText());
            kym = Integer.parseInt(tfMuutosKym.getText());
        } catch (NumberFormatException e) {          //Virhe jos joku syötteistä ei ole kokonaisluku
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ajanmuutos");
            alert.setHeaderText("Syötteiden luku ei onnistu!");
            alert.setContentText("Ajanmääreet ilmoitettava kokonaislukuna!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
        if (min != null && sek != null && kym != null) {  //Jos syötteet olivat oikeanlaiset
            if (min >= 0 && min < 100 && sek >= 0 && sek < 60 && kym >= 0 && kym < 10) {  //Jos syötteet on vielä oikean suuruiset,
                minuutit = min;
                sekunnit = sek;
                kymmenosat = kym;
                aikaLoppu = false;
                btnKelloPainike.setText("PÄÄLLE");
                paivitaKello();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ajanmuutos");
                alert.setHeaderText("Syöte/syötteet väärän suuruiset!");
                alert.setContentText("Sallitut arvot:\nMinuutit: 0-99.\nSekunnit: 0-59\nKymmenosat: 0-9");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();
            }
        }
    }

    /**
     * Päivittää kelloihin sen kutsuhetkellä asetetun ajan
     * @return asetettu ajan määrä
     */
    public void paivitaKello(){
        pelikello.setMinuutit(minuutit);
        pelikello.setSekunnit(sekunnit);
        pelikello.setKymmenosat(kymmenosat, neljannes);
        TulosT.lbKello.setText(pelikello.toString());   //päivitetään kellonaika tulostaululle
        lbOhjKello.setText(pelikello.toString());  //ja ohjauspaneeliin
    }

    /**
     * Käynnistää aikalisän ja määrittää kummalle joukkueelle se on.
     * @param koti onko kotijoukkue
     */
    public void aikalisa(boolean koti){
        //Kutsutaan ajanlaskentaa sekunnin välein
        Timeline aikalisaAnim = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            aikalisanLaskenta();    //Kutsutaan ajanlaskentaa sekunnin välein
        }));
        aikalisaAnim.setCycleCount(60);
        aikalisaAnim.play();
        btnKelloPainike.setDisable(true);
        btnAikalisaKoti.setDisable(true);
        btnAikalisaKotiPlus.setDisable(true);
        btnAikalisaKotiMiinus.setDisable(true);
        btnAikalisaVieras.setDisable(true);
        btnAikalisaVierasPlus.setDisable(true);
        btnAikalisaVierasMiinus.setDisable(true);
        btnOmaAikalisa.setDisable(true);
        TulosT.lbAikalisaKlo.setVisible(true);
        lisaaAikalisa(koti);
        if(koti){                    //Näytetään tulostaulun nuolilla kumman aikalisä kyseessä.
            pgKoti.setVisible(true);
            TulosT.pgKoti.setVisible(true);
        }else{
            pgVieras.setVisible(true);
            TulosT.pgVieras.setVisible(true);
        }
    }

    /**
     * Laskee ja päivittää aikalisäkellon aikaa.
     */
    public void aikalisanLaskenta(){
        aikalisaAika--;
        if(aikalisaAika == 0){      //Suoritetaan tarvittavat toimenpiteet aikalisän loppuessa
            summeri();
            TulosT.lbAikalisaKlo.setVisible(false);
            TulosT.pgKoti.setVisible(false);
            TulosT.pgVieras.setVisible(false);
            pgKoti.setVisible(false);
            pgVieras.setVisible(false);
            aikalisaAika = 60;
            lbAikalisaKlo.setText("60");
            btnKelloPainike.setDisable(false);
        }else if(aikalisaAika < 10) {   //jos alle kymmenen niin "0" jäljellä olevan sekunnin eteen.
            lbAikalisaKlo.setText("0" + aikalisaAika);
            TulosT.lbAikalisaKlo.setText("0" + aikalisaAika);
        }else{                      //Päivitetään aikaa normaalisti
            lbAikalisaKlo.setText("" + aikalisaAika);
            TulosT.lbAikalisaKlo.setText("" + aikalisaAika);
            if(aikalisaAika == 10){ //soitetaan 10 sek kohdalla summeria
                summeri();
            }
        }
    }

    /**
     * Annetaan ilmoitetulle joukkueelle aikalisä
     */
    public void lisaaAikalisa(boolean koti){
        if(koti) {
            aikalisatKoti++;
            paivitaAikalisat();
            if (eraNro < 3 && aikalisatKoti == 2) {
                aikalisatLoppuKoti = true;
                btnAikalisaKotiPlus.setDisable(true);
                btnAikalisaKoti.setDisable(true);
            } else if (eraNro < 5 && aikalisatKoti == 3) {
                aikalisatLoppuKoti = true;
                btnAikalisaKotiPlus.setDisable(true);
                btnAikalisaKoti.setDisable(true);
            } else if (eraNro > 4) {
                aikalisatLoppuKoti = true;
                btnAikalisaKotiPlus.setDisable(true);
                btnAikalisaKoti.setDisable(true);
            }
        }else{
            aikalisatVieras++;
            paivitaAikalisat();
            if(eraNro < 3 && aikalisatVieras == 2){
                aikalisatLoppuVieras = true;
                btnAikalisaVierasPlus.setDisable(true);
                btnAikalisaVieras.setDisable(true);
            }else if(eraNro < 5 && aikalisatVieras == 3){
                aikalisatLoppuVieras = true;
                btnAikalisaVierasPlus.setDisable(true);
                btnAikalisaVieras.setDisable(true);
            }else if(eraNro > 4){
                aikalisatLoppuVieras = true;
                btnAikalisaVierasPlus.setDisable(true);
                btnAikalisaVieras.setDisable(true);
            }
        }
        if(aikalisatLoppuKoti && aikalisatLoppuVieras){
            btnOmaAikalisa.setDisable(true);
        }
    }

    /**
     * Vähennetään ilmoitetulta joukkueelta aikalisä
     */
    public void vahennaAikalisa(boolean koti) {
        if (koti) {
            aikalisatKoti--;
            btnAikalisaKotiPlus.setDisable(false);
            paivitaAikalisat();
            if (aikalisatKoti == 0) {
                btnAikalisaKotiMiinus.setDisable(true);
            }
            aikalisatLoppuKoti = false;
        }else{
            aikalisatVieras--;
            btnAikalisaVierasPlus.setDisable(false);
            paivitaAikalisat();
            if(aikalisatVieras == 0) {
                btnAikalisaVierasMiinus.setDisable(true);
            }
            aikalisatLoppuVieras = false;
        }
        btnOmaAikalisa.setDisable(false);
    }

    /**
     * Päivitetään aikalisien tilanne tulostaululle
     */
    public void paivitaAikalisat(){
        switch (aikalisatKoti) {    //tilannepäivitys kotijoukkueelle
            case 0 -> {
                lbAikalisatKoti.setText("");
                TulosT.lbAikalisatKoti.setText("");
            }
            case 1 -> {
                lbAikalisatKoti.setText("*");
                TulosT.lbAikalisatKoti.setText("*");
                TulosT.lbAikalisatKoti.getStyleClass().add("label-red");
            }
            case 2 -> {
                lbAikalisatKoti.setText("* *");
                TulosT.lbAikalisatKoti.setText("* *");
            }
            case 3 -> {
                lbAikalisatKoti.setText("* * *");
                TulosT.lbAikalisatKoti.setText("* * *");
            }
        }
        switch (aikalisatVieras) {      //tilannepäivitys vierasjoukkueelle
            case 0 -> {
                lbAikalisatVieras.setText("");
                TulosT.lbAikalisatVieras.setText("");
            }
            case 1 -> {
                lbAikalisatVieras.setText("*");
                TulosT.lbAikalisatVieras.setText("*");
                TulosT.lbAikalisatVieras.getStyleClass().add("label-red");
            }
            case 2 -> {
                lbAikalisatVieras.setText("* *");
                TulosT.lbAikalisatVieras.setText("* *");
            }
            case 3 -> {
                lbAikalisatVieras.setText("* * *");
                TulosT.lbAikalisatVieras.setText("* * *");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Tulostaulu.fxml")); //Avataan tulostaulu ikkuna
            Parent root = (Parent) loader.load();
            TulosT = loader.getController();
            Rectangle2D bounds = screens.get(1).getVisualBounds();
            Stage stage = new Stage();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setAlwaysOnTop(true);
            stage.setFullScreen(true);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
        paivitaKello();
    }
}