package course.labs.graphicslab;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;


public class BubbleActivity extends Activity {

	private static final String TAG = "Lab7";

	// The Main view
	private RelativeLayout mFrame;

	// Bubble image's bitmap
	private Bitmap mBitmap;

	// Display dimensions
	private int mDisplayWidth, mDisplayHeight;

    // Gesture Detector
    private GestureDetector mGestureDetector;

    // A TextView to hold the current number of bubbles
    private TextView mBubbleCountTextView;

	// Sound variables

	// SoundPool
	private SoundPool mSoundPool;
	// ID for the bubble popping sound
	private int mSoundID;
	// Audio volume
	private float mStreamVolume;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// Set up user interface
		mFrame = (RelativeLayout) findViewById(R.id.frame);
        mBubbleCountTextView = (TextView) findViewById(R.id.countUnique);

		// Load basic bubble Bitmap
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.b64);

		Log.i(TAG, "onCreate");

	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(TAG, "onResume");

		// Manage bubble popping sound
		// Use AudioManager.STREAM_MUSIC as stream type

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		mStreamVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC)
				/ audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		// - make a new SoundPool, allowing up to 10 streams
        // Store this as mSoundPool
		mSoundPool = new SoundPool (10, AudioManager.STREAM_MUSIC, 0);
		Log.i(TAG, "soundPool created");

		// - set a SoundPool OnLoadCompletedListener that calls
		// setupGestureDetector()
		mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int i, int i1) {
				Log.i(TAG, "onLoadComplete");
				setupGestureDetector();
			}
		});

		// - load the sound from res/raw/bubble_pop.wav
        // Store this as mSoundID
		Log.i(TAG, "initializing sound");
		mSoundID = mSoundPool.load(BubbleActivity.this, R.raw.bubble_pop, 1);
		//mSoundID = mSoundPool.load("res/raw/bubble_pop.wav", 1);


	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			// Get the size of the display so this View knows where borders are
			mDisplayWidth = mFrame.getWidth();
			mDisplayHeight = mFrame.getHeight();

		}
	}

	// Set up GestureDetector
	private void setupGestureDetector() {

		mGestureDetector = new GestureDetector(this,
		new GestureDetector.SimpleOnGestureListener() {

			// If a fling gesture starts on a BubbleView then change the
			// BubbleView's velocity based on x and y velocity from
            // this gesture

			@Override
			public boolean onFling(MotionEvent event1, MotionEvent event2,
					float velocityX, float velocityY) {

				// - Implement onFling actions.
				// (See comment above for expected behaviour.)
				// You can get all Views in mFrame one at a time
				// using the ViewGroup.getChildAt() method.
				int count = mFrame.getChildCount();
				for (int i = 0; i < count; i++) {
					BubbleView bubble = (BubbleView) mFrame.getChildAt(i);
					if (bubble.intersects(event1.getX(), event1.getY()) ) {
						// you touched a bubble change velocity
						bubble.deflect(velocityX, velocityY);
						return true;
					}
				}
				return true;
			}

			// If a single tap intersects a BubbleView, then pop the BubbleView
			// Otherwise, create a new BubbleView at the tap's location and add
			// it to mFrame. Hint: Don't forget to start the movement of the
			// BubbleView.
			// Also update the number of bubbles displayed in the appropriate TextView

			@Override
			public boolean onSingleTapConfirmed(MotionEvent event) {

				// - Implement onSingleTapConfirmed actions.
                // (See comment above for expected behaviour.)
                // You can get all Views in mFrame using the
				// ViewGroup.getChildCount() method

				Log.i(TAG, "onSingleTapConfirmed");

				boolean doesIntersect = false;

				int views = mFrame.getChildCount();
				for(int i = 0; i < views; i++){
					Log.i(TAG, "view " + i + ":"+event.getX() + ":"+event.getY());
					BubbleView currBubble = (BubbleView) mFrame.getChildAt(i);
					if(currBubble.intersects(event.getX(),event.getY())){
						Log.i(TAG, "intersects on onSingleTapConfirmed");
						currBubble.stopMovement(true);
						doesIntersect = true;
					}
				}
				if(!doesIntersect){
					Log.i(TAG, "create new BubbleView on onSingleTapConfirmed");
					BubbleView newBubble = new BubbleView(BubbleActivity.this, event.getX(),event.getY());
					newBubble.startMovement();
					mFrame.addView(newBubble);
				}

				int currentViewCount = mFrame.getChildCount();
				mBubbleCountTextView.setText(currentViewCount + "");

				return true;
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// - Delegate the touch to the gestureDetector
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onPause() {

		// - Release all SoundPool resources
		mSoundPool.release();

		super.onPause();
	}

	// BubbleView is a View that displays a bubble.
	// This class handles animating, drawing, and popping amongst other actions.
	// A new BubbleView is created for each bubble on the display

	public class BubbleView extends View {

		private static final int BITMAP_SIZE = 64;
		private static final int REFRESH_RATE = 40;
		private final Paint mPainter = new Paint();
		private ScheduledFuture<?> mMoverFuture;
		private int mScaledBitmapSize;
		private Bitmap mScaledBitmap;

		// location and direction of the bubble
		private float mXPos, mYPos, mRadius;

        // Speed of bubble
        private float mDx, mDy;

        // Rotation and speed of rotation of the bubble
        private long mRotate, mDRotate;

		BubbleView(Context context, float x, float y) {
			super(context);

			// Create a new random number generator to
			// randomize size, rotation, speed and direction
			Random r = new Random();

			// Creates the bubble bitmap for this BubbleView
			createScaledBitmap(r);

			// Radius of the Bitmap
			mRadius = mScaledBitmapSize / 2;

			// Adjust position to center the bubble under user's finger
			mXPos = x - mRadius;
			mYPos = y - mRadius;

			// Set the BubbleView's speed and direction
			setSpeedAndDirection(r);

			// Set the BubbleView's rotation
			setRotation(r);

			mPainter.setAntiAlias(true);

		}

		private void setRotation(Random r) {
			// - set rotation in range [1..5]
			mRotate = r.nextInt(5) + 1;
			mDRotate = r.nextInt(5) + 1;
		}

		private void setSpeedAndDirection(Random r) {
			// - Set mDx and mDy to indicate movement direction and speed
			// Limit speed in the x and y direction to [-3..3] pixels per movement.
			mDx = r.nextInt(6) - 3;
			mDy = r.nextInt(5) - 3;
		}

		private void createScaledBitmap(Random r) {

            // - set scaled bitmap size (mScaledBitmapSize) in range [2..4] * BITMAP_SIZE
			int val = r.nextInt(2) + 2;
			mScaledBitmapSize = val * BITMAP_SIZE;

			// - create the scaled bitmap (mScaledBitmap) using size set above
			mScaledBitmap = Bitmap.createScaledBitmap(mBitmap,mScaledBitmapSize,mScaledBitmapSize,true);

		}

		// Start moving the BubbleView & updating the display
		private void startMovement() {

			// Creates a WorkerThread
			ScheduledExecutorService executor = Executors
					.newScheduledThreadPool(1);

			// Execute the run() in Worker Thread every REFRESH_RATE
			// milliseconds
			// Save reference to this job in mMoverFuture
			mMoverFuture = executor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {

					// - implement movement logic.
					// Each time this method is run the BubbleView should
					// move one step. (Use moveWhileOnScreen() to do this.)
					// If the BubbleView exits the display, stop the BubbleView's
					// Worker Thread. (Use stopMovement() to do this.) Otherwise,
					// request that the BubbleView be redrawn.
					if(!moveWhileOnScreen()){
						stopMovement(false);
						// - Update the TextView displaying the number of bubbles
						int currentViewCount = mFrame.getChildCount();
						mBubbleCountTextView.setText(currentViewCount + "");
					}else{
						postInvalidate();
					}
				}
			}, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);
		}

		// Returns true if the BubbleView intersects position (x,y)
		private synchronized boolean intersects(float x, float y) {
            float centerX = mXPos + mRadius;
            float centerY = mYPos + mRadius;

			// - Return true if the BubbleView intersects position (x,y)
			float mScaledBitmapWidth = mScaledBitmap.getWidth();
			if (mXPos <= x && x <= mXPos + mScaledBitmapWidth) {
				if (mYPos <= y && y <= mYPos + mScaledBitmapWidth) {
					return true;
				}
			}

            return false;
		}

		// Cancel the Bubble's movement
		// Remove Bubble from mFrame
		// Play pop sound if the BubbleView was popped

		private void stopMovement(final boolean wasPopped) {

			if (null != mMoverFuture) {

				if (!mMoverFuture.isDone()) {
					mMoverFuture.cancel(true);
				}

				// This work will be performed on the UI Thread
				mFrame.post(new Runnable() {
					@Override
					public void run() {
						// - Remove the BubbleView from mFrame
						mFrame.removeView(BubbleView.this);

						int currentViewCount = mFrame.getChildCount();
						mBubbleCountTextView.setText(currentViewCount + "");

						// - If the bubble was popped by user,
						// play the popping sound
						/*correctSoundId – this is the sound clip’s ID as returned by the load() method
						leftVolume – the left volume which can range from 0.0 to 1.0
						rightVolume – the right volume which can range from 0.0 to 1.0
						priority – the priority for this sound clip. 0 is the lowest priority, the higher the number, the higher the priority
						LOOP_FOREVER – our constant of -1 specifying that the sound clip should loop forever
						frequency – the playback rate or speed of playback. Ranges from 0.5 to 2.0 with 1.0 being the normal playback rate
						*/
						if(wasPopped){
							mSoundPool.play(mSoundID,1,1,1,2,1);
						}

					}
				});
			}
		}

		// Change the Bubble's speed and direction
		private synchronized void deflect(float velocityX, float velocityY) {
			mDx = velocityX / REFRESH_RATE;
			mDy = velocityY / REFRESH_RATE;
		}

		// Draw the Bubble at its current location
		@Override
		protected synchronized void onDraw(Canvas canvas) {

			// - save the canvas
			canvas.save();

			// - increase the rotation of the original image by mDRotate
			canvas.rotate(mDRotate);

			// - Rotate the canvas by current rotation
			// Hint - Rotate around the bubble's center, not its position
			canvas.rotate(mRotate, mXPos + mScaledBitmap.getWidth() / 2, mYPos + mScaledBitmap.getWidth() / 2);

			// - draw the bitmap at it's new location
			canvas.drawBitmap(mScaledBitmap,mXPos,mYPos,mPainter);

			//canvas.drawRect(mXPos, mYPos, mXPos+mScaledBitmap.getWidth(), mYPos+mScaledBitmap.getWidth(), mPainter);

			// - restore the canvas
			canvas.restore();

		}

		// Returns true if the BubbleView is still on the screen after the move
		// operation
		private synchronized boolean moveWhileOnScreen() {

			// - Move the BubbleView
			mXPos += mDx;
			mYPos += mDy;

			return isOutOfView();

		}

		// Return true if the BubbleView is still on the screen after the move
		// operation
		private boolean isOutOfView() {

			// - Return true if the BubbleView is still on the screen after
			// the move operation
			if(mYPos < 0 || mYPos > mDisplayHeight || mXPos < 0 || mXPos > mDisplayWidth){
				return false;
			}
			return true;
		}
	}


}