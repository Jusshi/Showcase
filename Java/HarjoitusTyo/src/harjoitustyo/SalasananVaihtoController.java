package harjoitustyo;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Salasanan vaihto lomakkeen Controller.
 * @author Jussi Kukkonen
 * @version 1.00 20.03.2020
 */
public class SalasananVaihtoController implements Initializable {
    
    private Connection yhteys;
    private Tunnus tunnusOlio;
    
    @FXML
    private PasswordField pfUusiSalasana2;
    @FXML
    private PasswordField pfUusiSalasana1;
    @FXML
    private PasswordField pfVanhaSalasana;
    @FXML
    private Text txIlmoitus;
    @FXML
    private Button btnPaluu;

    @FXML
    void kokeileSalasanaBtn(ActionEvent event) {
        tarkistaSalasana();
    }

    @FXML
    void kokeileSalasanaKey(KeyEvent event) {
        switch(event.getCode()){
            case ENTER: tarkistaSalasana();
        }
    }

    @FXML
    void vaihdaSalasanaBtn(ActionEvent event) {
        vaihdaSalasana();
    }

    @FXML
    void vaihdaSalasanaKey(KeyEvent event) {
        switch(event.getCode()){
            case ENTER: vaihdaSalasana();
        }
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
        try{
            tunnusOlio = Tunnus.haeTunnukset(yhteys);
        } catch (SQLException se) {
            System.out.print(se);
        } catch (Exception e) {
            System.out.print(e);
        }
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
     * Tarkistaa onko vanha salasana syötetty oikein.
     */
    public void tarkistaSalasana(){
        if(pfVanhaSalasana.getText().equals(tunnusOlio.getSalasana())){
            pfUusiSalasana1.setDisable(false);
            pfUusiSalasana2.setDisable(false);
            pfVanhaSalasana.setText("");
            txIlmoitus.setText("");
        }
        else txIlmoitus.setText("Väärin!");
    }
    /**
     * Vaihtaa salasanan tietokantaan.
     */
    public  void vaihdaSalasana() {
        if(pfUusiSalasana1.getText().equals(pfUusiSalasana2.getText())){
            if(!pfUusiSalasana2.getText().equals(tunnusOlio.getSalasana())){
                boolean salasana_vaihdettu = true;
                // asetetaan uusi salasana oliolle
                tunnusOlio.setSalasana(pfUusiSalasana2.getText());
                try {
                    System.out.println("koitetaan UPDATEe"); //debug
                    tunnusOlio.vaihdaSalasana(yhteys); //yritetaan vaihtaa salasanaa
                } catch (SQLException se) {
                    salasana_vaihdettu = false;
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Salasanan vaihto");
                    alert.setHeaderText("Tietokantavirhe");
                    alert.setContentText("Salasanan vaihtaminen ei onnistu.");
                    alert.showAndWait();
                    se.printStackTrace();
                } catch (Exception e) {
                    salasana_vaihdettu = false;
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Salasanan vaihto");
                    alert.setHeaderText("Tietokantavirhe");
                    alert.setContentText("Salasanan vaihtaminen ei onnistu.");
                    alert.showAndWait();
                    e.printStackTrace();
                } finally {
                    if (salasana_vaihdettu == true) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Salasanan vaihto");
                        alert.setHeaderText("Toiminto ok.");
                        alert.setContentText("Salasana vaihdettu.");				
                        alert.showAndWait();
                    }
                }
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Salasanan vaihto");
                alert.setHeaderText("Virhe!");
                alert.setContentText("Ei voi käyttää edellistä salasanaa.");
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Salasanan vaihto");
                alert.setHeaderText("Virhe!");
                alert.setContentText("Salasanat eivät täsmää.");
                alert.showAndWait();
        }
    }
}
