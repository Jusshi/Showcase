package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.shape.Polygon;

/**
 * Tulostaulun määrittävä ja sitä ohjaava Controller
 * @author Jussi Kukkonen
 * @version 1.4.2021
 */
public class Tulostaulu implements Initializable {

    @FXML
    public Label lbEra;
    @FXML
    public Label lbKello;
    @FXML
    public Polygon pgKoti;
    @FXML
    public Polygon pgVieras;
    @FXML
    public Label lbAikalisatKoti;
    @FXML
    public Label lbAikalisatVieras;
    @FXML
    public Label lbAikalisaKlo;
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
