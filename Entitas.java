import java.util.Date;
public abstract class Entitas implements Manageable {

    // Enkapsulasi: semua field bersifat private
    private int id;
    private Date tanggalDibuat;
    private Date tanggalDiperbarui;

    /**
     * Constructor default.
     */
    public Entitas() {
        this.tanggalDibuat = new Date();
        this.tanggalDiperbarui = new Date();
    }

    /**
     * Constructor dengan parameter id.
     *
     * @param id ID entitas
     */
    public Entitas(int id) {
        this.id = id;
        this.tanggalDibuat = new Date();
        this.tanggalDiperbarui = new Date();
    }

    // ===================== GETTER & SETTER =====================

    /**
     * @return ID entitas
     */
    public int getId() {
        return id;
    }

    /**
     * @param id ID entitas baru
     */
    public void setId(int id) {
        this.id = id;
        this.tanggalDiperbarui = new Date();
    }

    /**
     * @return tanggal dibuat
     */
    public Date getTanggalDibuat() {
        return tanggalDibuat;
    }

    /**
     * @return tanggal terakhir diperbarui
     */
    public Date getTanggalDiperbarui() {
        return tanggalDiperbarui;
    }

    /**
     * Memperbarui timestamp tanggalDiperbarui ke waktu sekarang.
     */
    public void updateTimestamp() {
        this.tanggalDiperbarui = new Date();
    }

    /**
     * Implementasi method isValid dari Manageable.
     * Entitas dianggap valid jika ID-nya > 0 atau belum disimpan (id = 0).
     *
     * @return true jika valid
     */
    @Override
    public boolean isValid() {
        return id >= 0;
    }

    /**
     * Override toString untuk debugging.
     *
     * @return representasi string dasar
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + "]";
    }
}