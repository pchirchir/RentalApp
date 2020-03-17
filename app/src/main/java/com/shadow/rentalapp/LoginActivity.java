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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    ImageView image;
    TextView tex1, text2;
    TextInputLayout email, passwordd;
    TextInputEditText emailTxt, passwordTxt;
    FirebaseAuth mAuth;
    private SignInButton signIn;
    GoogleSignInClient mGoogleSignInClient;
    String TAG="MainActivity";

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    static final int RC_SIGN_IN = 123;



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
        signIn = findViewById(R.id.Google);
        mAuth= FirebaseAuth.getInstance();


        emailTxt = findViewById(R.id.email_txt);
        passwordTxt = findViewById(R.id.password_txt);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("People");

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signIn.setOnClickListener(v -> signIn());



    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Unable to Login", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // ...
                });
    }

    private void updateUI(FirebaseUser user) {

        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
        startActivity(intent);


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
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);


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
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {

                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                    } else {

                        sendHome();

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

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



