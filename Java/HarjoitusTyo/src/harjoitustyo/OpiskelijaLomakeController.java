package harjoitustyo;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Opiskelija lomakkeen Controller.
 * @author Jussi Kukkonen
 * @version 1.00 20.03.2020
 */
public class OpiskelijaLomakeController implements Initializable {
    
    private Connection yhteys;
    private Opiskelija opiskelijaOlio;
    
    @FXML
    private TextField tfOpiskelijaID;
    @FXML
    private TextField tfEtunimi;
    @FXML
    private TextField tfSukunimi;
    @FXML
    private TextField tfLahiosoite;
    @FXML
    private TextField tfPostitoimipaikka;
    @FXML
    private TextField tfPostinro;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPuhelinnro;
    @FXML
    private Button btnPaluu;
    @FXML
    private ChoiceBox cbToiminto;

    @FXML
    void SuoritaToiminta(ActionEvent event) {
        String toiminto = (String) cbToiminto.getValue();
        switch(toiminto){
            case "Hae": haeTiedot();
            break;
            case "Lisää": lisaaTiedot();
            break;
            case "Muuta": muutaTiedot();
            break;
            case "Poista": poistaTiedot();
            break;
        }
    }
    
    @FXML
    void naytaOhjeet(ActionEvent event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Opiskelijan tietojen hallinta");
        alert.setHeaderText("Ohjeet:");
        alert.setContentText("Hallinnoidaksesi opiskelijan tietoja,\nsyötä opiskelijan ID-numero sekä tarvittavat tiedot ja valitse toiminto. "
                + "\n\nToiminto vahvistetaan \"Suorita\" painikkeella.\n\nTakaisin päävalikkoon pääset \"Paluu\" painikkeella.");
        alert.showAndWait();
    }
    
    @FXML
    void paluu(ActionEvent event) {
        suljeYhteys(yhteys);
        Stage stage = (Stage)btnPaluu.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbToiminto.getItems().addAll("Hae", "Lisää", "Muuta", "Poista");
        cbToiminto.setValue("Hae");
        yhteys = avaaYhteys(
        "jdbc:mariadb://maria.westeurope.cloudapp.azure.com:3306/karelia_jushi?user=opiskelija&password=opiskelija"
        );
    }    
    
