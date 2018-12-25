import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static final String DICTIONARY_FILE = "D:\\Programowanie\\Java\\NLP2018\\Lista_1\\dictionary.txt";
    public static final String BIGRAMS_FILE = "D:\\Programowanie\\Java\\NLP2018\\poleval_2grams.txt";
    public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url ="jdbc:mysql://nlp18-mysql.mysql.database.azure.com:3306/nlp18?"+
                    "useSSL=true&requireSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            Connection con= DriverManager.getConnection(
                    url,"nlp-user@nlp18-mysql","");
            con.createStatement().execute("ALTER DATABASE nlp18 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;");
            createTables(con);
            addBigramsToDB(con);
    }
    private static void createTables(Connection connection) throws SQLException {
        Statement stmt=connection.createStatement();
        String dropTables = "DROP TABLE IF EXISTS bigrams;";
        String bigrams = "CREATE TABLE BIGRAMS " +
                "(occurrences INTEGER, " +
                "word1 VARCHAR(255), " +
                "word2 VARCHAR(255));";
        stmt.execute(dropTables);
        stmt.execute(bigrams);
    }
    private static void addBigramsToDB(Connection connection) throws FileNotFoundException, SQLException {
        Statement statement = connection.createStatement();
        statement.execute("LOAD DATA LOCAL INFILE '"+"D:/Programowanie/Java/NLP2018/poleval_2grams.txt"+"' " +
                "INTO TABLE bigrams FIELDS TERMINATED BY ' ' (occurrences,word1,word2)");
    }
}
