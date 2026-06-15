import java.util.List;

public class ProdukController {

    private final ProdukDAO dao;

    public ProdukController() {
        this.dao = new ProdukDAO();
    }

    public List<Produk> getAllProduk() {
        return dao.getAll();
    }

    public List<Produk> cariProduk(String keyword) {
        return dao.cari(keyword);
    }

    public boolean simpanProduk(Produk produk) {
        return dao.simpan(produk);
    }

    public boolean updateProduk(Produk produk) {
        return dao.update(produk);
    }

    public boolean hapusProduk(int id) {
        return dao.hapus(id);
    }

    public Produk getProdukByKode(String kode) {
        return dao.getByKode(kode);
    }
}
