package com.genderanddevelopmentprimer.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class JumbledWords extends AppCompatActivity {

    int randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jumbled_words);

        LinearLayout layout = findViewById(R.id.jw_gamelayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("game").document("jumbledWords");
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> map = document.getData();
                    DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("game").document("jumbledWords");
                    documentReference1.addSnapshotListener((value, error) -> {
                        Random questionNumber = new Random();
                        randomNumber = questionNumber.nextInt(Objects.requireNonNull(map).size()) + 1;

                        String data = value.getString("word" + randomNumber); // Replace "field_name" with the name of the field you want to retrieve
                        char[] charArray = data.toCharArray(); // Convert the string to a char array
                        List<Character> charList = new ArrayList<>(); // Create a list to store the characters
                        for (char c : charArray) {
                            charList.add(c); // Add each character to the list
                        }
                        Collections.shuffle(charList); // Shuffle the list of characters
                        StringBuilder shuffledString = new StringBuilder(); // Create a StringBuilder to store the shuffled characters
                        for (char c : charList) {
                            shuffledString.append(c); // Append each character to the StringBuilder
                        }
                        String shuffledText = shuffledString.toString();// Convert the StringBuilder to a string

                        if (shuffledText == data) {
                            Collections.shuffle(charList);
                            shuffledString = new StringBuilder(); // Create a StringBuilder to store the shuffled characters
                            for (char c : charList) {
                                shuffledString.append(c); // Append each character to the StringBuilder
                            }
                            shuffledText = shuffledString.toString();

                            addQuestion(shuffledText, data, layout);
                        } else {
                            addQuestion(shuffledText, data, layout);
                        }
                    });
                }
            }
        });
    }

    private void addQuestion(String question, String answer, LinearLayout layout) {

        // Create the question TextView
        TextView questionTV = new TextView(JumbledWords.this);
        questionTV.setText(question);
        questionTV.setTextSize(50);
        questionTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        questionTV.setAllCaps(true);
        questionTV.setPadding(0, 0, 0, 100);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionTV.setLayoutParams(params);
        layout.addView(questionTV);

        // Create the answer EditText
        EditText answerBox = new EditText(JumbledWords.this);
        answerBox.setHint("Answer");
        LinearLayout.LayoutParams answerParams = new LinearLayout.LayoutParams(550, ViewGroup.LayoutParams.WRAP_CONTENT);
        answerBox.setLayoutParams(answerParams);
        layout.addView(answerBox);

        // Set a text changed listener for the answer EditText
        answerBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if the answer is correct when the user types in the EditText
                if (answerBox.getText().toString().equalsIgnoreCase(answer)) {
                    Toast.makeText(JumbledWords.this, "Correct", Toast.LENGTH_SHORT).show();
                    JumbledWords.this.finish();
                    JumbledWords.this.startActivity(new Intent(JumbledWords.this.getIntent()));
                    JumbledWords.this.overridePendingTransition(0, 0);
                }
            }
        });
    }

}