package com.example.day2day;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        String content = getIntent().getStringExtra("item_content");
        TextView textView = findViewById(R.id.full_screen_text);
        textView.setText(content);
    }
}