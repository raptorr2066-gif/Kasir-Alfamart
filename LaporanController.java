import java.util.List;

/**
 * LaporanController menghubungkan PanelLaporan dengan TransaksiDAO.
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public class LaporanController {

    private TransaksiDAO dao;

    public LaporanController() {
        this.dao = new TransaksiDAO();
    }

    /** Semua transaksi (seluruh waktu) */
    public List<Transaksi> getAllTransaksi() {
        return dao.getAll();
    }

    /** Transaksi hari ini saja — dipakai oleh tabel Riwayat Transaksi */
    public List<Transaksi> getTransaksiHariIni() {
        return dao.getHariIni();
    }

    /** Statistik hari ini: [jumlah_transaksi, total_pendapatan] */
    public double[] getLaporanHariIni() {
        return dao.getLaporanHariIni();
    }
}