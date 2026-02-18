package com.example.recyclingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        EditText etEmail = findViewById(R.id.etRegEmail);
        EditText etPassword = findViewById(R.id.etRegPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.length() < 6) {
                Toast.makeText(this, "Please enter a valid email and 6-digit password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            finish(); // Returns user to the Login screen
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }
}