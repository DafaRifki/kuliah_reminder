package com.example.kuliahreminder.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuliahreminder.R;
import com.example.kuliahreminder.adapter.ScheduleAdapter;
import com.example.kuliahreminder.database.DatabaseHelper;
import com.example.kuliahreminder.model.Schedule;
import com.example.kuliahreminder.utils.Constants;
import com.example.kuliahreminder.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleListener{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private Spinner spinnerFilterHari, spinnerFilterJenis;

    private DatabaseHelper db;
    private SessionManager sessionManager;
    private ScheduleAdapter adapter;
    private List<Schedule> scheduleList;

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
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        tvEmpty = findViewById(R.id.tv_empty);
        fabAdd = findViewById(R.id.fab_add);
        spinnerFilterHari = findViewById(R.id.spinner_filter_hari);
        spinnerFilterJenis = findViewById(R.id.spinner_filter_jenis);
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

    private void loadSchedules() {
        int userId = sessionManager.getUserId();

        // Load based on filters
        if (!selectedHari.equals("Semua") && !selectedJenis.equals("Semua")) {
            // Filter by both day and type
            scheduleList = filterByDayAndType(userId, selectedHari, selectedJenis);
        } else if (!selectedHari.equals("Semua")) {
            // Filter by day only
            scheduleList = db.getSchedulesByDay(userId, selectedHari);
        } else if (!selectedJenis.equals("Semua")) {
            // Filter by type only
            scheduleList = db.getSchedulesByType(userId, selectedJenis);
        } else {
            // No filter, get all
            scheduleList = db.getAllSchedules(userId);
        }

        // Update UI
        if (scheduleList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
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