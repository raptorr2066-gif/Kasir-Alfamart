import java.util.List;

public class KasirController {

    private final ProdukDAO produkDAO;
    private final TransaksiDAO transaksiDAO;

    public KasirController() {
        this.produkDAO = new ProdukDAO();
        this.transaksiDAO = new TransaksiDAO();
    }

    public List<Produk> getAllProduk() {
        return produkDAO.getAll();
    }

    public List<Produk> cariProduk(String keyword) {
        return produkDAO.cari(keyword);
    }

    public Produk getProdukByKode(String kode) {
        return produkDAO.getByKode(kode);
    }

    public boolean simpanTransaksi(Transaksi transaksi) {
        return transaksiDAO.simpan(transaksi);
    }
}