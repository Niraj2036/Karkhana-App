package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText contactInput;
    private Button btnGetOtp;
    private Button btnGoogleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        contactInput = findViewById(R.id.contactInput);
        btnGetOtp = findViewById(R.id.btnGetOtp);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        btnGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Google login selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, PersonalDetailsActivity.class));
        });

        // Get OTP button
        btnGetOtp.setOnClickListener(v -> {
            String contact = contactInput.getText().toString().trim();
            if (contact.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(contact).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
            intent.putExtra("contact", contact);
            startActivity(intent);
        });
    }
}
