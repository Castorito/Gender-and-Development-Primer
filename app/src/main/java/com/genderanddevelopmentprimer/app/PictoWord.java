package com.genderanddevelopmentprimer.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

public class PictoWord extends AppCompatActivity {
    int randomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictoword);

        LinearLayout gameLayout = findViewById(R.id.ll_game);
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        DocumentReference question = fStore.collection("game").document("questions");
        question.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> map = document.getData();
                    DocumentReference documentReference = fStore.collection("game").document("questions");
                    documentReference.addSnapshotListener(this, (value, error) -> {
                        Random questionNumber = new Random();
                        randomNumber = questionNumber.nextInt(Objects.requireNonNull(map).size()) + 1;

                        TextView questionTV = new TextView(this);
                        questionTV.setText(Objects.requireNonNull(value).getString("Q" + randomNumber));
                        questionTV.setTextSize(20);
                        questionTV.setLayoutParams(params);

                        gameLayout.addView(questionTV);
                    });

                    DocumentReference answer = fStore.collection("game").document("answers");
                    answer.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            if (documentSnapshot.exists()) {
                                DocumentReference getAnswer = fStore.collection("game").document("answers");
                                getAnswer.addSnapshotListener(this, (value1, error1) -> {
                                    String answerVal = Objects.requireNonNull(value1).getString("ans" + randomNumber);
                                    char[] answerArray = Objects.requireNonNull(answerVal).toCharArray();
                                    // Create a list of characters from the answer array
                                    List<Character> charList = new ArrayList<>();
                                    for (char c : answerArray) {
                                        charList.add(c);
                                    }
                                    // Add 8 more random letters to the list
                                    Random randomLetters = new Random();
                                    while (charList.size() < 16) {
                                        char randomChar = (char) (randomLetters.nextInt(26) + 'a');
                                        charList.add(randomChar);
                                    }
                                    // Shuffle the list of characters
                                    Collections.shuffle(charList);
                                    // Create a horizontal LinearLayout for the EditText and the backspace button
                                    LinearLayout editTextLayout = new LinearLayout(PictoWord.this);
                                    editTextLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    editTextLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    ));

                                    InputFilter filter = new InputFilter.AllCaps();

                                    EditText answerBox = new EditText(PictoWord.this);
                                    answerBox.setInputType(InputType.TYPE_CLASS_TEXT);
                                    answerBox.setEnabled(false);
                                    answerBox.setFilters(new InputFilter[]{filter});
                                    LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1
                                    );
                                    answerBox.setLayoutParams(editTextParams);
                                    editTextLayout.addView(answerBox);

                                    // Create the backspace button and set its layout parameters
                                    Button backspaceButton = new Button(PictoWord.this);
                                    backspaceButton.setText("⌫");
                                    backspaceButton.setOnClickListener(v -> {
                                        String currentText = answerBox.getText().toString();
                                        if (!currentText.isEmpty()) {
                                            answerBox.setText(currentText.substring(0, currentText.length() - 1));
                                        }
                                    });
                                    LinearLayout.LayoutParams backspaceParams = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    backspaceParams.gravity = Gravity.CENTER_VERTICAL;
                                    backspaceButton.setLayoutParams(backspaceParams);
                                    editTextLayout.addView(backspaceButton);

                                    gameLayout.addView(editTextLayout);

                                    int buttonCount = 0;

                                    LinearLayout row = new LinearLayout(PictoWord.this);
                                    row.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    ));
                                    row.setGravity(Gravity.CENTER);
                                    for (char c : charList) {
                                        Button button = new Button(PictoWord.this);
                                        button.setText(String.valueOf(c));
                                        button.setTextSize(12);
                                        button.setLayoutParams(params);

                                        button.setOnClickListener(v -> {
                                            // Add the text of the button to the EditText box
                                            String buttonText = ((Button) v).getText().toString();
                                            String currentText = answerBox.getText().toString();
                                            answerBox.setText(String.format("%s%s", currentText, buttonText));

                                            // Check if the answer is correct
                                            if (answerBox.getText().toString().equalsIgnoreCase(answerVal)) {
                                                // Do something if the answer is correct
                                                Toast.makeText(PictoWord.this, "Correct!", Toast.LENGTH_SHORT).show();
                                                PictoWord.this.finish();
                                                PictoWord.this.startActivity(new Intent(PictoWord.this.getIntent()));
                                                PictoWord.this.overridePendingTransition(0, 0);
                                            }
                                        });

                                        row.addView(button);
                                        buttonCount++;
                                        if (buttonCount == 4) {
                                            gameLayout.addView(row);
                                            row = new LinearLayout(PictoWord.this);
                                            row.setLayoutParams(new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                            ));
                                            row.setGravity(Gravity.CENTER);
                                            buttonCount = 0;
                                        }
                                    }
                                    if (buttonCount > 0) {
                                        for (int i = buttonCount; i < 4; i++) {
                                            View spacer = new View(PictoWord.this);
                                            spacer.setLayoutParams(new LinearLayout.LayoutParams(
                                                    0,
                                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                                    1
                                            ));
                                            row.addView(spacer);
                                        }
                                        gameLayout.addView(row);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}