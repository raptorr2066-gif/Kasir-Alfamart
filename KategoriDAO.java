import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    public KategoriDAO() {}

    public boolean simpan(Kategori k) {
        String sql = "INSERT INTO kategori (nama_kategori, deskripsi) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, k.getNamaKategori());
            ps.setString(2, k.getDeskripsi());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) k.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[KategoriDAO] Gagal simpan: " + e.getMessage());
        }
        return false;
    }

    public List<Kategori> getAll() {
        List<Kategori> list = new ArrayList<>();
        String sql = "SELECT * FROM kategori ORDER BY nama_kategori";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Kategori(rs.getInt("id_kategori"),
                        rs.getString("nama_kategori"), rs.getString("deskripsi")));
            }
        } catch (SQLException e) {
            System.err.println("[KategoriDAO] Gagal getAll: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Kategori k) {
        String sql = "UPDATE kategori SET nama_kategori=?, deskripsi=? WHERE id_kategori=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, k.getNamaKategori());
            ps.setString(2, k.getDeskripsi());
            ps.setInt(3, k.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[KategoriDAO] Gagal update: " + e.getMessage());
        }
        return false;
    }

    public boolean hapus(int id) {
        String sql = "DELETE FROM kategori WHERE id_kategori=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[KategoriDAO] Gagal hapus: " + e.getMessage());
        }
        return false;
    }
}