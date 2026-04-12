package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.karkhanaapp.repositories.ProfileRepository;
import com.google.firebase.auth.FirebaseAuth;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private Button btnVerify;
    private TextView tvResend, tvPhoneNumber;
    private CountDownTimer countDownTimer;
    private String contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btnVerify = findViewById(R.id.btnVerify);
        tvResend = findViewById(R.id.tvResend);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);

        // Show phone number from intent
        contact = getIntent().getStringExtra("contact");
        if (contact == null || contact.isEmpty()) {
            contact = getIntent().getStringExtra("phone");
        }
        if (contact != null && !contact.isEmpty()) {
            tvPhoneNumber.setText(getString(R.string.otp_sent_to_format, contact));
        }

        // Auto-focus between OTP fields
        setupOtpAutoFocus(otp1, otp2);
        setupOtpAutoFocus(otp2, otp3);
        setupOtpAutoFocus(otp3, otp4);
        setupOtpAutoFocus(otp4, null);

        // Start countdown timer
        startResendTimer();

        // Back button
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());

        // Verify button
        btnVerify.setOnClickListener(v -> {
            String otp = otp1.getText().toString().trim()
                    + otp2.getText().toString().trim()
                    + otp3.getText().toString().trim()
                    + otp4.getText().toString().trim();

            if (otp.length() != 4) {
                Toast.makeText(this, "Enter valid 4-digit OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null) {
                Intent intent = new Intent(OtpVerificationActivity.this, PersonalDetailsActivity.class);
                intent.putExtra("contact", contact);
                startActivity(intent);
                return;
            }

            new ProfileRepository().checkProfileExists(uid, exists -> {
                Intent intent;
                if (exists) {
                    intent = new Intent(OtpVerificationActivity.this, MainAppActivity.class);
                } else {
                    intent = new Intent(OtpVerificationActivity.this, PersonalDetailsActivity.class);
                    intent.putExtra("contact", contact);
                }
                startActivity(intent);
                finish();
            });
        });
    }

    private void setupOtpAutoFocus(EditText current, EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void startResendTimer() {
        tvResend.setEnabled(false);
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvResend.setText(getString(R.string.otp_resend_timer, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvResend.setText(getString(R.string.otp_resend));
                tvResend.setEnabled(true);
                tvResend.setOnClickListener(v -> {
                    Toast.makeText(OtpVerificationActivity.this, "OTP resent", Toast.LENGTH_SHORT).show();
                    startResendTimer();
                });
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
