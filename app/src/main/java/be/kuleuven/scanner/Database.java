package be.kuleuven.scanner;

import java.sql.Connection;
import java.sql.DriverManager;


public class Database {
    private static final String url = "jdbc:mysql://mysql.studev.groept.be:3306/a21pt304?characterEncoding=utf8";
    private static final String user = "a21pt304";
    private static final String pass = "secret";

    public static Connection connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
