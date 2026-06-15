import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;

public class PanelPengguna extends JPanel {

    private final PenggunaDAO penggunaDAO = new PenggunaDAO();
    private final Pengguna penggunaAktif;
    private JTable tabelPengguna;
    private DefaultTableModel modelTabel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNamaLengkap;
    private JComboBox<String> cbRole;
    private JCheckBox chkAktif;
    private JTextField txtCari;
    private int selectedUserId = -1;

    private static final Color MERAH      = new Color(140, 0, 0);
    private static final Color ABU_BORDER = new Color(200, 200, 200);

    static class RoundedBorder extends AbstractBorder {
        private final Color color; private final int radius, thickness;
        RoundedBorder(Color c, int r, int t) { this.color=c; this.radius=r; this.thickness=t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x+1,y+1,w-2,h-2,radius,radius));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness+4,thickness+8,thickness+4,thickness+8); }
        @Override public Insets getBorderInsets(Component c, Insets i) { i.set(thickness+4,thickness+8,thickness+4,thickness+8); return i; }
    }

    static class RoundedTextField extends JTextField {
        private final int radius; private final Color bc;
        RoundedTextField(int r, Color bc) { this.radius=r; this.bc=bc; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(5,10,5,10)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
            super.paintComponent(g); g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bc); g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,radius,radius));
            g2.dispose();
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        private final int radius; private final Color bc;
        RoundedPasswordField(int r, Color bc) { this.radius=r; this.bc=bc; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(5,10,5,10)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
            super.paintComponent(g); g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bc); g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,radius,radius));
            g2.dispose();
        }
    }

    static class RoundedButton extends JButton {
        private Color bgColor; private final int radius;
        RoundedButton(String text, Color bg, int r) {
            super(text); this.bgColor=bg; this.radius=r;
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed()?bgColor.darker():getModel().isRollover()?bgColor.brighter():bgColor);
            g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
            super.paintComponent(g); g2.dispose();
        }
    }

    static class RoundedPanel extends JPanel {
        private final int radius; private final Color borderColor;
        RoundedPanel(LayoutManager layout, int r, Color bc) { super(layout); this.radius=r; this.borderColor=bc; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
            g2.setColor(borderColor); g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,radius,radius));
            g2.dispose();
        }
    }

    static class RoundedComboBox extends JComboBox<String> {
        private final int radius;
        RoundedComboBox(String[] items, int r) {
            super(items); this.radius=r; setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2,8,2,8)); setBackground(Color.WHITE);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fill(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
            g2.setColor(ABU_BORDER); g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1,1,getWidth()-2,getHeight()-2,radius,radius));
            super.paintComponent(g); g2.dispose();
        }
    }

    public PanelPengguna(Pengguna penggunaAktif) {
        this.penggunaAktif = penggunaAktif;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(new Color(235, 235, 235));
        initComponents();
        muatData();
    }

    private void initComponents() {
        // ── HEADER ──
        RoundedPanel panelHeader = new RoundedPanel(new BorderLayout(), 12, ABU_BORDER);
        panelHeader.setBackground(Color.WHITE);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel lblHeader = new JLabel("Manajemen Pengguna", SwingConstants.LEFT);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelHeader.add(lblHeader, BorderLayout.WEST);

        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setOpaque(false);

        // ── PANEL TABEL ──
        RoundedPanel panelTabelUtama = new RoundedPanel(new BorderLayout(8, 8), 14, ABU_BORDER);
        panelTabelUtama.setBackground(Color.WHITE);
        panelTabelUtama.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblDaftar = new JLabel("Daftar Pengguna");
        lblDaftar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDaftar.setForeground(new Color(80, 80, 80));
        lblDaftar.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel panelCari = new JPanel(new BorderLayout(6, 0));
        panelCari.setOpaque(false);
        panelCari.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        txtCari = new RoundedTextField(10, ABU_BORDER);
        txtCari.setBackground(Color.WHITE);
        txtCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        RoundedButton btnCari = new RoundedButton("Cari", MERAH, 10);
        btnCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCari.setPreferredSize(new Dimension(90, 32));
        btnCari.addActionListener(e -> cariPengguna());
        txtCari.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode()==KeyEvent.VK_ENTER) cariPengguna(); }
        });

        RoundedButton btnMuat = new RoundedButton("Muat Ulang", new Color(100,100,100), 10);
        btnMuat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnMuat.setPreferredSize(new Dimension(110, 32));
        btnMuat.addActionListener(e -> { txtCari.setText(""); muatData(); });

        JPanel panelTombolCari = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelTombolCari.setOpaque(false);
        panelTombolCari.add(btnCari); panelTombolCari.add(btnMuat);

        JLabel lblCari = new JLabel("Cari username: ");
        lblCari.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelCari.add(lblCari, BorderLayout.WEST);
        panelCari.add(txtCari, BorderLayout.CENTER);
        panelCari.add(panelTombolCari, BorderLayout.EAST);

        String[] kolom = {"ID", "Username", "Nama", "Role", "Aktif"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelPengguna = new JTable(modelTabel);
        tabelPengguna.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelPengguna.setRowHeight(28);
        tabelPengguna.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelPengguna.getTableHeader().setBackground(MERAH);
        tabelPengguna.getTableHeader().setForeground(Color.WHITE);
        tabelPengguna.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabelPengguna.setSelectionBackground(new Color(255, 230, 230));
        tabelPengguna.setSelectionForeground(Color.BLACK);
        tabelPengguna.setGridColor(new Color(240, 240, 240));
        tabelPengguna.setShowVerticalLines(false);
        tabelPengguna.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabelPengguna.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabelPengguna.getColumnModel().getColumn(2).setPreferredWidth(180);
        tabelPengguna.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabelPengguna.getColumnModel().getColumn(4).setPreferredWidth(50);
        tabelPengguna.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (tabelPengguna.getSelectedRow()>=0) isiForm(); }
        });

        JScrollPane scrollTabel = new JScrollPane(tabelPengguna);
        scrollTabel.setBorder(new RoundedBorder(ABU_BORDER, 10, 1));
        scrollTabel.getViewport().setBackground(Color.WHITE);

        JPanel isiTabel = new JPanel(new BorderLayout(0, 4));
        isiTabel.setOpaque(false);
        isiTabel.add(lblDaftar, BorderLayout.NORTH);
        isiTabel.add(panelCari, BorderLayout.CENTER);

        panelTabelUtama.add(isiTabel, BorderLayout.NORTH);
        panelTabelUtama.add(scrollTabel, BorderLayout.CENTER);

        // ── PANEL FORM ──
        RoundedPanel panelForm = new RoundedPanel(new BorderLayout(), 14, ABU_BORDER);
        panelForm.setBackground(Color.WHITE);
        panelForm.setPreferredSize(new Dimension(280, 0));
        panelForm.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // ── LOGO ALFAMART ──
        JPanel panelLogo = new JPanel(new GridBagLayout());
        panelLogo.setOpaque(false);
        panelLogo.setPreferredSize(new Dimension(0, 85));
        try {
            java.net.URL imgURL = getClass().getResource("alfamart_logo.jpeg");
            if (imgURL != null) {
                ImageIcon iconAsli = new ImageIcon(imgURL);
                Image gambarSkala  = iconAsli.getImage().getScaledInstance(200, 70, Image.SCALE_SMOOTH);
                JLabel lblLogo     = new JLabel(new ImageIcon(gambarSkala), SwingConstants.CENTER);
                panelLogo.add(lblLogo);
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        // ── FIELDS + JUDUL dalam satu panel ──
        JPanel formField = new JPanel(new GridBagLayout());
        formField.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // gridy=0 → judul "Form Pengguna"
        JLabel lblJudulForm = new JLabel("Form Pengguna");
        lblJudulForm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblJudulForm.setForeground(new Color(80, 80, 80));
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; gbc.weightx=1;
        gbc.insets = new Insets(0, 0, 8, 0);
        formField.add(lblJudulForm, gbc);
        gbc.gridwidth=1;

        // gridy=1 dst → fields
        txtUsername    = new RoundedTextField(10, ABU_BORDER); txtUsername.setBackground(Color.WHITE);
        txtPassword    = new RoundedPasswordField(10, ABU_BORDER); txtPassword.setBackground(Color.WHITE);
        txtNamaLengkap = new RoundedTextField(10, ABU_BORDER); txtNamaLengkap.setBackground(Color.WHITE);
        cbRole         = new RoundedComboBox(new String[]{"admin","kasir"}, 10);
        chkAktif       = new JCheckBox("Aktif", true); chkAktif.setOpaque(false);

        String[] labels = {"Username", "Password", "Nama Lengkap", "Role", "Status Aktif"};
        JComponent[] comps = {txtUsername, txtPassword, txtNamaLengkap, cbRole, chkAktif};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx=0; gbc.gridy=i+1; gbc.weightx=0; // ← i+1 agar tidak tabrakan dengan judul
            gbc.insets = new Insets(6, 4, 6, 4);
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            formField.add(lbl, gbc);
            gbc.gridx=1; gbc.weightx=1;
            comps[i].setFont(new Font("Segoe UI", Font.PLAIN, 12));
            formField.add(comps[i], gbc);
        }

        // ── TOMBOL AKSI ──
        JPanel panelAksi = new JPanel(new GridLayout(2, 2, 8, 8));
        panelAksi.setOpaque(false);
        panelAksi.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        RoundedButton btnSimpan = new RoundedButton("Simpan",    new Color(0,100,50),  10);
        RoundedButton btnUpdate = new RoundedButton("Update",    new Color(100,0,0),   10);
        RoundedButton btnHapus  = new RoundedButton("Hapus",     new Color(100,0,0),   10);
        RoundedButton btnBersih = new RoundedButton("Bersihkan", new Color(0,100,50),  10);

        for (RoundedButton b : new RoundedButton[]{btnSimpan, btnUpdate, btnHapus, btnBersih}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setPreferredSize(new Dimension(0, 36));
        }

        btnSimpan.addActionListener(e -> simpanPengguna());
        btnUpdate.addActionListener(e -> updatePengguna());
        btnHapus.addActionListener(e  -> hapusPengguna());
        btnBersih.addActionListener(e -> bersihkanForm());

        panelAksi.add(btnSimpan); panelAksi.add(btnUpdate);
        panelAksi.add(btnHapus);  panelAksi.add(btnBersih);

        panelForm.add(panelLogo,  BorderLayout.NORTH);
        panelForm.add(formField,  BorderLayout.CENTER);
        panelForm.add(panelAksi,  BorderLayout.SOUTH);

        panelUtama.add(panelTabelUtama, BorderLayout.CENTER);
        panelUtama.add(panelForm, BorderLayout.EAST);

        add(panelHeader, BorderLayout.NORTH);
        add(panelUtama, BorderLayout.CENTER);
    }

    private void muatData() {
        modelTabel.setRowCount(0);
        List<Pengguna> list = penggunaDAO.getAll();
        for (Pengguna p : list) {
            String u = p.getUsername();
            if (u.equals("admin") || u.equals("kasir1") || u.equals("kasir2")) continue;
            modelTabel.addRow(new Object[]{
                p.getId(), p.getUsername(), p.getNamaLengkap(), p.getRole(), p.isAktif() ? "Ya" : "Tidak"
            });
        }
    }

    private void cariPengguna() {
        String keyword = txtCari.getText().trim();
        modelTabel.setRowCount(0);
        for (Pengguna p : penggunaDAO.getAll()) {
            String u = p.getUsername();
            if (u.equals("admin") || u.equals("kasir1") || u.equals("kasir2")) continue;
            if (keyword.isEmpty()
                    || p.getUsername().toLowerCase().contains(keyword.toLowerCase())
                    || p.getNamaLengkap().toLowerCase().contains(keyword.toLowerCase())) {
                modelTabel.addRow(new Object[]{
                    p.getId(), p.getUsername(), p.getNamaLengkap(), p.getRole(), p.isAktif() ? "Ya" : "Tidak"
                });
            }
        }
    }

    private void isiForm() {
        int row = tabelPengguna.getSelectedRow();
        if (row < 0) return;
        selectedUserId = (int) modelTabel.getValueAt(row, 0);
        Pengguna p = penggunaDAO.getById(selectedUserId);
        if (p == null) return;
        txtUsername.setText(p.getUsername());
        txtPassword.setText(p.getPassword());
        txtNamaLengkap.setText(p.getNamaLengkap());
        cbRole.setSelectedItem(p.getRole());
        chkAktif.setSelected(p.isAktif());
    }

    private void simpanPengguna() {
        try {
            Pengguna p = buatPenggunaDariForm(0);
            if (!p.isValid()) { JOptionPane.showMessageDialog(this,"Lengkapi data pengguna dengan benar.","Validasi",JOptionPane.WARNING_MESSAGE); return; }
            if (penggunaDAO.simpan(p)) { JOptionPane.showMessageDialog(this,"Pengguna berhasil disimpan.","Sukses",JOptionPane.INFORMATION_MESSAGE); bersihkanForm(); muatData(); }
            else JOptionPane.showMessageDialog(this,"Gagal menyimpan pengguna.","Error",JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Input tidak valid: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void updatePengguna() {
        if (selectedUserId < 0) { JOptionPane.showMessageDialog(this,"Pilih pengguna terlebih dahulu.","Info",JOptionPane.INFORMATION_MESSAGE); return; }
        try {
            Pengguna p = buatPenggunaDariForm(selectedUserId);
            if (penggunaDAO.update(p)) { JOptionPane.showMessageDialog(this,"Pengguna berhasil diupdate.","Sukses",JOptionPane.INFORMATION_MESSAGE); bersihkanForm(); muatData(); }
            else JOptionPane.showMessageDialog(this,"Gagal mengupdate pengguna.","Error",JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Input tidak valid: "+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
    }

    private void hapusPengguna() {
        int row = tabelPengguna.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Pilih pengguna terlebih dahulu.","Info",JOptionPane.INFORMATION_MESSAGE); return; }
        int id = (int) modelTabel.getValueAt(row, 0);
        String nama = (String) modelTabel.getValueAt(row, 2);
        int opt = JOptionPane.showConfirmDialog(this,"Hapus pengguna: "+nama+"?","Konfirmasi",JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (penggunaDAO.hapus(id)) { JOptionPane.showMessageDialog(this,"Pengguna berhasil dihapus.","Sukses",JOptionPane.INFORMATION_MESSAGE); bersihkanForm(); muatData(); }
            else JOptionPane.showMessageDialog(this,"Gagal menghapus pengguna.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private Pengguna buatPenggunaDariForm(int id) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String nama     = txtNamaLengkap.getText().trim();
        String role     = (String) cbRole.getSelectedItem();
        boolean aktif   = chkAktif.isSelected();
        if ("admin".equals(role)) return new Admin(id, username, password, nama, aktif, "admin");
        else return new Kasir(id, username, password, nama, aktif, "pagi");
    }

    private void bersihkanForm() {
        selectedUserId = -1;
        txtUsername.setText(""); txtPassword.setText(""); txtNamaLengkap.setText("");
        cbRole.setSelectedIndex(1); chkAktif.setSelected(true);
        tabelPengguna.clearSelection();
    }
}