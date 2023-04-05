package com.genderanddevelopmentprimer.app;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        LinearLayout linearLayout = findViewById(R.id.ll_aboutus);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(this);
        textView.setText("Sample Text");
        textView.setTextSize(25);
        textView.setLayoutParams(params);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        linearLayout.addView(textView);
    }
}