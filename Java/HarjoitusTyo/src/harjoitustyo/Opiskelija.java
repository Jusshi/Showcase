package harjoitustyo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
* Luokka joka maarittaa opiskelija-olion.
* @author Jussi Kukkonen
* @version 2.00 20.03.2020
*/
public class Opiskelija{

    //Attribuutit
    private int opiskelijaID;
    private String etunimi;
    private String sukunimi;
    private String lahiosoite;
    private String postitoimipaikka;
    private String postinro;
    private String email;
    private String puhelinnro;

    /**Konstruktori
    */
    public Opiskelija(){
    }

    //Getterit
    /**
    * Hakee opiskelijanumeron
    *@return Opiskelijan ID
    */
    public int getID(){
        return opiskelijaID;
    }
    /**
    * Hakee opiskelijan etunimen
    *@return etunimi
    */
    public String getEtunimi(){
        return etunimi;
    }
    /**
    * Hakee opiskelijan sukunimen
    *@return sukunimi
    */
    public String getSukunimi(){
        return sukunimi;
    }
    /**
    * Hakee opiskelijan lahiosoitteen
    *@return lahiosoite
    */
    public String getLahiosoite(){
        return lahiosoite;
    }
    /**
    * Hakee opiskelijan postinumeron
    *@return postinro
    */
    public String getPostinro(){
        return postinro;
    }
    /**
    * Hakee opiskelijan postitoimipaikan
    *@return postitoimipaikka
    */
    public String getPostitoimipaikka(){
        return postitoimipaikka;
    }
    /**
    * Hakee opiskelijan s-postin
    *@return email
    */
    public String getEmail(){
        return email;
    }
    /**
    * Hakee opiskelijan puhelinnumeron
    *@return puhelinnro
    */
    public String getPuhelinnro(){
        return puhelinnro;
    }

