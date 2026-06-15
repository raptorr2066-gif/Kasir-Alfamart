public class DetailTransaksi extends Entitas {

    private Produk produk;
    private int qty;
    private double hargaSatuan;
    private double subtotal;

   
    public DetailTransaksi() {
        super();
        this.qty = 1;
    }

    public DetailTransaksi(Produk produk, int qty, double hargaSatuan) {
        super();
        this.produk = produk;
        this.qty = qty;
        this.hargaSatuan = hargaSatuan;
        hitungSubtotal();
    }

    public DetailTransaksi(int id, Produk produk, int qty, double hargaSatuan) {
        super(id);
        this.produk = produk;
        this.qty = qty;
        this.hargaSatuan = hargaSatuan;
        hitungSubtotal();
    }

    public Produk getProduk() { return produk; }
    public void setProduk(Produk produk) { this.produk = produk; }

    public int getQty() { return qty; }
    public void setQty(int qty) {
        this.qty = qty;
        hitungSubtotal();
    }

    public double getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(double hargaSatuan) {
        this.hargaSatuan = hargaSatuan;
        hitungSubtotal();
    }

    public double getSubtotal() { return subtotal; }

    private void hitungSubtotal() {
        this.subtotal = this.qty * this.hargaSatuan;
    }


    @Override
    public boolean isValid() {
        return produk != null && qty > 0 && hargaSatuan > 0;
    }

    @Override
    public String getInfo() {
        return produk.getNamaProduk() + " x" + qty + " @ "
                + Manageable.formatRupiah(hargaSatuan)
                + " = " + Manageable.formatRupiah(subtotal);
    }
}