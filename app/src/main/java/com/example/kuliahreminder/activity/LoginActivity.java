package com.example.kuliahreminder.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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
import com.example.kuliahreminder.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvDaftar;

    private DatabaseHelper db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();

        // Initialize database and session
        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        // Setup listeners
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvDaftar = findViewById(R.id.tv_daftar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        tvDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (!validateInput(email, password)) {
            return;
        }

        // Check credentials
        User user = db.loginUser(email, password);

        if (user != null) {
            // Login success
            sessionManager.createLoginSession(user.getId(), user.getNamaLengkap(), user.getEmail());
            Toast.makeText(this, Constants.MSG_LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            // Login failed
            Toast.makeText(this, Constants.MSG_LOGIN_FAILED, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String email, String password) {
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

        return true;
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button on login screen
        super.onBackPressed();
        finishAffinity();
    }
}