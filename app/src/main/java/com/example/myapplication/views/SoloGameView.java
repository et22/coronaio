package com.example.myapplication.views;

import android.animation.TimeAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.myapplication.GameOver;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MapsActivity;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Random;
import android.animation.TimeAnimator;
        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.drawable.Drawable;
        import android.util.AttributeSet;
        import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.R;
import com.example.myapplication.loaders.TrackingService;
import com.example.myapplication.models.Player;
import com.example.myapplication.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;


/**
 * Solo Game Animation
 * */
public class SoloGameView extends View {


    /**
     * Class representing the state of a Corona
     */
    private static class Corona {
        private float x;
        private float y;
        private float scale;
        private float alpha;
        private float speed;
    }

    //new game instance variables
    private Intent serviceIntent;
    private Location squoPlayerLocation=null;
    private Location prevPlayerLocation=null;
    private MapsActivity mapsActivity;
    private VisibleRegion visibleRegion;
    private Point playerScreenLocation=null;
    private Point previousScreenLocation=null;
    private Context soloContext;
    private View mapView;
    private long prevUpdate = System.currentTimeMillis();
    private long nextUpdate =  System.currentTimeMillis()+ 5000;

    //old animation instance variables
    private static final int BASE_SPEED_DP_PER_S = 0;
    private static final int COUNT = 32;
    private static final int SEED = 1337;

    /** The minimum scale of a Corona */
    private static final float SCALE_MIN_PART = 0.45f;
    /** How much of the scale that's based on randomness */
    private static final float SCALE_RANDOM_PART = 0.55f;
    /** How much of the alpha that's based on the scale of the Corona */
    private static final float ALPHA_SCALE_PART = 0.5f;
    /** How much of the alpha that's based on randomness */
    private static final float ALPHA_RANDOM_PART = 0.5f;

    private final Corona[] mCoronas = new Corona[COUNT];
    public final Player player = new Player();
    private final Random mRnd = new Random(SEED);

    private TimeAnimator mTimeAnimator;
    private Drawable mDrawable;
    private Drawable mDrawablePlayer;

    private float mBaseSpeed;
    private float mBaseSize;
    private long mCurrentPlayTime;

    /** @see View#View(Context) */
    public SoloGameView(Context context) {
        super(context);
        soloContext = context;
        init();
    }

