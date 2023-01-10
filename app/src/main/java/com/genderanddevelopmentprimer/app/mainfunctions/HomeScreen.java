package com.genderanddevelopmentprimer.app.mainfunctions;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;

public class HomeScreen extends AppCompatActivity {
    TextView identifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        identifier = findViewById(R.id.identifier);
        identifier.setText(getIntent().getStringExtra("identify"));

        //lesson button opens teacher.class/student.class using identifier
    }
}