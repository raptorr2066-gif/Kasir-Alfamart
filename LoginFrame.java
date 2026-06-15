import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private static final Color MERAH        = new Color(204, 0, 0);
    private static final Color MERAH_GELAP  = new Color(160, 0, 0);
    private static final Color ABU_BORDER   = new Color(200, 200, 200);

    // ── Ganti nilai ini dengan nama file gambar Anda ──────────────────────────
    // Letakkan file gambar di folder yang sama dengan LoginFrame.java / .jar
    // Contoh: "background.jpg", "bg_alfamart.png", dst.
    private static final String BG_IMAGE_PATH = "background.jpg";
    // ──────────────────────────────────────────────────────────────────────────

    // ========================  Custom Components  ============================

    static class RoundedTextField extends JTextField {
        private final int radius;
        private final Color borderColor;
        RoundedTextField(int cols, int radius, Color borderColor) {
            super(cols);
            this.radius = radius;
            this.borderColor = borderColor;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            super.paintComponent(g);
            g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, radius, radius));
            g2.dispose();
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        private final int radius;
        private final Color borderColor;
        RoundedPasswordField(int cols, int radius, Color borderColor) {
            super(cols);
            this.radius = radius;
            this.borderColor = borderColor;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            super.paintComponent(g);
            g2.dispose();
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, radius, radius));
            g2.dispose();
        }
    }

    static class RoundedButton extends JButton {
        private final Color bgColor;
        private final int radius;
        RoundedButton(String text, Color bgColor, int radius) {
            super(text);
            this.bgColor = bgColor;
            this.radius = radius;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setForeground(Color.WHITE);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base = getModel().isPressed()  ? bgColor.darker()
                       : getModel().isRollover() ? bgColor.brighter()
                       : bgColor;
            g2.setColor(base);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            super.paintComponent(g);
            g2.dispose();
        }
    }

    // ========================  Background Panel  =============================

    /**
     * Panel yang menggambar gambar background (di-scale ke ukuran panel)
     * dengan overlay gelap semi-transparan agar form lebih terbaca.
     */
    static class BackgroundPanel extends JPanel {
        private BufferedImage bgImage;

        BackgroundPanel() {
            setLayout(new GridBagLayout()); // konten diletakkan di tengah
            loadImage();
        }

        private void loadImage() {
            try {
                File f = new File(BG_IMAGE_PATH);
                if (f.exists()) {
                    bgImage = ImageIO.read(f);
                } else {
                    // Coba baca dari classpath (jika dikemas dalam JAR)
                    var stream = getClass().getResourceAsStream("/" + BG_IMAGE_PATH);
                    if (stream != null) bgImage = ImageIO.read(stream);
                }
            } catch (Exception e) {
                bgImage = null; // Akan pakai gradient fallback
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            if (bgImage != null) {
                // Scale gambar agar memenuhi seluruh panel
                g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback: gradient merah Alfamart
                GradientPaint gp = new GradientPaint(
                    0, 0, MERAH,
                    getWidth(), getHeight(), MERAH_GELAP
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // Overlay hitam semi-transparan untuk keterbacaan form
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
        }
    }

    // ========================  Card Panel  ===================================

    /**
     * Panel kartu putih semi-transparan dengan sudut bulat dan bayangan.
     */
    static class CardPanel extends JPanel {
        CardPanel() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
            setLayout(new GridBagLayout());
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Bayangan kartu
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fill(new RoundRectangle2D.Double(6, 6, getWidth()-6, getHeight()-6, 24, 24));

            // Latar kartu putih semi-transparan
            g2.setColor(new Color(255, 255, 255, 230));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-6, getHeight()-6, 24, 24));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ========================  Constructor  ==================================

    public LoginFrame() {
        setTitle("Login – Alfamart POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Background panel (seluruh frame) ──
        BackgroundPanel bgPanel = new BackgroundPanel();
        setContentPane(bgPanel);

        // ── Card (form di tengah) ──
        CardPanel card = new CardPanel();
        card.setPreferredSize(new Dimension(420, 400));

        GridBagConstraints gbcCard = new GridBagConstraints();
        bgPanel.add(card, gbcCard);

        // ── Isi kartu ──
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(8, 8, 8, 8);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.gridx   = 0;
        gbc.weightx = 1.0;

        // Logo / judul
        JLabel lblLogo = new JLabel("ALFAMART", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblLogo.setForeground(MERAH);
        gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 8, 4, 8);
        card.add(lblLogo, gbc);

        JLabel lblSub = new JLabel("POS System", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(120, 120, 120));
        gbc.gridy = 1; gbc.insets = new Insets(0, 8, 20, 8);
        card.add(lblSub, gbc);

        // Garis pemisah tipis
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 220, 220));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 16, 0);
        card.add(sep, gbc);

        // Label username
        gbc.gridwidth = 1; gbc.insets = new Insets(4, 8, 2, 8);
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsername.setForeground(new Color(60, 60, 60));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        card.add(lblUsername, gbc);

        // Field username
        txtUsername = new RoundedTextField(20, 12, ABU_BORDER);
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setPreferredSize(new Dimension(300, 42));
        gbc.gridy = 4; gbc.insets = new Insets(0, 8, 10, 8);
        card.add(txtUsername, gbc);

        // Label password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(new Color(60, 60, 60));
        gbc.gridy = 5; gbc.insets = new Insets(4, 8, 2, 8);
        card.add(lblPassword, gbc);

        // Field password
        txtPassword = new RoundedPasswordField(20, 12, ABU_BORDER);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(300, 42));
        gbc.gridy = 6; gbc.insets = new Insets(0, 8, 20, 8);
        card.add(txtPassword, gbc);

        // Tombol Login
        RoundedButton btnLogin = new RoundedButton("LOGIN", MERAH, 12);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(300, 46));
        gbc.gridy = 7; gbc.insets = new Insets(0, 8, 8, 8);
        card.add(btnLogin, gbc);

        // Footer versi
        JLabel lblVersion = new JLabel("v16.4.8 2026 Alfamart", SwingConstants.CENTER);
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(new Color(160, 160, 160));
        gbc.gridy = 8; gbc.insets = new Insets(12, 8, 0, 8);
        card.add(lblVersion, gbc);

        // ── Event ──
        btnLogin.addActionListener(e -> performLogin());
        txtPassword.addActionListener(e -> performLogin());
        getRootPane().setDefaultButton(btnLogin);
    }

    // ========================  Login Logic  ==================================

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan password harus diisi.",
                "Login Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pengguna pengguna = new PenggunaDAO().login(username, password);
        if (pengguna != null) {
            SwingUtilities.invokeLater(() -> new MainFrame(pengguna).setVisible(true));
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Username atau password salah.",
                "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}