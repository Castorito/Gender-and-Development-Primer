package com.genderanddevelopmentprimer.app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.genderanddevelopmentprimer.app.commonfragment.CommonFrag1;
import com.genderanddevelopmentprimer.app.commonfragment.CommonFrag2;
import com.genderanddevelopmentprimer.app.commonfragment.CommonFrag3;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class TeacherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout teacher_drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        //call a navbar
        Toolbar teacher_toolbar = findViewById(R.id.teacher_toolbar);
        setSupportActionBar(teacher_toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        teacher_drawer = findViewById(R.id.activity_teacher);

        NavigationView navigationView = findViewById(R.id.nav_view_teacher);
        navigationView.setNavigationItemSelectedListener(this);

        //the "hamburger" icon on navbar
        ActionBarDrawerToggle teacher_toggle = new ActionBarDrawerToggle(this, teacher_drawer, teacher_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        teacher_drawer.addDrawerListener(teacher_toggle);
        teacher_toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_teacher, new CommonFrag1()).commit();
            navigationView.setCheckedItem(R.id.teacher_common_fragment_1);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.teacher_common_fragment_1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_teacher, new CommonFrag1()).commit();
        } else if (item.getItemId() == R.id.teacher_common_fragment_2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_teacher, new CommonFrag2()).commit();
        } else if (item.getItemId() == R.id.teacher_common_fragment_3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_teacher, new CommonFrag3()).commit();
        }
        teacher_drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (teacher_drawer.isDrawerOpen(GravityCompat.START)) {
            teacher_drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}