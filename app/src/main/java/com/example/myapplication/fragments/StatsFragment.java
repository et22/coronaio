package com.example.myapplication.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.models.GameStats;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.

 */
public class StatsFragment extends Fragment {

    String[] gameStrings = {"Won", "Lost"};
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;

    public StatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setupPieChart(GameStats gameStats) {
        //Populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(gameStats.getGamesWon(), gameStrings[0]));
        pieEntries.add(new PieEntry(gameStats.getGamesLost(), gameStrings[1]));


        PieDataSet dataSet = new PieDataSet(pieEntries, getResources().getString(R.string.pie_label));
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextSize(15f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(117, 208, 211));
        colors.add(Color.rgb(118, 100, 201));
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);

        PieChart chart;
        Description description = new Description();
        //get the chart
        if(gameStats.getGameType().equals("Solo")){
            chart = (PieChart) getView().findViewById(R.id.chart);
            description.setText("Solo Mode");
        }else{
            chart = (PieChart) getView().findViewById(R.id.chart2);
            description.setText("FFA Mode");
        }
        description.setTextSize(20);
        chart.setData(data);
        chart.setDescription(description);
        chart.setNoDataText("You have not played a game yet");
        chart.animateY(2000);
        chart.invalidate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();

        retrieveStats();


    }

    private void retrieveStats() {
        DatabaseReference stats = FirebaseDatabase.getInstance().getReference("/users/" + mUserId + "/Stats");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot myDataSnapshot: dataSnapshot.getChildren()){
                        GameStats gameStats = myDataSnapshot.getValue(GameStats.class);
                        assert gameStats != null;
                        setupPieChart(gameStats);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        stats.addListenerForSingleValueEvent(listener);

    }

}
