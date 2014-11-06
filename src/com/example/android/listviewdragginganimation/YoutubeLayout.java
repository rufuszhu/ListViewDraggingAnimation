package com.example.android.listviewdragginganimation;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class YoutubeLayout extends ViewGroup {

	private static final String TAG = "YoutubeLayout";

	private final ViewDragHelper mDragHelper;

	private View mHeaderView;
	private View mDescView;

	private float mInitialMotionX;
	private float mInitialMotionY;

	private int mDragRange;
	private int mTop;
	private int mLeft;
	private float mDragOffset;
	private Context context;
	private boolean isLongPressing = false;
	private boolean isOnSroll = false;
	private boolean mIsScrolling = false;
	private int mTouchSlop;

	private int mDraggingState = 0;
	private GestureDetectorCompat mDetector;

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		private static final String DEBUG_TAG = "Gestures";

		@Override
		public boolean onDown(MotionEvent event) {
			Log.d(DEBUG_TAG, "onDown: " + event.toString());
			return true;
		}

		@Override
		public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
			return true;
		}

		@Override
		public void onLongPress(MotionEvent event) {
			Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
			isLongPressing = true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			Log.d(DEBUG_TAG, "onScroll: " + e1.toString() + e2.toString());
			isOnSroll = true;
			return true;
		}

	}

	public YoutubeLayout(Context context) {
		this(context, null);
		this.context = context;
	}

	public YoutubeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
		mDetector = new GestureDetectorCompat(context, new MyGestureListener());
	}

	@Override
	protected void onFinishInflate() {
		mHeaderView = findViewById(R.id.viewHeader);
		mDescView = findViewById(R.id.viewDesc);

		TextView a = (TextView) findViewById(R.id.a);
		TextView b = (TextView) findViewById(R.id.b);
		a.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "a clicked", Toast.LENGTH_LONG).show();
			}
		});
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "b clicked", Toast.LENGTH_LONG).show();
			}
		});
	}

	public YoutubeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
	}

	// public void maximize() {
	// smoothSlideTo(mDescView.getMeasuredWidth());
	// }
	//
	// public void minimize() {
	// smoothSlideTo(0);
	// }
	//
	// boolean smoothSlideTo(float slideOffset) {
	// final int leftBound = getPaddingLeft();
	// int x = (int) (leftBound + slideOffset);
	// if (mDragHelper.smoothSlideViewTo(mHeaderView, x, mHeaderView.getTop())) {
	// ViewCompat.postInvalidateOnAnimation(this);
	// return true;
	// }
	// return false;
	// }

	private class DragHelperCallback extends ViewDragHelper.Callback {

//		@Override
//		public void onViewDragStateChanged(int state) {
//			if (state == mDraggingState) { // no change
//				return;
//			}
//			Log.e(TAG, "onViewDragStateChanged");
//			mDraggingState = state;
//		}

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == mHeaderView;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

			mLeft = left;

			requestLayout();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			Log.e(TAG, "onViewReleased");
			if (xvel > 0 || (xvel == 0 && (float) mLeft / mDescView.getWidth() > 0.5f)) {
				mDragHelper.settleCapturedViewAt(mDescView.getMeasuredWidth(), releasedChild.getTop());
			} else {
				mDragHelper.settleCapturedViewAt(0, releasedChild.getTop());
			}
			invalidate();
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return mDragRange;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {

			final int leftBound = 0;
			final int rightBound = mDescView.getWidth();

			final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

			return newLeft;
		}

	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//
////		final float x = ev.getX();
////		final float y = ev.getY();
////
////		boolean result = false;
////		final int action = MotionEventCompat.getActionMasked(ev);
////		// Always handle the case of the touch gesture being complete.
////		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
////			// Release the scroll.
////			mIsScrolling = false;
////			return false; // Do not intercept touch event, let the child handle it
////		}
////
////		switch (action) {
////		case MotionEvent.ACTION_DOWN: {
////			Log.e(TAG, "onInterceptTouchEvent ACTION_DOWN");
////			mInitialMotionX = x;
////			mInitialMotionY = y;
////			break;
////		}
////
////		case MotionEvent.ACTION_MOVE: {
////			Log.e(TAG, "onInterceptTouchEvent ACTION_MOVE");
////			if (mIsScrolling) {
////				// We're currently scrolling, so yes, intercept the
////				// touch event!
////				return true;
////			}
////
////			// If the user has dragged her finger horizontally more than
////			// the touch slop, start the scroll
////
////			// left as an exercise for the reader
////			final int xDiff = (int) x - (int) mInitialMotionX;
////
////			// Touch slop should be calculated using ViewConfiguration
////			// constants.
////			if (xDiff > mDragHelper.getTouchSlop()) {
////				// Start scrolling!
////				mIsScrolling = true;
////				return true;
////			}
////			break;
////		}
////
////		}
//
//		// In general, we don't want to intercept touch events. They should be
//		// handled by the child view.
//		return false;
//
//		// if (mDragHelper.shouldInterceptTouchEvent(event)) {
//		// Log.e(TAG, "onInterceptTouchEvent true");
//		// return true;
//		// } else {
//		// Log.e(TAG, "onInterceptTouchEvent false");
//		// return false;
//		// }
//	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// this.mDetector.onTouchEvent(event);
//		// if (event.getAction() == MotionEvent.ACTION_UP) {
//		// isLongPressing = false;
//		// isOnSroll = false;
//		// }
//		//
//		// if (isLongPressing)
//		// return super.onTouchEvent(event);
//		// if (isOnSroll) {
//		// if (mDragHelper != null)
//		// Log.e(TAG, "mDragHelper is not null");
//		//
//		// if (event != null)
//		// Log.e(TAG, "event is not null");
//		//
//		// mDragHelper.processTouchEvent(event);
//		// return true;
//		// }
//		//
//		// return true;
//		
////		final float x = event.getX();
////		final float y = event.getY();
////
////		switch (event.getAction()) {
////		case MotionEvent.ACTION_DOWN: {
////			Log.e(TAG, "onTouchEvent ACTION_DOWN");
////			mInitialMotionX = x;
////			mInitialMotionY = y;
////			break;
////		}
////		case MotionEvent.ACTION_MOVE: {
////			Log.e(TAG, "onTouchEvent ACTION_MOVE");
////			final float adx = Math.abs(x - mInitialMotionX);
////			final float ady = Math.abs(y - mInitialMotionY);
////
////			if (adx > mDragHelper.getTouchSlop()) {
////				mDragHelper.processTouchEvent(event);
////				return true;
////			}
////		}
////		}
////		
////		return super.onTouchEvent(event);
//
//		// if (result && isMoving()) {
//		 //Log.e(TAG, "onTouchEvent returning true");
//		 mDragHelper.processTouchEvent(event);
//		 return true;
//		// } else {
//		// Log.e(TAG, "onTouchEvent returning false");
//		// return super.onTouchEvent(event);
//		// }
//	}
	
	public void processEvent(MotionEvent event){
		mDragHelper.processTouchEvent(event);
	}

	public boolean isMoving() {
		return (mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING);
	}

	private boolean isViewHit(View view, int x, int y) {
		int[] viewLocation = new int[2];
		view.getLocationOnScreen(viewLocation);
		int[] parentLocation = new int[2];
		this.getLocationOnScreen(parentLocation);
		int screenX = parentLocation[0] + x;
		int screenY = parentLocation[1] + y;
		return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.getWidth() && screenY >= viewLocation[1]
				&& screenY < viewLocation[1] + view.getHeight();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
		int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mDragRange = mDescView.getWidth() - mLeft;

		mHeaderView.layout(mLeft, 0, mLeft + mHeaderView.getMeasuredWidth(), b);

		mDescView.layout(mLeft - mDescView.getMeasuredWidth(), 0, mLeft, b);

	}
}