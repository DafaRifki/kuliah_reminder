package com.example.kuliahreminder.utils;

public class Constants {
    // SharedPreferences Keys
    public static final String PREF_NAME = "JadwalKuliahPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_EMAIL = "userEmail";

    // Intent Extra Keys
    public static final String EXTRA_SCHEDULE_ID = "schedule_id";
    public static final String EXTRA_IS_EDIT_MODE = "is_edit_mode";

    // Jenis Jadwal
    public static final String JENIS_KULIAH = "Kuliah";
    public static final String JENIS_PRAKTIKUM = "Praktikum";
    public static final String JENIS_TUGAS = "Tugas";

    // Hari dalam Seminggu
    public static final String[] HARI_ARRAY = {
            "Semua",
            "Senin",
            "Selasa",
            "Rabu",
            "Kamis",
            "Jumat",
            "Sabtu",
            "Minggu"
    };

    // Jenis Array untuk Spinner
    public static final String[] JENIS_ARRAY = {
            JENIS_KULIAH,
            JENIS_PRAKTIKUM,
            JENIS_TUGAS
    };

    // Validation Messages
    public static final String MSG_FIELD_REQUIRED = "Field ini wajib diisi";
    public static final String MSG_EMAIL_INVALID = "Format email tidak valid";
    public static final String MSG_PASSWORD_MIN_LENGTH = "Password minimal 6 karakter";
    public static final String MSG_PASSWORD_NOT_MATCH = "Konfirmasi password tidak cocok";
    public static final String MSG_EMAIL_ALREADY_EXISTS = "Email sudah terdaftar";
    public static final String MSG_LOGIN_FAILED = "Email atau password salah";
    public static final String MSG_REGISTER_SUCCESS = "Registrasi berhasil! Silakan login";
    public static final String MSG_LOGIN_SUCCESS = "Login berhasil!";

    // Schedule Messages
    public static final String MSG_SCHEDULE_ADDED = "Jadwal berhasil ditambahkan";
    public static final String MSG_SCHEDULE_UPDATED = "Jadwal berhasil diupdate";
    public static final String MSG_SCHEDULE_DELETED = "Jadwal berhasil dihapus";
    public static final String MSG_SCHEDULE_EMPTY = "Belum ada jadwal";
    public static final String MSG_TIME_INVALID = "Waktu selesai harus lebih dari waktu mulai";

    // Request Codes
    public static final int REQUEST_ADD_SCHEDULE = 100;
    public static final int REQUEST_EDIT_SCHEDULE = 101;

    // Time Format
    public static final String TIME_FORMAT = "HH:mm";

    // Private constructor to prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
}
