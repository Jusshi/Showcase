package harjoitustyo;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Kurssi lomakkeen Controller.
 * @author Jussi Kukkonen
 * @version 1.00 20.03.2020
 */
public class KurssiLomakeController implements Initializable {
    
    private Connection yhteys;
    private Kurssi kurssiOlio;
    
    @FXML
    private TextField tfKurssiID;
    @FXML
    private TextField tfNimi;
    @FXML
    private TextField tfLaajuus;
    @FXML
    private TextArea taKuvaus;
    @FXML
    private Button btnPaluu;
    @FXML
    private TableView<Kurssi> tvKurssit;
    @FXML
    private TableColumn<Kurssi, Integer> tcID;
    @FXML
    private TableColumn<Kurssi, String> tcNimi;
    @FXML
    private ChoiceBox cbToiminto;

    @FXML
    void SuoritaToiminta(ActionEvent event) {
        String toiminto = (String) cbToiminto.getValue();
        switch(toiminto){
            case "Hae": haeTiedot();
            break;
            case "Lisää": lisaaTiedot();
                          ListaaKurssit();
            break;
            case "Muuta": muutaTiedot();
                          ListaaKurssit();
            break;
            case "Poista": poistaTiedot();
                           ListaaKurssit();
            break;
        }
    }
    
    @FXML
    void naytaOhjeet(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kurssin tietojen hallinta");
        alert.setHeaderText("Ohjeet:");
        alert.setContentText("Hallinnoidaksesi kurssin tietoja,\nsyötä kurssin ID-numero sekä tarvittavat tiedot ja valitse toiminto. "
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
        yhteys = avaaYhteys(
        "jdbc:mariadb://maria.westeurope.cloudapp.azure.com:3306/karelia_jushi?user=opiskelija&password=opiskelija"
        );
        tcID.setCellValueFactory(new PropertyValueFactory<>("kurssinID"));
        tcNimi.setCellValueFactory(new PropertyValueFactory<>("kurssiNimi"));
        ListaaKurssit();
        cbToiminto.getItems().addAll("Hae", "Lisää", "Muuta", "Poista");
        cbToiminto.setValue("Hae");
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
     * Hakee listauksen kursseista Kurssi-oliolta.
     */
    public void ListaaKurssit(){
        ObservableList<Kurssi> kurssiLista = FXCollections.observableArrayList();
        try {
            kurssiLista = Kurssi.haeKurssiLista(yhteys);
        } catch (SQLException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssien listaus");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
            se.printStackTrace ();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssien listaus");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
            e.printStackTrace ();
        }
        tvKurssit.setItems(kurssiLista);
    }
    
    /**
     * Hakee kurssin tiedot.
     */
    public  void haeTiedot() {
        kurssiOlio = null;
        try {
            kurssiOlio = Kurssi.haeKurssi(yhteys, Integer.parseInt(tfKurssiID.getText()));
        } catch (SQLException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
        }
        if (kurssiOlio.getKurssiNimi() == null) { //jos kurssia ei löydy, tyhjennetään kentät
            tfNimi.setText("");
            tfLaajuus.setText("");
            taKuvaus.setText("");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Kurssia ei loydy.");
            alert.showAndWait();
        }
        else {
            //jos kurssi loytyy, naytetaan tiedot
            tfNimi.setText(kurssiOlio.getKurssiNimi());
            tfLaajuus.setText(Integer.toString(kurssiOlio.getLaajuus()));
            taKuvaus.setText(kurssiOlio.getKuvaus());
        }
    }
    
    /**
     * Lisää kurssin tietokantaan.
     */
    public void lisaaTiedot() {
        boolean kurssi_lisatty = true;
        kurssiOlio = null;
        try {
            kurssiOlio = Kurssi.haeKurssi (yhteys, Integer.parseInt(tfKurssiID.getText()));
        } catch (SQLException se) {
            kurssi_lisatty = false;
            System.out.println(se); //debug
            se.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen lisaaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Kurssin tietojen lisääminen ei onnistu.");
            alert.showAndWait();		
        } catch (Exception e) {
            kurssi_lisatty = false;
            System.out.println(e); //debug
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen lisaaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Kurssin tietojen lisääminen ei onnistu.");
            alert.showAndWait();
        }
        if (kurssiOlio.getKurssiNimi() != null) { // kurssi on jo olemassa, näytetään tiedot
            kurssi_lisatty = false;
            tfNimi.setText(kurssiOlio.getKurssiNimi());
            tfLaajuus.setText(Integer.toString(kurssiOlio.getLaajuus()));
            taKuvaus.setText(kurssiOlio.getKuvaus());
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen lisaaminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Kurssi on jo olemassa.");
            alert.showAndWait();
        }
        else {
            // asetetaan tiedot oliolle
            kurssiOlio.setID(Integer.parseInt(tfKurssiID.getText()));
            kurssiOlio.setKurssiNimi(tfNimi.getText());
            kurssiOlio.setLaajuus(Integer.parseInt(tfLaajuus.getText()));
            kurssiOlio.setKuvaus(taKuvaus.getText());
            try {
                System.out.println("lisataan");
                kurssiOlio.lisaaKurssi(yhteys); // yritetään kirjoittaa kantaan
            } catch (SQLException se) {
                kurssi_lisatty = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Kurssin tietojen lisaaminen");
                alert.setHeaderText("Tietokantavirhe");
                alert.setContentText("Kurssin tietojen lisääminen ei onnistu.");
                alert.showAndWait();
                se.printStackTrace();
            } catch (Exception e) {
                kurssi_lisatty = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Kurssin tietojen lisaaminen");
                alert.setHeaderText("Tietokantavirhe");
                alert.setContentText("Kurssin tietojen lisääminen ei onnistu.");
                alert.showAndWait();
                e.printStackTrace();
            }finally {
                if (kurssi_lisatty == true) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Kurssin tietojen lisaaminen");
                    alert.setHeaderText("Toiminto ok.");
                    alert.setContentText("Kurssin tiedot lisatty tietokantaan.");
                    alert.showAndWait();
                }
            }
        }
    }
    
    /**
     * Muuttaa kurssin tietoja tietokannassa.
     */
    public  void muutaTiedot() {
        boolean kurssi_muutettu = true;
        // asetetaan tiedot oliolle
        kurssiOlio.setID(Integer.parseInt(tfKurssiID.getText()));
        kurssiOlio.setKurssiNimi(tfNimi.getText());
        kurssiOlio.setLaajuus(Integer.parseInt(tfLaajuus.getText()));
        kurssiOlio.setKuvaus(taKuvaus.getText());
        try {
            System.out.println("koitetaan UPDATEe"); //debug
            kurssiOlio.muutaKurssi(yhteys); //yritetaan muuttaa tietoja
        } catch (SQLException se) {
            kurssi_muutettu = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen muuttaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Kurssin tietojen muuttaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            kurssi_muutettu = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen muuttaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Kurssin tietojen muuttaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            if (kurssi_muutettu == true) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Kurssin tietojen muuttaminen");
                alert.setHeaderText("Toiminto ok.");
                alert.setContentText("Kurssin tiedot muutettu.");				
                alert.showAndWait();
            }
        }
    }
    
