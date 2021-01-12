package harjoitustyo;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Suoritus lomakkeen Controller.
 * @author Jussi Kukkonen
 * @version 1.00 20.03.2020
 */
public class SuoritusLomakeController implements Initializable {
    
    private Connection yhteys;
    private Suoritus suoritusOlio;
    private int valinta;
    
    @FXML
    private TextField tfHaettavaID;
    @FXML
    private RadioButton rbOpiskelija;
    @FXML
    private ToggleGroup tgValinta;
    @FXML
    private RadioButton rbKurssi;
    @FXML
    private TextField tfOpiskelijaID;
    @FXML
    private TextField tfKurssiID;
    @FXML
    private TextField tfArvosana;
    @FXML
    private TextField tfPvm;
    @FXML
    private Button btnPaluu;
    @FXML
    private TableView<Suoritus> tvSuoritukset;
    @FXML
    private TableColumn<Suoritus, Integer> tcOpiskelijaID;
    @FXML
    private TableColumn<Suoritus, Integer> tcKurssiID;
    @FXML
    private TableColumn<Suoritus, Integer> tcArvosana;
    @FXML
    private TableColumn<Suoritus, String> tcPvm;
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
    void HaeSuoritukset(ActionEvent event) {
        ListaaSuoritukset();
    }
    
    @FXML
    void naytaHallintaOhjeet(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Suoritusten hallinta");
        alert.setHeaderText("Ohjeet:");
        alert.setContentText("Hallinnoidaksesi suoritusten tietoja,\nsyötä tarvittavat tiedot , ja valitse toiminto. "
                + "\nHakemiseen tarvitaan sekä kurssin, että opiskelijan ID:t."
                + "\n\nToiminto vahvistetaan \"Suorita\" painikkeella.\n\nTakaisin päävalikkoon pääset \"Paluu\" painikkeella.");
        alert.showAndWait();
    }
    
    @FXML
    void naytaHakuOhjeet(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Suoritusten hallinta");
        alert.setHeaderText("Ohjeet:");
        alert.setContentText("Hakeaksesi suorituksia valitse haetko opiskelijan, vaiko kurssin suorituksia."
                + "\nSitten syötä ID-numero ja paina \"Hae\" painiketta. "
                + "\n\nTakaisin päävalikkoon pääset \"Paluu\" painikkeella.");
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
        tcOpiskelijaID.setCellValueFactory(new PropertyValueFactory<>("opiskelijaID"));
        tcKurssiID.setCellValueFactory(new PropertyValueFactory<>("kurssiID"));
        tcArvosana.setCellValueFactory(new PropertyValueFactory<>("arvosana"));
        tcPvm.setCellValueFactory(new PropertyValueFactory<>("suorPvm"));
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Yhteyden avaaminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Virhe yhteyttä avatessa.");
            alert.showAndWait();
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Yhteyden sulkeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Virhe yhteyttä suljettaessa.");
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    /**
     * Hakee listauksen suorituksista Suoritus-oliolta.
     * Riippuen valintapainikkeista haetaan joko opiskelijan tai kurssin suorituksia.
     */
    public void ListaaSuoritukset(){
        if(rbOpiskelija.isSelected()){
            valinta = 1;
        }
        else if(rbKurssi.isSelected()){
            valinta = 2;
        }
        try {
            tvSuoritukset.setItems(Suoritus.haeSuoritusLista(yhteys, valinta, Integer.parseInt(tfHaettavaID.getText())));
        } catch (SQLException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suoritusten hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
            se.printStackTrace ();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suoritusten hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
            e.printStackTrace ();
        }
    }
    
    /**
     * Hakee suorituksen tiedot.
     */
    public void haeTiedot() {
        suoritusOlio = null;
        if(tfOpiskelijaID.getText().equals("") || tfKurssiID.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Molemmat ID-kentät täytettävä!");
            alert.showAndWait();
            return;
        }
        try {
            suoritusOlio = Suoritus.haeSuoritus(yhteys, Integer.parseInt(tfOpiskelijaID.getText()),
                                                Integer.parseInt(tfKurssiID.getText()));
        } catch (SQLException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Haussa ilmeni virhe.");
            alert.showAndWait();
            e.printStackTrace();
        }
        if (suoritusOlio.getPvm() == null) { //jos Suoritusta ei löydy, tyhjennetään kentät
            tfArvosana.setText("");
            tfPvm.setText("");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Suoritusta ei loydy.");
            alert.showAndWait();
        }
        else {
            //jos suoritus loytyy, naytetaan tiedot
            tfArvosana.setText(Integer.toString(suoritusOlio.getArvosana()));
            tfPvm.setText(suoritusOlio.getPvm());
        }
    }
    
    /**
     * Lisää suorituksen tietokantaan.
     */
    public void lisaaTiedot() {
        boolean suoritus_lisatty = true;
        suoritusOlio = null;
        try {
            suoritusOlio = Suoritus.haeSuoritus(yhteys, Integer.parseInt(tfOpiskelijaID.getText()),
                                                Integer.parseInt(tfKurssiID.getText()));
        } catch (SQLException se) {
            suoritus_lisatty = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen lisaaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Suorituksen tietojen lisääminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            suoritus_lisatty = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen lisaaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Suorituksen tietojen lisääminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        }
        if (suoritusOlio.getPvm() != null) { //suoritus jo olemassa, näytetään tiedot
            suoritus_lisatty = false;
            tfArvosana.setText(Integer.toString(suoritusOlio.getArvosana()));
            tfPvm.setText(suoritusOlio.getPvm());
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen lisaaminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Suoritus on jo olemassa.");
            alert.showAndWait();
        }
        else {
            // asetetaan tiedot oliolle
            suoritusOlio.setKurssiID(Integer.parseInt(tfKurssiID.getText()));
            suoritusOlio.setOpiskelijaID(Integer.parseInt(tfOpiskelijaID.getText()));
            suoritusOlio.setArvosana(Integer.parseInt(tfArvosana.getText()));
            suoritusOlio.setPvm(tfPvm.getText());
            try {
                System.out.println("lisataan");
                suoritusOlio.lisaaSuoritus(yhteys); // yritetään kirjoittaa kantaan
            } catch (SQLException se) {
                suoritus_lisatty = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Suorituksen tietojen lisaaminen");
                alert.setHeaderText("Tietokantavirhe");
                alert.setContentText("Suorituksen tietojen lisääminen ei onnistu.");
                alert.showAndWait();
                se.printStackTrace();
            } catch (Exception e) {
                suoritus_lisatty = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Suorituksen tietojen lisaaminen");
                alert.setHeaderText("Tietokantavirhe");
                alert.setContentText("Suorituksen tietojen lisääminen ei onnistu.");
                alert.showAndWait();
                e.printStackTrace();
            }finally {
                if (suoritus_lisatty == true) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Suorituksen tietojen lisaaminen");
                    alert.setHeaderText("Toiminto ok.");
                    alert.setContentText("Suorituksen tiedot lisatty tietokantaan.");
                    alert.showAndWait();
                }
            }
        }
    }
    
