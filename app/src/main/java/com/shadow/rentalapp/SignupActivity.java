package com.shadow.rentalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    //Variables
    TextInputLayout regname, regemail, regphone, regpassword;
    RadioButton male, female;


    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("People");
        mAuth = FirebaseAuth.getInstance();

        regname = findViewById(R.id.Re_name);
        regemail = findViewById(R.id.Reg_email);
        regphone = findViewById(R.id.Reg_phone);
        regpassword = findViewById(R.id.Reg_password);
        male = findViewById(R.id.radio_male);
        female = findViewById(R.id.radio_female);


    }


    private Boolean validatePhone() {
        String val = regphone.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            regphone.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            regphone.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            regphone.setError("White Spaces are not allowed");
            return false;
        } else {
            regphone.setError(null);
            return true;
        }

    }

    private Boolean validateEmail() {
        String val = regemail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            regemail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regemail.setError("Invalid email address  ");
            return false;
        } else {
            regemail.setError(null);
            return true;
        }

    }

    private Boolean validateName() {
        String val = regname.getEditText().getText().toString();

        if (val.isEmpty()) {
            regname.setError("Field cannot be empty");
            return false;
        } else {
            regname.setError(null);
            regname.setEnabled(false);
            return true;
        }

    }

    private Boolean validatePssword() {
        String val = regpassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            regpassword.setError("Field cannot be empty");
            return false;
        } else {
            regpassword.setError(null);
            return true;
        }

    }

    public void logg(View view) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    //save data to firebase
    public void Signup(View view) {

        if (!validateName() | !validateEmail() | !validatePhone() | !validatePhone() | !validatePssword())
            return;


        String pass = regpassword.getEditText().getText().toString();
        String email = regemail.getEditText().getText().toString();


        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        saveBasicUserData();

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    } else {
                        Log.e(TAG, "Signup: createUserWithEmailAndPassword", task.getException());
                    }
                }
        );

    }

    private void saveBasicUserData() {
        String name = regname.getEditText().getText().toString();
        String phoneNo = regphone.getEditText().getText().toString();

        String gender = "";

        //Get all the values

        if (male.isChecked()) {
            gender = "Male";
            if (female.isChecked()) {
                gender = "Female";
            }
        }

        User user = new User(name, phoneNo, gender);

        reference.push().setValue(user);


        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
    }
}
