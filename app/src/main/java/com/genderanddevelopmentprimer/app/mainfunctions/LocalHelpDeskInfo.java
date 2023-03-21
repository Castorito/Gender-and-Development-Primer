package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.res.Configuration;
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

import java.util.Map;
import java.util.Objects;

public class LocalHelpDeskInfo extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    String municipality, province;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_help_desk_info);

        setBackground();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 50);
        linearLayout.setGravity(Gravity.CENTER);

        DocumentReference documentReference = fStore.collection("users").document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
        documentReference.get().addOnSuccessListener(documentSnapshot -> {

            municipality = documentSnapshot.getString("municipality");
            province = documentSnapshot.getString("province");

            DocumentReference verifyHelpDesk = fStore.collection("helpDesk").document(province).collection(municipality).document("Hotline");

            verifyHelpDesk.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        DocumentReference hotlines = fStore.collection("helpDesk").document(province).collection(municipality).document("Hotline");
                        hotlines.addSnapshotListener(LocalHelpDeskInfo.this, (value, error) -> {
                            for (int i = 0; i <= Objects.requireNonNull(map).size(); i++) {
                                TextView tv = new TextView(LocalHelpDeskInfo.this);
                                tv.setText(String.format(value.getString("GAD Hotline")));
                                tv.setTextSize(50);
                                tv.setBackgroundColor(Color.parseColor("#95808080"));
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                linearLayout.addView(tv);
                            }
                        });
                    }
                }
            });
        });

        sv.addView(linearLayout);

        this.addContentView(sv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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
}