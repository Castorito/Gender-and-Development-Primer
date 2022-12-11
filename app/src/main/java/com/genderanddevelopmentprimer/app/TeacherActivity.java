package com.genderanddevelopmentprimer.app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class TeacherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        Toolbar teacher_toolbar = findViewById(R.id.teacher_toolbar);
        setSupportActionBar(teacher_toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        DrawerLayout teacher_drawer = findViewById(R.id.activity_teacher);

        NavigationView navigationView = findViewById(R.id.nav_view_teacher);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle teacher_toggle = new ActionBarDrawerToggle(this, teacher_drawer, teacher_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        teacher_drawer.addDrawerListener(teacher_toggle);
        teacher_toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}