    /**
     * Poistaa kurssin tietokannasta.
     */
    public void poistaTiedot() {
        kurssiOlio = null;
        boolean kurssi_poistettu = false;
        try {
            kurssiOlio = kurssiOlio = Kurssi.haeKurssi (yhteys, Integer.parseInt(tfKurssiID.getText()));
        } catch (SQLException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen poistaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Kurssin tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen poistaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Kurssin tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        }
        if (kurssiOlio.getKurssiNimi() == null) { //jos kurssia ei löydy, tyhjennetään tiedot näytöltä
            tfNimi.setText("");
            tfLaajuus.setText("");
            taKuvaus.setText("");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Kurssia ei loydy.");
            alert.showAndWait();
            return;
        }
        else { // naytetaan poistettavan asiakkaan tiedot
            tfNimi.setText(kurssiOlio.getKurssiNimi());
            tfLaajuus.setText(Integer.toString(kurssiOlio.getLaajuus()));
            taKuvaus.setText(kurssiOlio.getKuvaus());
        }
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Kurssin tietojen poisto");
            alert.setHeaderText("Vahvista");
            alert.setContentText("Haluatko todella poistaa kurssin?");
            Optional<ButtonType> vastaus = alert.showAndWait();
            if (vastaus.get() == ButtonType.OK) {
                kurssiOlio.poistaKurssi(yhteys);
                kurssi_poistettu = true;
            }
        } catch (SQLException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen poisto");
            alert.setHeaderText("Virhe:");
            alert.setContentText("Kurssin tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            // muut virheet
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kurssin tietojen poisto");
            alert.setHeaderText("Virhe:");
            alert.setContentText("Kurssin tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            if (kurssi_poistettu == true) { // ainoastaan, jos vahvistettiin ja poisto onnistui
                tfKurssiID.setText("");
                tfNimi.setText("");
                tfLaajuus.setText("");
                taKuvaus.setText("");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Kurssin tietojen poisto");
                alert.setHeaderText("Results:");
                alert.setContentText("Kurssin tiedot poistettu tietokannasta.");
                alert.showAndWait();
                kurssiOlio = null;
            }
        }
    }
}
