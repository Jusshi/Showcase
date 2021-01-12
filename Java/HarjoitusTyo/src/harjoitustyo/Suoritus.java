package harjoitustyo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
* Luokka joka maarittaa suoritus-olion.
* @author Jussi Kukkonen
* @version 2.00 20.03.2020
*/
public class Suoritus{
//Attribuutit
	private int opiskelijaID;
	private int kurssiID;
	private int arvosana;
	private String suorPvm; //dd.MM.yyyy

/**Konstruktori
*/
public Suoritus(){
}
//Setterit
/**Asettaa suoritukselle uuden opiskelijan ID:n
*@param id uusi opiskelijaID
*/
public void setOpiskelijaID(int id){
	opiskelijaID = id;
}
/**Asettaa suoritukselle uuden kurssi ID:n
*@param id uusi kurssiID
*/
public void setKurssiID(int id){
	kurssiID = id;
}
/**Asettaa suoritukselle uuden arvosanan
*@param as uusi arvosana
*/
public void setArvosana(int as){
	arvosana = as;
}
/**Asettaa suoritukselle uuden suorituspaivamaaran
*@param pvm uusi suoritus_pvm
*/
public void setPvm(String pvm){
	suorPvm = pvm;
}
//Getterit
/**Palauttaa suorituksen opiskelija ID:n
*@return opiskelijaID
*/
public int getOpiskelijaID(){
	return opiskelijaID;
}
/**Palauttaa suorituksen kurssin ID:n
*@return kurssiID
*/
public int getKurssiID(){
	return kurssiID;
}
/**Palauttaa suorituksen arvosanan
*@return arvosana
*/
public int getArvosana(){
	return arvosana;
}
/**Palauttaa suorituksen suorituspaivamaaran
*@return suoritus_pvm
*/
public String getPvm(){
	return suorPvm;
}
/**Palauttaa suorituksen tiedot merkkijonona
*@return Suorituksen tiedot
*/
@Override
public String toString(){
	return opiskelijaID + ", " + kurssiID + ", " + arvosana + ", " + suorPvm;
}

/**hakee listan opiskelijan suorituksista tietokannasta
* @param conn tietokantayhteys
* @param valinta määrittää haetaanko opiskelijan vai kurssin suorituksia
* @param id opiskelijan id numero
* @return suoritusLista
* @throws java.sql.SQLException
*/
public static ObservableList<Suoritus> haeSuoritusLista(Connection conn, int valinta, int id) throws SQLException, Exception {
    String sql = null;
    if(valinta == 1){
    sql = "SELECT opiskelijaID, kurssiID, arvosana, paivamaara " +
                 "FROM suoritus WHERE opiskelijaID = ?";
    }
    else if(valinta == 2){
    sql = "SELECT opiskelijaID, kurssiID, arvosana, paivamaara " +
                 "FROM suoritus WHERE kurssiID = ?";    
    }
    ResultSet tulosjoukko = null;
    PreparedStatement lause;
    try {
        lause = conn.prepareStatement(sql);
        lause.setInt(1, id);
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
    ObservableList<Suoritus> suoritusLista = FXCollections.observableArrayList();
    try {
        while (tulosjoukko.next()) { //lisataan opiskelijan suoritukset tulosjoukosta ObservableListille
            Suoritus suoritusOlio = new Suoritus();
            suoritusOlio.setOpiskelijaID(tulosjoukko.getInt("opiskelijaID"));
            suoritusOlio.setKurssiID(tulosjoukko.getInt("kurssiID"));
            suoritusOlio.setArvosana(tulosjoukko.getInt("arvosana"));
            java.util.Date suorpvm = tulosjoukko.getDate("paivamaara");
            SimpleDateFormat dmyFormat = new SimpleDateFormat("dd.MM.yyyy");
            String dmy = dmyFormat.format(suorpvm);
            suoritusOlio.setPvm(dmy);
            suoritusLista.add(suoritusOlio);
            System.out.println("listalle " + suoritusOlio);
        }
    } catch (SQLException e) { 
        System.out.println(e); //debug
        throw e; 
    }
    return suoritusLista;
} 

/**
    * Staattinen metodi, jolla haetaan suorituksen tiedot tietokannasta.
    * @param conn tietokantayhteys
    * @param opisID haettavan opiskelijan ID
    * @param kursID haettavan kurssin ID
    * @return haetuista tiedoista luotu Kurssi-olio
    * @throws java.sql.SQLException
    */
    public static Suoritus haeSuoritus (Connection conn, int opisID, int kursID) throws SQLException, Exception {
        String sql = "SELECT opiskelijaID, kurssiID, arvosana, paivamaara " +
                     "FROM suoritus WHERE opiskelijaID = ? AND kurssiID = ?";
        ResultSet hakuTulokset = null; //tähän haetaan haun tulokset
        PreparedStatement lause;
    System.out.println("haetaan suoritusta kannasta"); //debug
        try {
            lause = conn.prepareStatement(sql);  //valmistellaan string sql:stä lause
            lause.setInt(1, opisID); //asetetaan 1. where ehtoon (?) arvo
            lause.setInt(2, kursID); //asetetaan 2. where ehtoon (?) arvo
            hakuTulokset = lause.executeQuery(); // suorita sql-lause
            if (hakuTulokset == null) {
                System.out.println("Suoritusta ei loydy"); //debug
                throw new Exception("Suoritusta ei loydy");
            }
        } catch (SQLException se) {
            System.out.println(se); //debug
            throw se;
        } catch (Exception e) {
            System.out.println(e); //debug
            throw e;
        }
    System.out.println("asetetaan tiedot olioon"); //debug
        Suoritus suoritusOlio = new Suoritus (); //alustetaan olio jolle sijoitetaan haun tulokset
        try {
            if (hakuTulokset.next() == true){
                suoritusOlio.setOpiskelijaID (hakuTulokset.getInt("opiskelijaID"));
                suoritusOlio.setKurssiID (hakuTulokset.getInt("kurssiID"));
                suoritusOlio.setArvosana(hakuTulokset.getInt("arvosana"));
                java.util.Date suorpvm = hakuTulokset.getDate("paivamaara");
                SimpleDateFormat dmyFormat = new SimpleDateFormat("dd.MM.yyyy");
                String dmy = dmyFormat.format(suorpvm);
                suoritusOlio.setPvm(dmy);
            }
        }catch (SQLException e) {
            throw e;
        }
        return suoritusOlio;
    }
/**
    * Metodi joka lisaa suoritus olioon syotetyt tiedot kantaan
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void lisaaSuoritus (Connection conn) throws SQLException, Exception {
	String sql = "SELECT opiskelijaID " +
                     "FROM suoritus WHERE opiskelijaID = ? AND kurssiID = ?"; //tarkistetaan loytyyko kurssia kannasta
        ResultSet hakuTulos;
        PreparedStatement lause;
    System.out.println("tarkistetaan loytyyko suoritusta kannasta"); //debug
        try {
            lause = conn.prepareStatement(sql); //valmistellaan string sql:stä lause
            lause.setInt(1, getOpiskelijaID()); //asetetaan 1. where ehtoon (?) arvo
            lause.setInt(2, getKurssiID()); //asetetaan 2. where ehtoon (?) arvo
            hakuTulos = lause.executeQuery(); // suorita sql-lause
            if (hakuTulos.next() == true) { // suoritus loytyi
    System.out.println("Suoritus on jo olemassa"); //debug
                throw new Exception("Suoritus on jo olemassa");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
        sql = "INSERT INTO suoritus " +
                "(opiskelijaID, kurssiID, arvosana, paivamaara) " + 
                " VALUES (?, ?, ?, STR_TO_DATE(?, '%d.%m.%Y'))";
    System.out.println("Kokeillaan lisata suoritus"); //debug
        try {
            lause = conn.prepareStatement(sql); //muodostetaan insert lause
            lause.setInt( 1, getOpiskelijaID());          //ja lisätään olion tiedot inserttiin
            lause.setInt(2, getKurssiID());
            lause.setInt(3, getArvosana());
            lause.setString(4, getPvm());
            
            int lkm = lause.executeUpdate(); // suoritetaan sql-lause
        System.out.println("lkm " + lkm); //debug
            if (lkm == 0) {
                throw new Exception("Suoritus lisaaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
    /**
    * Muutetaan suorituksen tietoja tietokantaan syotetylle id:lle. 
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void muutaSuoritus(Connection conn) throws SQLException, Exception {
        String sql = "SELECT opiskelijaID " +
                     "FROM suoritus WHERE opiskelijaID = ? AND kurssiID = ?"; //tarkistetaan loytyyhan suoritus kannasta
        ResultSet tulosjoukko;
        PreparedStatement lause;
        try {
            lause = conn.prepareStatement(sql); //valmistellaan sql:stä lause
            lause.setInt(1, getOpiskelijaID()); //asetetaan 1. where ehtoon (?) arvo
            lause.setInt(2, getKurssiID()); //asetetaan 2. where ehtoon (?) arvo
            tulosjoukko = lause.executeQuery(); // suorita sql-lause
            if (tulosjoukko.next() == false) { // kurssi ei löytynyt
        System.out.println("Suoritusta ei löytynyt"); //debug
                throw new Exception("Suoritusta ei loydy tietokannasta");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
        sql = "UPDATE suoritus " + 
                "SET arvosana = ?, paivamaara = STR_TO_DATE(?, '%d.%m.%Y') " + 
                "WHERE opiskelijaID = ? AND kurssiID = ?";
        try {
            lause = conn.prepareStatement(sql); //valmistellaan UPDATE sql:stä lause
            lause.setInt(1, getArvosana());   //ja laitetaan olion attribuuttien arvot UPDATEen
            lause.setString(2, getPvm());
            lause.setInt(3, getOpiskelijaID()); //1. where-ehdon arvo
            lause.setInt(4, getKurssiID()); //2. where-ehdon arvo

            int lkm = lause.executeUpdate(); //suorita sql-lause
        System.out.println("lkm " + lkm); //debug    
            if (lkm == 0) {
                throw new Exception("Suorituksen muuttaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
/**
    * Poistaa suorituksen tiedot tietokannasta. 
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void poistaSuoritus (Connection conn) throws SQLException, Exception {
        String sql = "DELETE FROM suoritus WHERE opiskelijaID = ? AND kurssiID = ?";
        try {
            PreparedStatement lause = conn.prepareStatement(sql);  //valmistellaan sql:stä lause
            lause.setInt(1, getOpiskelijaID()); //asetetaan 1. where ehtoon (?) arvo
            lause.setInt(2, getKurssiID()); //asetetaan 2. where ehtoon (?) arvo
            int lkm = lause.executeUpdate(); // suorita sql-lause
        System.out.println("lkm " + lkm); //debug    
            if (lkm == 0) {
                throw new Exception("Suorituksen poistaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
}