    //Setterit
    /**
    * Asettaa ID numeron opiskelijalle
    *@param id opiskelijan ID-numero
    */
    public void setID(int id){
        opiskelijaID = id;
    }
    /**
    * Asettaa etunimen opiskelijalle
    *@param enimi etunimi
    */
    public void setEtunimi(String enimi){
        etunimi = enimi;
    }
    /**
    * Asettaa sukunimen opiskelijalle
    *@param snimi sukunimi
    */
    public void setSukunimi(String snimi){
        sukunimi = snimi;
    }
    /**
    * Asettaa lahiosoitteen opiskelijalle
    *@param osoite lahiosoite
    */
    public void setLahiosoite(String osoite){
        lahiosoite = osoite;
    }
    /**
    * Asettaa postitoimipaikan opiskelijalle
    *@param postip postitoimipaikka
    */
    public void setPostitoimipaikka(String postip){
        postitoimipaikka = postip;
    }
    /**
    * Asettaa postinumeron opiskelijalle
    *@param postin postinumero
    */
    public void setPostinro(String postin){
        postinro = postin;
    }
    /**
    * Asettaa s-postin opiskelijalle
    *@param sposti sahkoposti
    */
    public void setEmail(String sposti){
        email = sposti;
    }
    /**
    * Asettaa puhelinnumeron opiskelijalle
    *@param puh
    */
    public void setPuhelinnro(String puh){
        puhelinnro = puh;
    }
    /**
    * Palauttaa opiskelija-olion tiedot merkkijonona
    *@return Opiskelijan tiedot
    */
    @Override
    public String toString(){
        return opiskelijaID + ", " + etunimi + " " +  sukunimi +
                "\n" + lahiosoite + ", " + postinro + " " + postitoimipaikka +
                "\n" + email + "\n" + puhelinnro;
    }
    /**
    * Staattinen metodi, jolla haetaan opiskelijan tiedot tietokannasta.
    * @param conn tietokantayhteys
    * @param id haettavan opiskelijan ID
    * @return haetuista tiedoista luotu opiskelija-olio
    * @throws java.sql.SQLException
    */
    public static Opiskelija haeOpiskelija (Connection conn, int id) throws SQLException, Exception {
        String sql = "SELECT opiskelijaID, etunimi, sukunimi, lahiosoite, postitoimipaikka, postinro, email, puhelinnro " +
                    "FROM opiskelija WHERE opiskelijaID = ?";
        ResultSet hakuTulokset = null; //tähän haetaan haun tulokset
        PreparedStatement lause;
    System.out.println("haetaan opiskelijaa kannasta"); //debug
        try {
            lause = conn.prepareStatement(sql);  //valmistellaan string sql:stä lause
            lause.setInt(1, id); //asetetaan where ehtoon (?) arvo
            hakuTulokset = lause.executeQuery(); // suorita sql-lause
            if (hakuTulokset == null) {
                System.out.println("Opiskelijaa ei loydy"); //debug
                throw new Exception("Opiskelijaa ei loydy");
            }
        } catch (SQLException se) {
            System.out.println(se); //debug
            throw se;
        } catch (Exception e) {
            System.out.println(e); //debug
            throw e;
        }
    System.out.println("asetetaan tiedot olioon"); //debug
        Opiskelija opiskelijaOlio = new Opiskelija (); //alustetaan olio jolle sijoitetaan haun tulokset
        try {
            if (hakuTulokset.next() == true){
                opiskelijaOlio.setID (hakuTulokset.getInt("opiskelijaID"));
                opiskelijaOlio.setEtunimi (hakuTulokset.getString("etunimi"));
                opiskelijaOlio.setSukunimi(hakuTulokset.getString("sukunimi"));
                opiskelijaOlio.setLahiosoite (hakuTulokset.getString("lahiosoite"));
                opiskelijaOlio.setPostitoimipaikka (hakuTulokset.getString("postitoimipaikka"));
                opiskelijaOlio.setPostinro (hakuTulokset.getString("postinro"));
                opiskelijaOlio.setEmail (hakuTulokset.getString("email"));
                opiskelijaOlio.setPuhelinnro (hakuTulokset.getString("puhelinnro"));
            }
        }catch (SQLException e) {
            throw e;
        }
        return opiskelijaOlio;
    }
    /**
    * Metodi joka lisaa opiskelija olioon syotetyt tiedot kantaan
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void lisaaOpiskelija (Connection conn) throws SQLException, Exception {
	String sql = "SELECT opiskelijaID" +
                    " FROM opiskelija WHERE opiskelijaID = ?"; //tarkistetaan loytyyko opiskelijaa kannasta
        ResultSet hakuTulos;
        PreparedStatement lause;
    System.out.println("tarkistetaan loytyyko opiskelijaa kannasta"); //debug
        try {
            lause = conn.prepareStatement(sql); //valmistellaan string sql:stä lause
            lause.setInt(1, getID()); //asetetaan where ehtoon (?) arvo
            hakuTulos = lause.executeQuery(); // suorita sql-lause
            if (hakuTulos.next() == true) { // Opiskelija loytyi
    System.out.println("Opiskelija on jo olemassa"); //debug
                throw new Exception("Opiskelija on jo olemassa");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
        sql = "INSERT INTO opiskelija " +
                "(opiskelijaID, etunimi, sukunimi, lahiosoite, postitoimipaikka, postinro, email, puhelinnro) " + 
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    System.out.println("Kokeillaan lisata opiskelija"); //debug
        try {
            lause = conn.prepareStatement(sql); //muodostetaan insert lause
            lause.setInt( 1, getID());          //ja lisätään olion tiedot inserttiin
            lause.setString(2, getEtunimi());
            lause.setString(3, getSukunimi());
            lause.setString(4, getLahiosoite());
            lause.setString(5, getPostitoimipaikka ());
            lause.setString(6, getPostinro ());
            lause.setString(7, getEmail ());
            lause.setString(8, getPuhelinnro ());
            
            int lkm = lause.executeUpdate(); // suoritetaan sql-lause
        System.out.println("lkm " + lkm); //debug
            if (lkm == 0) {
                throw new Exception("Opiskelijan lisaaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
    /**
    * Muutetaan opiskelijan tietoja tietokantaan syotetylle id:lle. 
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void muutaOpiskelija(Connection conn) throws SQLException, Exception {
        String sql = "SELECT opiskelijaID" + 
                    " FROM opiskelija WHERE opiskelijaID = ?"; //tarkistetaan loytyyhan opiskelija kannasta
        ResultSet tulosjoukko;
        PreparedStatement lause;
        try {
            lause = conn.prepareStatement(sql); //valmistellaan sql:stä lause
            lause.setInt(1, getID()); // asetetaan where ehtoon (?) arvo
            tulosjoukko = lause.executeQuery(); // suorita sql-lause
            if (tulosjoukko.next() == false) { // Opiskelijaa ei löytynyt
        System.out.println("Opiskelijaa ei löytynyt"); //debug
                throw new Exception("Opiskelijaa ei loydy tietokannasta");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
        sql = "UPDATE  opiskelija " + 
                "SET etunimi = ?, sukunimi = ?, lahiosoite = ?, postitoimipaikka = ?, postinro = ?, email = ?, puhelinnro = ? " + 
                "WHERE OpiskelijaID= ?";
        try {
            lause = conn.prepareStatement(sql); //valmistellaan UPDATE sql:stä lause
            lause.setString(1, getEtunimi());   //ja laitetaan olion attribuuttien arvot UPDATEen
            lause.setString(2, getSukunimi());
            lause.setString(3, getLahiosoite());
            lause.setString(4, getPostitoimipaikka ());
            lause.setString(5, getPostinro ());
            lause.setString(6, getEmail ());
            lause.setString(7, getPuhelinnro ());            
            lause.setInt(8, getID()); //where-ehdon arvo

            int lkm = lause.executeUpdate(); //suorita sql-lause
        System.out.println("lkm " + lkm); //debug    
            if (lkm == 0) {
                throw new Exception("Opiskelijan muuttaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
    /**
    * Poistaa opiskelijan tiedot tietokannasta. 
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void poistaOpiskelija (Connection conn) throws SQLException, Exception {
        String sql = "DELETE FROM opiskelija WHERE opiskelijaID = ?";
        try {
            PreparedStatement lause = conn.prepareStatement(sql);  //valmistellaan sql:stä lause
            lause.setInt( 1, getID()); // laitetaan arvo WHERE-ehtoon
            int lkm = lause.executeUpdate(); // suorita sql-lause
        System.out.println("lkm " + lkm); //debug    
            if (lkm == 0) {
                throw new Exception("Opiskelijan poistaminen ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
}