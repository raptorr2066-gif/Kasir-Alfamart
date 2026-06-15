import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Class MainFrame adalah JFrame utama aplikasi setelah login.
 * Menampilkan sidebar navigasi dan panel konten dinamis.
 * Akses menu disesuaikan dengan role pengguna (Admin/Kasir).
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public class MainFrame extends JFrame {

    private Pengguna penggunaAktif;

    // Komponen utama
    private JPanel panelSidebar;
    private JPanel panelKonten;
    private JLabel lblNamaPengguna;
    private JLabel lblRole;
    private JLabel lblJudul;
    private CardLayout cardLayout;

    // Warna tema Alfamart
    public static final Color MERAH_ALFAMART = new Color(204, 0, 0);
    public static final Color MERAH_GELAP    = new Color(160, 0, 0);
    public static final Color PUTIH          = Color.WHITE;
    public static final Color ABU_MUDA       = new Color(245, 245, 245);
    public static final Color ABU_SIDEBAR    = new Color(35, 35, 35);
    public static final Color ABU_HOVER      = new Color(60, 60, 60);

    /**
     * Constructor MainFrame.
     *
     * @param penggunaAktif pengguna yang sedang login
     */
    public MainFrame(Pengguna penggunaAktif) {
        this.penggunaAktif = penggunaAktif;
        initComponents();
        initMenu();
        // Buka halaman kasir langsung
        tampilKonten("kasir");
    }

    /**
     * Inisialisasi komponen utama frame.
     */
    private void initComponents() {
        setTitle("Alfamart POS System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── TOPBAR ────────────────────────────────────────────────
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(MERAH_ALFAMART);
        panelTop.setPreferredSize(new Dimension(0, 55));
        panelTop.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel lblLogo = new JLabel("ALFAMART");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(PUTIH);

        lblJudul = new JLabel("Sistem Point of Sale");
        lblJudul.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblJudul.setForeground(new Color(255, 200, 200));

        JPanel panelTopLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        panelTopLeft.setOpaque(false);
        panelTopLeft.add(lblLogo);
        panelTopLeft.add(new JSeparator(JSeparator.VERTICAL) {{
            setPreferredSize(new Dimension(2, 30));
            setForeground(new Color(255, 150, 150));
        }});
        panelTopLeft.add(lblJudul);

        JPanel panelTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panelTopRight.setOpaque(false);

        lblNamaPengguna = new JLabel(penggunaAktif.getNamaLengkap());
        lblNamaPengguna.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNamaPengguna.setForeground(PUTIH);

        lblRole = new JLabel("[" + penggunaAktif.getRole().toUpperCase() + "]");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(255, 200, 200));

        JButton btnLogout = buatTombolTop("Logout");
        btnLogout.addActionListener(e -> konfirmasiLogout());

        panelTopRight.add(lblRole);
        panelTopRight.add(lblNamaPengguna);
        panelTopRight.add(new JSeparator(JSeparator.VERTICAL) {{
            setPreferredSize(new Dimension(2, 25));
            setForeground(new Color(255, 150, 150));
        }});
        panelTopRight.add(btnLogout);

        panelTop.add(panelTopLeft, BorderLayout.WEST);
        panelTop.add(panelTopRight, BorderLayout.EAST);

        // ── SIDEBAR ───────────────────────────────────────────────
        panelSidebar = new JPanel();
        panelSidebar.setLayout(new BoxLayout(panelSidebar, BoxLayout.Y_AXIS));
        panelSidebar.setBackground(ABU_SIDEBAR);
        panelSidebar.setPreferredSize(new Dimension(200, 0));
        panelSidebar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // ── KONTEN ───────────────────────────────────────────────
        cardLayout = new CardLayout();
        panelKonten = new JPanel(cardLayout);
        panelKonten.setBackground(ABU_MUDA);

        // Register panels
        panelKonten.add(new PanelKasir(penggunaAktif), "kasir");
        panelKonten.add(new PanelProduk(penggunaAktif), "produk");
        panelKonten.add(new PanelLaporan(penggunaAktif), "laporan");
        panelKonten.add(new PanelPengguna(penggunaAktif), "pengguna");

        add(panelTop, BorderLayout.NORTH);
        add(panelSidebar, BorderLayout.WEST);
        add(panelKonten, BorderLayout.CENTER);

        // Window close confirmation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                konfirmasiKeluar();
            }
        });
    }

    /**
     * Membangun menu sidebar sesuai role pengguna.
     */
    private void initMenu() {
        panelSidebar.removeAll();

        // Header sidebar
        JPanel headerSidebar = new JPanel(new BorderLayout());
        headerSidebar.setBackground(MERAH_GELAP);
        headerSidebar.setMaximumSize(new Dimension(200, 60));
        headerSidebar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        JLabel lblMenu = new JLabel("MENU UTAMA");
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMenu.setForeground(new Color(255, 180, 180));
        headerSidebar.add(lblMenu);
        panelSidebar.add(headerSidebar);
        panelSidebar.add(Box.createVerticalStrut(5));

        // Tombol menu untuk semua role
        panelSidebar.add(buatTombolMenu("Kasir / POS", "kasir"));
        panelSidebar.add(Box.createVerticalStrut(2));

        // Tombol menu khusus Admin
        if (penggunaAktif instanceof Admin) {
            panelSidebar.add(buatTombolMenu("Manajemen Produk", "produk"));
            panelSidebar.add(Box.createVerticalStrut(2));
            panelSidebar.add(buatTombolMenu("Laporan Penjualan", "laporan"));
            panelSidebar.add(Box.createVerticalStrut(2));
            panelSidebar.add(buatTombolMenu("Manajemen Pengguna", "pengguna"));
        } else if (penggunaAktif instanceof Kasir) {
            // Kasir juga bisa lihat laporan harian
            panelSidebar.add(buatTombolMenu("Laporan Hari Ini", "laporan"));
        }

        panelSidebar.add(Box.createVerticalGlue());

        // Info versi
        JLabel lblVersi = new JLabel("  v18.7.4 | PBO 2026");
        lblVersi.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersi.setForeground(new Color(120, 120, 120));
        panelSidebar.add(lblVersi);

        panelSidebar.revalidate();
        panelSidebar.repaint();
    }
