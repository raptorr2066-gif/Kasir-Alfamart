/**
 * Interface Printable mendefinisikan kontrak untuk objek
 * yang dapat dicetak atau ditampilkan sebagai struk/nota.
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public interface Printable {

    /**
     * Method abstrak: menghasilkan teks struk/nota.
     *
     * @return String konten struk
     */
    String cetakStruk();

    /**
     * Method default: mencetak struk ke konsol.
     */
    default void print() {
        System.out.println(cetakStruk());
    }

    /**
     * Method static: menghasilkan garis separator untuk struk.
     *
     * @param panjang jumlah karakter garis
     * @return String garis separator
     */
    static String garisStruk(int panjang) {
        return "-".repeat(panjang);
    }
}