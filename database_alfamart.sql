-- Database: alfamart_kasir


CREATE DATABASE IF NOT EXISTS alfamart_kasir;
USE alfamart_kasir;

-- Tabel Kategori Produk
CREATE TABLE IF NOT EXISTS kategori (
    id_kategori INT AUTO_INCREMENT PRIMARY KEY,
    nama_kategori VARCHAR(100) NOT NULL,
    deskripsi TEXT
);

-- Tabel Produk
CREATE TABLE IF NOT EXISTS produk (
    id_produk INT AUTO_INCREMENT PRIMARY KEY,
    kode_produk VARCHAR(20) UNIQUE NOT NULL,
    nama_produk VARCHAR(200) NOT NULL,
    id_kategori INT,
    harga_beli DECIMAL(15,2) NOT NULL DEFAULT 0,
    harga_jual DECIMAL(15,2) NOT NULL DEFAULT 0,
    stok INT NOT NULL DEFAULT 0,
    satuan VARCHAR(20) DEFAULT 'pcs',
    gambar VARCHAR(255),
    FOREIGN KEY (id_kategori) REFERENCES kategori(id_kategori)
);

-- Tabel Pengguna / Kasir
CREATE TABLE IF NOT EXISTS pengguna (
    id_pengguna INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'kasir',
    aktif TINYINT(1) DEFAULT 1
);

-- Tabel Transaksi (Header)
CREATE TABLE IF NOT EXISTS transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    no_transaksi VARCHAR(30) UNIQUE NOT NULL,
    id_pengguna INT NOT NULL,
    id_member INT DEFAULT NULL,
    tanggal_transaksi DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_harga DECIMAL(15,2) NOT NULL DEFAULT 0,
    diskon DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_bayar DECIMAL(15,2) NOT NULL DEFAULT 0,
    uang_bayar DECIMAL(15,2) NOT NULL DEFAULT 0,
    kembalian DECIMAL(15,2) NOT NULL DEFAULT 0,
    metode_bayar VARCHAR(20) DEFAULT 'tunai',
    status VARCHAR(20) DEFAULT 'selesai',
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna)
);

-- Tabel Detail Transaksi
CREATE TABLE IF NOT EXISTS detail_transaksi (
    id_detail INT AUTO_INCREMENT PRIMARY KEY,
    id_transaksi INT NOT NULL,
    id_produk INT NOT NULL,
    qty INT NOT NULL DEFAULT 1,
    harga_satuan DECIMAL(15,2) NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi),
    FOREIGN KEY (id_produk) REFERENCES produk(id_produk)
);

-- Tabel Member
CREATE TABLE IF NOT EXISTS member (
    id_member INT AUTO_INCREMENT PRIMARY KEY,
    no_member VARCHAR(20) UNIQUE NOT NULL,
    nama_member VARCHAR(100) NOT NULL,
    no_hp VARCHAR(20),
    email VARCHAR(100),
    tanggal_lahir DATE,
    jenis_kelamin VARCHAR(10),
    alamat TEXT,
    tier VARCHAR(20) DEFAULT 'Silver',
    poin INT DEFAULT 0,
    diskon_persen DECIMAL(5,2) DEFAULT 0,
    total_belanja DECIMAL(15,2) DEFAULT 0,
    tanggal_expired DATE,
    status_member VARCHAR(20) DEFAULT 'AKTIF'
);

-- Tabel Riwayat Poin
CREATE TABLE IF NOT EXISTS riwayat_poin (
    id_riwayat INT AUTO_INCREMENT PRIMARY KEY,
    id_member INT NOT NULL,
    id_transaksi INT,
    jenis VARCHAR(10) NOT NULL,
    jumlah_poin INT NOT NULL,
    keterangan VARCHAR(255),
    tanggal DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_member) REFERENCES member(id_member)
);