    /**
     * Avaa yhteyden tietokantaan.
     * @param yhteysOsoite
     * @return 
     */
    private static Connection avaaYhteys(String yhteysOsoite){
        try {
            Connection yhteys = DriverManager.getConnection(yhteysOsoite);
            System.out.println("Yhteys avattu"); //debug
            return yhteys;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Sulkee yhteyden tietokantaan.
     * @param conn 
     */
    private static void suljeYhteys(Connection conn){
        try {
            if (conn != null){
                conn.close();
                System.out.println("Yhteys suljettu"); //debug
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Hakee opiskelijan tiedot.
     */
    public  void haeTiedot() {
        opiskelijaOlio = null;
        try {
            opiskelijaOlio = Opiskelija.haeOpiskelija(yhteys, Integer.parseInt(tfOpiskelijaID.getText()));
        } catch (SQLException se) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
        }
        if (opiskelijaOlio.getEtunimi() == null) { //jos opiskelijaa ei löydy, tyhjennetään kentät
            tfEtunimi.setText("");
            tfSukunimi.setText("");
            tfLahiosoite.setText("");
            tfPostinro.setText("");
            tfPostitoimipaikka.setText("");
            tfEmail.setText("");
            tfPuhelinnro.setText("");
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Opiskelijaa ei loydy.");
            alert.showAndWait();
        }
        else {
            //jos opiskelija loytyy, naytetaan tiedot
            tfEtunimi.setText(opiskelijaOlio.getEtunimi());
            tfSukunimi.setText(opiskelijaOlio.getSukunimi());
            tfLahiosoite.setText(opiskelijaOlio.getLahiosoite());
            tfPostinro.setText(opiskelijaOlio.getPostinro());
            tfPostitoimipaikka.setText(opiskelijaOlio.getPostitoimipaikka());
            tfEmail.setText(opiskelijaOlio.getEmail());
            tfPuhelinnro.setText(opiskelijaOlio.getPuhelinnro());
        }
    }
    
    /**
     * Lisää opiskelijan tietokantaan.
     */
    public void lisaaTiedot() {
        boolean opiskelija_lisatty = true;
        opiskelijaOlio = null;
        try {
            opiskelijaOlio = Opiskelija.haeOpiskelija (yhteys, Integer.parseInt(tfOpiskelijaID.getText()));
        } catch (SQLException se) {
            opiskelija_lisatty = false;
            System.out.println(se); //debug
            se.printStackTrace();
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen lisaaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Opiskelijan tietojen lisääminen ei onnistu.");
            alert.showAndWait();		
        } catch (Exception e) {
            opiskelija_lisatty = false;
            System.out.println(e); //debug
            e.printStackTrace();
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen lisaaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Opiskelijan tietojen lisääminen ei onnistu.");
            alert.showAndWait();
        }
        if (opiskelijaOlio.getEtunimi() != null) { // opiskelija jo olemassa, näytetään tiedot
            opiskelija_lisatty = false;
            tfEtunimi.setText(opiskelijaOlio.getEtunimi());
            tfSukunimi.setText(opiskelijaOlio.getSukunimi());
            tfLahiosoite.setText(opiskelijaOlio.getLahiosoite());
            tfPostinro.setText(opiskelijaOlio.getPostinro());
            tfPostitoimipaikka.setText(opiskelijaOlio.getPostitoimipaikka());
            tfEmail.setText(opiskelijaOlio.getEmail());
            tfPuhelinnro.setText(opiskelijaOlio.getPuhelinnro());
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen lisaaminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Opiskelija on jo olemassa.");
            alert.showAndWait();
        }
        else {
            // asetetaan tiedot oliolle
            opiskelijaOlio.setID(Integer.parseInt(tfOpiskelijaID.getText()));
            opiskelijaOlio.setEtunimi(tfEtunimi.getText());
            opiskelijaOlio.setSukunimi(tfSukunimi.getText());
            opiskelijaOlio.setLahiosoite(tfLahiosoite.getText());
            opiskelijaOlio.setPostinro(tfPostinro.getText());
            opiskelijaOlio.setPostitoimipaikka(tfPostitoimipaikka.getText());
            opiskelijaOlio.setEmail(tfEmail.getText());
            opiskelijaOlio.setPuhelinnro(tfPuhelinnro.getText());
            try {
                System.out.println("lisataan");
                opiskelijaOlio.lisaaOpiskelija(yhteys); // yritetään kirjoittaa kantaan
            } catch (SQLException se) {
                opiskelija_lisatty = false;
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Opiskelijan tietojen lisaaminen");
                alert.setHeaderText("Tietokantavirhe");
                alert.setContentText("Opiskelijan tietojen lisääminen ei onnistu.");
                alert.showAndWait();
                se.printStackTrace();
            } catch (Exception e) {
                opiskelija_lisatty = false;
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Opiskelijan tietojen lisaaminen");
                alert.setHeaderText("Tietokantavirhe");
                alert.setContentText("Opiskelijan tietojen lisääminen ei onnistu.");
                alert.showAndWait();
                e.printStackTrace();
            }finally {
                if (opiskelija_lisatty == true) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Opiskelijan tietojen lisaaminen");
                    alert.setHeaderText("Toiminto ok.");
                    alert.setContentText("Opiskelijan tiedot lisatty tietokantaan.");
                    alert.showAndWait();
                }
            }
        }
    }
    
    /**
     * Muuttaa opiskelijan tietoja tietokannassa.
     */
    public  void muutaTiedot() {
        boolean opiskelija_muutettu = true;
        // asetetaan tiedot oliolle
        opiskelijaOlio.setEtunimi(tfEtunimi.getText());
        opiskelijaOlio.setSukunimi(tfSukunimi.getText());
        opiskelijaOlio.setLahiosoite(tfLahiosoite.getText());
        opiskelijaOlio.setPostinro(tfPostinro.getText());
        opiskelijaOlio.setPostitoimipaikka(tfPostitoimipaikka.getText());
        opiskelijaOlio.setEmail(tfEmail.getText());
        opiskelijaOlio.setPuhelinnro(tfPuhelinnro.getText());
        try {
            System.out.println("koitetaan UPDATEe"); //debug
            opiskelijaOlio.muutaOpiskelija (yhteys); //yritetaan muuttaa tietoja
        } catch (SQLException se) {
            opiskelija_muutettu = false;
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen muuttaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Opiskelijan tietojen muuttaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            opiskelija_muutettu = false;
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen muuttaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Opiskelijan tietojen muuttaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            if (opiskelija_muutettu == true) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Opiskelijan tietojen muuttaminen");
                alert.setHeaderText("Toiminto ok.");
                alert.setContentText("Opiskelijan tiedot muutettu.");				
                alert.showAndWait();
            }
        }
    }
    
    /**
     * Poistaa opiskelijan tietokannasta.
     */
    public void poistaTiedot() {
        opiskelijaOlio = null;
        boolean opiskelija_poistettu = false;
        try {
            opiskelijaOlio = Opiskelija.haeOpiskelija (yhteys, Integer.parseInt(tfOpiskelijaID.getText()));
        } catch (SQLException se) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen poistaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Opiskelijan tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen poistaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Opiskelijan tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        }
        if (opiskelijaOlio.getEtunimi() == null) { //jos opiskelijaa ei löydy, tyhjennetään tiedot näytöltä
            tfEtunimi.setText("");
            tfSukunimi.setText("");
            tfLahiosoite.setText("");
            tfPostinro.setText("");
            tfPostitoimipaikka.setText("");
            tfEmail.setText("");
            tfPuhelinnro.setText("");
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen poisto");
            alert.setHeaderText("Virhe");
            alert.setContentText("Opiskelijaa ei loydy.");
            alert.showAndWait();
            return;
        }
        else { // naytetaan poistettavan asiakkaan tiedot
            tfEtunimi.setText(opiskelijaOlio.getEtunimi());
            tfSukunimi.setText(opiskelijaOlio.getSukunimi());
            tfLahiosoite.setText(opiskelijaOlio.getLahiosoite());
            tfPostinro.setText(opiskelijaOlio.getPostinro());
            tfPostitoimipaikka.setText(opiskelijaOlio.getPostitoimipaikka());
            tfEmail.setText(opiskelijaOlio.getEmail());
            tfPuhelinnro.setText(opiskelijaOlio.getPuhelinnro());
        }
        try {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Opiskelijan tietojen poisto");
            alert.setHeaderText("Vahvista");
            alert.setContentText("Haluatko todella poistaa opiskelijan?");
            Optional<ButtonType> vastaus = alert.showAndWait();
            if (vastaus.get() == ButtonType.OK) {
                opiskelijaOlio.poistaOpiskelija(yhteys);
                opiskelija_poistettu = true;
            }
        } catch (SQLException se) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen poisto");
            alert.setHeaderText("Virhe:");
            alert.setContentText("Opiskelijan tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            // muut virheet
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Opiskelijan tietojen poisto");
            alert.setHeaderText("Virhe:");
            alert.setContentText("Opiskelijan tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            if (opiskelija_poistettu == true) { // ainoastaan, jos vahvistettiin ja poisto onnistui
                tfOpiskelijaID.setText("");
                tfEtunimi.setText("");
                tfSukunimi.setText("");
                tfLahiosoite.setText("");
                tfPostinro.setText("");
                tfPostitoimipaikka.setText("");
                tfEmail.setText("");
                tfPuhelinnro.setText("");
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Opiskelijan tietojen poisto");
                alert.setHeaderText("Suoritettu:");
                alert.setContentText("Opiskelijan tiedot poistettu tietokannasta.");
                alert.showAndWait();
                opiskelijaOlio = null;
            }
        }
    }
}