package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;
import com.genderanddevelopmentprimer.app.navbaractivity.StudentActivity;
import com.genderanddevelopmentprimer.app.navbaractivity.TeacherActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class HomeScreen extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button btnLesson, btnSettings, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        setBackground();

        btnLesson = findViewById(R.id.btn_lesson);
        btnSettings = findViewById(R.id.btn_settings);
        btnLogout = findViewById(R.id.btn_logout);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //lesson button opens teacher.class/student.class
        btnLesson.setOnClickListener(v -> {
            userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener((value, error) -> {
                assert value != null;
                if (Objects.equals(value.getString("userType"), "Teacher")) {
                    startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
                } else if (Objects.equals(value.getString("userType"), "Student")) {
                    startActivity(new Intent(getApplicationContext(), StudentActivity.class));
                }
            });
        });

        btnSettings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Settings.class)));

        btnLogout.setOnClickListener(v -> {
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }

    private void setBackground() {
        //set background
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                getWindow().setBackgroundDrawableResource(R.drawable.darkbackground);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:

            case Configuration.UI_MODE_NIGHT_NO:
                getWindow().setBackgroundDrawableResource(R.drawable.lightbackground);
                break;
        }
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