package com.shadow.rentalapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shadow.rentalapp.R;
import com.shadow.rentalapp.data.models.Profile;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //Variables
    TextInputEditText nameTxt, emailTxt, passwordTxt;
    private ProgressBar registerProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

        //Init fire-base instances
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        nameTxt = findViewById(R.id.full_name_txt);
        emailTxt = findViewById(R.id.email_txt);
        passwordTxt = findViewById(R.id.password_txt);
        Button registerBtn = findViewById(R.id.register_button);

        registerProgressBar = findViewById(R.id.register_progress_bar);

        TextView loginTextView = findViewById(R.id.login_text_view);
        loginTextView.setOnClickListener(view -> finish());

        registerBtn.setOnClickListener(view -> {
            //Get user input
            String fullName = String.valueOf(nameTxt.getText());
            String email = String.valueOf(emailTxt.getText());
            String password = String.valueOf(passwordTxt.getText());

            //Validate user input
            if (!validateName(fullName)) return;
            if (!validateEmail(email)) return;
            if (!validatePassword(password)) return;

            //Create the user account using using fire-base auth
            registerUser(fullName, email, password);
        });


    }

    private void registerUser(String fullName, String email, String password) {

        registerProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        //Save username to the profile
                        saveBasicUserData(fullName);

                    } else {
                        registerProgressBar.setVisibility(View.GONE);
                        Log.e(TAG, "registerUser: failed: ", task.getException());
                        Toast.makeText(this, "Registration failed, try again later", Toast.LENGTH_SHORT).show();
                    }


                });

    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailTxt.setError("Email is required");
            emailTxt.requestFocus();
            return false;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Invalid email");
            emailTxt.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateName(String fullName) {

        if (TextUtils.isEmpty(fullName)) {
            nameTxt.setError("Full name is required");
            nameTxt.requestFocus();
            return false;
        }

        return true;

    }

    private boolean validatePassword(String password) {

        if (TextUtils.isEmpty(password)) {
            passwordTxt.setError("Field cannot be empty");
            passwordTxt.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordTxt.setError("At least 6 chars required");
            passwordTxt.requestFocus();
            return false;

        }

        return true;
    }

    private void saveBasicUserData(String fullName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        assert currentUser != null;
        String uid = currentUser.getUid();

        //Save the full name to profile
        Profile profile = new Profile(fullName, null, null, "Tenant");

        mDatabase.collection("Profiles")
                .document(uid)
                .set(profile)
                .addOnCompleteListener(task -> {
                    registerProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile full name set", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "saveBasicUserData: failed", task.getException());
                        Toast.makeText(this, "Setup your profile from home", Toast.LENGTH_SHORT).show();
                    }
                    sendHome();
                });
    }

    private void sendHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
