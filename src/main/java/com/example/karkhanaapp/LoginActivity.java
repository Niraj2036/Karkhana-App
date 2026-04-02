package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextView toggleFarmer, toggleFactory;
    private EditText phoneInput;
    private Button btnGetOtp;
    private boolean isFarmerSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toggleFarmer = findViewById(R.id.toggleFarmer);
        toggleFactory = findViewById(R.id.toggleFactory);
        phoneInput = findViewById(R.id.phoneInput);
        btnGetOtp = findViewById(R.id.btnGetOtp);

        // Toggle selection
        toggleFarmer.setOnClickListener(v -> selectRole(true));
        toggleFactory.setOnClickListener(v -> selectRole(false));

        // Get OTP button
        btnGetOtp.setOnClickListener(v -> {
            String phone = phoneInput.getText().toString().trim();
            Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
            intent.putExtra("phone", phone);
            startActivity(intent);
        });
    }

    private void selectRole(boolean farmer) {
        isFarmerSelected = farmer;
        if (farmer) {
            toggleFarmer.setBackgroundResource(R.drawable.selected_toggle_pill);
            toggleFarmer.setTextColor(getResources().getColor(R.color.primary, null));
            toggleFarmer.setElevation(4f);
            toggleFactory.setBackgroundResource(0);
            toggleFactory.setTextColor(getResources().getColor(R.color.text_hint, null));
            toggleFactory.setElevation(0f);
        } else {
            toggleFactory.setBackgroundResource(R.drawable.selected_toggle_pill);
            toggleFactory.setTextColor(getResources().getColor(R.color.primary, null));
            toggleFactory.setElevation(4f);
            toggleFarmer.setBackgroundResource(0);
            toggleFarmer.setTextColor(getResources().getColor(R.color.text_hint, null));
            toggleFarmer.setElevation(0f);
        }
    }
}
