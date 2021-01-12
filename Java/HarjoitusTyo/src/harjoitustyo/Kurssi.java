package harjoitustyo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
* Luokka joka maarittaa kurssi-olion.
* @author Jussi Kukkonen
* @version 2.00 20.03.2020
*/
public class Kurssi{
//Attribuutit
	private int kurssinID;
	private String kurssiNimi;
	private int opintopisteet;
	private String kuvaus;
	
/**Konstruktori
*/
public Kurssi(){
}
//Setterit
/**Asettaa kurssille uuden ID:n
*@param kurssiID id numero
*/
public void setID(int kurssiID){
	this.kurssinID = kurssiID;
}
/**Asettaa kurssille uuden nimen
*@param kurssiNimi kurssin kurssiNimi
*/
public void setKurssiNimi(String kurssiNimi){
	this.kurssiNimi = kurssiNimi;
}
/**Asettaa kurssille uudet opintopisteet
*@param opintopisteet kurssin opintopisteet
*/
public void setLaajuus(int opintopisteet){
	this.opintopisteet = opintopisteet;
}
/**Asettaa kurssille uuden kuvauksen
*@param kuvaus kurssin kuvaus
*/
public void setKuvaus(String kuvaus){
	this.kuvaus = kuvaus;
}
//Getterit
/**Palauttaa kurssin ID:n
*@return kurssiID id numero
*/
public int getID(){
	return kurssinID;
}
/**Palauttaa kurssin nimen
*@return kurssiNimi kurssin kurssiNimi
*/
public String getKurssiNimi(){
	return kurssiNimi;
}
/**Palauttaa kurssin opintopisteet
*@return opintopisteet kurssin opintopisteet
*/
public int getLaajuus(){
	return opintopisteet;
}
/**Palauttaa kurssin kuvauksen
*@return kuvaus kurssin kuvaus
*/
public String getKuvaus(){
	return kuvaus;
}
/**Palauttaa kurssin tiedot merkkijonona
*@return Kurssin tiedot
*/
@Override
public String toString(){
	return kurssinID + ", " + kurssiNimi + 
	"\n" + kuvaus +
	"\n" + opintopisteet + "op";
}

/**hakee listan kursseista tietokannasta
* @param conn tietokantayhteys
* @return kurssiLista
* @throws java.sql.SQLException
*/
public static ObservableList<Kurssi> haeKurssiLista(Connection conn) throws SQLException, Exception {
    String sql = "SELECT kurssiID, kurssi_nimi FROM kurssi";
    ResultSet tulosjoukko = null;
    PreparedStatement lause;
    try {
        lause = conn.prepareStatement(sql);
        tulosjoukko = lause.executeQuery();
        }
    catch (SQLException se) {
        System.out.println(se); //debug
        throw se;
    }
    catch (Exception e) {
        System.out.println(e); //debug
        throw e;
    }
    ObservableList<Kurssi> kurssiLista = FXCollections.observableArrayList();
    try {
        while (tulosjoukko.next()) { //lisataan kurssit tulosjoukosta ObservableListille
            Kurssi kurssiOlio = new Kurssi();
            kurssiOlio.setID(tulosjoukko.getInt("kurssiID"));
            kurssiOlio.setKurssiNimi(tulosjoukko.getString("kurssi_nimi"));
            kurssiLista.add(kurssiOlio);
            System.out.println("listalle " + kurssiOlio);
        }
    } catch (SQLException e) { 
        System.out.println(e); //debug
        throw e; 
    }
    return kurssiLista;
} 

