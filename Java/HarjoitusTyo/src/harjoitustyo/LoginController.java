package harjoitustyo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Kirjautumis sivun Controller.
 * @author Jussi Kukkonen
 * @version 1.00 20.03.2020
 */
public class LoginController implements Initializable {
    
    private Connection yhteys;
    private Tunnus tunnusOlio;
    
    @FXML
    private TextField tfTunnus;
    @FXML
    private PasswordField pfSalasana;
    @FXML
    private Text txIlmoitus;
    
    @FXML
    void KirjauduBtn(ActionEvent event) throws IOException {
        kokeileKirjautua();
    }
    
    @FXML
    void KirjauduKey(KeyEvent event) throws IOException{
        switch(event.getCode()){
            case ENTER: kokeileKirjautua();
        }
    }
    
    @FXML
    void unohtuneetTunnukset(MouseEvent event) {
        System.out.println(tunnusOlio);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Kirjautuminen");
            alert.setHeaderText("Unohtuneet tunnukset.");
            alert.setContentText("Katsoppa Output :)");
            alert.showAndWait();
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
        suljeYhteys(yhteys);
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
    public void kokeileKirjautua() throws IOException {
        if(tfTunnus.getText().equals(tunnusOlio.getTunnus()) && pfSalasana.getText().equals(tunnusOlio.getSalasana())){
            Parent root =
                    FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Päävalikko");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        }
        else{
            txIlmoitus.setText("Väärin!");
        }
    }
}
