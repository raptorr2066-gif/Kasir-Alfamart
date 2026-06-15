import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class Transaksi merepresentasikan satu transaksi penjualan.
 * Extends Entitas dan mengimplementasikan interface Printable.
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public class Transaksi extends Entitas implements Printable {

    private String noTransaksi;
    private Pengguna kasir;
    private Date tanggalTransaksi;
    private List<DetailTransaksi> listDetail;
    private double totalHarga;
    private double diskon;
    private double totalBayar;
    private double uangBayar;
    private double kembalian;
    private String metodeBayar; // "tunai", "debit", "qris"
    private String status;

    /**
     * Constructor default.
     */
    public Transaksi() {
        super();
        this.listDetail = new ArrayList<>();
        this.tanggalTransaksi = new Date();
        this.metodeBayar = "tunai";
        this.status = "selesai";
        this.noTransaksi = Manageable.generateKode("TRX");
    }

    /**
     * Constructor dengan kasir.
     *
     * @param kasir objek pengguna/kasir yang melakukan transaksi
     */
    public Transaksi(Pengguna kasir) {
        super();
        this.kasir = kasir;
        this.listDetail = new ArrayList<>();
        this.tanggalTransaksi = new Date();
        this.metodeBayar = "tunai";
        this.status = "selesai";
        this.noTransaksi = Manageable.generateKode("TRX");
    }

    /**
     * Constructor lengkap dari database.
     *
     * @param id              ID transaksi
     * @param noTransaksi     nomor transaksi
     * @param kasir           kasir pelaksana
     * @param tanggal         tanggal transaksi
     * @param totalHarga      total harga sebelum diskon
     * @param diskon          nilai diskon
     * @param totalBayar      total yang harus dibayar
     * @param uangBayar       uang yang dibayarkan
     * @param kembalian       kembalian
     * @param metodeBayar     metode pembayaran
     * @param status          status transaksi
     */
    public Transaksi(int id, String noTransaksi, Pengguna kasir, Date tanggal,
                     double totalHarga, double diskon, double totalBayar,
                     double uangBayar, double kembalian, String metodeBayar, String status) {
        super(id);
        this.noTransaksi = noTransaksi;
        this.kasir = kasir;
        this.tanggalTransaksi = tanggal;
        this.listDetail = new ArrayList<>();
        this.totalHarga = totalHarga;
        this.diskon = diskon;
        this.totalBayar = totalBayar;
        this.uangBayar = uangBayar;
        this.kembalian = kembalian;
        this.metodeBayar = metodeBayar;
        this.status = status;
    }

    // ===================== BUSINESS LOGIC =====================

    /**
     * Menambahkan item ke transaksi.
     *
     * @param detail item detail transaksi
     */
    public void tambahItem(DetailTransaksi detail) {
        // Cek apakah produk sudah ada di list
        for (DetailTransaksi d : listDetail) {
            if (d.getProduk().getId() == detail.getProduk().getId()) {
                d.setQty(d.getQty() + detail.getQty());
                hitungTotal();
                return;
            }
        }
        listDetail.add(detail);
        hitungTotal();
    }

    /**
     * Menghapus item dari transaksi berdasarkan index.
     *
     * @param index index item di list
     */
    public void hapusItem(int index) {
        if (index >= 0 && index < listDetail.size()) {
            listDetail.remove(index);
            hitungTotal();
        }
    }

    /**
     * Menghitung ulang total harga dari semua item.
     */
    public void hitungTotal() {
        totalHarga = 0;
        for (DetailTransaksi d : listDetail) {
            totalHarga += d.getSubtotal();
        }
        totalBayar = totalHarga - diskon;
    }

    /**
     * Menghitung kembalian.
     *
     * @param uangBayar uang yang dibayarkan pelanggan
     * @return kembalian
     * @throws IllegalArgumentException jika uang kurang
     */
    public double hitungKembalian(double uangBayar) {
        if (uangBayar < totalBayar) {
            throw new IllegalArgumentException("Uang tidak cukup. Kurang: "
                    + Manageable.formatRupiah(totalBayar - uangBayar));
        }
        this.uangBayar = uangBayar;
        this.kembalian = uangBayar - totalBayar;
        return this.kembalian;
    }

    // ===================== IMPLEMENTASI INTERFACE PRINTABLE =====================

    /**
     * Menghasilkan teks struk belanja Alfamart.
     * Implementasi method abstrak dari interface Printable.
     *
     * @return String struk belanja
     */
    @Override
    public String cetakStruk() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        String garis = Printable.garisStruk(40); // pemanggilan static method interface

        sb.append(garis).append("\n");
        sb.append("         ALFAMART\n");
        sb.append("   Jl. Contoh No.1, Jakarta\n");
        sb.append("   Telp: (021) 123-4567\n");
        sb.append(garis).append("\n");
        sb.append("No  : ").append(noTransaksi).append("\n");
        sb.append("Tgl : ").append(sdf.format(tanggalTransaksi)).append("\n");
        sb.append("Kasir: ").append(kasir != null ? kasir.getNamaLengkap() : "-").append("\n");
        sb.append(garis).append("\n");

        for (DetailTransaksi d : listDetail) {
            String namaProduk = d.getProduk().getNamaProduk();
            if (namaProduk.length() > 22) namaProduk = namaProduk.substring(0, 22);
            sb.append(String.format("%-22s\n", namaProduk));
            sb.append(String.format("  %3dx%-10s %10s\n",
                    d.getQty(),
                    Manageable.formatRupiah(d.getHargaSatuan()),
                    Manageable.formatRupiah(d.getSubtotal())));
        }

        sb.append(garis).append("\n");
        sb.append(String.format("%-20s %10s\n", "Total Belanja:", Manageable.formatRupiah(totalHarga)));
        if (diskon > 0) {
            sb.append(String.format("%-20s %10s\n", "Diskon:", "- " + Manageable.formatRupiah(diskon)));
        }
        sb.append(String.format("%-20s %10s\n", "Total Bayar:", Manageable.formatRupiah(totalBayar)));
        sb.append(String.format("%-20s %10s\n", "Tunai:", Manageable.formatRupiah(uangBayar)));
        sb.append(String.format("%-20s %10s\n", "Kembali:", Manageable.formatRupiah(kembalian)));
        sb.append(garis).append("\n");
        sb.append("    Terima kasih telah berbelanja\n");
        sb.append("        di Alfamart!\n");
        sb.append(garis).append("\n");

        return sb.toString();
    }

    // ===================== GETTER & SETTER =====================

    public String getNoTransaksi() { return noTransaksi; }
    public void setNoTransaksi(String noTransaksi) { this.noTransaksi = noTransaksi; }

    public Pengguna getKasir() { return kasir; }
    public void setKasir(Pengguna kasir) { this.kasir = kasir; }

    public Date getTanggalTransaksi() { return tanggalTransaksi; }
    public void setTanggalTransaksi(Date tanggalTransaksi) { this.tanggalTransaksi = tanggalTransaksi; }

    public List<DetailTransaksi> getListDetail() { return listDetail; }
    public void setListDetail(List<DetailTransaksi> listDetail) { this.listDetail = listDetail; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }

    public double getDiskon() { return diskon; }
    public void setDiskon(double diskon) {
        this.diskon = diskon;
        hitungTotal();
    }

    public double getTotalBayar() { return totalBayar; }
    public void setTotalBayar(double totalBayar) { this.totalBayar = totalBayar; }

    public double getUangBayar() { return uangBayar; }
    public void setUangBayar(double uangBayar) { this.uangBayar = uangBayar; }

    public double getKembalian() { return kembalian; }
    public void setKembalian(double kembalian) { this.kembalian = kembalian; }

    public String getMetodeBayar() { return metodeBayar; }
    public void setMetodeBayar(String metodeBayar) { this.metodeBayar = metodeBayar; }

    public String getStatus() { return status; }
    public void setStatusTransaksi(String status) { this.status = status; }

    // ===================== INTERFACE IMPLEMENTATION =====================

    @Override
    public boolean isValid() {
        return noTransaksi != null && kasir != null && !listDetail.isEmpty();
    }

    @Override
    public String getInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return "Transaksi " + noTransaksi + " | "
                + sdf.format(tanggalTransaksi) + " | "
                + "Kasir: " + (kasir != null ? kasir.getNamaLengkap() : "-") + " | "
                + "Total: " + Manageable.formatRupiah(totalBayar);
    }

    @Override
    public String toString() {
        return noTransaksi + " - " + Manageable.formatRupiah(totalBayar);
    }
}