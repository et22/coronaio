package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MapsActivity;
import com.example.myapplication.R;
import com.example.myapplication.views.CoronaAnimationView;


public class HomeFragment extends Fragment {
    //animation
    private CoronaAnimationView mAnimationView;

    //ui components
    private Button mPlayFFA, mPlaySolo;

    Window mWindow;
    public HomeFragment(Window window){
        this.mWindow = window;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mAnimationView = (CoronaAnimationView) view.findViewById(R.id.animated_view);


        //initializing ui components
        mPlayFFA = view.findViewById(R.id.button_play_ffa);
        mPlaySolo = view.findViewById(R.id.button_play_solo);

        //creating onclicks for ffa and solo
        onClickFFA();
        onClickSolo();
    }
    private void onClickSolo() {
        mPlaySolo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("GameType", "Solo");
                startActivity(intent);
            }
        });
    }

    private void onClickFFA() {
        mPlayFFA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra("GameType", "FFA");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mAnimationView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAnimationView.pause();
    }
}
