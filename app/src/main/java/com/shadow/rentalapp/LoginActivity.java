package com.shadow.rentalapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    ImageView image;
    TextView tex1, text2;
    TextInputLayout email, passwordd;
    TextInputEditText emailTxt, passwordTxt;
    FirebaseAuth fAuth;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    static final int GOOGLE_SIGN = 123;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        image = findViewById(R.id.iimage);
        tex1 = findViewById(R.id.welcome);
        text2 = findViewById(R.id.join);
        email = findViewById(R.id.email);
        passwordd = findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();

        emailTxt = findViewById(R.id.email_txt);
        passwordTxt = findViewById(R.id.password_txt);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("People");

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        if (fAuth.getCurrentUser() != null) {
            FirebaseUser user = fAuth.getCurrentUser();
        }


    }


    private Boolean validatePhone() {
        String val = emailTxt.getText().toString();

        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else {
            email.setError(null);
            email.setEnabled(false);
            return true;
        }

    }

    private Boolean validatePassword() {

        String val = passwordTxt.getText().toString();

        if (val.isEmpty()) {
            passwordd.setError("Field cannot be empty");
            return false;
        } else {
            passwordd.setError(null);
            passwordd.setEnabled(false);
            return true;
        }

    }


    public void Signup(View view) {
        Intent intent = new Intent(LoginActivity.this, UserProfile.class);


        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View, String>(image, "logo_image");
        pairs[1] = new Pair<View, String>(tex1, "logo_name");
        pairs[2] = new Pair<View, String>(text2, "but");
        pairs[3] = new Pair<View, String>(email, "full");
        pairs[4] = new Pair<View, String>(passwordd, "pass");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
        startActivity(intent, options.toBundle());
    }

    public void login(View view) {
        //Validate Login Info
        if (!validatePhone() | !validatePassword())
            return;

        isUser();

    }


    private void isUser() {
        final String email = emailTxt.getText().toString().trim();
        final String password = passwordTxt.getText().toString().trim();

        //authenticate user
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                    } else {

                        sendHome();

                    }
                });

    }

    public void Signin(View view) {

        Intent signIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signIntent, GOOGLE_SIGN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "Signin Successfully");
                        FirebaseUser user = fAuth.getCurrentUser();

                        if (user != null) {
                            sendHome();
                        }

                    } else {
                        Log.w("TAG", "Signin Failure", task.getException());
                        Toast.makeText(this, "SignIn Failed!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = fAuth.getCurrentUser();

        if (user != null) {
            sendHome();
        }
    }

    private void sendHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


}



