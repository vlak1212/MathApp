package com.example.ocr2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    private ImageView img;
    private static final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView img = findViewById(R.id.splashImage);
        TextView appTitle = findViewById(R.id.appTitle);
        String text = "Math App";
        SpannableString spannableString = new SpannableString(text);

        int[] colors = new int[]{
                Color.parseColor("#FF6F61"),
                Color.parseColor("#2EC4B6"),
                Color.parseColor("#FFD166"),
                Color.parseColor("#A9DEF9"),
                Color.parseColor("#EF476F"),
                Color.parseColor("#06D6A0"),
                Color.parseColor("#B2B1CF"),
                Color.parseColor("#E63946")
        };


        for (int i = 0; i < text.length(); i++) {
            spannableString.setSpan(
                    new ForegroundColorSpan(colors[i % colors.length]),
                    i, i + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        appTitle.setText(spannableString);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
