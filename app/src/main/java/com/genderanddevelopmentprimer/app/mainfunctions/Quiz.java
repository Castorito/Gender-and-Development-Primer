package com.genderanddevelopmentprimer.app.mainfunctions;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.genderanddevelopmentprimer.app.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Quiz extends AppCompatActivity {

    FirebaseFirestore fStore;
    EditText ans;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        setBackground();

        fStore = FirebaseFirestore.getInstance();

        //identify which fragment a button is clicked
        String val = getIntent().getExtras().getString("identifier");

        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Button btn = new Button(this);
        btn.setText("Submit");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(700, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        btn.setLayoutParams(params);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50, 50, 50, 50);

        DocumentReference docRef = fStore.collection("questions").document(val);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> map = document.getData();
                    DocumentReference documentReference = fStore.collection("questions").document(val);
                    documentReference.addSnapshotListener(Quiz.this, (value, error) -> {
                        for (int i = 1; i <= Objects.requireNonNull(map).size(); i++) {

                            TextView tv = new TextView(Quiz.this);
                            tv.setText(String.format("%d. %s", i, Objects.requireNonNull(value).getString("Q" + i)));
                            tv.setTextSize(17);
                            tv.setBackgroundColor(Color.parseColor("#95808080"));
                            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            ans = new EditText(Quiz.this);
                            ans.setId(i);
                            ans.setHint("answer");
                            ans.setInputType(InputType.TYPE_CLASS_TEXT);
                            ans.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            linearLayout.addView(tv);
                            linearLayout.addView(ans);
                        }

                        //---adds the button---
                        linearLayout.addView(btn);
                    });
                }
            }
        });

        sv.addView(linearLayout);

        //submit button
        btn.setOnClickListener(view -> {

            ArrayList<String> answerList = new ArrayList<>();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            DocumentReference documentReference1 = fStore.collection("answers").document(val);
            //check then count the numbers inside the database
            documentReference1.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //scan all answers in database if matches with the answers given or not
                        Map<String, Object> map = document.getData();
                        DocumentReference documentReference = fStore.collection("answers").document(val);
                        documentReference.addSnapshotListener(Quiz.this, (value, error) -> {
                            for (int i = 1; i <= Objects.requireNonNull(map).size(); i++) {

                                EditText answers = findViewById(i);

                                if (answers.getText().toString().equals(Objects.requireNonNull(value).getString("ans" + i))) {
                                    score++;
                                    answerList.add(String.format("%d. %s  ⁄", i, answers.getText().toString()));
                                }else{
                                    answerList.add(String.format("%d. %s  ×", i, answers.getText().toString()));
                                }
                            }
                            AlertDialog.Builder showScore = new AlertDialog.Builder(this);
                            showScore.setTitle(String.format("Your score is: %d", score));
                            showScore.setItems(answerList.toArray(new CharSequence[0]),null);
                            showScore.setCancelable(false);
                            showScore.setNegativeButton("Done", (dialog, which) -> finish());
                            AlertDialog show = showScore.create();
                            show.show();

                            show.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); //create a new one
                            layoutParams.gravity = Gravity.END; //this is layout_gravity
                            layoutParams.rightMargin = 10;

                            show.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(layoutParams);
                        });
                    }
                }
            });
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