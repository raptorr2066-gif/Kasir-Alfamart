import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;

public class PanelLaporan extends JPanel {

    private Pengguna penggunaAktif;
    private LaporanController controller;
    private NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private JTable tabelTransaksi;
    private DefaultTableModel modelTabel;
    private JLabel lblTotalTransaksi, lblTotalPendapatan;

    private static final Color MERAH      = new Color(140, 0, 0);
    private static final Color ABU_BORDER = new Color(200, 200, 200);

    // ===== ROUNDED PANEL =====
    static class RoundedPanel extends JPanel {
        private final int radius; private final Color borderColor;
        RoundedPanel(LayoutManager layout, int r, Color bc) {
            super(layout); this.radius = r; this.borderColor = bc; setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.setColor(borderColor); g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, radius, radius));
            g2.dispose();
        }
    }

    // ===== ROUNDED KARTU STATISTIK =====
    static class RoundedKartu extends JPanel {
        private final Color warna; private final int radius = 14;
        RoundedKartu(Color warna) { this.warna = warna; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(warna);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }

    // ===== ROUNDED BUTTON =====
    static class RoundedButton extends JButton {
        private Color bgColor; private final int radius;
        RoundedButton(String text, Color bg, int r) {
            super(text); this.bgColor = bg; this.radius = r;
            setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
            setFocusPainted(false); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? bgColor.darker() : getModel().isRollover() ? bgColor.brighter() : bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            super.paintComponent(g); g2.dispose();
        }
    }

    // ===== ROUNDED BORDER (ScrollPane) =====
    static class RoundedBorder extends AbstractBorder {
        private final Color color; private final int radius, thickness;
        RoundedBorder(Color c, int r, int t) { this.color = c; this.radius = r; this.thickness = t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x+1, y+1, w-2, h-2, radius, radius));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(2, 2, 2, 2); }
    }

    public PanelLaporan(Pengguna penggunaAktif) {
        this.penggunaAktif = penggunaAktif;
        this.controller = new LaporanController();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(new Color(235, 235, 235));
        initComponents();
        muatData();
    }

    private void initComponents() {
        // ── KARTU STATISTIK ──
        JPanel panelStat = new JPanel(new GridLayout(1, 2, 15, 0));
        panelStat.setOpaque(false);
        panelStat.setPreferredSize(new Dimension(0, 90));

        // Warna kartu: merah gelap & hijau gelap sesuai tema
        RoundedKartu kartuTransaksi  = buatKartuStat("Total Transaksi Hari Ini", "0",    new Color(100, 0, 0));
        RoundedKartu kartuPendapatan = buatKartuStat("Pendapatan Hari Ini",      "Rp 0", new Color(0, 100, 50));

        lblTotalTransaksi  = (JLabel) ((JPanel) kartuTransaksi.getComponent(0)).getComponent(1);
        lblTotalPendapatan = (JLabel) ((JPanel) kartuPendapatan.getComponent(0)).getComponent(1);

        panelStat.add(kartuTransaksi);
        panelStat.add(kartuPendapatan);

        // ── PANEL TABEL (Rounded) ──
        RoundedPanel panelTabel = new RoundedPanel(new BorderLayout(5, 5), 14, ABU_BORDER);
        panelTabel.setBackground(Color.WHITE);
        panelTabel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblJudul = new JLabel("Riwayat Transaksi");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblJudul.setForeground(new Color(80, 80, 80));
        lblJudul.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] kolom = {"No. Transaksi", "Tanggal", "Kasir", "Total", "Metode Bayar", "Status"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelTransaksi = new JTable(modelTabel);
        tabelTransaksi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabelTransaksi.setRowHeight(28);
        tabelTransaksi.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelTransaksi.getTableHeader().setBackground(MERAH);
        tabelTransaksi.getTableHeader().setForeground(Color.WHITE);
        tabelTransaksi.getTableHeader().setPreferredSize(new Dimension(0, 32));
        tabelTransaksi.setSelectionBackground(new Color(255, 230, 230));
        tabelTransaksi.setGridColor(new Color(240, 240, 240));
        tabelTransaksi.setShowVerticalLines(false);
        tabelTransaksi.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) lihatDetailTransaksi();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabelTransaksi);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new RoundedBorder(ABU_BORDER, 10, 1));

        JPanel panelToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelToolbar.setOpaque(false);
        panelToolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        RoundedButton btnMuat = new RoundedButton("Refresh", new Color(100, 100, 100), 10);
        btnMuat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnMuat.setPreferredSize(new Dimension(110, 32));
        btnMuat.addActionListener(e -> muatData());
        panelToolbar.add(btnMuat);

        JPanel isiTabel = new JPanel(new BorderLayout(0, 4));
        isiTabel.setOpaque(false);
        isiTabel.add(lblJudul,    BorderLayout.NORTH);
        isiTabel.add(panelToolbar, BorderLayout.CENTER);

        panelTabel.add(isiTabel,   BorderLayout.NORTH);
        panelTabel.add(scrollPane, BorderLayout.CENTER);

        add(panelStat,  BorderLayout.NORTH);
        add(panelTabel, BorderLayout.CENTER);
    }

    private void muatData() {
        // ── PERUBAHAN: getTransaksiHariIni() → hanya tampilkan data hari ini ──
        List<Transaksi> list = controller.getTransaksiHariIni();
        // ─────────────────────────────────────────────────────────────────────
        double[] stat = controller.getLaporanHariIni();
        lblTotalTransaksi.setText(String.valueOf((int) stat[0]));
        lblTotalPendapatan.setText("Rp " + nf.format(stat[1]));
        modelTabel.setRowCount(0);
        for (Transaksi t : list)
            modelTabel.addRow(new Object[]{
                t.getNoTransaksi(),
                sdf.format(t.getTanggalTransaksi()),
                t.getKasir() != null ? t.getKasir().getNamaLengkap() : "-",
                "Rp " + nf.format(t.getTotalBayar()),
                t.getMetodeBayar().toUpperCase(),
                t.getStatus().toUpperCase()
            });
    }

    private void lihatDetailTransaksi() {
        JOptionPane.showMessageDialog(this,
            "Double-klik fitur detail transaksi aktif.\nFitur ini menampilkan item per transaksi.",
            "Detail Transaksi", JOptionPane.INFORMATION_MESSAGE);
    }

    private RoundedKartu buatKartuStat(String judul, String nilai, Color warna) {
        RoundedKartu kartu = new RoundedKartu(warna);
        kartu.setLayout(new BorderLayout());
        kartu.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JPanel dalam = new JPanel(new GridLayout(2, 1));
        dalam.setOpaque(false);
        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblJudul.setForeground(new Color(220, 220, 220));
        JLabel lblNilai = new JLabel(nilai);
        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblNilai.setForeground(Color.WHITE);
        dalam.add(lblJudul);
        dalam.add(lblNilai);
        kartu.add(dalam);
        return kartu;
    }
}