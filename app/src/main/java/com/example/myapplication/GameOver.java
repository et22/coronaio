package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.utils.Constants;

public class GameOver extends AppCompatActivity {
    private View mWonScore;
    private Button mBackHome, mPlayAgain;
    private TextView myScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        getSupportActionBar().hide();
        mWonScore = findViewById(R.id.win_score_text_view);
        mBackHome = findViewById(R.id.button_home);
        mPlayAgain = findViewById(R.id.button_play_again);
        myScore = findViewById(R.id.score_text_view);
        Intent intent = getIntent();
        int score = intent.getIntExtra(Constants.SCORE_EXTRA,0);
        myScore.setText(score + "");
        mBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        mPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }
}
