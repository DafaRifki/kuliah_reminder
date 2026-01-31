package com.example.kuliahreminder.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kuliahreminder.R;
import com.example.kuliahreminder.database.DatabaseHelper;
import com.example.kuliahreminder.model.User;
import com.example.kuliahreminder.utils.Constants;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNamaLengkap, etEmail, etPassword, etKonfirmasiPassword;
    private Button btnDaftar;
    private TextView tvLogin;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        initViews();

        // Initialize database
        db = new DatabaseHelper(this);

        // Setup listeners
        setupListeners();
    }

    private void initViews() {
        etNamaLengkap = findViewById(R.id.et_nama_lengkap);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etKonfirmasiPassword = findViewById(R.id.et_konfirmasi_password);
        btnDaftar = findViewById(R.id.btn_daftar);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void setupListeners() {
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        String namaLengkap = etNamaLengkap.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String konfirmasiPassword = etKonfirmasiPassword.getText().toString().trim();

        // Validation
        if (!validateInput(namaLengkap, email, password, konfirmasiPassword)) {
            return;
        }

        // Check if email already exists
        if (db.isEmailExists(email)) {
            etEmail.setError(Constants.MSG_EMAIL_ALREADY_EXISTS);
            etEmail.requestFocus();
            return;
        }

        // Create new user
        User user = new User(namaLengkap, email, password);
        long result = db.addUser(user);

        if (result != -1) {
            // Registration success
            Toast.makeText(this, Constants.MSG_REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Registration failed
            Toast.makeText(this, "Registrasi gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String namaLengkap, String email, String password, String konfirmasiPassword) {
        if (TextUtils.isEmpty(namaLengkap)) {
            etNamaLengkap.setError(Constants.MSG_FIELD_REQUIRED);
            etNamaLengkap.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(Constants.MSG_FIELD_REQUIRED);
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(Constants.MSG_EMAIL_INVALID);
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(Constants.MSG_FIELD_REQUIRED);
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError(Constants.MSG_PASSWORD_MIN_LENGTH);
            etPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(konfirmasiPassword)) {
            etKonfirmasiPassword.setError(Constants.MSG_FIELD_REQUIRED);
            etKonfirmasiPassword.requestFocus();
            return false;
        }

        if (!password.equals(konfirmasiPassword)) {
            etKonfirmasiPassword.setError(Constants.MSG_PASSWORD_NOT_MATCH);
            etKonfirmasiPassword.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}