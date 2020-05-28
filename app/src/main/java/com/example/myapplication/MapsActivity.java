package com.example.myapplication;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.loaders.TrackingService;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.views.CoronaAnimationView;
import com.example.myapplication.views.FFAGameView;
import com.example.myapplication.views.SoloGameView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private int PERMISSION_REQUEST_CODE = 1;
    public TextView mScoreTextView;
    public TextView mLeaderBoard;
    public String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        from = getIntent().getStringExtra("GameType");
        //instantiate text views
        mScoreTextView = findViewById(R.id.score_text_view);
        mLeaderBoard = findViewById(R.id.leaderboard_text);

        createTrackingService();
    }

    private void createTrackingService() {
        //letting tracking service know that the app is visible
        TrackingService.activityOn();
        //check permissions and start tracking activity
        if (!checkPermission())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        else
            startAnimation();
    }

    private void startAnimation(){
        if(from.equals("FFA")){
            FFAGameView mAnimationView= (FFAGameView) findViewById(R.id.ffa_animated_view);
        }
        else if(from.equals("Solo")){
            SoloGameView mAnimationView = (SoloGameView) findViewById(R.id.solo_animated_view);
        }
    }

    //starts location tracking service
    /**
     * Check location tracking permissions
     * @return
     */
    private boolean checkPermission(){
        int allowed = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (allowed == PackageManager.PERMISSION_GRANTED) return true;
        else return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startAnimation();
        } else {
            finish();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
