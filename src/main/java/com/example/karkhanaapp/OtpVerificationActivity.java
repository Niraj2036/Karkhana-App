package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4;
    private Button btnVerify;
    private TextView tvResend, tvPhoneNumber;
    private CountDownTimer countDownTimer;

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
        String phone = getIntent().getStringExtra("phone");
        if (phone != null && !phone.isEmpty()) {
            tvPhoneNumber.setText("+91 " + phone);
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
            startActivity(new Intent(OtpVerificationActivity.this, PersonalDetailsActivity.class));
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
                tvResend.setText(String.format("Resend OTP in %ds", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tvResend.setText(getString(R.string.otp_resend));
                tvResend.setEnabled(true);
                tvResend.setOnClickListener(v -> startResendTimer());
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
