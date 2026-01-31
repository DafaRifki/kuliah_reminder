package com.example.kuliahreminder.model;

public class Schedule {
    private int id;
    private int userId;
    private String namaMatkul;
    private String jenis;          // Kuliah, Praktikum, Tugas
    private String hari;           // Senin, Selasa, Rabu, dst
    private String waktuMulai;     // Format: HH:mm
    private String waktuSelesai;   // Format: HH:mm
    private String ruangan;
    private String keterangan;
    private String createdAt;

    public Schedule() {
    }

    public Schedule(int userId, String namaMatkul, String jenis, String hari, String waktuMulai, String waktuSelesai, String ruangan, String keterangan) {
        this.userId = userId;
        this.namaMatkul = namaMatkul;
        this.jenis = jenis;
        this.hari = hari;
        this.waktuMulai = waktuMulai;
        this.waktuSelesai = waktuSelesai;
        this.ruangan = ruangan;
        this.keterangan = keterangan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNamaMatkul() {
        return namaMatkul;
    }

    public void setNamaMatkul(String namaMatkul) {
        this.namaMatkul = namaMatkul;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(String waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public String getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(String waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedTime() {
        return waktuMulai + " - " + waktuSelesai;
    }

    public int getColorByType() {
        switch (jenis) {
            case "Kuliah":
                return android.graphics.Color.parseColor("#3F51B5"); // Blue
            case "Praktikum":
                return android.graphics.Color.parseColor("#4CAF50"); // Green
            case "Tugas":
                return android.graphics.Color.parseColor("#FF9800"); // Orange
            default:
                return android.graphics.Color.parseColor("#757575"); // Gray
        }
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", namaMatkul='" + namaMatkul + '\'' +
                ", jenis='" + jenis + '\'' +
                ", hari='" + hari + '\'' +
                ", waktuMulai='" + waktuMulai + '\'' +
                ", waktuSelesai='" + waktuSelesai + '\'' +
                ", ruangan='" + ruangan + '\'' +
                '}';
    }
}
