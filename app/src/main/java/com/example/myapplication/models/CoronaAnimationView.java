package com.example.myapplication.models;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import java.util.Random;


/**
 * Continuous animation where Coronas slide from the bottom to the top
 * Created by Patrick Ivarsson on 7/23/17.
 * almost 100% of this code is from https://medium.com/@patrick_iv/continuous-animation-using-timeanimator-5b8a903603fb
 */
public class CoronaAnimationView extends View {

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

    private static final int BASE_SPEED_DP_PER_S = 200;
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
    private final Random mRnd = new Random(SEED);

    private TimeAnimator mTimeAnimator;
    private Drawable mDrawable;

    private float mBaseSpeed;
    private float mBaseSize;
    private long mCurrentPlayTime;

    /** @see View#View(Context) */
    public CoronaAnimationView(Context context) {
        super(context);
        init();
    }

    /** @see View#View(Context, AttributeSet) */
    public CoronaAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** @see View#View(Context, AttributeSet, int) */
    public CoronaAnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.corona1);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int viewHeight = getHeight();
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
            final float progress = (Corona.y + CoronaSize) / viewHeight;
            canvas.rotate(360 * progress);

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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimeAnimator.cancel();
        mTimeAnimator.setTimeListener(null);
        mTimeAnimator.removeAllListeners();
        mTimeAnimator = null;
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
    }

    /**
     * Initialize the given Corona by randomizing it's position, scale and alpha
     * @param Corona the Corona to initialize
     * @param viewWidth the view width
     * @param viewHeight the view height
     */
    private void initializeCorona(Corona Corona, int viewWidth, int viewHeight) {
        // Set the scale based on a min value and a random multiplier
        Corona.scale = SCALE_MIN_PART + SCALE_RANDOM_PART * mRnd.nextFloat();

        // Set X to a random value within the width of the view
        Corona.x = viewWidth * mRnd.nextFloat();

        // Set the Y position
        // Coronat at the bottom of the view
        Corona.y = viewHeight;
        // The Y value is in the center of the Corona, add the size
        // to make sure it Coronats outside of the view bound
        Corona.y += Corona.scale * mBaseSize;
        // Add a random offset to create a small delay before the
        // Corona appears again.
        Corona.y += viewHeight * mRnd.nextFloat() / 4f;

        // The alpha is determined by the scale of the Corona and a random multiplier.
        Corona.alpha = ALPHA_SCALE_PART * Corona.scale + ALPHA_RANDOM_PART * mRnd.nextFloat();
        // The bigger and brighter a Corona is, the faster it moves
        Corona.speed = mBaseSpeed * Corona.alpha * Corona.scale;
    }
}