    /**
     * Muuttaa suorituksen tietoja tietokannassa.
     */
    public  void muutaTiedot() {
        boolean suoritus_muutettu = true;
        // asetetaan tiedot oliolle
        suoritusOlio.setOpiskelijaID(Integer.parseInt(tfOpiskelijaID.getText()));
        suoritusOlio.setKurssiID(Integer.parseInt(tfKurssiID.getText()));
        suoritusOlio.setArvosana(Integer.parseInt(tfArvosana.getText()));
        suoritusOlio.setPvm(tfPvm.getText());
        try {
            System.out.println("koitetaan UPDATEe"); //debug
            suoritusOlio.muutaSuoritus(yhteys); //yritetaan muuttaa tietoja
        } catch (SQLException se) {
            suoritus_muutettu = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen muuttaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Suorituksen tietojen muuttaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            suoritus_muutettu = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen muuttaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Suorituksen tietojen muuttaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            if (suoritus_muutettu == true) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suorituksen tietojen muuttaminen");
                alert.setHeaderText("Toiminto ok.");
                alert.setContentText("Suorituksen tiedot muutettu.");				
                alert.showAndWait();
            }
        }
    }
    
    /**
     * Poistaa suorituksen tietokannasta.
     */
    public void poistaTiedot() {
        suoritusOlio = null;
        boolean suoritus_poistettu = false;
        try {
            suoritusOlio = Suoritus.haeSuoritus(yhteys, Integer.parseInt(tfOpiskelijaID.getText()),
                                                Integer.parseInt(tfKurssiID.getText()));
        } catch (SQLException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen poistaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Suorituksen tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen poistaminen");
            alert.setHeaderText("Tietokantavirhe");
            alert.setContentText("Suorituksen tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        }
        if (suoritusOlio.getPvm() == null) { //jos kurssia ei löydy, tyhjennetään tiedot näytöltä
            tfArvosana.setText("");
            tfPvm.setText("");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen hakeminen");
            alert.setHeaderText("Virhe");
            alert.setContentText("Suoritusta ei loydy.");
            alert.showAndWait();
            return;
        }
        else { // naytetaan poistettavan asiakkaan tiedot
            tfArvosana.setText(Integer.toString(suoritusOlio.getArvosana()));
            tfPvm.setText(suoritusOlio.getPvm());
        }
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Suorituksen tietojen poisto");
            alert.setHeaderText("Vahvista");
            alert.setContentText("Haluatko todella poistaa suorituksen ?");
            Optional<ButtonType> vastaus = alert.showAndWait();
            if (vastaus.get() == ButtonType.OK) {
                suoritusOlio.poistaSuoritus(yhteys);
                suoritus_poistettu = true;
            }
        } catch (SQLException se) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen poisto");
            alert.setHeaderText("Virhe:");
            alert.setContentText("Suorituksen tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            se.printStackTrace();
        } catch (Exception e) {
            // muut virheet
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Suorituksen tietojen poisto");
            alert.setHeaderText("Virhe:");
            alert.setContentText("Suorituksen tietojen poistaminen ei onnistu.");
            alert.showAndWait();
            e.printStackTrace();
        } finally {
            if (suoritus_poistettu == true) { // ainoastaan, jos vahvistettiin ja poisto onnistui
                tfOpiskelijaID.setText("");
                tfKurssiID.setText("");
                tfArvosana.setText("");
                tfPvm.setText("");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Suorituksen tietojen poisto");
                alert.setHeaderText("Results:");
                alert.setContentText("Suorituksen tiedot poistettu tietokannasta.");
                alert.showAndWait();
                suoritusOlio = null;
            }
        }
    }
}
