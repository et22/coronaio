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

import java.util.ArrayList;

public class GameOver extends AppCompatActivity {
    private View mWonScore;
    private Button mBackHome, mPlayAgain;
    private TextView myScore;
    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;
    private boolean mWin;
    private String mGameType;



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
        mGameType = from_intent.getStringExtra("GameType");
        mDatabase = FirebaseDatabase.getInstance().getReference("/users/" + mUserId + "/Stats" );
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
                String key;
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot: dataSnapshot.getChildren()){
                        GameStats test = myDataSnapshot.getValue(GameStats.class);
                        assert test != null;
                        if(test.getGameType().equals(mGameType)){
                            GameStats newGameStats;
                            key = myDataSnapshot.getKey();
                            if(mWin){
                                newGameStats = new GameStats(test.getGamesWon() + 1, test.getGamesLost(), mGameType);
                            }else{
                                newGameStats = new GameStats(test.getGamesWon(), test.getGamesLost() + 1, mGameType);
                            }
                            mDatabase.child(key).setValue(newGameStats);
                        }
                        //key = myDataSnapshot.getKey();
                        //mGameStats.add(test);
                    }
                }
//                for(int i=0; i<mGameStats.size(); i++){
//                    GameStats gameStats = mGameStats.get(i);
//                    GameStats newGameStats;
//                    if(gameStats.getGameType().equals(mGameType)){
//                        if(mWin){
//                            newGameStats = new GameStats(gameStats.getGamesWon() + 1, gameStats.getGamesLost(), mGameType);
//                        }else{
//                            newGameStats = new GameStats(gameStats.getGamesWon(), gameStats.getGamesLost() + 1, mGameType);
//                        }
//                        mDatabase.child(key).setValue(newGameStats);
//                    }
//                }

//                GameStats newGameStats;
//                if(mWin){
//                    newGameStats = new GameStats(mGameStats.getGamesWon() + 1, mGameStats.getGamesLost());
//                }else{
//                    newGameStats = new GameStats(mGameStats.getGamesWon(), mGameStats.getGamesLost() + 1);
//                }
//
//                mDatabase.setValue(newGameStats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(onceListen);
    }
}