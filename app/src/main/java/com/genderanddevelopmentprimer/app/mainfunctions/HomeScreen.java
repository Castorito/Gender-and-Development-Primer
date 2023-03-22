package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;
import com.genderanddevelopmentprimer.app.navbaractivity.DrawerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeScreen extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button btnLesson, btnSettings, btnLogout, btnHelp, btnAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        btnLesson = findViewById(R.id.btn_lesson);
        btnSettings = findViewById(R.id.btn_settings);
        btnLogout = findViewById(R.id.btn_logout);
        btnHelp = findViewById(R.id.btn_helpdesk);
        btnAboutUs = findViewById(R.id.btn_aboutUs);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //lesson button opens teacher.class/student.class
        btnLesson.setOnClickListener(v -> {
            userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener((value, error) -> startActivity(new Intent(getApplicationContext(), DrawerActivity.class)));
        });

        btnHelp.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LocalHelpDeskInfo.class)));

        btnSettings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Settings.class)));

        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeScreen.this, "TEST CLICK! WORKING", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
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
}