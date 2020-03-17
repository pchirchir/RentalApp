package com.shadow.rentalapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private ImageView image;
    private ProgressBar loginProgressBar;
    TextView tex1, text2;
    TextInputLayout email, passwordd;
    TextInputEditText emailTxt, passwordTxt;
    private FirebaseAuth fAuth;

    static final int GOOGLE_SIGN = 123;

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //Initialize fire-base instances
        fAuth = FirebaseAuth.getInstance();

        //Register views
        image = findViewById(R.id.iimage);
        tex1 = findViewById(R.id.welcome);
        text2 = findViewById(R.id.join);
        email = findViewById(R.id.email);
        passwordd = findViewById(R.id.password);

        emailTxt = findViewById(R.id.email_txt);
        passwordTxt = findViewById(R.id.password_txt);

        loginProgressBar = findViewById(R.id.login_progress_bar);

        SignInButton googleSignInBtn = findViewById(R.id.google_sign_in_btn);


        //Sign in with google account
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.CLIENT_ID))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googleSignInBtn.setOnClickListener(v -> googleSignIn());

    }


    private boolean validateEmail(String email) {

        if (TextUtils.isEmpty(email)) {
            emailTxt.setError("Email is required");
            emailTxt.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Invalid email format");
            emailTxt.requestFocus();
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


    public void Signup(View view) {

        Intent intent = new Intent(this, SignupActivity.class);

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

        String email = String.valueOf(emailTxt.getText());
        String password = String.valueOf(passwordTxt.getText());

        //Validate Login Info
        if (!validateEmail(email) || !validatePassword(password)) return;

        login(email, password);

    }


    private void login(String email, String password) {

        //Authenticate user
        loginProgressBar.setVisibility(View.VISIBLE);
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        sendHome();
                    } else {
                        Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "login: authentication: ", task.getException());
                    }
                });

    }

    public void googleSignIn() {

        loginProgressBar.setVisibility(View.VISIBLE);

        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN) {

            loginProgressBar.setVisibility(View.GONE);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult: Google Sign In: ", e);
                e.printStackTrace();
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "Sign In Successfully");
                        sendHome();

                    } else {
                        Log.e("TAG", getString(R.string.auth_failed), task.getException());
                        assert task.getException() != null;
                        Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}



