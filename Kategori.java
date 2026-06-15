/**
 * Class Kategori merepresentasikan kategori produk di Alfamart.
 * Extends Entitas (inheritance) dan menerapkan enkapsulasi.
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public class Kategori extends Entitas {

    private String namaKategori;
    private String deskripsi;

    /**
     * Constructor default.
     */
    public Kategori() {
        super();
    }

    /**
     * Constructor dengan parameter lengkap.
     *
     * @param id           ID kategori
     * @param namaKategori nama kategori
     * @param deskripsi    deskripsi kategori
     */
    public Kategori(int id, String namaKategori, String deskripsi) {
        super(id);
        this.namaKategori = namaKategori;
        this.deskripsi = deskripsi;
    }

    /**
     * Constructor overloading: hanya nama dan deskripsi (tanpa ID).
     *
     * @param namaKategori nama kategori
     * @param deskripsi    deskripsi kategori
     */
    public Kategori(String namaKategori, String deskripsi) {
        super();
        this.namaKategori = namaKategori;
        this.deskripsi = deskripsi;
    }

    // ===================== GETTER & SETTER =====================

    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    // ===================== IMPLEMENTASI INTERFACE =====================

    @Override
    public boolean isValid() {
        return super.isValid() && namaKategori != null && !namaKategori.trim().isEmpty();
    }

    @Override
    public String getInfo() {
        return "Kategori: " + namaKategori + (deskripsi != null ? " | " + deskripsi : "");
    }

    @Override
    public String toString() {
        return namaKategori;
    }
}