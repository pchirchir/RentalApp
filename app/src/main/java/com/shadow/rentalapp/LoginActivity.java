package com.shadow.rentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    ImageView image;
    TextView tex1, text2;
    TextInputLayout Fphone, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        image = findViewById(R.id.iimage);
        tex1 = findViewById(R.id.welcome);
        text2 = findViewById(R.id.join);
        Fphone = findViewById(R.id.phone);
        password = findViewById(R.id.password);


    }

    private Boolean validatePhone() {
        String val = Fphone.getEditText().getText().toString();

        if (val.isEmpty()) {
            Fphone.setError("Field cannot be empty");
            return false;
        } else {
            Fphone.setError(null);
            Fphone.setEnabled(false);
            return true;
        }

    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setEnabled(false);
            return true;
        }

    }


    public void Signup(View view) {
        Intent intent = new Intent(LoginActivity.this, Signup.class);


        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View, String>(image, "logo_image");
        pairs[1] = new Pair<View, String>(tex1, "logo_name");
        pairs[2] = new Pair<View, String>(text2, "but");
        pairs[3] = new Pair<View, String>(Fphone, "full");
        pairs[4] = new Pair<View, String>(password, "pass");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
        startActivity(intent, options.toBundle());
    }

    public void login(View view) {
        //Validate Login Info
        if (!validatePhone() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        final String userEnteredPhone = Fphone.getEditText().toString().trim();
        final String userEnteredPassword = password.getEditText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("People");

        Query checkUser = reference.orderByChild("name").equalTo(userEnteredPhone);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Fphone.setError(null);
                    Fphone.setEnabled(false);
                    String passwordFromDB = dataSnapshot.child(userEnteredPhone).child("Password").getValue(String.class);

                    if (passwordFromDB.equals(userEnteredPassword)) {

                        Fphone.setError(null);
                        Fphone.setEnabled(false);


                        Toast.makeText(LoginActivity.this, "Logged in Successfuly", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));

/*
                        String nameFromDB= dataSnapshot.child(userEnteredPhone).child("name").getValue(String.class);
                        String emailFromDB= dataSnapshot.child(userEnteredPhone).child("email").getValue(String.class);
                        String phoneNoFromDB= dataSnapshot.child(userEnteredPhone).child("PhoneNo").getValue(String.class);
                        String genderFromDB= dataSnapshot.child(userEnteredPhone).child("Gender").getValue(String.class);

                        Intent intent2= new Intent(getApplicationContext(),UserProfile.class);

                        intent2.putExtra("name",nameFromDB);
                        intent2.putExtra("email",emailFromDB);
                        intent2.putExtra("phoneNo",phoneNoFromDB);
                        intent2.putExtra("password",passwordFromDB);
                        intent2.putExtra("Gender",genderFromDB);

                        startActivity(intent2);*/


                    } else {
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                } else {
                    Fphone.setError("No such User Exist");
                    Fphone.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

