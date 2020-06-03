package com.example.myapplication.views;

import android.animation.TimeAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.GameOver;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MapsActivity;
import com.example.myapplication.R;
import com.example.myapplication.loaders.TrackingService;
import com.example.myapplication.models.Player;
import com.example.myapplication.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


/**
 * Solo Game Animation
 * */
public class FFAGameView extends View {


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

    //database references
    DatabaseReference reference ;
    DatabaseReference ffaReference;
    private String currentuser;

    //new game instance variables
    private Intent serviceIntent;
    private Location squoPlayerLocation=null;
    private Location prevPlayerLocation=null;
    private MapsActivity mapsActivity;
    private VisibleRegion visibleRegion;
    private Point playerScreenLocation=null;
    private Point previousScreenLocation=null;
    private Context soloContext;
    private ArrayList<Player> mPlayers = new ArrayList<Player>();
    private HashSet<String> haveEaten = new HashSet<String>();
    public CountDownTimer timers;
    private long prevUpdate = System.currentTimeMillis();
    private long nextUpdate =  System.currentTimeMillis()+ 5000;
    private int onUpdate = 0;
    public int winiter=0;
    public boolean won= false;

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
    private final Player player = new Player();
    private final Random mRnd = new Random(SEED);

    private TimeAnimator mTimeAnimator;
    private Drawable mDrawable;
    private Drawable mDrawablePlayer;

    private float mBaseSpeed;
    private float mBaseSize;
    private long mCurrentPlayTime;

    /** @see View#View(Context) */
    public FFAGameView(Context context) {
        super(context);
        soloContext = context;
        init();
    }