/**
     * Menampilkan panel konten berdasarkan nama.
     * Sudah dilengkapi dengan auto-refresh UI dan pelacak error.
     *
     * @param nama nama panel di CardLayout
     */
    public void tampilKonten(String nama) {
        try {
            // 1. Pindahkan halaman CardLayout
            cardLayout.show(panelKonten, nama);
            
            // 2. Update teks judul di Topbar
            String[] judulMap = {
                "kasir", "Kasir / Point of Sale",
                "produk", "Manajemen Produk",
                "laporan", "Laporan Penjualan",
                "pengguna", "Manajemen Pengguna"
            };
            for (int i = 0; i < judulMap.length; i += 2) {
                if (judulMap[i].equals(nama)) {
                    lblJudul.setText(judulMap[i + 1]);
                    break;
                }
            }
            
            // 3. PAKSA SWING UNTUK REFRESH DAN GAMBAR ULANG LAYAR (Paling Penting)
            panelKonten.revalidate();
            panelKonten.repaint();
            this.revalidate();
            this.repaint();
            
        } catch (Exception e) {
            // Jika PanelKasir atau PanelProduk kamu ternyata crash diam-diam,
            // baris ini akan menangkapnya dan langsung memunculkan pesan error di layar!
            JOptionPane.showMessageDialog(this, 
                "Gagal memuat halaman: " + nama + "\nError: " + e.toString(), 
                "Error Konten Aplikasi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ===================== HELPER BUILDER =====================

    private JButton buatTombolMenu(String teks, String target) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(220, 220, 220));
        btn.setBackground(ABU_SIDEBAR);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(ABU_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(ABU_SIDEBAR); }
        });
        btn.addActionListener(e -> tampilKonten(target));
        return btn;
    }

    private JButton buatTombolTop(String teks) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(PUTIH);
        btn.setBackground(MERAH_GELAP);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void konfirmasiLogout() {
        int opt = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void konfirmasiKeluar() {
        int opt = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin keluar dari aplikasi?",
                "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            DatabaseConnection.closeConnection();
            System.exit(0);
        }
    }
}