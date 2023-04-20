package com.genderanddevelopmentprimer.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class GameMenu extends AppCompatActivity {
    ImageButton btnJumbledWords, btnPictoWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        btnJumbledWords = findViewById(R.id.btn_jumbledwords);
        btnPictoWord = findViewById(R.id.btn_pictoword);

        btnJumbledWords.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), JumbledWords.class)));
        btnPictoWord.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PictoWord.class)));

    }
}