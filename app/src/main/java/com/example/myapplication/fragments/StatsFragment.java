package com.example.myapplication.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.

 */
public class StatsFragment extends Fragment {

    int games[] = {10, 20};
    String gameStrings[] = {"Won", "Lost"};
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;
    private GameStats mGameStats;

    public StatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setupPieChart() {
        //Populating a list of PieEntries
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(games[0], gameStrings[0]));
        pieEntries.add(new PieEntry(games[1], gameStrings[1]));


        PieDataSet dataSet = new PieDataSet(pieEntries, getResources().getString(R.string.pie_label));
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextSize(15f);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(117, 208, 211));
        colors.add(Color.rgb(118, 100, 201));
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);

        //get the chart
        PieChart chart = (PieChart) getView().findViewById(R.id.chart);
        chart.setData(data);
        Description description = new Description();
        description.setText("FFA Mode");
        description.setTextSize(20);
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
        DatabaseReference soloStats = FirebaseDatabase.getInstance().getReference("/users/" + mUserId + "/soloStats" );;
        DatabaseReference ffaStats = FirebaseDatabase.getInstance().getReference("/users/" + mUserId + "/ffaStats");;
        setupPieChart();
    }
}
