import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "alfamart_kasir";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false"
            + "&serverTimezone=Asia/Jakarta"
            + "&allowPublicKeyRetrieval=true"
            + "&autoReconnect=true";

    private DatabaseConnection() {}
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Driver MySQL tidak ditemukan!\nPastikan mysql-connector-java ada di Libraries.",
                    "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            System.err.println("[DB ERROR] Driver tidak ditemukan: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal terhubung ke database!\n"
                    + "Pastikan MySQL berjalan dan konfigurasi benar.\n"
                    + "Error: " + e.getMessage(),
                    "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            System.err.println("[DB ERROR] SQLException: " + e.getMessage());
            return null;
        }
    }

    public static void closeConnection() {
    }
    public static boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}