package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class Quiz extends AppCompatActivity {

    FirebaseFirestore fStore;
    String val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        setBackground();

        fStore = FirebaseFirestore.getInstance();

        val = getIntent().getExtras().getString("Value");

        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Button btn = new Button(this);
        btn.setText("Submit");
        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20,20,20,50);

        DocumentReference docRef = fStore.collection("questions").document(val);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        DocumentReference documentReference = fStore.collection("questions").document(val);
                        documentReference.addSnapshotListener(Quiz.this, (value, error) -> {
                            for (int i = 1; i <= Objects.requireNonNull(map).size(); i++) {

                                TextView tv = new TextView(Quiz.this);
                                tv.setId(i);
                                tv.setText(String.format("%d. %s", i, Objects.requireNonNull(value).getString("Q" + i)));
                                tv.setTextSize(17);
                                tv.setBackgroundColor(Color.parseColor("#95808080"));
                                tv.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                EditText ans = new EditText(Quiz.this);
                                ans.setId(View.generateViewId());
                                ans.setHint("Answer " + i);
                                ans.setInputType(InputType.TYPE_CLASS_TEXT);
                                ans.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                                linearLayout.addView(tv);
                                linearLayout.addView(ans);
                            }

                            //---adds the button---
                            linearLayout.addView(btn);
                        });
                    }
                }
            }
        });

        sv.addView(linearLayout);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Quiz.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

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