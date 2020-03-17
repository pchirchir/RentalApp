package com.shadow.rentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    //Firebase Variable
    FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();


        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        toolbar.setTitle("MY APP");

        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }





    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.share){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("plaintext/string");
            String header = "Share App via";
            String subheading = "Sharing";
            String shareBody = "app link";

            shareIntent.putExtra(Intent.EXTRA_SUBJECT,subheading);
            shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);

            /*Start activity*/
            startActivity(Intent.createChooser(shareIntent,header));
        }   if (id==R.id.notification){
            Toast.makeText(this,"notification Clicked",Toast.LENGTH_LONG).show();
        }  if (id==R.id.profile){
            Intent intent = new Intent(HomeActivity.this, UserProfile .class);
           // intent.putExtra("Email","example@gmail.com");
            startActivity(intent);
           // Animatoo.animateDiagonal(this);
        } if (id==R.id.settings){
            Toast.makeText(this,"Settings Clicked",Toast.LENGTH_LONG).show();
        }if (id==R.id.logout){
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
           // Animatoo.animateSpin(this);

        }

        return super.onOptionsItemSelected(item);
    }


}
