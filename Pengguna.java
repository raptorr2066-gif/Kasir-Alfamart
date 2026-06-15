/**
 * Class Pengguna sebagai superclass untuk semua tipe pengguna.
 * Extends Entitas dan menerapkan enkapsulasi penuh.
 *
 * @author Kelompok Alfamart Kasir
 * @version 1.0
 */
public class Pengguna extends Entitas {

    private String username;
    private String password;
    private String namaLengkap;
    private boolean aktif;

    /**
     * Constructor default.
     */
    public Pengguna() {
        super();
        this.aktif = true;
    }

    /**
     * Constructor dengan parameter.
     *
     * @param id          ID pengguna
     * @param username    username login
     * @param password    password (plain text; di produksi harus di-hash)
     * @param namaLengkap nama lengkap
     * @param aktif       status aktif
     */
    public Pengguna(int id, String username, String password, String namaLengkap, boolean aktif) {
        super(id);
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.aktif = aktif;
    }

    /**
     * Constructor overloading: untuk pengguna baru (belum punya ID).
     *
     * @param username    username
     * @param password    password
     * @param namaLengkap nama lengkap
     */
    public Pengguna(String username, String password, String namaLengkap) {
        super();
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.aktif = true;
    }

    // ===================== GETTER & SETTER =====================

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    /**
     * Mendapatkan role pengguna. Akan di-override oleh subclass.
     *
     * @return String role pengguna
     */
    public String getRole() {
        return "pengguna";
    }

    /**
     * Mengecek apakah password cocok (overriding di subclass bisa menambah logika).
     *
     * @param inputPassword password yang dimasukkan
     * @return true jika cocok
     */
    public boolean cekPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }

    // ===================== IMPLEMENTASI INTERFACE =====================

    @Override
    public boolean isValid() {
        return super.isValid()
                && username != null && !username.trim().isEmpty()
                && password != null && !password.trim().isEmpty()
                && namaLengkap != null && !namaLengkap.trim().isEmpty();
    }

    @Override
    public String getInfo() {
        return "Pengguna: " + namaLengkap + " (@" + username + ") | Role: " + getRole();
    }

    @Override
    public String toString() {
        return namaLengkap + " (" + username + ")";
    }
}