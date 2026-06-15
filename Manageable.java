/**
 * Interface Manageable mendefinisikan kontrak untuk semua entitas
 * yang dapat dikelola (disimpan, diperbarui, dihapus).
 *
 * Interface ini memenuhi spesifikasi:
 * - Minimal 1 method abstrak
 * - Minimal 1 method static
 * - Minimal 1 method default
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public interface Manageable {

    /**
     * Method abstrak: validasi data entitas.
     * Setiap class yang mengimplementasikan interface ini
     * wajib mendefinisikan logika validasinya sendiri.
     *
     * @return true jika data valid, false jika tidak valid
     */
    boolean isValid();

    /**
     * Method abstrak: mendapatkan representasi string ringkas dari entitas.
     *
     * @return String informasi singkat entitas
     */
    String getInfo();

    /**
     * Method default: menampilkan status entitas.
     * Dapat di-override oleh implementing class.
     *
     * @return String status entitas
     */
    default String getStatus() {
        return isValid() ? "AKTIF" : "TIDAK VALID";
    }

    /**
     * Method default: mencetak info entitas ke konsol (untuk debugging).
     */
    default void printInfo() {
        System.out.println("[" + getStatus() + "] " + getInfo());
    }

    /**
     * Method static: memformat harga ke format Rupiah.
     *
     * @param harga nilai harga dalam double
     * @return String harga terformat, e.g. "Rp 15.000"
     */
    static String formatRupiah(double harga) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance(new java.util.Locale("id", "ID"));
        return "Rp " + nf.format(harga);
    }

    /**
     * Method static: membuat kode unik berdasarkan timestamp.
     *
     * @param prefix awalan kode, e.g. "TRX"
     * @return String kode unik
     */
    static String generateKode(String prefix) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        return prefix + "-" + sdf.format(new java.util.Date());
    }
}