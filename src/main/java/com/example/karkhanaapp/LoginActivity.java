package com.example.karkhanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText contactInput;
    private Button btnGetOtp;
    private Button btnGoogleLogin;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private boolean googleSignInConfigured;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                btnGoogleLogin.setEnabled(true);

                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    Toast.makeText(this, "Google sign-in cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    GoogleSignInAccount account = GoogleSignIn
                            .getSignedInAccountFromIntent(result.getData())
                            .getResult(ApiException.class);

                    if (account == null || account.getIdToken() == null) {
                        Toast.makeText(this, "Unable to get Google account token", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.e(TAG, "Google sign-in failed", e);
                    Toast.makeText(this, "Google sign-in failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        int webClientIdResId = getResources().getIdentifier(
                "default_web_client_id", "string", getPackageName());
        String webClientId = webClientIdResId == 0 ? null : getString(webClientIdResId);
        googleSignInConfigured = !TextUtils.isEmpty(webClientId);

        if (googleSignInConfigured) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(this, gso);
        }

        contactInput = findViewById(R.id.contactInput);
        btnGetOtp = findViewById(R.id.btnGetOtp);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        btnGoogleLogin.setOnClickListener(v -> {
            if (!googleSignInConfigured || googleSignInClient == null) {
                Toast.makeText(this, "Google Sign-In not configured. Add SHA-1 in Firebase and download a new google-services.json.", Toast.LENGTH_LONG).show();
                return;
            }

            btnGoogleLogin.setEnabled(false);
            googleSignInClient.signOut().addOnCompleteListener(task ->
                    googleSignInLauncher.launch(googleSignInClient.getSignInIntent()));
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

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainAppActivity.class));
            finish();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    btnGoogleLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainAppActivity.class));
                        finish();
                    } else {
                        Log.e(TAG, "Firebase auth with Google failed", task.getException());
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
