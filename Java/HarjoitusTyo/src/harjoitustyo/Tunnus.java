package harjoitustyo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
* Luokka joka maarittaa tunnus-olion.
* @author Jussi Kukkonen
* @version 1.00 20.03.2020
*/
public class Tunnus {
    //Attribuutit
    private String kayttajaTunnus;
    private String salasana;
    
    //Setterit
    /**
     * Asettaa tunnus oliolle kayttajatunnuksen.
     * @param tunnus
     */
    public void setTunnus(String tunnus){
        kayttajaTunnus = tunnus;
    }
    /**
     * Asettaa salasanan oliolle kayttajatunnuksen.
     * @param sana
     */
    public void setSalasana(String sana){
        salasana = sana;
    }
    //getterit
    /**
     * Hakee kayttajatunnuksen oliolta.
     * @return kayttajaTunnus
     */
    public String getTunnus(){
        return kayttajaTunnus;
    }
    /**
     * Hakee salasanan oliolta.
     * @return salasana
     */
    public String getSalasana(){
        return salasana;
    }
    /**Palauttaa tunnusOlion merkkijonona
    *@return tunnukset
    */
    @Override
    public String toString(){
	return "Kayttajatunnus: " + kayttajaTunnus +
                "\nSalasana: " + salasana;
    }
    /**
    * Staattinen metodi, jolla haetaan tunnukset tietokannasta.
    * @param conn tietokantayhteys
    * @return haetuista tunnuksista luotu tunnusOlio
    * @throws java.sql.SQLException
    */
    public static Tunnus haeTunnukset (Connection conn) throws SQLException, Exception {
        String sql = "SELECT tunnus, salasana FROM tunnukset";
        ResultSet hakuTulokset = null; //tähän haetaan haun tulokset
        PreparedStatement lause;
    System.out.println("haetaan tunnukset kannasta"); //debug
        try {
            lause = conn.prepareStatement(sql);  //valmistellaan string sql:stä lause
            hakuTulokset = lause.executeQuery(); // suorita sql-lause
            if (hakuTulokset == null) {
                System.out.println("Tunnuksia ei loydy"); //debug
            }
        } catch (SQLException se) {
            System.out.println(se); //debug
            throw se;
        } catch (Exception e) {
            System.out.println(e); //debug
            throw e;
        }
    System.out.println("asetetaan tiedot olioon"); //debug
        Tunnus tunnusOlio = new Tunnus (); //alustetaan olio jolle sijoitetaan haun tulokset
        try {
            if (hakuTulokset.next()){
                tunnusOlio.setTunnus (hakuTulokset.getString("tunnus"));
                tunnusOlio.setSalasana (hakuTulokset.getString("salasana"));
            }
        }catch (SQLException e) {
            throw e;
        }
        return tunnusOlio;
    }
    
    /**
    * Vaihtaa salasanan tietokantaan. 
    * @param conn tietokantayhteys
    * @throws java.sql.SQLException
    */
    public void vaihdaSalasana(Connection conn) throws SQLException, Exception {
        String sql = "UPDATE tunnukset SET salasana = ? WHERE tunnus = ?";
        PreparedStatement lause;
        try {
            lause = conn.prepareStatement(sql); //valmistellaan UPDATE sql:stä lause
            lause.setString(1, getSalasana());   //ja laitetaan olion attribuutin arvo UPDATEen
            lause.setString(2, getTunnus()); //where-ehdon arvo

            int lkm = lause.executeUpdate(); //suorita sql-lause
        System.out.println("lkm " + lkm); //debug    
            if (lkm == 0) {
                throw new Exception("Salasanan vaihto ei onnistu");
            }
        } catch (SQLException se) {
            throw se;
        } catch (Exception e) {
            throw e;
        }
    }
}