-- Tabel Log Diskon Member
CREATE TABLE IF NOT EXISTS log_diskon_member (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    id_transaksi INT NOT NULL,
    id_member INT NOT NULL,
    diskon_persen DECIMAL(5,2),
    nominal_diskon DECIMAL(15,2),
    FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi),
    FOREIGN KEY (id_member) REFERENCES member(id_member)
);

-- View info member (pengganti v_info_member di PostgreSQL)
CREATE OR REPLACE VIEW v_info_member AS
SELECT
    id_member,
    no_member,
    nama_member,
    no_hp,
    email,
    tier,
    poin,
    diskon_persen,
    total_belanja,
    DATE_FORMAT(tanggal_expired, '%Y-%m-%d') AS tanggal_expired,
    CASE
        WHEN status_member = 'NONAKTIF'      THEN 'NONAKTIF'
        WHEN tanggal_expired < CURDATE()     THEN 'EXPIRED'
        ELSE 'AKTIF'
    END AS status_member
FROM member;

-- Semua data kasir alfamart

-- Kategori
INSERT INTO kategori (nama_kategori, deskripsi) VALUES
('Minuman',              'Minuman botol, kaleng, sachet'),
('Makanan Ringan',       'Snack, keripik, biskuit'),
('Produk Segar',         'Roti, kue, produk roti segar'),
('Produk Rumah Tangga',  'Sabun, detergen, peralatan rumah'),
('Rokok',                'Rokok berbagai merek'),
('Susu & Produk Dairy',  'Susu, yogurt, keju'),
('Personal Care',        'Shampo, pasta gigi, deodorant');

-- User default
INSERT INTO pengguna (username, password, nama_lengkap, role) VALUES
('Fadhli', 'Fadhli12345', 'Muhammad Fadhli Wijaya',       'admin'),
('Alfian', 'Alfian12345', 'Muhammad Alfian Nur Ramadhan', 'kasir');

-- Produk
INSERT INTO produk (kode_produk, nama_produk, id_kategori, harga_beli, harga_jual, stok, satuan) VALUES
('101001','Aqua 600ml',                   1, 2500,  3500,  71,  'botol'),
('101002','Teh Botol Sosro 450ml',        1, 4000,  5500,  80,  'botol'),
('101003','Coca Cola 390ml',              1, 5000,  7000,  34,  'kaleng'),
('101004','Pocari Sweat 500ml',           1, 7000,  9500,  50,  'botol'),
('101005','Good Day Cappuccino',          1, 2500,  3500,  120, 'sachet'),
('201001','Chitato Rasa Sapi 68gr',       2, 8000,  11000, 75,  'pcs'),
('201002','Pringles Original 107gr',      2, 25000, 32000, 40,  'kaleng'),
('201003','Oreo Original 137gr',          2, 10000, 14500, 60,  'pcs'),
('201004','Indomie Goreng',               2, 2800,  3500,  200, 'pcs'),
('201005','Pop Mie Ayam 75gr',            2, 4000,  5500,  90,  'pcs'),
('301001','Sari Roti Tawar',              3, 7000,  12000, 31,  'pcs'),
('301002','Roti Boy Original',            3, 7000,  10000, 25,  'pcs'),
('401001','Sabun Lifebuoy 85gr',          4, 4000,  6000,  80,  'pcs'),
('401002','Rinso Deterjen 800gr',         4, 18000, 24000, 39,  'pcs'),
('401003','Sunlight 500ml',               4, 10000, 14000, 55,  'botol'),
('501001','Gudang Garam Surya 12',        5, 22000, 27000, 92,  'bungkus'),
('501002','Djarum Super 12',              5, 21000, 26000, 100, 'bungkus'),
('601001','Indomilk UHT Full Cream 250ml',6, 4500,  6500,  70,  'pcs'),
('601002','Ultra Milk Coklat 250ml',      6, 4500,  6500,  70,  'pcs'),
('701001','Pantene Shampoo 170ml',        7, 20000, 27000, 45,  'botol'),
('701002','Pepsodent 190gr',              7, 12000, 17000, 60,  'pcs');