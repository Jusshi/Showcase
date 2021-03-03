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
 * @author JKukkonen & ... & ...
 * @version
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

    /////////////////KELLON METODIT///////////////////
    /**
     * Soittaa summeria painettessa sen painiketta
     */
    @FXML
    void soitaSummeri(ActionEvent event){
        pelikello.summeri();
    }

    /**
     * Käynnistää tai pysäyttää kellon tarpeen mukaan kutsumalla sille tarkoitettua tulostaulun metodia
     * @param event
     * @throws IOException
     * @throws LineUnavailableException
     */
    @FXML
    void kelloPainike(ActionEvent event) throws IOException, LineUnavailableException {
        kellonKaytto();
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
            paivitaMuutosPane();
        }else if(aikaLoppu) {   //jos ajanlaskenta ilmoittaa että kello on nollilla, soitetaan summeria
            tikittaa = false;
            pelikello.summeri();
            kelloAnim.stop();
            btnKelloPainike.setText("PÄÄLLE");
            vaihdaNeljannes();
        }else{  //pysäytetään kello
            tikittaa = false;
            kelloAnim.stop();
            btnKelloPainike.setText("PÄÄLLE");
            paivitaMuutosPane();
        }
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
        paivitaKello();   //päivitetään kellonaika kelloihin
    }

    /**
     * Asettaa pelikellolle käyttäjän syöttämän lähtölaskenta-ajan.
     * @param actionEvent
     */
    public void syotaLahtoAika(ActionEvent actionEvent) {
        try{
            minuutit = Integer.parseInt(tfLähtölaskentaMin.getText());
        } catch (NumberFormatException e){          //Virhe jos syöte ei ole kokonaisluku
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virheellinen syöte");
            alert.setHeaderText("Syötteen luku ei onnistu!");
            alert.setContentText("Aika ilmoitettava kokonaislukuna!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
        sekunnit = 0;
        kymmenosat = 0;
        paivitaKello();
    }

    /**
     * Paivittaa kelloon neljänneksen lähtötilanteen.
     */
    public void vaihdaNeljannes(){
        if(!neljannes){         //Jos nyt loppui lähtölaskenta eikä neljännes
            neljannes = true;
            minuutit = 0;
            sekunnit = 10;
            paivitaKello();
            aikaLoppu = false;
            lahtolaskuPane.setVisible(false);
            ajanmuutosPane.setVisible(true);
        }else{          //neljänneksen loppuessa asetetaan uudelle neljännekselle aika
            minuutit = 10;
            sekunnit = 0;
            paivitaKello();
            aikaLoppu = false;
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
    public void muutaAika(ActionEvent actionEvent) {
        minuutit = Integer.parseInt(tfMuutosMin.getText());
        sekunnit = Integer.parseInt(tfMuutosSek.getText());
        kymmenosat = Integer.parseInt(tfMuutosKym.getText());
        paivitaKello();
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
            //stage.setFullScreen(true);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
        paivitaKello();
    }
}