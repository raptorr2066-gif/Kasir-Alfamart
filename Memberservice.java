import java.sql.*;
/**
 * MemberService.java
 * Service class untuk semua operasi member Alfamart.
 * Digunakan oleh PanelKasir dan panel manajemen member.
 */
public class MemberService {

    // =========================================================
    // INNER CLASS: Model data Member
    // =========================================================
    public static class Member {
        public int    idMember;
        public String noMember;
        public String namaMember;
        public String noHp;
        public String tier;           // Silver / Gold / Platinum
        public int    poin;
        public double diskonPersen;
        public double totalBelanja;
        public String tanggalExpired;
        public String statusMember;   // AKTIF / EXPIRED / NONAKTIF

        @Override
        public String toString() {
            return noMember + " — " + namaMember;
        }
    }

    // =========================================================
    // CEK & AMBIL DATA MEMBER DARI DATABASE
    // =========================================================
    public static Member cekMember(String noMember) throws SQLException {
        String sql = "SELECT * FROM v_info_member WHERE no_member = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noMember);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Member m = new Member();
                m.idMember      = rs.getInt("id_member");
                m.noMember      = rs.getString("no_member");
                m.namaMember    = rs.getString("nama_member");
                m.noHp          = rs.getString("no_hp");
                m.tier          = rs.getString("tier");
                m.poin          = rs.getInt("poin");
                m.diskonPersen  = rs.getDouble("diskon_persen");
                m.totalBelanja  = rs.getDouble("total_belanja");
                m.tanggalExpired = rs.getString("tanggal_expired");
                m.statusMember  = rs.getString("status_member");
                return m;
            }
        }
    }

    // =========================================================
    // TAMBAH POIN SETELAH TRANSAKSI SELESAI
    // Aturan: setiap Rp 10.000 belanja = 1 poin
    // Diganti dari pemanggilan function PostgreSQL menjadi
    // logika Java langsung UPDATE tabel member & insert riwayat_poin
    // =========================================================
    public static int tambahPoin(int idMember, int idTransaksi, double totalBayar)
            throws SQLException {

        // Hitung poin: setiap Rp 10.000 = 1 poin
        int poinDidapat = (int) Math.floor(totalBayar / 10000);

        if (poinDidapat <= 0) return 0; // tidak ada poin jika belanja < Rp 10.000

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update poin dan total belanja di tabel member
                String sqlUpdate = "UPDATE member "
                        + "SET poin = poin + ?, total_belanja = total_belanja + ? "
                        + "WHERE id_member = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, poinDidapat);
                    ps.setDouble(2, totalBayar);
                    ps.setInt(3, idMember);
                    ps.executeUpdate();
                }

                // 2. Catat ke riwayat_poin (jika tabel ada)
                try {
                    String sqlRiwayat = "INSERT INTO riwayat_poin "
                            + "(id_member, id_transaksi, jenis, jumlah_poin, keterangan, tanggal) "
                            + "VALUES (?, ?, 'TAMBAH', ?, ?, NOW())";
                    try (PreparedStatement ps = conn.prepareStatement(sqlRiwayat)) {
                        ps.setInt(1, idMember);
                        ps.setInt(2, idTransaksi);
                        ps.setInt(3, poinDidapat);
                        ps.setString(4, "Poin dari transaksi Rp " + (long) totalBayar);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    // Tabel riwayat_poin mungkin belum ada, abaikan error ini
                    System.out.println("[MemberService] riwayat_poin skip: " + e.getMessage());
                }

                conn.commit();
                conn.setAutoCommit(true);
                return poinDidapat;

            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw e;
            }
        }
    }

    // =========================================================
    // CATAT LOG DISKON MEMBER
    // =========================================================
    public static void catatDiskon(int idTransaksi, int idMember,
                                   double diskonPersen, double nominalDiskon)
            throws SQLException {
        String sql = "INSERT INTO log_diskon_member "
                   + "(id_transaksi, id_member, diskon_persen, nominal_diskon) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTransaksi);
            ps.setInt(2, idMember);
            ps.setDouble(3, diskonPersen);
            ps.setDouble(4, nominalDiskon);
            ps.executeUpdate();
        }
    }

    // =========================================================
    // DAFTARKAN MEMBER BARU
    // Syntax disesuaikan untuk MySQL (bukan PostgreSQL)
    // =========================================================
    public static String daftarMemberBaru(String nama, String noHp, String email,
                                          String tglLahir, String jenisKelamin,
                                          String alamat) throws SQLException {
        String noMember = generateNoMember();
        // Ganti sintaks PostgreSQL (::DATE, INTERVAL) ke MySQL
        String sql = "INSERT INTO member "
                   + "(no_member, nama_member, no_hp, email, tanggal_lahir, "
                   + " jenis_kelamin, alamat, tanggal_expired) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, DATE_ADD(CURDATE(), INTERVAL 1 YEAR))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, noMember);
            ps.setString(2, nama);
            ps.setString(3, noHp);
            ps.setString(4, email);
            ps.setString(5, tglLahir.isEmpty() ? null : tglLahir);
            ps.setString(6, jenisKelamin);
            ps.setString(7, alamat);
            ps.executeUpdate();
        }
        return noMember;
    }

    // =========================================================
    // GENERATE NOMOR MEMBER 16 DIGIT UNIK
    // Format: timestamp (13 digit) + 3 digit random → 16 digit
    // =========================================================
    private static String generateNoMember() {
        long ts  = System.currentTimeMillis();
        int  rnd = (int)(Math.random() * 900) + 100;
        String raw = ts + "" + rnd;
        return raw.substring(0, 16);
    }

    // =========================================================
    // AMBIL RIWAYAT POIN MEMBER (untuk panel histori)
    // =========================================================
    public static java.util.List<Object[]> riwayatPoin(int idMember) throws SQLException {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT r.tanggal, r.jenis, r.jumlah_poin, r.keterangan "
                   + "FROM riwayat_poin r "
                   + "WHERE r.id_member = ? "
                   + "ORDER BY r.tanggal DESC LIMIT 50";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMember);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getString("tanggal"),
                        rs.getString("jenis"),
                        rs.getInt("jumlah_poin"),
                        rs.getString("keterangan")
                    });
                }
            }
        }
        return list;
    }
}