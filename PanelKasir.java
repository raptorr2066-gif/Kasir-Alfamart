import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelKasir extends JPanel {

    private JTextField txtKodeProduk, txtQty, txtUangBayar, txtKembalian;
    private JTextField txtNomorMember;
    private JLabel lblNamaMember, lblPoinMember, lblDiskonMember, lblInfoMember;
    private JButton btnTambah, btnSelesai, btnCekMember, btnHapusMember;
    private JButton btnCash, btnQRIS, btnDebit;
    private JTable tabelKeranjang;
    private DefaultTableModel tableModel;
    private JLabel lblTotalHarga, lblTotalBayar, lblDiskonNominal;

    private double totalHargaSemua = 0;
    private double diskonPersen    = 0;
    private MemberService.Member memberAktif = null;
    private String metodeBayar = "tunai";
    private Pengguna userLogin;

    private static final Color MERAH       = new Color(140, 0, 0);
    private static final Color MERAH_GELAP = new Color(100, 0, 0);
    private static final Color HIJAU       = new Color(0, 102, 51);
    private static final Color ABU         = new Color(245, 245, 245);
    private static final Color ABU_BORDER  = new Color(200, 200, 200);

    // ========== ROUNDED BORDER ==========
    static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius, thickness;
        RoundedBorder(Color color, int radius, int thickness) {
            this.color = color; this.radius = radius; this.thickness = thickness;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x+thickness/2.0, y+thickness/2.0,
                    w-thickness, h-thickness, radius, radius));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness+4, thickness+8, thickness+4, thickness+8); }
        @Override public Insets getBorderInsets(Component c, Insets i) { i.left=i.right=thickness+8; i.top=i.bottom=thickness+4; return i; }
    }

    // ========== ROUNDED TEXTFIELD ==========
    static class RoundedTextField extends JTextField {
        private final int radius; private final Color borderColor;
        RoundedTextField(int radius, Color borderColor) {
            this.radius=radius; this.borderColor=borderColor;
            setOpaque(false); setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
            super.paintComponent(g); g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor); g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,radius,radius));
            g2.dispose();
        }
    }

    // ========== ROUNDED BUTTON ==========
    static class RoundedButton extends JButton {
        private final int radius; private Color bgColor;
        RoundedButton(String text, Color bgColor, int radius) {
            super(text); this.bgColor=bgColor; this.radius=radius;
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);
        }
        public void setBgColor(Color c) { this.bgColor=c; repaint(); }
        public Color getBgColor() { return bgColor; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed()?bgColor.darker():getModel().isRollover()?bgColor.brighter():bgColor);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
            super.paintComponent(g); g2.dispose();
        }
    }

    // ========== ROUNDED PANEL ==========
    static class RoundedPanel extends JPanel {
        private final int radius; private final Color borderColor;
        RoundedPanel(int radius, Color borderColor) {
            this.radius=radius; this.borderColor=borderColor; setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
            g2.setColor(borderColor); g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,radius,radius));
            g2.dispose();
        }
    }

    public PanelKasir(Pengguna pengguna) {
        this.userLogin = pengguna;
        setLayout(new BorderLayout(0,0));
        setBackground(new Color(235,235,235));
        add(buatPanelHeader(pengguna), BorderLayout.NORTH);
        JPanel panelTengah = new JPanel(new BorderLayout(10,0));
        panelTengah.setOpaque(false);
        panelTengah.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panelTengah.add(buatPanelKiri(), BorderLayout.CENTER);
        panelTengah.add(buatPanelKanan(), BorderLayout.EAST);
        add(panelTengah, BorderLayout.CENTER);
        add(buatPanelFooter(), BorderLayout.SOUTH);
        daftarkanListener();
    }

    private JPanel buatPanelHeader(Pengguna pengguna) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MERAH);
        panel.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        JLabel lblJudul = new JLabel("TRANSAKSI KASIR ALFAMART");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblJudul.setForeground(Color.WHITE);
        JLabel lblKasir = new JLabel(pengguna.getNamaLengkap()+" ("+pengguna.getRole()+")");
        lblKasir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblKasir.setForeground(new Color(255,200,200));
        panel.add(lblJudul, BorderLayout.WEST);
        panel.add(lblKasir, BorderLayout.EAST);
        return panel;
    }

    private JPanel buatPanelKiri() {
        JPanel panel = new JPanel(new BorderLayout(0,8));
        panel.setOpaque(false);
        RoundedPanel panelMember = new RoundedPanel(12, ABU_BORDER);
        panelMember.setBackground(Color.WHITE);
        panelMember.setLayout(new BorderLayout(8,0));
        panelMember.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
        JLabel lblSeksiMember = new JLabel("INFO MEMBER");
        lblSeksiMember.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSeksiMember.setForeground(new Color(120,120,120));
        txtNomorMember = new RoundedTextField(10, ABU_BORDER);
        txtNomorMember.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNomorMember.setPreferredSize(new Dimension(200,32));
        txtNomorMember.setBackground(Color.WHITE);
        btnCekMember   = new RoundedButton("Cek Member", MERAH, 10);
        btnCekMember.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCekMember.setPreferredSize(new Dimension(110,32));
        btnHapusMember = new RoundedButton("Hapus", new Color(150,150,150), 10);
        btnHapusMember.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHapusMember.setPreferredSize(new Dimension(70,32));
        JPanel barisCariMember = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        barisCariMember.setOpaque(false);
        barisCariMember.add(new JLabel("No. Member:"));
        barisCariMember.add(txtNomorMember);
        barisCariMember.add(btnCekMember);
        barisCariMember.add(btnHapusMember);
        lblInfoMember = new JLabel("Belum ada member yang dipilih");
        lblInfoMember.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfoMember.setForeground(new Color(150,150,150));
        lblInfoMember.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));
        JPanel isiMember = new JPanel(new BorderLayout());
        isiMember.setOpaque(false);
        isiMember.add(lblSeksiMember, BorderLayout.NORTH);
        isiMember.add(barisCariMember, BorderLayout.CENTER);
        isiMember.add(lblInfoMember, BorderLayout.SOUTH);
        panelMember.add(isiMember, BorderLayout.CENTER);
        String[] kolom = {"No","Kode","Nama Barang","Harga Satuan","Qty","Subtotal"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelKeranjang = new JTable(tableModel);
        tabelKeranjang.setRowHeight(28);
        tabelKeranjang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelKeranjang.setGridColor(new Color(235,235,235));
        tabelKeranjang.setSelectionBackground(new Color(255,220,220));
        tabelKeranjang.setShowVerticalLines(false);
        tabelKeranjang.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabelKeranjang.getColumnModel().getColumn(1).setPreferredWidth(70);
        tabelKeranjang.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabelKeranjang.getColumnModel().getColumn(3).setPreferredWidth(90);
        tabelKeranjang.getColumnModel().getColumn(4).setPreferredWidth(40);
        tabelKeranjang.getColumnModel().getColumn(5).setPreferredWidth(90);
        JTableHeader header = tabelKeranjang.getTableHeader();
        header.setBackground(MERAH); header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0,30));
        JScrollPane scrollTabel = new JScrollPane(tabelKeranjang);
        scrollTabel.setBorder(new RoundedBorder(ABU_BORDER,12,1));
        scrollTabel.getViewport().setBackground(Color.WHITE);
        panel.add(panelMember, BorderLayout.NORTH);
        panel.add(scrollTabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelKanan() {
        RoundedPanel panel = new RoundedPanel(12, ABU_BORDER);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300,0));
        panel.setBorder(BorderFactory.createEmptyBorder(14,14,14,14));
        panel.add(buatLabelSeksi("INPUT BARANG"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buatLabelField("Kode Produk:"));
        panel.add(Box.createVerticalStrut(3));
        txtKodeProduk = buatRoundedTextField("Contoh: 101001");
        panel.add(txtKodeProduk);
        panel.add(Box.createVerticalStrut(6));
        panel.add(buatLabelField("Jumlah (Qty):"));
        panel.add(Box.createVerticalStrut(3));
        txtQty = buatRoundedTextField("1"); txtQty.setText("1");
        panel.add(txtQty);
        panel.add(Box.createVerticalStrut(10));
        btnTambah = new RoundedButton("+ Tambah ke Keranjang", MERAH, 10);
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTambah.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        btnTambah.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(btnTambah);
        panel.add(Box.createVerticalStrut(14));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));
        panel.add(buatLabelSeksi("TOTAL BELANJA"));
        panel.add(Box.createVerticalStrut(4));
        lblTotalHarga = new JLabel("Rp 0");
        lblTotalHarga.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotalHarga.setForeground(MERAH);
        lblTotalHarga.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTotalHarga);
        lblDiskonNominal = new JLabel("Diskon member: Rp 0");
        lblDiskonNominal.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDiskonNominal.setForeground(HIJAU);
        lblDiskonNominal.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDiskonNominal);
        JPanel panelTotalBayar = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        panelTotalBayar.setOpaque(false);
        panelTotalBayar.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
        JLabel lblTotalBayarLabel = new JLabel("Total bayar: ");
        lblTotalBayarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalBayar = new JLabel("Rp 0");
        lblTotalBayar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelTotalBayar.add(lblTotalBayarLabel);
        panelTotalBayar.add(lblTotalBayar);
        panel.add(panelTotalBayar);
        panel.add(Box.createVerticalStrut(12));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));
        panel.add(buatLabelSeksi("METODE PEMBAYARAN"));
        panel.add(Box.createVerticalStrut(6));
        JPanel panelMetode = new JPanel(new GridLayout(1,3,6,0));
        panelMetode.setOpaque(false);
        panelMetode.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        panelMetode.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCash=buatTombolMetode("Cash"); btnQRIS=buatTombolMetode("QRIS"); btnDebit=buatTombolMetode("Debit");
        panelMetode.add(btnCash); panelMetode.add(btnQRIS); panelMetode.add(btnDebit);
        setMetodeAktif(btnCash);
        panel.add(panelMetode);
        panel.add(Box.createVerticalStrut(10));
        panel.add(buatLabelField("Uang Bayar (Rp):"));
        panel.add(Box.createVerticalStrut(3));
        txtUangBayar = buatRoundedTextField("");
        txtUangBayar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(txtUangBayar);
        panel.add(Box.createVerticalStrut(6));
        panel.add(buatLabelField("Uang Kembalian (Rp):"));
        panel.add(Box.createVerticalStrut(3));
        txtKembalian = new RoundedTextField(10, new Color(100,180,100));
        txtKembalian.setText("0");
        txtKembalian.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtKembalian.setForeground(HIJAU);
        txtKembalian.setEditable(false);
        txtKembalian.setBackground(new Color(240,255,245));
        txtKembalian.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        txtKembalian.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtKembalian);
        panel.add(Box.createVerticalStrut(14));
        btnSelesai = new RoundedButton("SELESAI TRANSAKSI", HIJAU, 10);
        btnSelesai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSelesai.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        btnSelesai.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(btnSelesai);
        return panel;
    }

    private JPanel buatPanelFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50,50,50));
        panel.setBorder(BorderFactory.createEmptyBorder(5,15,5,15));
        JLabel lblVer = new JLabel("v1.0.0 | PBO 2025");
        lblVer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVer.setForeground(new Color(180,180,180));
        panel.add(lblVer, BorderLayout.WEST);
        return panel;
    }

    private JLabel buatLabelSeksi(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(120,120,120));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
    private JLabel buatLabelField(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
    private JTextField buatRoundedTextField(String placeholder) {
        RoundedTextField tf = new RoundedTextField(10, ABU_BORDER);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(Color.WHITE);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }
    private JButton buatTombolMetode(String teks) {
        RoundedButton btn = new RoundedButton(teks, Color.WHITE, 8);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(new Color(80,80,80));
        return btn;
    }
    private void setMetodeAktif(JButton tombolAktif) {
        JButton[] semua = {btnCash, btnQRIS, btnDebit};
        for (JButton b : semua) {
            if (b==null) continue;
            if (b instanceof RoundedButton rb) rb.setBgColor(Color.WHITE);
            b.setForeground(new Color(80,80,80));
        }
        if (tombolAktif instanceof RoundedButton rb) rb.setBgColor(new Color(255,230,230));
        tombolAktif.setForeground(MERAH);
    }

    private void daftarkanListener() {
        btnTambah.addActionListener(e -> tambahBarang());
        txtKodeProduk.addActionListener(e -> tambahBarang());
        txtUangBayar.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { hitungKembalian(); }
        });
        btnSelesai.addActionListener(e -> simpanTransaksi());
        btnCekMember.addActionListener(e -> cekMember());
        txtNomorMember.addActionListener(e -> cekMember());
        btnHapusMember.addActionListener(e -> hapusMember());
        btnCash.addActionListener(e  -> { metodeBayar="tunai"; setMetodeAktif(btnCash);  });
        btnQRIS.addActionListener(e  -> { metodeBayar="qris";  setMetodeAktif(btnQRIS);  });
        btnDebit.addActionListener(e -> { metodeBayar="debit"; setMetodeAktif(btnDebit); });
    }

    private void cekMember() {
        String noMember = txtNomorMember.getText().trim();
        if (noMember.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan nomor member terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            MemberService.Member m = MemberService.cekMember(noMember);
            if (m==null) {
                JOptionPane.showMessageDialog(this, "Member tidak ditemukan!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (!"AKTIF".equals(m.statusMember)) {
                JOptionPane.showMessageDialog(this, "Member "+m.namaMember+" berstatus "+m.statusMember, "Member Tidak Aktif", JOptionPane.WARNING_MESSAGE);
                return;
            }
            memberAktif=m; diskonPersen=m.diskonPersen;
            String badge = switch (m.tier) {
                case "Gold"     -> "GOLD";
                case "Platinum" -> "PLATINUM";
                default         -> "SILVER";
            };
            lblInfoMember.setText(m.namaMember+"  |  "+badge+"  |  Poin: "+m.poin+"  |  Diskon: "+(int)m.diskonPersen+"%");
            lblInfoMember.setForeground(HIJAU);
            updateLabelTotal();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal akses data member:\n"+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusMember() {
        memberAktif=null; diskonPersen=0;
        txtNomorMember.setText("");
        lblInfoMember.setText("Belum ada member yang dipilih");
        lblInfoMember.setForeground(new Color(150,150,150));
        updateLabelTotal();
    }

    private void tambahBarang() {
        String kode   = txtKodeProduk.getText().trim();
        String qtyStr = txtQty.getText().trim();
        if (kode.isEmpty()||qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode produk dan Qty tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int qty;
        try { qty = Integer.parseInt(qtyStr); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Qty harus berupa angka!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "SELECT nama_produk, harga_jual, stok FROM produk WHERE kode_produk = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nama         = rs.getString("nama_produk");
                    double harga        = rs.getDouble("harga_jual");
                    int    stokTersedia = rs.getInt("stok");
                    if (stokTersedia < qty) {
                        JOptionPane.showMessageDialog(this, "Stok tidak mencukupi! Sisa stok: "+stokTersedia, "Stok Habis", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double subtotal = harga * qty;
                    int    no       = tableModel.getRowCount()+1;
                    tableModel.addRow(new Object[]{no, kode, nama, formatRupiah(harga), qty, formatRupiah(subtotal)});
                    totalHargaSemua += subtotal;
                    updateLabelTotal();
                    txtKodeProduk.setText(""); txtQty.setText("1");
                    txtKodeProduk.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(this, "Produk dengan kode '"+kode+"' tidak ditemukan!", "Salah Kode", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data produk: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hitungKembalian() {
        try {
            String bayarStr = txtUangBayar.getText().trim();
            if (bayarStr.isEmpty()) { txtKembalian.setText("0"); return; }
            double uangBayar          = parseCurrency(bayarStr);
            double totalSetelahDiskon = totalHargaSemua-(totalHargaSemua*diskonPersen/100);
            double kembalian          = uangBayar-totalSetelahDiskon;
            txtKembalian.setText(kembalian>=0 ? formatRupiah(kembalian) : "Kurang");
        } catch (NumberFormatException ex) { txtKembalian.setText("Input salah"); }
    }

    private void updateLabelTotal() {
        Locale id = new Locale("in","ID");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(id);
        lblTotalHarga.setText(fmt.format(totalHargaSemua));
        double diskonNominal      = totalHargaSemua*diskonPersen/100;
        double totalSetelahDiskon = totalHargaSemua-diskonNominal;
        lblDiskonNominal.setText("Diskon member ("+(int)diskonPersen+"%): -"+fmt.format(diskonNominal));
        lblTotalBayar.setText(fmt.format(totalSetelahDiskon));
        hitungKembalian();
    }

    private String formatRupiah(double nominal) {
        return NumberFormat.getCurrencyInstance(new Locale("in","ID")).format(nominal);
    }

    private double parseCurrency(String s) throws NumberFormatException {
        if (s==null) return 0;
        s=s.replaceAll("Rp","").trim().replaceAll("[^0-9,\\.]","");
        if (s.isEmpty()) return 0;
        if (s.contains(",")&&s.contains(".")) s=s.replaceAll("\\.","").replace(',','.');
        else if (s.contains(",")) s=s.replace(',','.');
        return Double.parseDouble(s);
    }

    // ========== SIMPAN TRANSAKSI — MySQL version ==========
    private void simpanTransaksi() {
        if (tableModel.getRowCount()==0) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong!", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String bayarStr           = txtUangBayar.getText().trim();
        double totalSetelahDiskon = totalHargaSemua-(totalHargaSemua*diskonPersen/100);
        double bayarVal = 0;
        try {
            if (!bayarStr.isEmpty()) bayarVal = parseCurrency(bayarStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format uang tidak valid!", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (bayarStr.isEmpty()||bayarVal<totalSetelahDiskon) {
            JOptionPane.showMessageDialog(this, "Uang pembayaran kurang atau belum diisi!", "Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double uangBayar     = bayarVal;
        double kembalian     = uangBayar-totalSetelahDiskon;
        double diskonNominal = totalHargaSemua*diskonPersen/100;
        String noTransaksi   = "TRX-"+System.currentTimeMillis();

        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // ── MYSQL: tidak pakai RETURNING, pakai GENERATED_KEYS ──
            String sqlTrx = "INSERT INTO transaksi "
                + "(no_transaksi, id_pengguna, id_member, tanggal_transaksi, "
                + " total_harga, diskon, total_bayar, uang_bayar, kembalian, metode_bayar, status) "
                + "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, 'selesai')";

            int idTransaksiGenerated = 0;
            try (PreparedStatement psTrx = conn.prepareStatement(sqlTrx, Statement.RETURN_GENERATED_KEYS)) {
                psTrx.setString(1, noTransaksi);
                psTrx.setInt(2, userLogin.getId());
                if (memberAktif!=null) psTrx.setInt(3, memberAktif.idMember);
                else                   psTrx.setNull(3, Types.INTEGER);
                psTrx.setDouble(4, totalHargaSemua);
                psTrx.setDouble(5, diskonNominal);
                psTrx.setDouble(6, totalSetelahDiskon);
                psTrx.setDouble(7, uangBayar);
                psTrx.setDouble(8, kembalian);
                psTrx.setString(9, metodeBayar);
                psTrx.executeUpdate();
                // ── MYSQL: ambil ID dari getGeneratedKeys() ──
                try (ResultSet rs = psTrx.getGeneratedKeys()) {
                    if (rs.next()) idTransaksiGenerated = rs.getInt(1);
                }
            }

            String sqlDetail     = "INSERT INTO detail_transaksi (id_transaksi, id_produk, qty, harga_satuan, subtotal) VALUES (?, ?, ?, ?, ?)";
            String sqlUpdateStok = "UPDATE produk SET stok = stok - ? WHERE kode_produk = ?";

            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                 PreparedStatement psStok   = conn.prepareStatement(sqlUpdateStok)) {
                for (int i=0; i<tableModel.getRowCount(); i++) {
                    String kodeProduk = tableModel.getValueAt(i,1).toString();
                    int    qty        = (int) tableModel.getValueAt(i,4);
                    String sqlProduk  = "SELECT id_produk, harga_jual FROM produk WHERE kode_produk = ?";
                    try (PreparedStatement psProd = conn.prepareStatement(sqlProduk)) {
                        psProd.setString(1, kodeProduk);
                        try (ResultSet rsProd = psProd.executeQuery()) {
                            if (rsProd.next()) {
                                int    idProduk = rsProd.getInt("id_produk");
                                double harga    = rsProd.getDouble("harga_jual");
                                psDetail.setInt(1, idTransaksiGenerated);
                                psDetail.setInt(2, idProduk);
                                psDetail.setInt(3, qty);
                                psDetail.setDouble(4, harga);
                                psDetail.setDouble(5, harga*qty);
                                psDetail.addBatch();
                            }
                        }
                    }
                    psStok.setInt(1, qty);
                    psStok.setString(2, kodeProduk);
                    psStok.addBatch();
                }
                psDetail.executeBatch();
                psStok.executeBatch();
            }

            conn.commit();
            conn.setAutoCommit(true);
            // ── Tidak conn.close() — pakai singleton ──

            if (memberAktif!=null) {
                try {
                    int poinDapat = MemberService.tambahPoin(memberAktif.idMember, idTransaksiGenerated, totalSetelahDiskon);
                    if (diskonNominal>0) MemberService.catatDiskon(idTransaksiGenerated, memberAktif.idMember, diskonPersen, diskonNominal);
                    String pesanPoin = poinDapat>0 ? "\nPoin diperoleh: +"+poinDapat : "";
                    JOptionPane.showMessageDialog(this,
                        "Transaksi Berhasil!\nNo: "+noTransaksi+"\nKembalian: "+formatRupiah(kembalian)+pesanPoin,
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException exMember) {
                    exMember.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Transaksi berhasil, tapi gagal proses poin:\n"+exMember.getMessage(),
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Transaksi Berhasil!\nNo: "+noTransaksi+"\nKembalian: "+formatRupiah(kembalian),
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }

            tableModel.setRowCount(0);
            totalHargaSemua=0; diskonPersen=0;
            updateLabelTotal();
            txtUangBayar.setText(""); txtKembalian.setText("0");
            hapusMember();

        } catch (SQLException ex) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Transaksi Gagal: "+ex.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }
}