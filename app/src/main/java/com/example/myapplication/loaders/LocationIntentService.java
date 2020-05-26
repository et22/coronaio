package com.example.myapplication.loaders;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.utils.Constants;
import com.google.android.gms.location.LocationResult;

import java.util.ArrayList;

public class LocationIntentService extends IntentService {
    public static final String REQUEST_IN = "REQUEST_IN";
    public LocationIntentService() {
        super("LocationIntentService");
        Log.d("gps", "constructor for location intent service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("gps", "intentisnull");
        assert intent != null;
        LocationResult locationResult = intent.getParcelableExtra(REQUEST_IN);
        ArrayList<LocationResult> locationUpdates = TrackingService.getLocationUpdates();
        Intent releaseIntent;
        Log.d("gps", "NOTinIsActivityVisible");
        if(TrackingService.isActivityVisible()){
            Log.d("gps", "inIsActivityVisible");
            //if the arraylist is not empty, broadcast the locationresults saved
            if(locationUpdates.size() != 0){
                for(int i=0; i<locationUpdates.size();i++){
                    Log.d("LocationIntentService", "inforloop");
                    releaseIntent = new Intent(Constants.BROADCAST_LOCATION);
                    releaseIntent.putExtra(Constants.LOCATION_POSITION_EXTRA, locationUpdates.get(i).getLastLocation());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(releaseIntent);
                }
                locationUpdates.clear();
            }
            //Once the Arraylist is empty, broadcast the current locationResult
            releaseIntent = new Intent(Constants.BROADCAST_LOCATION);
            releaseIntent.putExtra(Constants.LOCATION_POSITION_EXTRA, locationResult.getLastLocation());
            LocalBroadcastManager.getInstance(this).sendBroadcast(releaseIntent);
        }else{
            Log.d("gps", "inNOTisActivityVisible");
            locationUpdates.add(locationResult);
            Log.d("LocationIntentService", locationUpdates.toString());
        }

    }
}
