package com.example.kuliahreminder.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuliahreminder.R;
import com.example.kuliahreminder.adapter.ScheduleAdapter;
import com.example.kuliahreminder.database.DatabaseHelper;
import com.example.kuliahreminder.model.Schedule;
import com.example.kuliahreminder.utils.AlarmHelper;
import com.example.kuliahreminder.utils.Constants;
import com.example.kuliahreminder.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabAdd;
    private Spinner spinnerFilterHari, spinnerFilterJenis;
    private MaterialButton btnTestNotification;  // ← TAMBAHAN

    private DatabaseHelper db;
    private SessionManager sessionManager;
    private ScheduleAdapter adapter;
    private List<Schedule> scheduleList;

    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private static final int ALARM_PERMISSION_CODE = 101;

    private String selectedHari = "Semua";
    private String selectedJenis = "Semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize
        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        // Initialize views
        initViews();

        // Setup toolbar
        setupToolbar();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup filters
        setupFilters();

        // Load data
        loadSchedules();

        // Setup FAB
        setupFAB();

        // Setup Test Button  ← TAMBAHAN
        setupTestButton();

        // Request permissions
        requestAllPermissions();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        tvEmpty = findViewById(R.id.tv_empty);
        layoutEmpty = findViewById(R.id.layout_empty);  // ← TAMBAHAN
        fabAdd = findViewById(R.id.fab_add);
        spinnerFilterHari = findViewById(R.id.spinner_filter_hari);
        spinnerFilterJenis = findViewById(R.id.spinner_filter_jenis);
        btnTestNotification = findViewById(R.id.btn_test_notification);  // ← TAMBAHAN
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Jadwal Kuliah");
            getSupportActionBar().setSubtitle("Selamat datang, " + sessionManager.getUserName());
        }
    }

    private void setupRecyclerView() {
        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(this, scheduleList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupFilters() {
        // Filter Hari
        ArrayAdapter<String> hariAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Constants.HARI_ARRAY
        );
        hariAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterHari.setAdapter(hariAdapter);

        spinnerFilterHari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHari = Constants.HARI_ARRAY[position];
                loadSchedules();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Filter Jenis
        String[] jenisFilterArray = {"Semua", "Kuliah", "Praktikum", "Tugas"};
        ArrayAdapter<String> jenisAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                jenisFilterArray
        );
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterJenis.setAdapter(jenisAdapter);

        spinnerFilterJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedJenis = jenisFilterArray[position];
                loadSchedules();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupFAB() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditScheduleActivity.class);
                intent.putExtra(Constants.EXTRA_IS_EDIT_MODE, false);
                startActivityForResult(intent, Constants.REQUEST_ADD_SCHEDULE);
            }
        });
    }

    // ========== TAMBAHAN: TEST NOTIFICATION ==========

    /**
     * Setup test button untuk testing notifikasi
     */
    private void setupTestButton() {
        btnTestNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTestMenu();
            }
        });
    }

    /**
     * Show test menu dengan berbagai pilihan
     */
    private void showTestMenu() {
        new AlertDialog.Builder(this)
                .setTitle("🔔 Test Notifikasi")
                .setMessage("Pilih jenis test:")
                .setPositiveButton("Instant (Langsung)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testNotificationInstant();
                    }
                })
                .setNeutralButton("5 Detik", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testNotificationDelayed(5);
                    }
                })
                .setNegativeButton("30 Detik", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testNotificationDelayed(30);
                    }
                })
                .show();
    }

    /**
     * Test notifikasi instant (langsung muncul)
     */
    private void testNotificationInstant() {
        Intent intent = new Intent(this, com.example.kuliahreminder.receiver.AlarmReceiver.class);
        intent.putExtra("schedule_id", 99999);
        intent.putExtra("nama_matkul", "🔔 TEST INSTANT");
        intent.putExtra("waktu", "Sekarang");
        intent.putExtra("ruangan", "Test Room");
        intent.putExtra("jenis", "Test");

        sendBroadcast(intent);

        Toast.makeText(this, "✅ Notifikasi test muncul sekarang!\nCheck notification panel",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Test notifikasi dengan delay (5 atau 30 detik)
     */
    private void testNotificationDelayed(int seconds) {
        // Create test schedule
        Schedule testSchedule = new Schedule();
        testSchedule.setId(99999);
        testSchedule.setNamaMatkul("🔔 TEST " + seconds + " DETIK");
        testSchedule.setJenis("Test");
        testSchedule.setHari("Test");
        testSchedule.setWaktuMulai("Test");
        testSchedule.setRuangan("Test Room");
        testSchedule.setNotificationEnabled(true);

        // Set alarm
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, com.example.kuliahreminder.receiver.AlarmReceiver.class);

        intent.putExtra("schedule_id", testSchedule.getId());
        intent.putExtra("nama_matkul", testSchedule.getNamaMatkul());
        intent.putExtra("waktu", "Dalam " + seconds + " detik");
        intent.putExtra("ruangan", testSchedule.getRuangan());
        intent.putExtra("jenis", testSchedule.getJenis());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                testSchedule.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = System.currentTimeMillis() + (seconds * 1000L);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                );
            }

            Toast.makeText(this,
                    "⏰ Alarm di-set!\nNotifikasi akan muncul dalam " + seconds + " detik\n" +
                            "Jangan tutup aplikasi!",
                    Toast.LENGTH_LONG).show();

        } catch (SecurityException e) {
            Toast.makeText(this, "❌ Gagal set alarm: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    // ========== END TEST NOTIFICATION ==========

    /**
     * Request all required permissions
     */
    private void requestAllPermissions() {
        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            } else {
                // Permission already granted, set alarms
                setAllScheduleAlarms();
            }
        } else {
            // For Android 12 and below, no runtime permission needed
            setAllScheduleAlarms();
        }

        // Request exact alarm permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (!alarmManager.canScheduleExactAlarms()) {
                // Show dialog to user
                new AlertDialog.Builder(this)
                        .setTitle("Izin Alarm Diperlukan")
                        .setMessage("Aplikasi memerlukan izin untuk mengatur alarm tepat waktu agar notifikasi pengingat dapat berfungsi.")
                        .setPositiveButton("Buka Pengaturan", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(
                                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                );
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Nanti", null)
                        .show();
            }
        }
    }

    private void setAllScheduleAlarms() {
        int userId = sessionManager.getUserId();
        List<Schedule> allSchedules = db.getAllSchedules(userId);
        AlarmHelper.setAllAlarms(this, allSchedules);

        // Optional: Show toast
        if (allSchedules.size() > 0) {
            Toast.makeText(this, "Pengingat diaktifkan untuk " +
                    allSchedules.size() + " jadwal", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "✅ Izin notifikasi diberikan", Toast.LENGTH_SHORT).show();
                setAllScheduleAlarms();
            } else {
                Toast.makeText(this,
                        "⚠️ Izin notifikasi ditolak. Pengingat tidak akan berfungsi.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadSchedules() {
        int userId = sessionManager.getUserId();

        // Load based on filters
        if (!selectedHari.equals("Semua") && !selectedJenis.equals("Semua")) {
            scheduleList = filterByDayAndType(userId, selectedHari, selectedJenis);
        } else if (!selectedHari.equals("Semua")) {
            scheduleList = db.getSchedulesByDay(userId, selectedHari);
        } else if (!selectedJenis.equals("Semua")) {
            scheduleList = db.getSchedulesByType(userId, selectedJenis);
        } else {
            scheduleList = db.getAllSchedules(userId);
        }

        // Update UI
        if (scheduleList.isEmpty()) {
            if (layoutEmpty != null) {
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
            }
            recyclerView.setVisibility(View.GONE);
        } else {
            if (layoutEmpty != null) {
                layoutEmpty.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter.updateData(scheduleList);
    }

    private List<Schedule> filterByDayAndType(int userId, String hari, String jenis) {
        List<Schedule> allSchedules = db.getAllSchedules(userId);
        List<Schedule> filteredList = new ArrayList<>();

        for (Schedule schedule : allSchedules) {
            if (schedule.getHari().equals(hari) && schedule.getJenis().equals(jenis)) {
                filteredList.add(schedule);
            }
        }

        return filteredList;
    }

    @Override
    public void onScheduleClick(int position) {
        Schedule schedule = scheduleList.get(position);

        Intent intent = new Intent(MainActivity.this, AddEditScheduleActivity.class);
        intent.putExtra(Constants.EXTRA_IS_EDIT_MODE, true);
        intent.putExtra(Constants.EXTRA_SCHEDULE_ID, schedule.getId());
        startActivityForResult(intent, Constants.REQUEST_EDIT_SCHEDULE);
    }

    @Override
    public void onScheduleDelete(int position) {
        final Schedule schedule = scheduleList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Hapus Jadwal")
                .setMessage("Apakah Anda yakin ingin menghapus jadwal " + schedule.getNamaMatkul() + "?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel alarm
                        AlarmHelper.cancelAlarm(MainActivity.this, schedule.getId());

                        // Delete from database
                        db.deleteSchedule(schedule.getId());
                        Toast.makeText(MainActivity.this, Constants.MSG_SCHEDULE_DELETED, Toast.LENGTH_SHORT).show();
                        loadSchedules();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            loadSchedules();
            setAllScheduleAlarms();
            Toast.makeText(this, "Data diperbarui", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.logoutUser();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            loadSchedules();
            setAllScheduleAlarms();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}