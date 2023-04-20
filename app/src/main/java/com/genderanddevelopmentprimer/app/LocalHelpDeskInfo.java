package com.genderanddevelopmentprimer.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LocalHelpDeskInfo extends AppCompatActivity {
    LinearLayout linearLayout;
    LinearLayout.LayoutParams paramsLabel, paramsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_help_desk_info);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        linearLayout = findViewById(R.id.ll_helpdesk);
        linearLayout.setGravity(Gravity.CENTER);

        paramsInfo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsInfo.setMargins(0, 0, 0, 30);

        paramsLabel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsLabel.setMargins(0, 50, 0, 50);

        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.addSnapshotListener((userSnapshot, error) -> {
            if (userSnapshot != null) {
                String municipality = userSnapshot.getString("municipality");
                String province = userSnapshot.getString("province");

                DocumentReference provinceRef = db.collection("helpDesk").document(province);
                provinceRef.get().addOnSuccessListener(provinceSnapshot -> {
                    if (provinceSnapshot == null || !provinceSnapshot.exists()) {
                        TextView err = new TextView(LocalHelpDeskInfo.this);
                        err.setText("Hotline Unavailable for " + province);
                        err.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        err.setBackgroundColor(Color.LTGRAY);
                        err.setTextSize(25);
                        err.setLayoutParams(paramsLabel);
                        linearLayout.addView(err);
                    } else {
                        TextView provinceTextViewLabel = new TextView(this);
                        provinceTextViewLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        provinceTextViewLabel.setText(province);
                        provinceTextViewLabel.setBackgroundColor(Color.LTGRAY);
                        provinceTextViewLabel.setLayoutParams(paramsLabel);
                        provinceTextViewLabel.setTextSize(30);
                        linearLayout.addView(provinceTextViewLabel);

                        for (Map.Entry<String, Object> entry : provinceSnapshot.getData().entrySet()) {
                            TextView provinceTextView = new TextView(this);
                            provinceTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            provinceTextView.setText(entry.getKey() + ":");
                            provinceTextView.setLayoutParams(paramsInfo);
                            linearLayout.addView(provinceTextView);

                            TextView provinceTextView1 = new TextView(this);
                            provinceTextView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            provinceTextView1.setText(entry.getValue().toString());
                            provinceTextView1.setLayoutParams(paramsInfo);
                            provinceTextView1.setPaintFlags(provinceTextView1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            provinceTextView1.setTextSize(25);
                            linearLayout.addView(provinceTextView1);

                            provinceTextView1.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", entry.getValue().toString(), null))));

                        }
                    }
                });

                String cityCollectionName = String.format("%s City", municipality);
                final DocumentReference[] cityRef = {db.collection("helpDesk").document(province).collection(cityCollectionName).document("Hotline")};
                cityRef[0].get().addOnSuccessListener(citySnapshot -> {
                    if (!citySnapshot.exists()) {
                        cityRef[0] = db.collection("helpDesk").document(province).collection(municipality).document("Hotline");
                        cityRef[0].get().addOnSuccessListener(city1Snapshot -> {
                            if (city1Snapshot.getData() == null) {
                                TextView err = new TextView(LocalHelpDeskInfo.this);
                                err.setText("Hotline Unavailable for " + municipality);
                                err.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                err.setBackgroundColor(Color.LTGRAY);
                                err.setTextSize(25);
                                err.setLayoutParams(paramsLabel);
                                linearLayout.addView(err);
                            } else {
                                displayCityInfo(municipality, city1Snapshot);
                            }
                        });
                    } else {
                        displayCityInfo(cityCollectionName, citySnapshot);
                    }
                });
            }
        });
    }

    private void displayCityInfo(String city, DocumentSnapshot citySnapshot) {
        TextView cityTextViewLabel = new TextView(this);
        cityTextViewLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cityTextViewLabel.setText(city);
        cityTextViewLabel.setBackgroundColor(Color.LTGRAY);
        cityTextViewLabel.setLayoutParams(paramsLabel);
        cityTextViewLabel.setTextSize(30);
        linearLayout.addView(cityTextViewLabel);

        for (Map.Entry<String, Object> entry : citySnapshot.getData().entrySet()) {
            TextView cityTextView = new TextView(this);
            cityTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            cityTextView.setText(entry.getKey() + ":");
            cityTextView.setLayoutParams(paramsInfo);
            linearLayout.addView(cityTextView);

            TextView cityTextView1 = new TextView(this);
            cityTextView1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            cityTextView1.setText(entry.getValue().toString());
            cityTextView1.setLayoutParams(paramsInfo);
            cityTextView1.setPaintFlags(cityTextView1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            cityTextView1.setTextSize(25);
            linearLayout.addView(cityTextView1);

            cityTextView1.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", entry.getValue().toString(), null))));
        }
    }
}

