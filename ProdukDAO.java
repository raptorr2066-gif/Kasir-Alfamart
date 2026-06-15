import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdukDAO {

    public ProdukDAO() {}

    public boolean simpan(Produk produk) {
        String sql = "INSERT INTO produk (kode_produk, nama_produk, id_kategori, "
                + "harga_beli, harga_jual, stok, satuan) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produk.getKodeProduk());
            ps.setString(2, produk.getNamaProduk());
            ps.setInt(3, produk.getKategori() != null ? produk.getKategori().getId() : 0);
            ps.setDouble(4, produk.getHargaBeli());
            ps.setDouble(5, produk.getHargaJual());
            ps.setInt(6, produk.getStok());
            ps.setString(7, produk.getSatuan());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) produk.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ProdukDAO] Gagal simpan: " + e.getMessage());
        }
        return false;
    }

    public List<Produk> getAll() {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kategori, k.deskripsi FROM produk p "
                + "LEFT JOIN kategori k ON p.id_kategori = k.id_kategori "
                + "ORDER BY p.nama_produk";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[ProdukDAO] Gagal getAll: " + e.getMessage());
        }
        return list;
    }

    public Produk getById(int id) {
        String sql = "SELECT p.*, k.nama_kategori, k.deskripsi FROM produk p "
                + "LEFT JOIN kategori k ON p.id_kategori = k.id_kategori "
                + "WHERE p.id_produk = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[ProdukDAO] Gagal getById: " + e.getMessage());
        }
        return null;
    }

    public Produk getByKode(String kode) {
        String sql = "SELECT p.*, k.nama_kategori, k.deskripsi FROM produk p "
                + "LEFT JOIN kategori k ON p.id_kategori = k.id_kategori "
                + "WHERE p.kode_produk = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, kode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSet(rs);
        } catch (SQLException e) {
            System.err.println("[ProdukDAO] Gagal getByKode: " + e.getMessage());
        }
        return null;
    }

    public List<Produk> cari(String keyword) {
        List<Produk> list = new ArrayList<>();
        String sql = "SELECT p.*, k.nama_kategori, k.deskripsi FROM produk p "
                + "LEFT JOIN kategori k ON p.id_kategori = k.id_kategori "
                + "WHERE p.nama_produk LIKE ? OR p.kode_produk LIKE ? "
                + "ORDER BY p.nama_produk";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            System.err.println("[ProdukDAO] Gagal cari: " + e.getMessage());
        }
        return list;
    }

    public boolean update(Produk produk) {
        String sql = "UPDATE produk SET kode_produk=?, nama_produk=?, id_kategori=?, "
                + "harga_beli=?, harga_jual=?, stok=?, satuan=? WHERE id_produk=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, produk.getKodeProduk());
            ps.setString(2, produk.getNamaProduk());
            ps.setInt(3, produk.getKategori() != null ? produk.getKategori().getId() : 0);
            ps.setDouble(4, produk.getHargaBeli());
            ps.setDouble(5, produk.getHargaJual());
            ps.setInt(6, produk.getStok());
            ps.setString(7, produk.getSatuan());
            ps.setInt(8, produk.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProdukDAO] Gagal update: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStok(int idProduk, int stokBaru) {
        String sql = "UPDATE produk SET stok=? WHERE id_produk=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, stokBaru);
            ps.setInt(2, idProduk);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProdukDAO] Gagal updateStok: " + e.getMessage());
        }
        return false;
    }

    public boolean hapus(int id) {
        String sql = "DELETE FROM produk WHERE id_produk=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProdukDAO] Gagal hapus: " + e.getMessage());
        }
        return false;
    }

    private Produk mapResultSet(ResultSet rs) throws SQLException {
        Kategori kategori = new Kategori(
                rs.getInt("id_kategori"),
                rs.getString("nama_kategori"),
                rs.getString("deskripsi")
        );
        return new Produk(
                rs.getInt("id_produk"),
                rs.getString("kode_produk"),
                rs.getString("nama_produk"),
                kategori,
                rs.getDouble("harga_beli"),
                rs.getDouble("harga_jual"),
                rs.getInt("stok"),
                rs.getString("satuan")
        );
    }
}