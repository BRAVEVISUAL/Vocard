package com.example.vocard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CardFlipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_flip);

        // chapter 데이터 가져오기
        int level = getIntent().getIntExtra("chapter", -1);
        // Word 배열 가져오기
        Word[] wordArray = (Word[]) getIntent().getSerializableExtra("wordArray");
    }
}
