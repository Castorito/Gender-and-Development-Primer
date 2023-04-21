package com.genderanddevelopmentprimer.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
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
import java.util.Random;

public class JumbledWords extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jumbled_words);

        LinearLayout layout = findViewById(R.id.jw_gamelayout);

        retrieveGameData(layout);
    }

    private void retrieveGameData(LinearLayout layout) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("game").document("jumbledWords");
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> map = document.getData();
                    setUpQuestionListener(map, layout);
                }
            }
        });
    }

    private void setUpQuestionListener(Map<String, Object> map, LinearLayout layout) {
        DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("game").document("jumbledWords");
        documentReference1.addSnapshotListener((value, error) -> {
            List<Integer> prevRandomNumbers = new ArrayList<>();
            Random questionNumber = new Random();
            int randomNumber;
            do {
                randomNumber = questionNumber.nextInt(map.size()) + 1;
            } while (prevRandomNumbers.contains(randomNumber));
            prevRandomNumbers.add(randomNumber);
            if (prevRandomNumbers.size() > 5) {
                prevRandomNumbers.remove(0);
            }
            String data = value.getString("word" + randomNumber);
            shuffleAndAddQuestion(data, layout);
        });
    }

    private void shuffleAndAddQuestion(String data, LinearLayout layout) {
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

        if (shuffledText.equals(data)) {
            Collections.shuffle(charList);
            shuffledString = new StringBuilder(); // Create a StringBuilder to store the shuffled characters
            for (char c : charList) {
                shuffledString.append(c); // Append each character to the StringBuilder
            }
            shuffledText = shuffledString.toString();
        }

        addQuestion(shuffledText, data, layout);
    }

    private void addQuestion(String question, String answer, LinearLayout layout) {
        // Create the question TextView
        TextView questionTV = new TextView(JumbledWords.this);
        questionTV.setText(question);
        questionTV.setTextSize(40);
        questionTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        questionTV.setAllCaps(true);
        questionTV.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 100);
        questionTV.setLayoutParams(params);
        layout.addView(questionTV);

        // Create the answer EditText
        InputFilter filter = new InputFilter.AllCaps();

        EditText answerBox = new EditText(JumbledWords.this);
        answerBox.setHint("Answer");
        answerBox.setHintTextColor(Color.DKGRAY);
        answerBox.setTextColor(Color.BLACK);
        answerBox.setFilters(new InputFilter[]{filter});
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
                // Check the answer and update the score
                if (s.toString().equalsIgnoreCase(answer)) {
                    Toast.makeText(JumbledWords.this, "Correct!", Toast.LENGTH_SHORT).show();
                    answerBox.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout.removeView(questionTV);
                            layout.removeView(answerBox);
                            // Retrieve a new question
                            retrieveGameData(layout);
                        }
                    }, 1500); // delay for 1.5 seconds
                }
            }
        });
    }
}