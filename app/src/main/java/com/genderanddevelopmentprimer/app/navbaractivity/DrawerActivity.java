package com.genderanddevelopmentprimer.app.navbaractivity;

import static android.content.ContentValues.TAG;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.genderanddevelopmentprimer.app.teacherfragment.TeacherFrag1;
import com.genderanddevelopmentprimer.app.teacherfragment.TeacherFrag2;
import com.genderanddevelopmentprimer.app.teacherfragment.TeacherFrag3;
import com.genderanddevelopmentprimer.app.teacherfragment.TeacherFrag4;
import com.genderanddevelopmentprimer.app.teacherfragment.TeacherFrag5;
import com.genderanddevelopmentprimer.app.teacherfragment.TeacherFrag6;
import com.genderanddevelopmentprimer.app.teacherfragment.TeacherFrag7;
import com.genderanddevelopmentprimer.app.teacherfragment.TeacherFrag8;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String filePath, fileName, downloadUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Toolbar drawerToolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(drawerToolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.activity_drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, drawerToolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        if (fAuth.getCurrentUser() != null) {
            DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
            documentReference.addSnapshotListener((value, error) -> {
                if (value != null) {
                    if (Objects.equals(value.getString("userType"), "Teacher")) {
                        navigationView.inflateMenu(R.menu.drawer_menu_teacher);

                        if (savedInstanceState == null) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new CommonFrag1()).commit();
                            filePath = "/Common PDF Files/Common Fragment1.pdf";
                            fileName = "Common Fragment 1.pdf";
                            navigationView.setCheckedItem(R.id.teacher_common_fragment_1);
                        }

                    } else if (Objects.equals(value.getString("userType"), "Student")) {
                        navigationView.inflateMenu(R.menu.drawer_menu_student);

                        if (savedInstanceState == null) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new CommonFrag1()).commit();
                            filePath = "/Common PDF Files/Common Fragment1.pdf";
                            fileName = "Common Fragment 1.pdf";
                            navigationView.setCheckedItem(R.id.student_common_fragment_1);
                        }
                    }
                }
            });
        }else{
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            //common fragments
            case R.id.teacher_common_fragment_1:
            case R.id.student_common_fragment_1:
                filePath = "/Common PDF Files/Common Fragment1.pdf";
                fileName = "Common Fragment 1.pdf";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new CommonFrag1()).commit();
                break;
            case R.id.teacher_common_fragment_2:
            case R.id.student_common_fragment_2:
                filePath = "/Common PDF Files/Common Fragment2.pdf";
                fileName = "Common Fragment 2.pdf";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new CommonFrag2()).commit();
                break;
            case R.id.teacher_common_fragment_3:
            case R.id.student_common_fragment_3:
                filePath = "/Common PDF Files/Common Fragment3.pdf";
                fileName = "Common Fragment 3.pdf";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new CommonFrag3()).commit();
                break;

            //teacher fragments
            case R.id.teacher_fragment_1:
                filePath = "/Teacher PDF Files/Teacher Fragment1.pdf";
                fileName = "Teacher Fragment 1";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new TeacherFrag1()).commit();
                break;
            case R.id.teacher_fragment_2:
                filePath = "/Teacher PDF Files/Teacher Fragment2.pdf";
                fileName = "Teacher Fragment 2";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new TeacherFrag2()).commit();
                break;
            case R.id.teacher_fragment_3:
                filePath = "/Teacher PDF Files/Teacher Fragment3.pdf";
                fileName = "Teacher Fragment 3";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new TeacherFrag3()).commit();
                break;
            case R.id.teacher_fragment_4:
                filePath = "/Teacher PDF Files/Teacher Fragment4.pdf";
                fileName = "Teacher Fragment 4";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new TeacherFrag4()).commit();
                break;
            case R.id.teacher_fragment_5:
                filePath = "/Teacher PDF Files/Teacher Fragment5.pdf";
                fileName = "Teacher Fragment 5";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new TeacherFrag5()).commit();
                break;
            case R.id.teacher_fragment_6:
                filePath = "/Teacher PDF Files/Teacher Fragment6.pdf";
                fileName = "Teacher Fragment 6";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new TeacherFrag6()).commit();
                break;
            case R.id.teacher_fragment_7:
                filePath = "/Teacher PDF Files/Teacher Fragment7.pdf";
                fileName = "Teacher Fragment 7";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new TeacherFrag7()).commit();
                break;
            case R.id.teacher_fragment_8:
                filePath = "/Teacher PDF Files/Teacher Fragment8.pdf";
                fileName = "Teacher Fragment 8";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new TeacherFrag8()).commit();
                break;

            //student fragments
            case R.id.student_fragment_1:
                filePath = "/Student PDF Files/Student Fragment1.pdf";
                fileName = "Student Fragment 1";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new StudentFrag1()).commit();
                break;
            case R.id.student_fragment_2:
                filePath = "/Student PDF Files/Student Fragment2.pdf";
                fileName = "Student Fragment 2";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new StudentFrag2()).commit();
                break;
            case R.id.student_fragment_3:
                filePath = "/Student PDF Files/Student Fragment3.pdf";
                fileName = "Student Fragment 3";
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, new StudentFrag3()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(filePath);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Handle successful download URL generation
            downloadUrl = uri.toString();

            if (item.getItemId() == R.id.downloadicon) {
                Toast.makeText(DrawerActivity.this, "Downloading...", Toast.LENGTH_LONG).show();

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.format("%s", fileName));

                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);

            }
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.e(TAG, "Error getting download URL: " + exception.getMessage());
        });
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
