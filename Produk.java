/**
 * Class Produk merepresentasikan produk yang dijual di Alfamart.
 * Extends Entitas (inheritance) dengan enkapsulasi penuh.
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public class Produk extends Entitas {

    private String kodeProduk;
    private String namaProduk;
    private Kategori kategori;
    private double hargaBeli;
    private double hargaJual;
    private int stok;
    private String satuan;

    /**
     * Constructor default.
     */
    public Produk() {
        super();
        this.stok = 0;
        this.satuan = "pcs";
    }

    /**
     * Constructor lengkap.
     *
     * @param id          ID produk
     * @param kodeProduk  kode unik produk
     * @param namaProduk  nama produk
     * @param kategori    objek kategori
     * @param hargaBeli   harga beli dari supplier
     * @param hargaJual   harga jual ke konsumen
     * @param stok        jumlah stok
     * @param satuan      satuan produk (pcs, botol, dll)
     */
    public Produk(int id, String kodeProduk, String namaProduk,
                  Kategori kategori, double hargaBeli, double hargaJual,
                  int stok, String satuan) {
        super(id);
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.kategori = kategori;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
        this.satuan = satuan;
    }

    /**
     * Constructor overloading: tanpa ID (untuk produk baru).
     *
     * @param kodeProduk kode produk
     * @param namaProduk nama produk
     * @param hargaJual  harga jual
     * @param stok       stok awal
     */
    public Produk(String kodeProduk, String namaProduk, double hargaJual, int stok) {
        super();
        this.kodeProduk = kodeProduk;
        this.namaProduk = namaProduk;
        this.hargaJual = hargaJual;
        this.stok = stok;
        this.satuan = "pcs";
    }

    // ===================== GETTER & SETTER =====================

    public String getKodeProduk() { return kodeProduk; }
    public void setKodeProduk(String kodeProduk) { this.kodeProduk = kodeProduk; }

    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }

    public Kategori getKategori() { return kategori; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }

    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }

    // ===================== BUSINESS LOGIC =====================

    /**
     * Mengurangi stok saat terjadi penjualan.
     *
     * @param jumlah jumlah yang dijual
     * @throws IllegalArgumentException jika stok tidak mencukupi
     */
    public void kurangiStok(int jumlah) {
        if (jumlah > stok) {
            throw new IllegalArgumentException("Stok tidak mencukupi. Stok saat ini: " + stok);
        }
        this.stok -= jumlah;
    }

    /**
     * Menambah stok saat terjadi pembelian/restock.
     *
     * @param jumlah jumlah yang ditambahkan
     */
    public void tambahStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari 0");
        }
        this.stok += jumlah;
    }

    /**
     * Menghitung keuntungan per unit produk.
     *
     * @return selisih harga jual dan harga beli
     */
    public double getKeuntungan() {
        return hargaJual - hargaBeli;
    }

    /**
     * Mengecek apakah stok produk menipis (di bawah 10).
     *
     * @return true jika stok kurang dari 10
     */
    public boolean isStokMenupis() {
        return stok < 10;
    }

    // ===================== IMPLEMENTASI INTERFACE =====================

    @Override
    public boolean isValid() {
        return super.isValid()
                && kodeProduk != null && !kodeProduk.trim().isEmpty()
                && namaProduk != null && !namaProduk.trim().isEmpty()
                && hargaJual > 0
                && stok >= 0;
    }

    @Override
    public String getInfo() {
        return kodeProduk + " | " + namaProduk + " | " + Manageable.formatRupiah(hargaJual) + " | Stok: " + stok;
    }

    @Override
    public String toString() {
        return kodeProduk + " - " + namaProduk;
    }
}