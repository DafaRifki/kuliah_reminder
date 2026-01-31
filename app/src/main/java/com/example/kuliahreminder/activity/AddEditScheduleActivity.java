package com.example.kuliahreminder.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kuliahreminder.R;
import com.example.kuliahreminder.database.DatabaseHelper;
import com.example.kuliahreminder.model.Schedule;
import com.example.kuliahreminder.utils.Constants;
import com.example.kuliahreminder.utils.SessionManager;

import java.util.Calendar;
import java.util.List;

public class AddEditScheduleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etNamaMatkul, etWaktuMulai, etWaktuSelesai, etRuangan, etKeterangan;
    private Spinner spinnerJenis, spinnerHari;
    private Button btnSimpan;

    private DatabaseHelper db;
    private SessionManager sessionManager;

    private boolean isEditMode = false;
    private int scheduleId = -1;
    private Schedule currentSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_schedule);

        // Initialize
        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Get intent extras
        isEditMode = getIntent().getBooleanExtra(Constants.EXTRA_IS_EDIT_MODE, false);
        scheduleId = getIntent().getIntExtra(Constants.EXTRA_SCHEDULE_ID, -1);

        // Initialize views
        initViews();

        // Setup toolbar
        setupToolbar();

        // Setup spinners
        setupSpinners();

        // Setup time pickers
        setupTimePickers();

        // Load data if edit mode
        if (isEditMode && scheduleId != -1) {
            loadScheduleData();
        }

        // Setup save button
        setupSaveButton();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etNamaMatkul = findViewById(R.id.et_nama_matkul);
        etWaktuMulai = findViewById(R.id.et_waktu_mulai);
        etWaktuSelesai = findViewById(R.id.et_waktu_selesai);
        etRuangan = findViewById(R.id.et_ruangan);
        etKeterangan = findViewById(R.id.et_keterangan);
        spinnerJenis = findViewById(R.id.spinner_jenis);
        spinnerHari = findViewById(R.id.spinner_hari);
        btnSimpan = findViewById(R.id.btn_simpan);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "Edit Jadwal" : "Tambah Jadwal");
        }
    }

    private void setupSpinners() {
        // Jenis Spinner
        ArrayAdapter<String> jenisAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Constants.JENIS_ARRAY
        );
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenis.setAdapter(jenisAdapter);

        // Hari Spinner (without "Semua")
        String[] hariArray = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        ArrayAdapter<String> hariAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                hariArray
        );
        hariAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHari.setAdapter(hariAdapter);
    }

    private void setupTimePickers() {
        etWaktuMulai.setFocusable(false);
        etWaktuMulai.setClickable(true);
        etWaktuMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(etWaktuMulai);
            }
        });

        etWaktuSelesai.setFocusable(false);
        etWaktuSelesai.setClickable(true);
        etWaktuSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(etWaktuSelesai);
            }
        });
    }

    private void showTimePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                        editText.setText(time);
                    }
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private void loadScheduleData() {
        // Load schedule from database
        List<Schedule> schedules = db.getAllSchedules(sessionManager.getUserId());
        for (Schedule schedule : schedules) {
            if (schedule.getId() == scheduleId) {
                currentSchedule = schedule;
                break;
            }
        }

        if (currentSchedule != null) {
            etNamaMatkul.setText(currentSchedule.getNamaMatkul());
            etWaktuMulai.setText(currentSchedule.getWaktuMulai());
            etWaktuSelesai.setText(currentSchedule.getWaktuSelesai());
            etRuangan.setText(currentSchedule.getRuangan());
            etKeterangan.setText(currentSchedule.getKeterangan());

            // Set spinner selections
            for (int i = 0; i < Constants.JENIS_ARRAY.length; i++) {
                if (Constants.JENIS_ARRAY[i].equals(currentSchedule.getJenis())) {
                    spinnerJenis.setSelection(i);
                    break;
                }
            }

            String[] hariArray = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
            for (int i = 0; i < hariArray.length; i++) {
                if (hariArray[i].equals(currentSchedule.getHari())) {
                    spinnerHari.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupSaveButton() {
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSchedule();
            }
        });
    }

    private void saveSchedule() {
        String namaMatkul = etNamaMatkul.getText().toString().trim();
        String jenis = spinnerJenis.getSelectedItem().toString();
        String hari = spinnerHari.getSelectedItem().toString();
        String waktuMulai = etWaktuMulai.getText().toString().trim();
        String waktuSelesai = etWaktuSelesai.getText().toString().trim();
        String ruangan = etRuangan.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();

        // Validation
        if (!validateInput(namaMatkul, waktuMulai, waktuSelesai)) {
            return;
        }

        // Create or update schedule
        if (isEditMode) {
            currentSchedule.setNamaMatkul(namaMatkul);
            currentSchedule.setJenis(jenis);
            currentSchedule.setHari(hari);
            currentSchedule.setWaktuMulai(waktuMulai);
            currentSchedule.setWaktuSelesai(waktuSelesai);
            currentSchedule.setRuangan(ruangan);
            currentSchedule.setKeterangan(keterangan);

            int result = db.updateSchedule(currentSchedule);
            if (result > 0) {
                Toast.makeText(this, Constants.MSG_SCHEDULE_UPDATED, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        } else {
            Schedule newSchedule = new Schedule(
                    sessionManager.getUserId(),
                    namaMatkul,
                    jenis,
                    hari,
                    waktuMulai,
                    waktuSelesai,
                    ruangan,
                    keterangan
            );

            long result = db.addSchedule(newSchedule);
            if (result != -1) {
                Toast.makeText(this, Constants.MSG_SCHEDULE_ADDED, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private boolean validateInput(String namaMatkul, String waktuMulai, String waktuSelesai) {
        if (TextUtils.isEmpty(namaMatkul)) {
            etNamaMatkul.setError(Constants.MSG_FIELD_REQUIRED);
            etNamaMatkul.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(waktuMulai)) {
            etWaktuMulai.setError(Constants.MSG_FIELD_REQUIRED);
            etWaktuMulai.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(waktuSelesai)) {
            etWaktuSelesai.setError(Constants.MSG_FIELD_REQUIRED);
            etWaktuSelesai.requestFocus();
            return false;
        }

        // Validate time range
        if (waktuMulai.compareTo(waktuSelesai) >= 0) {
            Toast.makeText(this, Constants.MSG_TIME_INVALID, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}