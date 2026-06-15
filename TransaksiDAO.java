import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class TransaksiDAO menangani operasi CRUD untuk entitas Transaksi
 * dan DetailTransaksi.
 *
 * PERBAIKAN: Tidak lagi menyimpan Connection sebagai field.
 * Setiap method mengambil koneksi fresh dari DatabaseConnection.getConnection()
 * sehingga tidak terpengaruh koneksi yang sudah di-drop PostgreSQL.
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public class TransaksiDAO {

    // ✅ TIDAK ada "private Connection conn" di sini
    // ✅ TIDAK ada this.conn = ... di constructor

    public TransaksiDAO() {}

    // ===================== CREATE =====================

    public boolean simpan(Transaksi transaksi) {
        String sqlTrx = "INSERT INTO transaksi (no_transaksi, id_pengguna, tanggal_transaksi, "
                + "total_harga, diskon, total_bayar, uang_bayar, kembalian, metode_bayar, status) "
                + "VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO detail_transaksi (id_transaksi, id_produk, qty, harga_satuan, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";
        String sqlStok = "UPDATE produk SET stok = stok - ? WHERE id_produk = ?";

        Connection conn = DatabaseConnection.getConnection(); // ← fresh setiap transaksi
        try {
            conn.setAutoCommit(false);

            int idTransaksi;
            try (PreparedStatement ps = conn.prepareStatement(sqlTrx, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, transaksi.getNoTransaksi());
                ps.setInt(2, transaksi.getKasir().getId());
                ps.setDouble(3, transaksi.getTotalHarga());
                ps.setDouble(4, transaksi.getDiskon());
                ps.setDouble(5, transaksi.getTotalBayar());
                ps.setDouble(6, transaksi.getUangBayar());
                ps.setDouble(7, transaksi.getKembalian());
                ps.setString(8, transaksi.getMetodeBayar());
                ps.setString(9, transaksi.getStatus());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) throw new SQLException("Gagal mendapatkan ID transaksi");
                idTransaksi = rs.getInt(1);
                transaksi.setId(idTransaksi);
            }

            for (DetailTransaksi detail : transaksi.getListDetail()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                    ps.setInt(1, idTransaksi);
                    ps.setInt(2, detail.getProduk().getId());
                    ps.setInt(3, detail.getQty());
                    ps.setDouble(4, detail.getHargaSatuan());
                    ps.setDouble(5, detail.getSubtotal());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(sqlStok)) {
                    ps.setInt(1, detail.getQty());
                    ps.setInt(2, detail.getProduk().getId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            System.err.println("[TransaksiDAO] Gagal simpan: " + e.getMessage());
            return false;
        }
    }

    // ===================== READ =====================

    public List<Transaksi> getAll() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, p.nama_lengkap FROM transaksi t "
                + "JOIN pengguna p ON t.id_pengguna = p.id_pengguna "
                + "ORDER BY t.tanggal_transaksi DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement(); // ← fresh
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapHeader(rs));
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] Gagal getAll: " + e.getMessage());
        }
        return list;
    }

    public List<Transaksi> getHariIni() {
        List<Transaksi> list = new ArrayList<>();
        String sql = "SELECT t.*, p.nama_lengkap FROM transaksi t "
                + "JOIN pengguna p ON t.id_pengguna = p.id_pengguna "
                + "WHERE DATE(t.tanggal_transaksi) = CURRENT_DATE "
                + "ORDER BY t.tanggal_transaksi DESC";
        try (Statement st = DatabaseConnection.getConnection().createStatement(); // ← fresh
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapHeader(rs));
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] Gagal getHariIni: " + e.getMessage());
        }
        return list;
    }

    public List<DetailTransaksi> getDetail(int idTransaksi) {
        List<DetailTransaksi> list = new ArrayList<>();
        String sql = "SELECT dt.*, p.kode_produk, p.nama_produk, p.satuan "
                + "FROM detail_transaksi dt "
                + "JOIN produk p ON dt.id_produk = p.id_produk "
                + "WHERE dt.id_transaksi = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) { // ← fresh
            ps.setInt(1, idTransaksi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Produk produk = new Produk(
                        rs.getInt("id_produk"),
                        rs.getString("kode_produk"),
                        rs.getString("nama_produk"),
                        null, 0,
                        rs.getDouble("harga_satuan"),
                        0,
                        rs.getString("satuan")
                );
                list.add(new DetailTransaksi(
                        rs.getInt("id_detail"),
                        produk,
                        rs.getInt("qty"),
                        rs.getDouble("harga_satuan")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] Gagal getDetail: " + e.getMessage());
        }
        return list;
    }

    public double[] getLaporanHariIni() {
        double[] hasil = {0, 0};
        String sql = "SELECT COUNT(*) as jml, SUM(total_bayar) as total "
                + "FROM transaksi WHERE DATE(tanggal_transaksi) = CURRENT_DATE";
        try (Statement st = DatabaseConnection.getConnection().createStatement(); // ← fresh
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                hasil[0] = rs.getInt("jml");
                hasil[1] = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("[TransaksiDAO] Gagal getLaporanHariIni: " + e.getMessage());
        }
        return hasil;
    }

    // ===================== HELPER =====================

    private Transaksi mapHeader(ResultSet rs) throws SQLException {
        Pengguna kasir = new Kasir(
                rs.getInt("id_pengguna"),
                "", "", rs.getString("nama_lengkap"), true, "pagi"
        );
        return new Transaksi(
                rs.getInt("id_transaksi"),
                rs.getString("no_transaksi"),
                kasir,
                rs.getTimestamp("tanggal_transaksi"),
                rs.getDouble("total_harga"),
                rs.getDouble("diskon"),
                rs.getDouble("total_bayar"),
                rs.getDouble("uang_bayar"),
                rs.getDouble("kembalian"),
                rs.getString("metode_bayar"),
                rs.getString("status")
        );
    }
}