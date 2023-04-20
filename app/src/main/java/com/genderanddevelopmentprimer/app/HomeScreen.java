package com.genderanddevelopmentprimer.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class HomeScreen extends AppCompatActivity {
    Button btnLesson, btnHelp, btnAboutUs, btnGames, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Toolbar drawerToolbar = findViewById(R.id.drawer_toolbar_home);
        setSupportActionBar(drawerToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        btnLesson = findViewById(R.id.btn_lesson);
        btnGames = findViewById(R.id.btn_games);
        btnHelp = findViewById(R.id.btn_helpdesk);
        btnAboutUs = findViewById(R.id.btn_aboutUs);
        btnSettings = findViewById(R.id.btn_acc_sett);

        //lesson button opens teacher.class/student.class
        btnLesson.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), DrawerActivity.class)));

        btnGames.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), GameMenu.class)));

        btnHelp.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LocalHelpDeskInfo.class)));

        btnSettings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Settings.class)));

        btnAboutUs.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AboutUs.class)));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            googleSignInClient.signOut().addOnCompleteListener(this, task -> {
                        // update your UI if needed
                        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                    });
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
        return super.onOptionsItemSelected(item);
    }
}