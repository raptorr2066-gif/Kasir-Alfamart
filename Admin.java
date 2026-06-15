public class Admin extends Pengguna {

    private String levelAdmin; 

   
    public Admin() {
        super();
        this.levelAdmin = "admin";
    }

    /**
     *
     * @param id          ID pengguna
     * @param username    username
     * @param password    password
     * @param namaLengkap nama lengkap
     * @param aktif       status aktif
     * @param levelAdmin  level admin
     */
    public Admin(int id, String username, String password, String namaLengkap,
                 boolean aktif, String levelAdmin) {
        super(id, username, password, namaLengkap, aktif);
        this.levelAdmin = levelAdmin;
    }

    /**
     * @param username    username
     * @param password    password
     * @param namaLengkap nama lengkap
     */
    public Admin(String username, String password, String namaLengkap) {
        super(username, password, namaLengkap);
        this.levelAdmin = "admin";
    }

    public String getLevelAdmin() { return levelAdmin; }
    public void setLevelAdmin(String levelAdmin) { this.levelAdmin = levelAdmin; }

    @Override
    public String getRole() {
        return "admin";

    }

    @Override
    public String getInfo() {
        return super.getInfo() + " | Level: " + levelAdmin;
    }

    public boolean isSuperAdmin() {
        return "super_admin".equals(levelAdmin);
    }
}