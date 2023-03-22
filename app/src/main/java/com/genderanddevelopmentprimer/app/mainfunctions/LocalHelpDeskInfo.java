package com.genderanddevelopmentprimer.app.mainfunctions;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LocalHelpDeskInfo extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    String municipality, province;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_help_desk_info);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 50);
        linearLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 100);

        DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
        documentReference.get().addOnSuccessListener(documentSnapshot -> {

            municipality = documentSnapshot.getString("municipality");
            province = documentSnapshot.getString("province");

            DocumentReference verifyHelpDeskProvincial = fStore.collection("helpDesk").document(province);

            verifyHelpDeskProvincial.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        DocumentReference hotlinesPro = fStore.collection("helpDesk").document(province);
                        hotlinesPro.addSnapshotListener(LocalHelpDeskInfo.this, (value, error) -> {

                            TextView provinceName = new TextView(LocalHelpDeskInfo.this);
                            provinceName.setText(province + " Province");
                            provinceName.setTextSize(35);
                            provinceName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            provinceName.setBackgroundColor(Color.parseColor("#95808080"));
                            provinceName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            TextView tvProLabel = new TextView(LocalHelpDeskInfo.this);
                            tvProLabel.setText("Provincial Police Station:");
                            tvProLabel.setTextSize(20);
//                            tvProLabel.setBackgroundColor(Color.parseColor("#95808080"));
                            tvProLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            tvProLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            TextView tvPro = new TextView(LocalHelpDeskInfo.this);
                            tvPro.setText(String.format(value.getString("Provincial Police Station")));
                            tvPro.setTextSize(25);
                            tvPro.setBackgroundColor(Color.parseColor("#95808080"));
                            tvPro.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            tvPro.setLayoutParams(params);

                            TextView tvProLabel1 = new TextView(LocalHelpDeskInfo.this);
                            tvProLabel1.setText("Provincial Social Welfare & Development Office:");
                            tvProLabel1.setTextSize(20);
//                            tvProLabel1.setBackgroundColor(Color.parseColor("#95808080"));
                            tvProLabel1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            tvProLabel1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            TextView tvPro1 = new TextView(LocalHelpDeskInfo.this);
                            tvPro1.setText(String.format(value.getString("Provincial Social Welfare & Development Office")));
                            tvPro1.setTextSize(25);
                            tvPro1.setBackgroundColor(Color.parseColor("#95808080"));
                            tvPro1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            tvPro1.setLayoutParams(params);

                            linearLayout.addView(provinceName);
                            linearLayout.addView(tvProLabel);
                            linearLayout.addView(tvPro);
                            linearLayout.addView(tvProLabel1);
                            linearLayout.addView(tvPro1);
                        });
                    }
                }

                DocumentReference verifyHelpDesk = fStore.collection("helpDesk").document(province).collection(municipality).document("Hotline");

                verifyHelpDesk.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot document1 = task1.getResult();
                        if (document1.exists()) {
                            DocumentReference hotlines = fStore.collection("helpDesk").document(province).collection(municipality).document("Hotline");
                            hotlines.addSnapshotListener(LocalHelpDeskInfo.this, (value1, error) -> {

                                TextView cityName = new TextView(LocalHelpDeskInfo.this);
                                cityName.setText(municipality);
                                cityName.setTextSize(35);
                                cityName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                cityName.setBackgroundColor(Color.parseColor("#95808080"));
                                cityName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                TextView tvLabel = new TextView(LocalHelpDeskInfo.this);
                                tvLabel.setText("City Police Station:");
                                tvLabel.setTextSize(20);
//                                tvLabel.setBackgroundColor(Color.parseColor("#95808080"));
                                tvLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tvLabel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                TextView tv = new TextView(LocalHelpDeskInfo.this);
                                tv.setText(String.format(value1.getString("City Police Station")));
                                tv.setTextSize(25);
                                tv.setBackgroundColor(Color.parseColor("#95808080"));
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tv.setLayoutParams(params);

                                TextView tvLabel1 = new TextView(LocalHelpDeskInfo.this);
                                tvLabel1.setText("City Social Welfare and Development Office:");
                                tvLabel1.setTextSize(20);
//                                tvLabel.setBackgroundColor(Color.parseColor("#95808080"));
                                tvLabel1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tvLabel1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                TextView tv1 = new TextView(LocalHelpDeskInfo.this);
                                tv1.setText(String.format(value1.getString("City Social Welfare & Development Office")));
                                tv1.setTextSize(25);
                                tv1.setBackgroundColor(Color.parseColor("#95808080"));
                                tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tv1.setLayoutParams(params);

                                TextView tvLabel2 = new TextView(LocalHelpDeskInfo.this);
                                tvLabel2.setText("Public Attorney's Office:");
                                tvLabel2.setTextSize(20);
//                                tvLabel.setBackgroundColor(Color.parseColor("#95808080"));
                                tvLabel2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tvLabel2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                TextView tv2 = new TextView(LocalHelpDeskInfo.this);
                                tv2.setText(String.format(value1.getString("Public Attorney's Office")));
                                tv2.setTextSize(25);
                                tv2.setBackgroundColor(Color.parseColor("#95808080"));
                                tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tv2.setLayoutParams(params);

                                linearLayout.addView(cityName);
                                linearLayout.addView(tvLabel);
                                linearLayout.addView(tv);
                                linearLayout.addView(tvLabel1);
                                linearLayout.addView(tv1);
                                linearLayout.addView(tvLabel2);
                                linearLayout.addView(tv2);
                            });
                        }
                    }
                });
            });

        });

        sv.addView(linearLayout);

        this.addContentView(sv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }
}