    /** @see View#View(Context, AttributeSet) */
    public FFAGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        soloContext = context;
        init();
    }

    /** @see View#View(Context, AttributeSet, int) */
    public FFAGameView(Context context, AttributeSet attrs, int defStyle) {
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
            Log.d("TAGTAGTAG", "drewPlayer");
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
        for(Player player1: mPlayers)
        {
            if(player.alpha>0) {
                drawOtherPlayerHelper(canvas, viewHeight, player1);
            }
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

    private void drawOtherPlayerHelper(Canvas canvas, int viewHeight, Player otherPlayer) {
        // Ignore the Corona if it's outside of the view bounds
        final float playerSize = otherPlayer.scale * mBaseSize;
        // Save the current canvas state
        final int save = canvas.save();
        // Move the canvas to the center of the player
        canvas.translate(otherPlayer.x, otherPlayer.y);
        // Rotate the canvas based on how far the player has moved
        //final float progress = (player.y + playerSize) / viewHeight;
        //long currTime = System.currentTimeMillis();
        //final float progress = (((float)(currTime-player.spawnTime))/(float)10000.0);
        //Log.d("TAGGATGAT", progress + "");
        //canvas.rotate(360 * progress);
        // Prepare the size and alpha of the drawable
        final int size = Math.round(playerSize);
        mDrawablePlayer.setBounds(-size, -size, size, size);
        mDrawablePlayer.setAlpha(Math.round(255 * otherPlayer.alpha));
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
        //get database reference, set up ffa game and addValueEventListener
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        ffaReference = reference.child("ffa");
        ValueEventListener playerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                mPlayers = new ArrayList<Player>();
                //int maxScore = player.score;
                //Player bestPlayer = player;
                // String key = reference.child("users").child(player.userid).child("userName").getKey();
                //String uname = reference.child("users").child(bestPlayer.userid).child("userName").child(key).toString();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    if(!child.getKey().equals(currentuser)) {
                        Player player1 = child.getValue(Player.class);
                        player1.x = convertDpToPixel(player1.dpX, mapsActivity);
                        player1.y = convertDpToPixel(player1.dpY, mapsActivity);
                        mPlayers.add(player1);
                       /* if(player1.score>maxScore){
                            maxScore = player1.score;
                            key = reference.child("users").child(player1.userid).child("userName").getKey();
                            uname = reference.child("users").child(bestPlayer.userid).child("userName").child(key).getKey();
                        }*/
                    }
                }
                //mapsActivity.mLeaderBoard.setText(getResources().getString(R.string.leaderboard) + uname);
                // ...
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ffaReference.addValueEventListener(playerListener);
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
                //mapsActivity.mLeaderBoard.setVisibility(TextView.VISIBLE);
                mapsActivity.mCountDown.setVisibility(TextView.INVISIBLE);
                if(player!=null) player.alpha = ALPHA_SCALE_PART * player.scale + ALPHA_RANDOM_PART * mRnd.nextFloat();
                if(mCoronas!=null) {
                    for (FFAGameView.Corona Corona : mCoronas)
                        Corona.alpha = ALPHA_SCALE_PART * Corona.scale + ALPHA_RANDOM_PART * mRnd.nextFloat();
                }
            }
        }.start();

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
                prevUpdate=nextUpdate;
                nextUpdate=System.currentTimeMillis();
                onUpdate = 0;

                boolean firstIter=false;
                if(squoPlayerLocation!=null) prevPlayerLocation = squoPlayerLocation;
                else firstIter=true;
                squoPlayerLocation = intent.getParcelableExtra(Constants.LOCATION_POSITION_EXTRA);
                LatLng here = new LatLng(squoPlayerLocation.getLatitude(), squoPlayerLocation.getLongitude());
                if(firstIter) {
                    mapsActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(here, 19));
                    mapsActivity.mMap.getUiSettings().setZoomGesturesEnabled(false);
                    mapsActivity.mMap.getUiSettings().setRotateGesturesEnabled(false);
                    mapsActivity.mMap.getUiSettings().setTiltGesturesEnabled(false);
                    mapsActivity.mMap.getUiSettings().setScrollGesturesEnabled(true);
                    visibleRegion = mapsActivity.mMap.getProjection().getVisibleRegion();
                }
                Log.d("TAGTAGTAG", "herher");
                if(playerScreenLocation!=null){
                    previousScreenLocation = playerScreenLocation;
                }
                playerScreenLocation = mapsActivity.mMap.getProjection().toScreenLocation(here);
            }
        }
    };


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLocationBroadcast != null) {
            //stopService(new Intent(this, TrackingService.class));
            LocalBroadcastManager.getInstance(this.soloContext).unregisterReceiver(mLocationBroadcast);
        }
        if(mTimeAnimator!=null) {
            mTimeAnimator.cancel();
            mTimeAnimator.setTimeListener(null);
            mTimeAnimator.removeAllListeners();
            mTimeAnimator = null;
        }
        //delete player from ffa database
        reference.child("ffa").child(currentuser).setValue(null);
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
        if(won==true){
            winiter++;
            if(winiter>20){
                Intent intent = new Intent(mapsActivity, GameOver.class);
                intent.putExtra(Constants.SCORE_EXTRA, player.score);
                intent.putExtra("GameType", "FFA");
                intent.putExtra("wl", true);
                onDetachedFromWindow();
                soloContext.startActivity(intent);
            }
        }

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
            int updateTime = 25;
            if(onUpdate<updateTime&&previousScreenLocation!=null){
                player.x = (float) (player.x + (previousScreenLocation.x - player.x)*1/updateTime);
                player.y = (float) (player.y+ (previousScreenLocation.y-player.y)*1/updateTime);
                onUpdate++;
            }
            if(previousScreenLocation!=null) {
                player.x = player.x + (playerScreenLocation.x - previousScreenLocation.x) * deltaMs / TrackingService.UPDATE_INTERVAL;
                player.y = player.y + (playerScreenLocation.y - previousScreenLocation.y) * deltaMs / TrackingService.UPDATE_INTERVAL;
            }
            else{
                player.x = playerScreenLocation.x;
                player.y = playerScreenLocation.y;
            }
            player.dpX = convertPixelsToDp(player.x, mapsActivity);
            player.dpY = convertPixelsToDp(player.y, mapsActivity);
            //update player position in the database
            reference.child("ffa").child(currentuser).setValue(player);

            //check for intersection with virus
            int count = 0;
            final float size = player.scale * mBaseSize;
            final float dpsize = convertPixelsToDp(size, mapsActivity);
            for (final Corona Corona : mCoronas) {
                count++;
                if(player.x+size>Corona.x&&player.x-size<Corona.x&&player.y+size>Corona.y&&player.y-size<Corona.y){
                    if(Corona.alpha!=0.0) {
                        Corona.alpha = 0;
                        player.score += 1;
                        player.scale += .1;
                        //update score textview
                        Log.d("TAGTAGGAT", ""+player.score);
                        mapsActivity.mScoreTextView.setText(getResources().getString(R.string.score_text)+ player.score);
                        //check if Got all 32 viruses
                       /* if(player.score>=COUNT){
                            Intent intent = new Intent(mapsActivity, GameOver.class);
                            intent.putExtra(Constants.SCORE_EXTRA, player.score);
                            onDetachedFromWindow();
                            soloContext.startActivity(intent);
                        }*/
                    }
                }
            }
            //check intersection with other player
            for(Player otherPlayer: mPlayers) {
                final float otherDpsize = convertPixelsToDp(otherPlayer.scale * mBaseSize, mapsActivity);

                if (player.dpX + dpsize > otherPlayer.dpX + otherDpsize && player.dpX - dpsize < otherPlayer.dpX - otherDpsize && player.dpY + dpsize > otherPlayer.dpY + otherDpsize && player.dpY - dpsize < otherPlayer.dpY - otherDpsize) {
                    if (otherPlayer.scale < player.scale) {
                        if (!haveEaten.contains(otherPlayer.userid)) {
                            player.scale += otherPlayer.scale / 2;
                            player.score += 4;
                            player.score += otherPlayer.score / 2;
                            haveEaten.add(otherPlayer.userid);
                            int numEaten = 0;
                            for (Player otherPlayer1 : mPlayers) {
                                if (haveEaten.contains(otherPlayer1.userid)) {
                                    numEaten++;
                                }
                            }
                            if (numEaten == mPlayers.size()) {
                                won = true;
                            }
                        }
                    }
                }
                if (player.dpX + dpsize < otherPlayer.dpX + otherDpsize && player.dpX - dpsize > otherPlayer.dpX - otherDpsize && player.dpY + dpsize < otherPlayer.dpY + otherDpsize && player.dpY - dpsize > otherPlayer.dpY - otherDpsize) {
                    if (otherPlayer.scale > player.scale) {
                        Intent intent = new Intent(mapsActivity, GameOver.class);
                        intent.putExtra(Constants.SCORE_EXTRA, player.score);
                        intent.putExtra("GameType", "FFA");
                        intent.putExtra("wl", false);
                        onDetachedFromWindow();
                        soloContext.startActivity(intent);
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
        player.x = viewWidth * mRnd.nextFloat();
        // Set the Y position
        // put player at center of screen
        player.y = viewHeight/2;
        // The Y value is in the center of the Corona, add the size
        // to make sure it Coronats outside of the view bound
        //player.y += player.scale * mBaseSize;
        // Add a random offset to create a small difference so players don't regen in the same spot
        player.y += viewHeight * mRnd.nextFloat() / 4f;
        // get time of spawn to track progress
        player.spawnTime = System.currentTimeMillis();
        //set score to zero
        player.score = 0;
        // The alpha is determined by the scale of the Corona and a random multiplier.
        player.alpha = 0;
        // The bigger and brighter a Corona is, the faster it moves
        player.speed = mBaseSpeed * player.alpha * player.scale;
        player.userid = currentuser;

        player.dpX = convertPixelsToDp(player.x, mapsActivity);
        player.dpY = convertPixelsToDp(player.y, mapsActivity);
        reference.child("ffa").child(currentuser).setValue(player);
        //update player score on the screen
        mapsActivity.mScoreTextView.setText(getResources().getString(R.string.score_text) + player.score);
    }
    //from https://stackoverflow.com/questions/4605527/converting-pixels-to-dp
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}