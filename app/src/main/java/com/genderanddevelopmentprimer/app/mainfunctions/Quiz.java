package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        setBackground();

        fStore = FirebaseFirestore.getInstance();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        //create scrollview layout
        ScrollView sv = new ScrollView(this);

        //create a layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //create a button
        Button btn = new Button(Quiz.this);
        btn.setText("Submit");
        btn.setLayoutParams(params);

        //create a layout param for the layout
        LinearLayout.LayoutParams layoutParam =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParam.setMargins(20, 100, 20, 20);

        ViewGroup.LayoutParams svParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        sv.addView(layout, layoutParam);

        DocumentReference docRef = fStore.collection("questions").document("questionnaire");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();

                        DocumentReference documentReference = fStore.collection("questions").document("questionnaire");
                        documentReference.addSnapshotListener(Quiz.this, (value, error) -> {
                            for (int i = 1; i <= Objects.requireNonNull(map).size(); i++) {
                                assert value != null;

                                TextView tv = new TextView(Quiz.this);
                                tv.setId(i);
                                tv.setText(value.getString("Q" + i));
                                tv.setTextSize(20);
                                tv.setBackgroundColor(Color.parseColor("#95808080"));
                                tv.setLayoutParams(params2);

                                EditText ans = new EditText(Quiz.this);
                                ans.setId(View.generateViewId());
                                ans.setHint("Answer " + i);
                                ans.setInputType(InputType.TYPE_CLASS_TEXT);
                                ans.setLayoutParams(params);

                                layout.addView(tv);
                                layout.addView(ans);
                            }

                            //---adds the button---
                            layout.addView(btn);
                        });
                    }
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Quiz.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        this.addContentView(sv, svParams);
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