    /**
    * Staattinen metodi, jolla haetaan kurssin tiedot tietokannasta.
    * @param conn tietokantayhteys
    * @param id haettavan kurssin ID
    * @return haetuista tiedoista luotu Kurssi-olio
    * @throws java.sql.SQLException
    */
    public static Kurssi haeKurssi (Connection conn, int id) throws SQLException, Exception {
        String sql = "SELECT kurssiID, kurssi_nimi, opintopisteet, kuvaus " +
                    "FROM kurssi WHERE kurssiID = ?";
        ResultSet hakuTulokset = null; //tähän haetaan haun tulokset
        PreparedStatement lause;
    System.out.println("haetaan kurssia kannasta"); //debug
        try {
            lause = conn.prepareStatement(sql);  //valmistellaan string sql:stä lause
            lause.setInt(1, id); //asetetaan where ehtoon (?) arvo
            hakuTulokset = lause.executeQuery(); // suorita sql-lause
            if (hakuTulokset == null) {
                System.out.println("Kurssia ei loydy"); //debug
                throw new Exception("Kurssia ei loydy");
            }
        } catch (SQLException se) {
            System.out.println(se); //debug
            throw se;
        } catch (Exception e) {
            System.out.println(e); //debug
            throw e;
        }
    System.out.println("asetetaan tiedot olioon"); //debug
        Kurssi kurssiOlio = new Kurssi (); //alustetaan olio jolle sijoitetaan haun tulokset
        try {
            if (hakuTulokset.next() == true){
                kurssiOlio.setID (hakuTulokset.getInt("kurssiID"));
                kurssiOlio.setKurssiNimi (hakuTulokset.getString("kurssi_nimi"));
                kurssiOlio.setLaajuus(hakuTulokset.getInt("opintopisteet"));
                kurssiOlio.setKuvaus (hakuTulokset.getString("kuvaus"));
            }
        }catch (SQLException e) {
            throw e;
        }
        return kurssiOlio;
    }
/**
    * Metodi joka lisaa kurssin olioon syotetyt tiedot kantaan
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void lisaaKurssi (Connection conn) throws SQLException, Exception {
	String sql = "SELECT kurssiID" +
                    " FROM kurssi WHERE kurssiID = ?"; //tarkistetaan loytyyko kurssia kannasta
        ResultSet hakuTulos;
        PreparedStatement lause;
    System.out.println("tarkistetaan loytyyko kurssia kannasta"); //debug
        try {
            lause = conn.prepareStatement(sql); //valmistellaan string sql:stä lause
            lause.setInt(1, getID()); //asetetaan where ehtoon (?) arvo
            hakuTulos = lause.executeQuery(); // suorita sql-lause
            if (hakuTulos.next() == true) { // kurssi loytyi
    System.out.println("Kurssi on jo olemassa"); //debug
                throw new Exception("Kurssi on jo olemassa");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
        sql = "INSERT INTO kurssi " +
                "(kurssiID, kurssi_nimi, opintopisteet, kuvaus) " + 
                " VALUES (?, ?, ?, ?)";
    System.out.println("Kokeillaan lisata kurssi"); //debug
        try {
            lause = conn.prepareStatement(sql); //muodostetaan insert lause
            lause.setInt( 1, getID());          //ja lisätään olion tiedot inserttiin
            lause.setString(2, getKurssiNimi());
            lause.setInt(3, getLaajuus());
            lause.setString(4, getKuvaus());
            
            int lkm = lause.executeUpdate(); // suoritetaan sql-lause
        System.out.println("lkm " + lkm); //debug
            if (lkm == 0) {
                throw new Exception("Kurssin lisaaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
    /**
    * Muutetaan kurssin tietoja tietokantaan syotetylle id:lle. 
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void muutaKurssi(Connection conn) throws SQLException, Exception {
        String sql = "SELECT kurssiID" + 
                    " FROM kurssi WHERE kurssiID = ?"; //tarkistetaan loytyyhan kurssi kannasta
        ResultSet tulosjoukko;
        PreparedStatement lause;
        try {
            lause = conn.prepareStatement(sql); //valmistellaan sql:stä lause
            lause.setInt(1, getID()); // asetetaan where ehtoon (?) arvo
            tulosjoukko = lause.executeQuery(); // suorita sql-lause
            if (tulosjoukko.next() == false) { // kurssi ei löytynyt
        System.out.println("Kurssia ei löytynyt"); //debug
                throw new Exception("Kurssia ei loydy tietokannasta");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
        sql = "UPDATE kurssi " + 
                "SET kurssi_nimi = ?, opintopisteet = ?, kuvaus = ? " + 
                "WHERE kurssiID= ?";
        try {
            lause = conn.prepareStatement(sql); //valmistellaan UPDATE sql:stä lause
            lause.setString(1, getKurssiNimi());   //ja laitetaan olion attribuuttien arvot UPDATEen
            lause.setInt(2, getLaajuus());
            lause.setString(3, getKuvaus());
            lause.setInt(4, getID()); //where-ehdon arvo

            int lkm = lause.executeUpdate(); //suorita sql-lause
        System.out.println("lkm " + lkm); //debug    
            if (lkm == 0) {
                throw new Exception("Kurssin muuttaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
/**
    * Poistaa kurssin tiedot tietokannasta. 
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void poistaKurssi (Connection conn) throws SQLException, Exception {
        String sql = "DELETE FROM kurssi WHERE kurssiID = ?";
        try {
            PreparedStatement lause = conn.prepareStatement(sql);  //valmistellaan sql:stä lause
            lause.setInt( 1, getID()); // laitetaan arvo WHERE-ehtoon
            int lkm = lause.executeUpdate(); // suorita sql-lause
        System.out.println("lkm " + lkm); //debug    
            if (lkm == 0) {
                throw new Exception("Kurssin poistaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
/**Palauttaa kokoelman kurssin opiskelijoista
*@return opiskelijat kokoelma kurssin opiskelijoista
*/
/*public LinkedList<Opiskelija> getKurssinOpiskelijat(){
}
*/
}