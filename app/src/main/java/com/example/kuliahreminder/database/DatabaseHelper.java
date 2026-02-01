package com.example.kuliahreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.kuliahreminder.model.Schedule;
import com.example.kuliahreminder.model.User;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "jadwal_kuliah.db";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SCHEDULES = "schedules";

    // Common Column Names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // USERS Table - Column Names
    private static final String KEY_USER_NAMA = "nama_lengkap";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PASSWORD = "password";

    // SCHEDULES Table - Column Names
    private static final String KEY_SCHEDULE_USER_ID = "user_id";
    private static final String KEY_SCHEDULE_MATKUL = "nama_matkul";
    private static final String KEY_SCHEDULE_JENIS = "jenis";
    private static final String KEY_SCHEDULE_HARI = "hari";
    private static final String KEY_SCHEDULE_WAKTU_MULAI = "waktu_mulai";
    private static final String KEY_SCHEDULE_WAKTU_SELESAI = "waktu_selesai";
    private static final String KEY_SCHEDULE_RUANGAN = "ruangan";
    private static final String KEY_SCHEDULE_KETERANGAN = "keterangan";
    private static final String KEY_SCHEDULE_NOTIFICATION_ENABLED = "notification_enabled";

    // Table Create Statements
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_USER_NAMA + " TEXT NOT NULL, " +
                    KEY_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    KEY_USER_PASSWORD + " TEXT NOT NULL, " +
                    KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    private static final String CREATE_TABLE_SCHEDULES =
            "CREATE TABLE " + TABLE_SCHEDULES + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_SCHEDULE_USER_ID + " INTEGER NOT NULL, " +
                    KEY_SCHEDULE_MATKUL + " TEXT NOT NULL, " +
                    KEY_SCHEDULE_JENIS + " TEXT NOT NULL, " +
                    KEY_SCHEDULE_HARI + " TEXT NOT NULL, " +
                    KEY_SCHEDULE_WAKTU_MULAI + " TEXT NOT NULL, " +
                    KEY_SCHEDULE_WAKTU_SELESAI + " TEXT NOT NULL, " +
                    KEY_SCHEDULE_RUANGAN + " TEXT, " +
                    KEY_SCHEDULE_KETERANGAN + " TEXT, " +
                    KEY_SCHEDULE_NOTIFICATION_ENABLED + " INTEGER DEFAULT 1, " +
                    KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + KEY_SCHEDULE_USER_ID + ") REFERENCES " +
                    TABLE_USERS + "(" + KEY_ID + ") ON DELETE CASCADE" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method helper untuk get current datetime
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                new Locale("id", "ID"));
        return sdf.format(new Date());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SCHEDULES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAMA, user.getNamaLengkap());
        values.put(KEY_USER_EMAIL, user.getEmail());
        values.put(KEY_USER_PASSWORD, user.getPassword());
        values.put(KEY_CREATED_AT, getCurrentDateTime());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();

        return id;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + KEY_USER_EMAIL + " = ? AND " +
                KEY_USER_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email, password});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            user.setNamaLengkap(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_NAMA)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PASSWORD)));
        }

        cursor.close();
        db.close();

        return user;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + KEY_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
            user.setNamaLengkap(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_NAMA)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_PASSWORD)));
        }

        cursor.close();
        db.close();

        return user;
    }

    public long addSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCHEDULE_USER_ID, schedule.getUserId());
        values.put(KEY_SCHEDULE_MATKUL, schedule.getNamaMatkul());
        values.put(KEY_SCHEDULE_JENIS, schedule.getJenis());
        values.put(KEY_SCHEDULE_HARI, schedule.getHari());
        values.put(KEY_SCHEDULE_WAKTU_MULAI, schedule.getWaktuMulai());
        values.put(KEY_SCHEDULE_WAKTU_SELESAI, schedule.getWaktuSelesai());
        values.put(KEY_SCHEDULE_RUANGAN, schedule.getRuangan());
        values.put(KEY_SCHEDULE_KETERANGAN, schedule.getKeterangan());
        values.put(KEY_SCHEDULE_NOTIFICATION_ENABLED, schedule.isNotificationEnabled() ? 1 : 0);

        long id = db.insert(TABLE_SCHEDULES, null, values);
        db.close();

        return id;
    }

    public List<Schedule> getAllSchedules(int userId) {
        List<Schedule> schedules = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SCHEDULES +
                " WHERE " + KEY_SCHEDULE_USER_ID + " = ?" +
                " ORDER BY " + KEY_SCHEDULE_HARI + ", " + KEY_SCHEDULE_WAKTU_MULAI;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                schedule.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_USER_ID)));
                schedule.setNamaMatkul(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_MATKUL)));
                schedule.setJenis(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_JENIS)));
                schedule.setHari(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_HARI)));
                schedule.setWaktuMulai(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_WAKTU_MULAI)));
                schedule.setWaktuSelesai(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_WAKTU_SELESAI)));
                schedule.setRuangan(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_RUANGAN)));
                schedule.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_KETERANGAN)));
                schedule.setNotificationEnabled(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_NOTIFICATION_ENABLED)) == 1);

                schedules.add(schedule);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return schedules;
    }

    public List<Schedule> getSchedulesByDay(int userId, String hari) {
        List<Schedule> schedules = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SCHEDULES +
                " WHERE " + KEY_SCHEDULE_USER_ID + " = ? AND " +
                KEY_SCHEDULE_HARI + " = ?" +
                " ORDER BY " + KEY_SCHEDULE_WAKTU_MULAI;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), hari});

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                schedule.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_USER_ID)));
                schedule.setNamaMatkul(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_MATKUL)));
                schedule.setJenis(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_JENIS)));
                schedule.setHari(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_HARI)));
                schedule.setWaktuMulai(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_WAKTU_MULAI)));
                schedule.setWaktuSelesai(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_WAKTU_SELESAI)));
                schedule.setRuangan(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_RUANGAN)));
                schedule.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_KETERANGAN)));

                schedules.add(schedule);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return schedules;
    }

    public List<Schedule> getSchedulesByType(int userId, String jenis) {
        List<Schedule> schedules = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SCHEDULES +
                " WHERE " + KEY_SCHEDULE_USER_ID + " = ? AND " +
                KEY_SCHEDULE_JENIS + " = ?" +
                " ORDER BY " + KEY_SCHEDULE_HARI + ", " + KEY_SCHEDULE_WAKTU_MULAI;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), jenis});

        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
                schedule.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_USER_ID)));
                schedule.setNamaMatkul(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_MATKUL)));
                schedule.setJenis(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_JENIS)));
                schedule.setHari(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_HARI)));
                schedule.setWaktuMulai(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_WAKTU_MULAI)));
                schedule.setWaktuSelesai(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_WAKTU_SELESAI)));
                schedule.setRuangan(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_RUANGAN)));
                schedule.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SCHEDULE_KETERANGAN)));

                schedules.add(schedule);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return schedules;
    }

    public int updateSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCHEDULE_MATKUL, schedule.getNamaMatkul());
        values.put(KEY_SCHEDULE_JENIS, schedule.getJenis());
        values.put(KEY_SCHEDULE_HARI, schedule.getHari());
        values.put(KEY_SCHEDULE_WAKTU_MULAI, schedule.getWaktuMulai());
        values.put(KEY_SCHEDULE_WAKTU_SELESAI, schedule.getWaktuSelesai());
        values.put(KEY_SCHEDULE_RUANGAN, schedule.getRuangan());
        values.put(KEY_SCHEDULE_KETERANGAN, schedule.getKeterangan());
        values.put(KEY_SCHEDULE_NOTIFICATION_ENABLED, schedule.isNotificationEnabled() ? 1 : 0 );
        values.put(KEY_CREATED_AT, getCurrentDateTime());

        int result = db.update(TABLE_SCHEDULES, values,
                KEY_ID + " = ?",
                new String[]{String.valueOf(schedule.getId())});
        db.close();

        return result;
    }

    public void deleteSchedule(int scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, KEY_ID + " = ?",
                new String[]{String.valueOf(scheduleId)});
        db.close();
    }

    public void deleteAllSchedules(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULES, KEY_SCHEDULE_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        db.close();
    }

    public int getScheduleCount(int userId) {
        String query = "SELECT COUNT(*) FROM " + TABLE_SCHEDULES +
                " WHERE " + KEY_SCHEDULE_USER_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }
}
