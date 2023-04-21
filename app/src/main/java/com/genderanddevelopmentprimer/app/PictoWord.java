package com.genderanddevelopmentprimer.app;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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

public class PictoWord extends AppCompatActivity {
    private Map<String, Object> map;
    LinearLayout gameLayout;
    LinearLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictoword);

        gameLayout = findViewById(R.id.ll_game);
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        DocumentReference question = fStore.collection("game").document("questions");
        question.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    map = document.getData();
                    getRandomQuestion(map, fStore, gameLayout, params);
                }
            }
        });
    }

    private void getRandomQuestion(Map<String, Object> map, FirebaseFirestore fStore, LinearLayout gameLayout, LinearLayout.LayoutParams params) {
        DocumentReference documentReference = fStore.collection("game").document("questions");
        documentReference.addSnapshotListener(this, (value, error) -> {
            List<Integer> prevRandomNumbers = new ArrayList<>();
            Random rand = new Random();
            int randomNumber;
            do {
                randomNumber = rand.nextInt(map.size()) + 1;
            } while (prevRandomNumbers.contains(randomNumber));
            prevRandomNumbers.add(randomNumber);
            if (prevRandomNumbers.size() > 10) {
                prevRandomNumbers.remove(0);
            }
            displayQuestion(value, randomNumber, fStore, gameLayout, params);
        });
    }

    private void displayQuestion(DocumentSnapshot value, int randomNumber, FirebaseFirestore fStore, LinearLayout gameLayout, LinearLayout.LayoutParams params) {
        TextView questionTV = new TextView(this);
        questionTV.setText(value.getString("Q" + randomNumber));
        questionTV.setTextSize(50);
        questionTV.setPadding(200, 200, 200, 200);
        questionTV.setBackgroundColor(Color.GRAY);
        questionTV.setLayoutParams(params);
        gameLayout.addView(questionTV);
        getAnswer(randomNumber, fStore, gameLayout);
    }

    private void getAnswer(int randomNumber, FirebaseFirestore fStore, LinearLayout gameLayout) {
        DocumentReference answer = fStore.collection("game").document("answers");
        answer.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task1.getResult();
                if (documentSnapshot.exists()) {
                    String answerVal = documentSnapshot.getString("ans" + randomNumber);
                    displayAnswer(answerVal, gameLayout);
                }
            }
        });
    }

    private void displayAnswer(String answerVal, LinearLayout gameLayout) {
        char[] answerArray = answerVal.toCharArray();
        List<Character> charList = new ArrayList<>();

        // Add characters from answer to list
        for (char c : answerArray) {
            charList.add(c);
        }

        // Add unique random characters to list
        Random randomLetters = new Random();
        while (charList.size() < 12) {
            char randomChar = (char) (randomLetters.nextInt(26) + 'a');
            charList.add(randomChar);
        }
        // Shuffle the list of characters
        Collections.shuffle(charList);
        displayAnswerLayout(answerVal, charList, gameLayout);
    }

    private void displayAnswerLayout(String answerVal, List<Character> charList, LinearLayout gameLayout) {
        // Create a horizontal LinearLayout for the EditText and the backspace button
        LinearLayout editTextLayout = new LinearLayout(this);
        editTextLayout.setPadding(0, 300, 0, 0);
        editTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        editTextLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        InputFilter filter = new InputFilter.AllCaps();

        EditText answerBox = new EditText(this);
        answerBox.setInputType(InputType.TYPE_CLASS_TEXT);
        answerBox.setEnabled(false);
        answerBox.setMinWidth(320);
        answerBox.setFilters(new InputFilter[]{filter});
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        answerBox.setLayoutParams(editTextParams);
        editTextLayout.addView(answerBox);
        Button backspaceButton = new Button(this);
        backspaceButton.setText("⌫");
        backspaceButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        backspaceButton.setOnClickListener(v -> {
            String currentText = answerBox.getText().toString();
            if (currentText.length() > 0) {
                answerBox.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
        editTextLayout.addView(backspaceButton);
        gameLayout.addView(editTextLayout);

        // Create a grid of buttons for the characters
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(4);
        gridLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        for (int i = 0; i < charList.size(); i++) {
            Button button = new Button(this);
            button.setText(charList.get(i).toString());
            button.setOnClickListener(v -> {
                String currentText = answerBox.getText().toString();
                if (currentText.length() < answerVal.length()) {
                    answerBox.setText(String.format("%s%s", currentText, button.getText().toString()));
                }
                if (answerBox.getText().length() == answerVal.length()) {
                    checkAnswer(answerBox.getText().toString(), answerVal, gameLayout);
                }
            });
            gridLayout.addView(button);
        }

        gameLayout.addView(gridLayout);
    }

    private void checkAnswer(String input, String answer, LinearLayout gameLayout) {
        if (input.equalsIgnoreCase(answer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            gameLayout.removeAllViews();
            getRandomQuestion(map, FirebaseFirestore.getInstance(), gameLayout, params);
        } else {
            Toast.makeText(this, "Incorrect, try again!", Toast.LENGTH_SHORT).show();
            clearAnswerBox(gameLayout);
        }
    }

    private void clearAnswerBox(LinearLayout gameLayout) {
        EditText answerBox = (EditText) ((LinearLayout) gameLayout.getChildAt(1)).getChildAt(0);
        answerBox.setText("");
    }
}