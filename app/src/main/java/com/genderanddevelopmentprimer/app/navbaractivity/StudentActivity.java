package com.genderanddevelopmentprimer.app.navbaractivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.genderanddevelopmentprimer.app.R;
import com.genderanddevelopmentprimer.app.commonfragment.CommonFrag1;
import com.genderanddevelopmentprimer.app.commonfragment.CommonFrag2;
import com.genderanddevelopmentprimer.app.commonfragment.CommonFrag3;
import com.genderanddevelopmentprimer.app.studentfragment.StudentFrag1;
import com.genderanddevelopmentprimer.app.studentfragment.StudentFrag2;
import com.genderanddevelopmentprimer.app.studentfragment.StudentFrag3;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class StudentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout student_drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Toolbar student_toolbar = findViewById(R.id.student_toolbar);
        setSupportActionBar(student_toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        student_drawer = findViewById(R.id.activity_student);

        NavigationView navigationView = findViewById(R.id.nav_view_student);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, student_drawer, student_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        student_drawer.addDrawerListener(toggle);
        toggle.syncState();

        //saved the instance while rotated
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_student, new CommonFrag1()).commit();
            navigationView.setCheckedItem(R.id.student_common_fragment_1);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.student_common_fragment_1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_student, new CommonFrag1()).commit();
        } else if (item.getItemId() == R.id.student_common_fragment_2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_student, new CommonFrag2()).commit();
        } else if (item.getItemId() == R.id.student_common_fragment_3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_student, new CommonFrag3()).commit();
        } else if (item.getItemId() == R.id.student_fragment_1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_student, new StudentFrag1()).commit();
        } else if (item.getItemId() == R.id.student_common_fragment_2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_student, new StudentFrag2()).commit();
        } else if (item.getItemId() == R.id.student_fragment_3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_student, new StudentFrag3()).commit();
        }
        student_drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (student_drawer.isDrawerOpen(GravityCompat.START)) {
            student_drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}