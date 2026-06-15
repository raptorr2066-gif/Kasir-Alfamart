import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenggunaDAO {

    public PenggunaDAO() {}

    public Pengguna login(String username, String password) {
        String sql = "SELECT * FROM pengguna WHERE username=? AND password=? AND aktif=1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                int id = rs.getInt("id_pengguna");
                String nama = rs.getString("nama_lengkap");
                boolean aktif = rs.getBoolean("aktif");
                if ("admin".equals(role)) {
                    return new Admin(id, username, password, nama, aktif, "admin");
                } else {
                    return new Kasir(id, username, password, nama, aktif, "pagi");
                }
            }
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Gagal login: " + e.getMessage());
        }
        return null;
    }

    public boolean simpan(Pengguna pengguna) {
        String role = (pengguna instanceof Admin) ? "admin" : "kasir";
        String sql = "INSERT INTO pengguna (username, password, nama_lengkap, role, aktif) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pengguna.getUsername());
            ps.setString(2, pengguna.getPassword());
            ps.setString(3, pengguna.getNamaLengkap());
            ps.setString(4, role);
            ps.setBoolean(5, pengguna.isAktif());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) pengguna.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Gagal simpan: " + e.getMessage());
        }
        return false;
    }

    public List<Pengguna> getAll() {
        List<Pengguna> list = new ArrayList<>();
        String sql = "SELECT * FROM pengguna ORDER BY nama_lengkap";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Gagal getAll: " + e.getMessage());
        }
        return list;
    }

    public Pengguna getById(int id) {
        String sql = "SELECT * FROM pengguna WHERE id_pengguna=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Gagal getById: " + e.getMessage());
        }
        return null;
    }

    public boolean update(Pengguna pengguna) {
        String role = (pengguna instanceof Admin) ? "admin" : "kasir";
        String sql = "UPDATE pengguna SET username=?, password=?, nama_lengkap=?, role=?, aktif=? WHERE id_pengguna=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, pengguna.getUsername());
            ps.setString(2, pengguna.getPassword());
            ps.setString(3, pengguna.getNamaLengkap());
            ps.setString(4, role);
            ps.setBoolean(5, pengguna.isAktif());
            ps.setInt(6, pengguna.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Gagal update: " + e.getMessage());
        }
        return false;
    }

    public boolean hapus(int id) {
        String sql = "DELETE FROM pengguna WHERE id_pengguna=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PenggunaDAO] Gagal hapus: " + e.getMessage());
        }
        return false;
    }

    private Pengguna mapResultSet(ResultSet rs) throws SQLException {
        int id        = rs.getInt("id_pengguna");
        String uname  = rs.getString("username");
        String pass   = rs.getString("password");
        String nama   = rs.getString("nama_lengkap");
        String role   = rs.getString("role");
        boolean aktif = rs.getBoolean("aktif");
        if ("admin".equals(role)) {
            return new Admin(id, uname, pass, nama, aktif, "admin");
        } else {
            return new Kasir(id, uname, pass, nama, aktif, "pagi");
        }
    }
}