    /** @see View#View(Context, AttributeSet) */
    public SoloGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        soloContext = context;
        init();
    }

    /** @see View#View(Context, AttributeSet, int) */
    public SoloGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        soloContext = context;
        init();
    }

    private void init() {
        mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.corona1);
        mDrawablePlayer = ContextCompat.getDrawable(getContext(), R.drawable.macrophage);
        mBaseSize = Math.max(mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight()) / 2f;
        mBaseSpeed = BASE_SPEED_DP_PER_S * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        // The Coronating position is dependent on the size of the view,
        // which is why the model is initialized here, when the view is measured.
        for (int i = 0; i < mCoronas.length; i++) {
            final Corona Corona = new Corona();
            initializeCorona(Corona, width, height);
            mCoronas[i] = Corona;
        }
        initializePlayer(player, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int viewHeight = getHeight();
        //drawing the coronas in mCoronas
        //drawing the player at current Location
        if(player!=null){
            drawPlayerHelper(canvas, viewHeight);
            Log.d("123TAGTAGTAG", "drewPlayer");
        }
        for (final Corona Corona : mCoronas) {
            // Ignore the Corona if it's outside of the view bounds
            final float CoronaSize = Corona.scale * mBaseSize;
            if (Corona.y + CoronaSize < 0 || Corona.y - CoronaSize > viewHeight) {
                continue;
            }

            // Save the current canvas state
            final int save = canvas.save();

            // Move the canvas to the center of the Corona
            canvas.translate(Corona.x, Corona.y);

            // Rotate the canvas based on how far the Corona has moved
            //final float progress = (Corona.y + CoronaSize) / viewHeight;
            if(mRnd.nextBoolean()) {
                canvas.rotate(30 * mRnd.nextLong());
            }
            else{
                canvas.rotate(-30 * mRnd.nextLong());
            }
            // Prepare the size and alpha of the drawable
            final int size = Math.round(CoronaSize);
            mDrawable.setBounds(-size, -size, size, size);
            mDrawable.setAlpha(Math.round(255 * Corona.alpha));
            // Draw the Corona to the canvas
            mDrawable.draw(canvas);

            // Restore the canvas to it's previous position and rotation
            canvas.restoreToCount(save);
        }
    }

    private void drawPlayerHelper(Canvas canvas, int viewHeight) {
        // Ignore the Corona if it's outside of the view bounds
        final float playerSize = player.scale * mBaseSize;
        // Save the current canvas state
        final int save = canvas.save();
        // Move the canvas to the center of the player
        canvas.translate(player.x, player.y);
        // Rotate the canvas based on how far the player has moved
        //final float progress = (player.y + playerSize) / viewHeight;
        long currTime = System.currentTimeMillis();
        final float progress = (((float)(currTime-player.spawnTime))/(float)10000.0);
        Log.d("TAGGATGAT", progress + "");
        canvas.rotate(360 * progress);
        // Prepare the size and alpha of the drawable
        final int size = Math.round(playerSize);
        mDrawablePlayer.setBounds(-size, -size, size, size);
        mDrawablePlayer.setAlpha(Math.round(255 * player.alpha));
        // Draw the player to the canvas
        mDrawablePlayer.draw(canvas);
        // Restore the canvas to it's previous position and rotation
        canvas.restoreToCount(save);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        serviceIntent = new Intent(getContext(), TrackingService.class);
        Log.d("gps", "3");
        getContext().startService(serviceIntent);
        mapsActivity = (MapsActivity) getContext();
        LocalBroadcastManager.getInstance(mapsActivity).registerReceiver(mLocationBroadcast,
                new IntentFilter(Constants.BROADCAST_LOCATION));
        //set quit button click listener
        //mQuitButton onclick
        mapsActivity.mQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mapsActivity, MainActivity.class);
                onDetachedFromWindow();
                soloContext.startActivity(intent);
            }
        });
        //start countdown
        new CountDownTimer(11000, 1000){
            public void onTick(long millisUntilFinished){
                long seconds = (millisUntilFinished/1000)%60;
                mapsActivity.mCountDown.setText(String.format("%02d",seconds));
            }
            public  void onFinish(){
                //mapsActivity.getSupportFragmentManager().beginTransaction().show(mapsActivity.mapFragment).commit();
                mapsActivity.mScoreTextView.setVisibility(TextView.VISIBLE);
                mapsActivity.mQuitButton.setVisibility(Button.VISIBLE);
                mapsActivity.mLeaderBoard.setVisibility(TextView.VISIBLE);
                mapsActivity.mCountDown.setVisibility(TextView.INVISIBLE);
                if(player!=null) player.alpha = ALPHA_SCALE_PART * player.scale + ALPHA_RANDOM_PART * mRnd.nextFloat();
                if(mCoronas!=null) {
                    for (Corona Corona : mCoronas)
                        Corona.alpha = ALPHA_SCALE_PART * Corona.scale + ALPHA_RANDOM_PART * mRnd.nextFloat();
                }
                new CountDownTimer(151000, 1000){
                    public void onTick(long millisUntilFinished){
                        long minutes = (millisUntilFinished/1000)/60;
                        long seconds = (millisUntilFinished/1000)%60;
                        mapsActivity.mLeaderBoard.setText(String.format("%d",minutes) + ":" + String.format("%02d",seconds));
                    }
                    public  void onFinish(){
                        Intent intent = new Intent(mapsActivity, GameOver.class);
                        assert player != null;
                        intent.putExtra(Constants.SCORE_EXTRA, player.score);
                        intent.putExtra("wl", false);
                        intent.putExtra("GameType", "Solo");
                        onDetachedFromWindow();
                        soloContext.startActivity(intent);
                    }
                }.start();
            }
        }.start();

        //mapview
       mapView = mapsActivity.getSupportFragmentManager().findFragmentById(R.id.map).getView();

        //set up time animation
        mTimeAnimator = new TimeAnimator();
        mTimeAnimator.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                if (!isLaidOut()) {
                    // Ignore all calls before the view has been measured and laid out.
                    return;
                }
                updateState(deltaTime);
                invalidate();
            }
        });
        mTimeAnimator.start();
    }

    BroadcastReceiver mLocationBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.BROADCAST_LOCATION)) {


                Log.d("gps received location", "in broadcast location");
                boolean firstIter=false;
                if(squoPlayerLocation!=null) prevPlayerLocation = squoPlayerLocation;
                else firstIter=true;
                squoPlayerLocation = intent.getParcelableExtra(Constants.LOCATION_POSITION_EXTRA);
                final LatLng here = new LatLng(squoPlayerLocation.getLatitude(), squoPlayerLocation.getLongitude());
                if(firstIter) {
                    mapsActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 19));
                    //visibleRegion = mapsActivity.mMap.getProjection().getVisibleRegion();
                    /*if (mapView.getViewTreeObserver().isAlive()) {
                        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                //removelistener
                                //mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                // set map viewport
                                // CENTER is LatLng object with the center of the map
                                //mapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 19));
                                // ! you can query Projection object here

                                playerScreenLocation = mapsActivity.mMap.getProjection().toScreenLocation(here);
                            }
                        });
                    }*/
                    mapsActivity.mMap.getUiSettings().setZoomGesturesEnabled(false);
                    mapsActivity.mMap.getUiSettings().setRotateGesturesEnabled(false);
                    mapsActivity.mMap.getUiSettings().setTiltGesturesEnabled(false);
                    mapsActivity.mMap.getUiSettings().setScrollGesturesEnabled(true);
                }
                Log.d("TAGTAGTAG", "herher");
                if(playerScreenLocation!=null){
                    previousScreenLocation = playerScreenLocation;
                }
                Projection projection = mapsActivity.mMap.getProjection();
                playerScreenLocation = projection.toScreenLocation(here);
                //mapsActivity.mMap.addMarker(new MarkerOptions().position(projection.fromScreenLocation(playerScreenLocation).title(""));
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mTimeAnimator!=null) {
            mTimeAnimator.cancel();
            mTimeAnimator.setTimeListener(null);
            mTimeAnimator.removeAllListeners();
            mTimeAnimator = null;
        }
    }

    /**
     * Pause the animation if it's running
     */
    public void pause() {
        if (mTimeAnimator != null && mTimeAnimator.isRunning()) {
            // Store the current play time for later.
            mCurrentPlayTime = mTimeAnimator.getCurrentPlayTime();
            mTimeAnimator.pause();
        }
    }

    /**
     * Resume the animation if not already running
     */
    public void resume() {
        if (mTimeAnimator != null && mTimeAnimator.isPaused()) {
            mTimeAnimator.start();
            // Why set the current play time?
            // TimeAnimator uses timestamps internally to determine the delta given
            // in the TimeListener. When resumed, the next delta received will the whole
            // pause duration, which might cause a huge jank in the animation.
            // By setting the current play time, it will pick of where it left off.
            mTimeAnimator.setCurrentPlayTime(mCurrentPlayTime);
        }
    }

    /**
     * Progress the animation by moving the Coronas based on the elapsed time
     * @param deltaMs time delta since the last frame, in millis
     */
    private void updateState(float deltaMs) {
        // Converting to seconds since PX/S constants are easier to understand
        final float deltaSeconds = deltaMs / 1000f;
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();

        for (final Corona Corona : mCoronas) {
            // Move the Corona based on the elapsed time and it's speed
            Corona.y -= Corona.speed * deltaSeconds;

            // If the Corona is completely outside of the view bounds after
            // updating it's position, recycle it.
            final float size = Corona.scale * mBaseSize;
            if (Corona.y + size < 0) {
                initializeCorona(Corona, viewWidth, viewHeight);
            }
        }
        if(playerScreenLocation != null){
            Log.d("TAGTAGTAG", "playerScreenLoc not nulll");
            //update player location
            if(previousScreenLocation!=null) {
                player.x = player.x + (playerScreenLocation.x - previousScreenLocation.x) * deltaMs / TrackingService.UPDATE_INTERVAL;
                player.y = player.y + (playerScreenLocation.y - previousScreenLocation.y) * deltaMs / TrackingService.UPDATE_INTERVAL;
            }
            else{
                player.x = playerScreenLocation.x;
                player.y = playerScreenLocation.y;
            }
            //check for intersection
            int count = 0;
            for (final Corona Corona : mCoronas) {
                count++;
                final float size = player.scale * mBaseSize;
                if(player.x+size>Corona.x&&player.x-size<Corona.x&&player.y+size>Corona.y&&player.y-size<Corona.y){
                    if(Corona.alpha!=0.0) {
                        Corona.alpha = 0;
                        player.score += 1;
                        player.scale += .2;
                        //update score textview
                        Log.d("TAGTAGGAT", ""+player.score);
                        mapsActivity.mScoreTextView.setText(getResources().getString(R.string.score_text) + player.score);
                        //check if Got all 32 viruses
                        if(player.score>=COUNT){
                            Intent intent = new Intent(mapsActivity, GameOver.class);
                            intent.putExtra(Constants.SCORE_EXTRA, player.score);
                            intent.putExtra("GameType", "Solo");
                            onDetachedFromWindow();
                            soloContext.startActivity(intent);
                        }
                    }
                }
            }
        }
    }

    /**
     * Initialize the given Corona by randomizing it's position, scale and alpha
     * @param Corona the Corona to initialize
     * @param viewWidth the view width
     * @param viewHeight the view height
     */
    private void initializeCorona(Corona Corona, int viewWidth, int viewHeight) {
        // Set the scale based on a min value and a random multiplier
        Corona.scale = SCALE_MIN_PART;// + SCALE_RANDOM_PART * mRnd.nextFloat();

        // Set X to a random value within the width of the view
        Corona.x = viewWidth * mRnd.nextFloat();

        // Set the Y position
        // Coronat at the bottom of the view
        Corona.y = viewHeight*mRnd.nextFloat();
        // The Y value is in the center of the Corona, add the size
        // to make sure it Coronats outside of the view bound
        //Corona.y += Corona.scale * mBaseSize;
        // Add a random offset to create a small delay before the
        // Corona appears again.
        //Corona.y += viewHeight * mRnd.nextFloat() / 4f;

        // The alpha is determined by the scale of the Corona and a random multiplier.
        Corona.alpha = 0;
        // The bigger and brighter a Corona is, the faster it moves
        Corona.speed = mBaseSpeed * Corona.alpha * Corona.scale;
    }

    private void initializePlayer(Player player, int viewWidth, int viewHeight) {
        player.scale = SCALE_MIN_PART + SCALE_RANDOM_PART;
        // Set X to a random value within the width of the view
        //player.x = viewWidth * mRnd.nextFloat();
        // Set the Y position
        // put player at center of screen
        //player.y = viewHeight/2;
        // The Y value is in the center of the Corona, add the size
        // to make sure it Coronats outside of the view bound
        //player.y += player.scale * mBaseSize;
        // Add a random offset to create a small difference so players don't regen in the same spot
        //player.y += viewHeight * mRnd.nextFloat() / 4f;
        // get time of spawn to track progress
        player.spawnTime = System.currentTimeMillis();
        //set score to zero
        player.score = 0;
        // The alpha is determined by the scale of the Corona and a random multiplier.
        //player.alpha = ALPHA_SCALE_PART * player.scale + ALPHA_RANDOM_PART * mRnd.nextFloat();
        player.alpha = 0;
        // The bigger and brighter a Corona is, the faster it moves
        player.speed = mBaseSpeed * player.alpha * player.scale;
        //update player score on the screen
        mapsActivity.mScoreTextView.setText(getResources().getString(R.string.score_text) + player.score);
    }
}

