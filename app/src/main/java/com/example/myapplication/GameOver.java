package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.models.GameStats;
import com.example.myapplication.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameOver extends AppCompatActivity {
    private View mWonScore;
    private Button mBackHome, mPlayAgain;
    private TextView myScore;
    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;
    private String mStat;
    private GameStats mGameStats;
    private boolean mWin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        getSupportActionBar().hide();
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();


        mWonScore = findViewById(R.id.win_score_text_view);
        mBackHome = findViewById(R.id.button_home);
        mPlayAgain = findViewById(R.id.button_play_again);
        myScore = findViewById(R.id.score_text_view);
        final Intent from_intent = getIntent();
        int score = from_intent.getIntExtra(Constants.SCORE_EXTRA,0);
        mWin = from_intent.getBooleanExtra("wl", true);
        String gameType = from_intent.getStringExtra("GameType");
        if(gameType.equals("Solo")){
            mStat = "soloStats";
        }else{
            mStat = "ffaStats";
        }
        mDatabase = FirebaseDatabase.getInstance().getReference("/users/" + mUserId + "/" + mStat);
        if(!mWin) mWonScore.setBackground(getDrawable(R.drawable.you_lost));

        saveWin();

        myScore.setText(score + "");
        mBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, MapsActivity.class);
                String from = from_intent.getStringExtra("GameType");
                intent.putExtra("GameType", from);
                startActivity(intent);
            }
        });
    }

    private void saveWin() {
        ValueEventListener onceListen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChildren()){
//                    for(DataSnapshot myDataSnapshot: dataSnapshot.getChildren()){
//                        mGameStats = myDataSnapshot.getValue(GameStats.class);
//                    }
//                }
                mGameStats = dataSnapshot.getValue(GameStats.class);
                GameStats newGameStats;
                if(mWin){
                    newGameStats = new GameStats(mGameStats.getGamesWon() + 1, mGameStats.getGamesLost());
                }else{
                    newGameStats = new GameStats(mGameStats.getGamesWon(), mGameStats.getGamesLost() + 1);
                }

                mDatabase.setValue(newGameStats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(onceListen);
    }
}