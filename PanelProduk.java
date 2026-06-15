import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelProduk extends JPanel {

    private Pengguna penggunaAktif;
    private ProdukController controller;
    private NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));

    private JTable tabelProduk;
    private DefaultTableModel modelTabel;
    private JTextField txtKode, txtNama, txtHargaBeli, txtHargaJual, txtStok, txtSatuan, txtCari;
    private JComboBox<Kategori> cbKategori;

    private static final Color MERAH     = new Color(140, 0, 0);
    private static final Color MERAH_ABU = new Color(100, 100, 100);

    // ── Rounded Border ────────────────────────────────────────────────────────
    static class RoundedTitledBorder extends AbstractBorder {
        private final String title;
        private final int radius;
        private final Color borderColor;
        RoundedTitledBorder(String title, int radius, Color borderColor) {
            this.title = title; this.radius = radius; this.borderColor = borderColor;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor); g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(x+1, y+8, w-3, h-10, radius, radius));
            FontMetrics fm = g2.getFontMetrics(new Font("Segoe UI", Font.BOLD, 12));
            int tw = fm.stringWidth(title);
            g2.setColor(c.getBackground()); g2.fillRect(x+12, y, tw+8, 16);
            g2.setColor(new Color(60,60,60)); g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.drawString(title, x+16, y+12); g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(18,12,10,12); }
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(18,12,10,12); return i; }
    }

    // ── Rounded Button ────────────────────────────────────────────────────────
    static class RoundedButton extends JButton {
        private final Color bgColor;
        RoundedButton(String text, Color bgColor) {
            super(text); this.bgColor = bgColor;
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false); setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = getModel().isPressed() ? bgColor.darker() : getModel().isRollover() ? bgColor.brighter() : bgColor;
            g2.setColor(c);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),10,10));
            super.paintComponent(g); g2.dispose();
        }
    }

    // ── Rounded TextField ─────────────────────────────────────────────────────
    static class RoundedField extends JTextField {
        RoundedField(int cols) {
            super(cols); setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),10,10));
            super.paintComponent(g); g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200,200,200)); g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,10,10));
            g2.dispose();
        }
    }

    // ── Rounded ComboBox ──────────────────────────────────────────────────────
    static class RoundedComboBox<T> extends JComboBox<T> {
        RoundedComboBox() {
            super(); setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2,6,2,6));
            putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),10,10));
            g2.setColor(new Color(200,200,200)); g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,10,10));
            g2.dispose(); super.paintComponent(g);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    public PanelProduk(Pengguna penggunaAktif) {
        this.penggunaAktif = penggunaAktif;
        this.controller    = new ProdukController();
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        setBackground(new Color(245,245,245));
        initComponents();
        muatData();
    }

    private void initComponents() {
        // ── TABEL ─────────────────────────────────────────────────────────────
        JPanel panelTabel = new JPanel(new BorderLayout(5,5));
        panelTabel.setBackground(Color.WHITE);
        panelTabel.setBorder(new RoundedTitledBorder("Data Produk", 16, new Color(200,200,200)));

        String[] kolom = {"ID","Kode","Nama Produk","Kategori","Harga Beli","Harga Jual","Stok","Satuan"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelProduk = new JTable(modelTabel);
        tabelProduk.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelProduk.setRowHeight(26);
        tabelProduk.setShowGrid(true);
        tabelProduk.setGridColor(new Color(230,230,230));

        JTableHeader header = tabelProduk.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(MERAH);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(val != null ? val.toString() : "", SwingConstants.CENTER) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        int totalCols = t.getColumnCount();
                        if (col == 0) {
                            g2.setColor(MERAH);
                            g2.fillRoundRect(0, 0, getWidth()+10, getHeight(), 12, 12);
                            g2.fillRect(getWidth()-10, 0, 10, getHeight());
                        } else if (col == totalCols - 1) {
                            g2.setColor(MERAH);
                            g2.fillRoundRect(-10, 0, getWidth()+10, getHeight(), 12, 12);
                            g2.fillRect(0, 0, 10, getHeight());
                        } else {
                            g2.setColor(MERAH);
                            g2.fillRect(0, 0, getWidth(), getHeight());
                        }
                        if (col < totalCols - 1) {
                            g2.setColor(new Color(255, 255, 255, 120));
                            g2.setStroke(new BasicStroke(1.0f));
                            g2.drawLine(getWidth() - 1, 6, getWidth() - 1, getHeight() - 6);
                        }
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setOpaque(false);
                lbl.setBorder(BorderFactory.createEmptyBorder(0,6,0,6));
                return lbl;
            }
        });

        tabelProduk.setSelectionBackground(new Color(255,230,230));
        tabelProduk.getColumnModel().getColumn(0).setMaxWidth(40);
        tabelProduk.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { isiForm(); }
        });

        // ── Search bar ────────────────────────────────────────────────────────
        JPanel panelCari = new JPanel(new BorderLayout(5,0));
        panelCari.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        panelCari.setBackground(Color.WHITE);

        txtCari = new RoundedField(20);
        txtCari.setBackground(Color.WHITE);
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtCari.setPreferredSize(new Dimension(0, 32));

        RoundedButton btnCari = new RoundedButton("Cari", MERAH);
        btnCari.setPreferredSize(new Dimension(75, 32));
        btnCari.addActionListener(e -> cariProduk());
        txtCari.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode()==KeyEvent.VK_ENTER) cariProduk(); }
        });

        RoundedButton btnMuat = new RoundedButton("Muat Ulang", MERAH_ABU);
        btnMuat.setPreferredSize(new Dimension(105, 32));
        btnMuat.addActionListener(e -> { txtCari.setText(""); muatData(); });

        JPanel panelCariKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelCariKanan.setOpaque(false);
        panelCariKanan.add(btnCari); panelCariKanan.add(btnMuat);

        JLabel lblCari = new JLabel("Cari: ");
        lblCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelCari.add(lblCari,        BorderLayout.WEST);
        panelCari.add(txtCari,        BorderLayout.CENTER);
        panelCari.add(panelCariKanan, BorderLayout.EAST);

        panelTabel.add(panelCari,                    BorderLayout.NORTH);
        panelTabel.add(new JScrollPane(tabelProduk), BorderLayout.CENTER);

        // ── FORM ──────────────────────────────────────────────────────────────
        JPanel panelForm = new JPanel(new BorderLayout(5,5));
        panelForm.setBackground(Color.WHITE);
        panelForm.setPreferredSize(new Dimension(320,0));
        panelForm.setBorder(new RoundedTitledBorder("Form Produk", 16, new Color(200,200,200)));

        JPanel formField = new JPanel(new GridBagLayout());
        formField.setBackground(Color.WHITE);
        formField.setBorder(BorderFactory.createEmptyBorder(10,15,5,15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,4,5,4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        txtKode      = new RoundedField(15); txtKode.setBackground(Color.WHITE);
        txtNama      = new RoundedField(15); txtNama.setBackground(Color.WHITE);
        txtHargaBeli = new RoundedField(15); txtHargaBeli.setText("0"); txtHargaBeli.setBackground(Color.WHITE);
        txtHargaJual = new RoundedField(15); txtHargaJual.setText("0"); txtHargaJual.setBackground(Color.WHITE);
        txtStok      = new RoundedField(15); txtStok.setText("0");      txtStok.setBackground(Color.WHITE);
        txtSatuan    = new RoundedField(15); txtSatuan.setText("pcs");  txtSatuan.setBackground(Color.WHITE);

        List<Kategori> kategoriList = new KategoriDAO().getAll();
        cbKategori = new RoundedComboBox<>();
        cbKategori.setBackground(Color.WHITE);
        java.util.Set<String> namaSudahAda = new java.util.LinkedHashSet<>();
        for (Kategori k : kategoriList) {
            if (namaSudahAda.add(k.getNamaKategori())) {
                cbKategori.addItem(k);
            }
        }

        String[] labels = {"Kode Produk:","Nama Produk:","Kategori:","Harga Beli:","Harga Jual:","Stok:","Satuan:"};
        JComponent[] comps = {txtKode, txtNama, cbKategori, txtHargaBeli, txtHargaJual, txtStok, txtSatuan};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            formField.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            comps[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            comps[i].setPreferredSize(new Dimension(0, 32));
            formField.add(comps[i], gbc);
        }

        // ── Tombol Aksi ───────────────────────────────────────────────────────
        JPanel panelAksi = new JPanel(new GridLayout(2,2,8,8));
        panelAksi.setBorder(BorderFactory.createEmptyBorder(8,15,10,15));
        panelAksi.setBackground(Color.WHITE);

        RoundedButton btnSimpan = new RoundedButton("Simpan",    new Color(0, 100, 50));
        RoundedButton btnUpdate = new RoundedButton("Update",    new Color(100, 0, 0));
        RoundedButton btnHapus  = new RoundedButton("Hapus",     new Color(100, 0, 0));
        RoundedButton btnBersih = new RoundedButton("Bersihkan", new Color(0, 100, 50));

        for (RoundedButton b : new RoundedButton[]{btnSimpan,btnUpdate,btnHapus,btnBersih})
            b.setPreferredSize(new Dimension(0,38));

        btnSimpan.addActionListener(e -> simpanProduk());
        btnUpdate.addActionListener(e -> updateProduk());
        btnHapus.addActionListener(e  -> hapusProduk());
        btnBersih.addActionListener(e -> bersihkanForm());

        panelAksi.add(btnSimpan); panelAksi.add(btnUpdate);
        panelAksi.add(btnHapus);  panelAksi.add(btnBersih);

        // ── LOGO ALFAMART ─────────────────────────────────────────────────────
        JPanel panelLogo = new JPanel(new BorderLayout());
        panelLogo.setBackground(Color.WHITE);
        panelLogo.setBorder(BorderFactory.createEmptyBorder(20, 10, 30, 10));
        try {
            java.net.URL imgURL = getClass().getResource("alfamart_logo.jpeg");
            if (imgURL != null) {
                ImageIcon iconAsli = new ImageIcon(imgURL);
                Image gambarSkala  = iconAsli.getImage().getScaledInstance(220, 78, Image.SCALE_SMOOTH);
                JLabel lblLogo     = new JLabel(new ImageIcon(gambarSkala), SwingConstants.CENTER);
                panelLogo.add(lblLogo);
            } else {
                JLabel lblFallback = new JLabel("ALFAMART", SwingConstants.CENTER);
                lblFallback.setFont(new Font("Segoe UI", Font.BOLD, 22));
                lblFallback.setForeground(MERAH);
                panelLogo.add(lblFallback);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // ── Logo di ATAS form field, tombol di SOUTH ──────────────────────────
        JPanel panelTengah = new JPanel(new BorderLayout());
        panelTengah.setBackground(Color.WHITE);
        panelTengah.add(panelLogo,  BorderLayout.NORTH);
        panelTengah.add(formField,  BorderLayout.CENTER);

        panelForm.add(panelTengah, BorderLayout.CENTER);
        panelForm.add(panelAksi,   BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTabel, panelForm);
        split.setResizeWeight(0.7); split.setDividerSize(4); split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    // ── DATA ──────────────────────────────────────────────────────────────────

    private void muatData() {
        modelTabel.setRowCount(0);
        for (Produk p : controller.getAllProduk()) {
            modelTabel.addRow(new Object[]{
                p.getId(), p.getKodeProduk(), p.getNamaProduk(),
                p.getKategori()!=null ? p.getKategori().getNamaKategori() : "-",
                "Rp "+nf.format(p.getHargaBeli()), "Rp "+nf.format(p.getHargaJual()),
                p.getStok(), p.getSatuan()
            });
        }
    }

    private void cariProduk() {
        String kw = txtCari.getText().trim();
        modelTabel.setRowCount(0);
        for (Produk p : (kw.isEmpty() ? controller.getAllProduk() : controller.cariProduk(kw))) {
            modelTabel.addRow(new Object[]{
                p.getId(), p.getKodeProduk(), p.getNamaProduk(),
                p.getKategori()!=null ? p.getKategori().getNamaKategori() : "-",
                "Rp "+nf.format(p.getHargaBeli()), "Rp "+nf.format(p.getHargaJual()),
                p.getStok(), p.getSatuan()
            });
        }
    }

    private void isiForm() {
        int baris = tabelProduk.getSelectedRow();
        if (baris < 0) return;
        txtKode.setText((String) modelTabel.getValueAt(baris,1));
        txtNama.setText((String) modelTabel.getValueAt(baris,2));
        String hb = ((String)modelTabel.getValueAt(baris,4)).replace("Rp ","").replace(".","").replace(",","");
        String hj = ((String)modelTabel.getValueAt(baris,5)).replace("Rp ","").replace(".","").replace(",","");
        txtHargaBeli.setText(hb); txtHargaJual.setText(hj);
        txtStok.setText(String.valueOf(modelTabel.getValueAt(baris,6)));
        txtSatuan.setText((String)modelTabel.getValueAt(baris,7));
    }

    private void simpanProduk() {
        try {
            Produk p = buatProdukDariForm(0);
            if (!p.isValid()) { JOptionPane.showMessageDialog(this,"Lengkapi semua field!","Validasi",JOptionPane.WARNING_MESSAGE); return; }
            if (controller.simpanProduk(p)) { JOptionPane.showMessageDialog(this,"Produk berhasil disimpan!","Sukses",JOptionPane.INFORMATION_MESSAGE); bersihkanForm(); muatData(); }
            else JOptionPane.showMessageDialog(this,"Gagal menyimpan produk!","Error",JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Input tidak valid: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void updateProduk() {
        int baris = tabelProduk.getSelectedRow();
        if (baris<0) { JOptionPane.showMessageDialog(this,"Pilih produk yang akan diupdate!","Info",JOptionPane.INFORMATION_MESSAGE); return; }
        try {
            int id = (int)modelTabel.getValueAt(baris,0);
            Produk p = buatProdukDariForm(id);
            if (controller.updateProduk(p)) { JOptionPane.showMessageDialog(this,"Produk berhasil diupdate!","Sukses",JOptionPane.INFORMATION_MESSAGE); bersihkanForm(); muatData(); }
            else JOptionPane.showMessageDialog(this,"Gagal mengupdate produk!","Error",JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Input tidak valid: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void hapusProduk() {
        int baris = tabelProduk.getSelectedRow();
        if (baris<0) { JOptionPane.showMessageDialog(this,"Pilih produk yang akan dihapus!","Info",JOptionPane.INFORMATION_MESSAGE); return; }
        int id = (int)modelTabel.getValueAt(baris,0);
        String nama = (String)modelTabel.getValueAt(baris,2);
        int opt = JOptionPane.showConfirmDialog(this,"Hapus produk: "+nama+"?","Konfirmasi",JOptionPane.YES_NO_OPTION);
        if (opt==JOptionPane.YES_OPTION) {
            if (controller.hapusProduk(id)) { JOptionPane.showMessageDialog(this,"Produk berhasil dihapus!","Sukses",JOptionPane.INFORMATION_MESSAGE); bersihkanForm(); muatData(); }
            else JOptionPane.showMessageDialog(this,"Gagal menghapus produk!","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bersihkanForm() {
        txtKode.setText(""); txtNama.setText("");
        txtHargaBeli.setText("0"); txtHargaJual.setText("0");
        txtStok.setText("0"); txtSatuan.setText("pcs");
        tabelProduk.clearSelection();
    }

    private Produk buatProdukDariForm(int id) {
        Kategori kat = (Kategori)cbKategori.getSelectedItem();
        return new Produk(id, txtKode.getText().trim(), txtNama.getText().trim(), kat,
                Double.parseDouble(txtHargaBeli.getText().trim()),
                Double.parseDouble(txtHargaJual.getText().trim()),
                Integer.parseInt(txtStok.getText().trim()),
                txtSatuan.getText().trim());
    }
}