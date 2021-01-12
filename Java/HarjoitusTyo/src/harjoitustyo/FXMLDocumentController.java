package harjoitustyo;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Päävalikon Controller.
 * @author Jussi Kukkonen
 * @version 1.00 20.03.2020
 */
public class FXMLDocumentController {
    
    @FXML
    void avaaKurssit(ActionEvent event) throws IOException {
        Parent root =
                FXMLLoader.load(getClass().getResource("KurssiLomake.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Kurssit");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void avaaOpiskelijat(ActionEvent event) throws IOException {
        Parent root =
                FXMLLoader.load(getClass().getResource("OpiskelijaLomake.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Opiskelijat");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void avaaSuoritukset(ActionEvent event) throws IOException {
        Parent root =
                FXMLLoader.load(getClass().getResource("SuoritusLomake.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Suoritukset");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    @FXML
    void avaaSalasananVaihto(ActionEvent event) throws IOException {
        Parent root =
                FXMLLoader.load(getClass().getResource("SalasananVaihto.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Salasanan vaihto");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    @FXML
    void lopeta(ActionEvent event){
        System.exit(0);
    }
}