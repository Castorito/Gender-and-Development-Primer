package com.genderanddevelopmentprimer.app;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    FirebaseFirestore fStore;
    String filePath, downloadUrl;
    DisplayFragment fragment;
    Bundle args;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        fStore = FirebaseFirestore.getInstance();
        fragment = new DisplayFragment();
        args = new Bundle();

        Toolbar drawerToolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(drawerToolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.activity_drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, drawerToolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();


        DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                String userType = value.getString("userType");
                if (userType != null) {
                    int menuResId = (userType.equals("Teacher")) ? R.menu.drawer_menu_teacher : R.menu.drawer_menu_student;
                    navigationView.inflateMenu(menuResId);

                    if (savedInstanceState == null) {
                        filePath = "/Common PDF Files/Common Fragment1.pdf";
                        args.putString("identifier", "cf1");
                        fragment.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, fragment).commit();

                        int checkedItemId = (userType.equals("Teacher")) ? R.id.teacher_common_fragment_1 : R.id.student_common_fragment_1;
                        navigationView.setCheckedItem(checkedItemId);
                    }
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        fragment = new DisplayFragment();
        args = new Bundle();

        switch (item.getItemId()) {
            //common fragments
            default:
            case R.id.teacher_common_fragment_1:
            case R.id.student_common_fragment_1:
                filePath = "/Common PDF Files/Common Fragment1.pdf";
                args.putString("identifier", "cf1");
                break;
            case R.id.teacher_common_fragment_2:
            case R.id.student_common_fragment_2:
                filePath = "/Common PDF Files/Common Fragment2.pdf";
                args.putString("identifier", "cf2");
                break;
            case R.id.teacher_common_fragment_3:
            case R.id.student_common_fragment_3:
                filePath = "/Common PDF Files/Common Fragment3.pdf";
                args.putString("identifier", "cf3");
                break;

            //teacher fragments
            case R.id.teacher_fragment_1:
                filePath = "/Teacher PDF Files/Teacher Fragment1.pdf";
                args.putString("identifier", "tf1");
                break;
            case R.id.teacher_fragment_2:
                filePath = "/Teacher PDF Files/Teacher Fragment2.pdf";
                args.putString("identifier", "tf2");
                break;
            case R.id.teacher_fragment_3:
                filePath = "/Teacher PDF Files/Teacher Fragment3.pdf";
                args.putString("identifier", "tf3");
                break;
            case R.id.teacher_fragment_4:
                filePath = "/Teacher PDF Files/Teacher Fragment4.pdf";
                args.putString("identifier", "tf4");
                break;
            case R.id.teacher_fragment_5:
                filePath = "/Teacher PDF Files/Teacher Fragment5.pdf";
                args.putString("identifier", "tf5");
                break;
            case R.id.teacher_fragment_6:
                filePath = "/Teacher PDF Files/Teacher Fragment6.pdf";
                args.putString("identifier", "tf6");
                break;
            case R.id.teacher_fragment_7:
                filePath = "/Teacher PDF Files/Teacher Fragment7.pdf";
                args.putString("identifier", "tf7");
                break;
            case R.id.teacher_fragment_8:
                filePath = "/Teacher PDF Files/Teacher Fragment8.pdf";
                args.putString("identifier", "tf8");
                break;

            //student fragments
            case R.id.student_fragment_1:
                filePath = "/Student PDF Files/Student Fragment1.pdf";
                args.putString("identifier", "sf1");
                break;
            case R.id.student_fragment_2:
                filePath = "/Student PDF Files/Student Fragment2.pdf";
                args.putString("identifier", "sf2");
                break;
            case R.id.student_fragment_3:
                filePath = "/Student PDF Files/Student Fragment3.pdf";
                args.putString("identifier", "sf3");
                break;
        }
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_drawer, fragment).commit();
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
                Toast.makeText(DrawerActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, new File(uri.getPath()).getName());

                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
            }
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(DrawerActivity.this, " Download Failed. " + exception.getMessage(), Toast.LENGTH_SHORT).show();
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
