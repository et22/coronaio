package com.example.myapplication.loaders;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.utils.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

public class DetectedActivityIntentService extends IntentService {
    //TODO automatic
    protected static final String TAG = DetectedActivityIntentService.class.getSimpleName();
    private ArrayList<ActivityRecognitionResult> activityUpdates= new ArrayList<>();

    public DetectedActivityIntentService() {
        super(TAG);
        // Log.d(TAG,TAG + "DetectedActivityIntentService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Log.d(TAG,TAG + "onCreate()");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,TAG + "onHandleIntent()");
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        if(TrackingService.isActivityVisible()){
            if(activityUpdates.size() != 0){
                for(int i =0; i<activityUpdates.size(); i++){
                    List<DetectedActivity> detectedActivities = activityUpdates.get(i).getProbableActivities();
                    for (DetectedActivity activity : detectedActivities) {
                        Log.d("detectact", "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
                        broadcastActivity(activity);
                    }
                }
                activityUpdates.clear();
            }
            //Once the arraylist is empty, we can broadcast the current activity update
            List<DetectedActivity> detectedActivities = result.getProbableActivities();
            for (DetectedActivity activity : detectedActivities) {
                Log.d("detectact", "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
                broadcastActivity(activity);
            }
        }else{
            activityUpdates.add(result);
        }

    }

    private void broadcastActivity(DetectedActivity activity) {
        // Log.d(TAG,TAG+ "broadcastActivity()");
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
