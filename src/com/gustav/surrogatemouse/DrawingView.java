package com.gustav.surrogatemouse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

class DrawingView extends View {
	Paint mPaint;
	// MaskFilter mEmboss;
	// MaskFilter mBlur;
	Bitmap mBitmap;
	Canvas mCanvas;
	Path mPath;
	Paint mBitmapPaint;
	long drawCounter = 0;
	TextView drawIndicator;
	double px;
	double py;
	
	
	public DrawingView(Context context, TextView textView) {
		super(context);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(20);

		mPath = new Path();
		mBitmapPaint = new Paint();
		mBitmapPaint.setColor(Color.RED);
		drawIndicator = textView;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	
	public void drawMousePixel(double px, double py) {
		this.px = px;
		this.py = py;
		invalidate();
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		canvas.drawColor(Color.TRANSPARENT);

		canvas.drawPoint((float)px, (float)py, mPaint);		
		//canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		//canvas.drawPath(mPath, mPaint);
		
		drawCounter++;
		drawIndicator.setText(Long.toString(drawCounter));
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		// mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
		// kill this so we don't double draw
		mPath.reset();
		// mPath= new Path();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

}
