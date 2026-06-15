/**
 * Class Kasir adalah subclass dari Pengguna.
 * Kasir hanya dapat melakukan transaksi penjualan.
 *
 * Menerapkan:
 * - Inheritance dari Pengguna
 * - Overriding method getRole()
 * - Pemanggilan super constructor dan super method
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public class Kasir extends Pengguna {

    private String nomorShift; // "pagi", "siang", "malam"
    private int totalTransaksiHariIni;

    /**
     * Constructor default.
     */
    public Kasir() {
        super();
        this.nomorShift = "pagi";
        this.totalTransaksiHariIni = 0;
    }

    /**
     * Constructor dengan parameter lengkap.
     *
     * @param id          ID pengguna
     * @param username    username
     * @param password    password
     * @param namaLengkap nama lengkap
     * @param aktif       status aktif
     * @param nomorShift  shift kasir
     */
    public Kasir(int id, String username, String password,
                 String namaLengkap, boolean aktif, String nomorShift) {
        super(id, username, password, namaLengkap, aktif); // super constructor
        this.nomorShift = nomorShift;
        this.totalTransaksiHariIni = 0;
    }

    /**
     * Constructor overloading: untuk kasir baru tanpa ID.
     *
     * @param username    username
     * @param password    password
     * @param namaLengkap nama lengkap
     */
    public Kasir(String username, String password, String namaLengkap) {
        super(username, password, namaLengkap); // super constructor
        this.nomorShift = "pagi";
        this.totalTransaksiHariIni = 0;
    }

    // ===================== GETTER & SETTER =====================

    public String getNomorShift() { return nomorShift; }
    public void setNomorShift(String nomorShift) { this.nomorShift = nomorShift; }

    public int getTotalTransaksiHariIni() { return totalTransaksiHariIni; }
    public void tambahTransaksi() { this.totalTransaksiHariIni++; }

    // ===================== OVERRIDE =====================

    /**
     * Override getRole dari superclass Pengguna.
     *
     * @return String "kasir"
     */
    @Override
    public String getRole() {
        return "kasir"; // overriding
    }

    /**
     * Override getInfo, menambahkan info shift dan total transaksi.
     *
     * @return String info kasir
     */
    @Override
    public String getInfo() {
        // Pemanggilan method superclass dengan super
        return super.getInfo() + " | Shift: " + nomorShift
                + " | Transaksi Hari Ini: " + totalTransaksiHariIni;
    }

    @Override
    public String toString() {
        return getNamaLengkap() + " [Kasir - Shift " + nomorShift + "]";
    }
}