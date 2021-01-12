package harjoitustyo.tietokannanLuonti;

import java.sql.*;
/**
 * Ohjelma luo tietokannan opiskelijoiden suoritusten hallintaohjelmaa varten.
 * Samalla luodaan tunnukset sovelluksen käyttöä varten.
 * @author Jussi Kukkonen
 * @version 1.00 20.03.2020
 */
public class TietokannanLuonti {
    
    /** Avaa yhteyden tietokantaan
    * @param yhteysOsoite
    * @return yhteys 
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
    /** Sulkee yhteyden tietokantaan
    * @param conn yhteys
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
    /** Luo tietokannan
    * @param conn yhteys
    * @param tkanta tietokannan nimi
    */
    private static void luoTietokanta(Connection conn, String tkanta){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery("DROP DATABASE IF EXISTS " + tkanta);
            System.out.println("Tietokanta " + tkanta + " tuhottu");
            
            stmt.executeQuery("CREATE DATABASE " + tkanta);
            System.out.println("Tietokanta " + tkanta + " luotu");
            
            stmt.executeQuery("USE " + tkanta);
            System.out.println("Kaytetaan tietokantaa " + tkanta);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** Luo uuden taulun tietokantaan
    * @param conn yhteys
    * @param sqlLause SQL-lause joka luo taulun
    */
    public static void luoTaulu(Connection conn, String sqlLause){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sqlLause);
            System.out.println("Taulu luotu");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /** Luo tunnukset tietokantaan
    * @param conn yhteys
    * @param sqlLause SQL-lause joka luo tunnukset
    */
    public static void luoTunnukset(Connection conn, String sqlLause){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sqlLause);
            System.out.println("Tunnukset luotu");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        //Avataan yhteys azure pilveen johon tietokanta luodaan
        Connection yhteys = avaaYhteys(
        "jdbc:mariadb://maria.westeurope.cloudapp.azure.com:3306?user=opiskelija&password=opiskelija"
        );
        
        //Luodaan tietokanta
        luoTietokanta(yhteys, "karelia_jushi");
        
        //Luodaan tarvittavat taulut
        luoTaulu(yhteys,
                "CREATE TABLE opiskelija (" +
                        "opiskelijaID INT NOT NULL PRIMARY KEY," +
                        "etunimi VARCHAR(15)," +
                        "sukunimi VARCHAR(15)," +
                        "lahiosoite VARCHAR(30)," +
                        "postitoimipaikka VARCHAR(15)," +
                        "postinro VARCHAR(5)," +
                        "email VARCHAR(30)," +
                        "puhelinnro VARCHAR(10))"
                        );
        luoTaulu(yhteys,
                "CREATE TABLE kurssi (" +
                        "kurssiID INT NOT NULL PRIMARY KEY," +
                        "kurssi_nimi VARCHAR(50) NOT NULL," +
                        "opintopisteet INT(1) NOT NULL," +
                        "kuvaus VARCHAR(100) NOT NULL)"
                );
        luoTaulu(yhteys,
                "CREATE TABLE suoritus (" +
                        "opiskelijaID INT NOT NULL, " +
                        "kurssiID INT NOT NULL," +
                        "arvosana INT NOT NULL," +
                        "paivamaara DATE NOT NULL," +
                        "FOREIGN KEY (opiskelijaID) REFERENCES opiskelija(opiskelijaID)," +
                        "FOREIGN KEY (kurssiID) REFERENCES kurssi(kurssiID))"
                );
        luoTaulu(yhteys,
                "CREATE TABLE tunnukset (" +
                        "tunnus VARCHAR(20) NOT NULL PRIMARY KEY, " +
                        "salasana VARCHAR(20) NOT NULL)"
                );
        luoTunnukset(yhteys,
                "INSERT INTO tunnukset " +
                        "VALUES('paavo', 'pesusieni')"
                );
        //Lopuksi suljetaan yhteys
        suljeYhteys(yhteys);
    }
}