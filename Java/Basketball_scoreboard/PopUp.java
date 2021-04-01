package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Popup ikkuna jota voidaan käyttä erinäisiin syötteiden kyselyihin/varmistuksiin
 * @author Jussi Kukkonen
 * @version 1.4.2021
 */
public class PopUp {

    public Integer syotettyEra = null;
    public Integer omaAika = null;
    public boolean aikaValittu = false;
    @FXML
    public AnchorPane apTaukoajanValinta;
    @FXML
    public Button btnTaukoValinta;
    @FXML
    public ToggleGroup tgTaukoajat;
    @FXML
    public RadioButton rbAsetus;
    @FXML
    public RadioButton rbOma;
    @FXML
    public AnchorPane apEranVaihto;
    @FXML
    public TextField tfOmaTaukoaika;
    @FXML
    public Button btnEranvaihto;
    @FXML
    public TextField tfEraNro;
    @FXML
    public ToggleGroup tgEraValinnat;
    @FXML
    public RadioButton rbVarsinainen;
    @FXML
    public RadioButton rbJatko;

    public void asetusValittu(ActionEvent actionEvent) {
        tfOmaTaukoaika.setDisable(true);
    }

    public void omaValittu(ActionEvent actionEvent) {
        tfOmaTaukoaika.setDisable(false);
    }

    /**
     * Sulkee ikkunan + Mikäli käyttäjä syöttää oman ajan tauolle, varmistetaan syötteen sopivuus.
     *
     * @param actionEvent
     */
    public void valitseTaukoaika(ActionEvent actionEvent) {
        if (rbOma.isSelected()) { // jos syötetään oma aika
            try {
                omaAika = Integer.parseInt(tfOmaTaukoaika.getText());
            } catch (NumberFormatException e) {          //Virhe jos syöte ei ole kokonaisluku
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Virheellinen syöte");
                alert.setHeaderText("Syötteen luku ei onnistu!");
                alert.setContentText("Aika ilmoitettava kokonaislukuna!");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();
            }
            if (omaAika != null) {   //Jos syöte oli oikeanlainen
                if (omaAika < 100 && omaAika > 0) {  //Syötteen oltava sopivan arvoinen
                    aikaValittu = true;
                    Stage stage = (Stage) btnTaukoValinta.getScene().getWindow();
                    stage.close();              //Jos syöte kunnossa, suljetaan ikkuna
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Virheellinen syöte");
                    alert.setHeaderText("Syötetty aika ei kelpaa!");
                    alert.setContentText("Aika on oltava välillä 1-99 minuuttia.");
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.showAndWait();
                }
            }
        } else {    //Jos valitaan asetusten mukainen aika
            aikaValittu = true;
            Stage stage = (Stage) btnTaukoValinta.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Varmistaa syötteen sopivuuden ja sulkee ikkunan.
     * @param actionEvent
     */
    public void vaihdaNeljannes(ActionEvent actionEvent) {
        try {
            syotettyEra = Integer.parseInt(tfEraNro.getText());
        } catch (NumberFormatException e) {          //Virhe jos syöte ei ole kokonaisluku
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virheellinen syöte");
            alert.setHeaderText("Syötteen luku ei onnistu!");
            alert.setContentText("Eränumero ilmoitettava kokonaislukuna!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
        if (syotettyEra != null) {  //Jos syöte oli oikeanlainen
            if (syotettyEra > 0) {  //Jos syöte on myös enemmän kuin nolla, suljetaan ikkuna
                Stage stage = (Stage) btnTaukoValinta.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Virheellinen syöte");
                alert.setHeaderText("Syötetty eränumero ei kelpaa!");
                alert.setContentText("Eränumero ei voi olla vähempää kuin yksi.");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();
            }
        }
    }
}
