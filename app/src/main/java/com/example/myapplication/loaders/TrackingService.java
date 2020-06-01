package com.example.myapplication.loaders;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.R;
import com.example.myapplication.MapsActivity;
import com.example.myapplication.utils.Constants;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TrackingService extends Service {
    public static final long UPDATE_INTERVAL = 5000;
    private static final long FAST_INTERVAL = 5000;
    //TODO automatic variables for automatic
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    public static ArrayList<LocationResult> locationUpdates = new ArrayList<>();

    public TrackingService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("gps", "9");
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        Log.d("gps", "10");
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_INTERVAL);
        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback(){
        // Possibly initialize intendservice here
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d("gps", "11");
            Intent requestIntent = new Intent (getApplicationContext(), LocationIntentService.class);
            requestIntent.putExtra(LocationIntentService.REQUEST_IN, locationResult);
            startService(requestIntent);
        }

    };
    public static ArrayList<LocationResult> getLocationUpdates(){
        return locationUpdates;
    }


    // This determines if the MapsActivity is visible
    private static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityOn() {
        activityVisible = true;
    }

    public static void activityDestroyed() {
        activityVisible = false;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //TODO automatic
      /*  if(intent.getIntExtra(Constants.REQUEST_ACTIVITY, 0) == Constants.ACTIVITY_RECOGNITION){
            Log.d("TrackingService", "herere");
            // We initialize the variables we need to "requestActivityUpdates"
            mActivityRecognitionClient = new ActivityRecognitionClient(this);
            Intent mIntentService = new Intent(this, DetectedActivityIntentService.class);
            mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
            requestActivityUpdatesHandler();
        }*/

        //createNotification(intent);
        return START_STICKY;
    }

    //TODO automatic
    // request updates and set up callbacks for success or failure
    public void requestActivityUpdatesHandler() {
        Log.d("TrackingService", "requestActivityUpdatesHandler()");
        if(mActivityRecognitionClient != null){
            Log.d("TrackingService", "in requestActivityUpdatesHandler()");
            Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                    Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                    mPendingIntent);

            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d("TAG", "Successfully requested activity updates");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "Requesting activity updates failed to start");
                }
            });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO automatic
        // need to remove the request to Google play services. Brings down the connection.
        removeActivityUpdatesHandler();
    }

    //TODO automatic
    // remove updates and set up callbacks for success or failure
    public void removeActivityUpdatesHandler() {
        if(mActivityRecognitionClient != null){
            Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                    mPendingIntent);
            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d("TAG", "Removed activity updates successfully!");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TAG", "Failed to remove activity updates!");
                }
            });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
