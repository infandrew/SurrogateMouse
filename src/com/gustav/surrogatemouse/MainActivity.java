package com.gustav.surrogatemouse;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	DrawingView mDrawingView;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	SensorManager sensorManager = null;

	TextView outputX;
	TextView outputY;
	TextView outputZ;

	// for orientation values
	TextView outputX2;
	TextView outputY2;
	TextView outputZ2;

	TextView outputX3;
	TextView outputY3;
	TextView outputZ3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		// mSensorManager = (SensorManager)
		// getSystemService(Context.SENSOR_SERVICE);
		// mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		outputX = (TextView) findViewById(R.id.textView1);
		outputY = (TextView) findViewById(R.id.textView2);
		outputZ = (TextView) findViewById(R.id.textView3);

		outputX2 = (TextView) findViewById(R.id.textView4);
		outputY2 = (TextView) findViewById(R.id.textView5);
		outputZ2 = (TextView) findViewById(R.id.textView6);

		outputX3 = (TextView) findViewById(R.id.textView7);
		outputY3 = (TextView) findViewById(R.id.textView8);
		outputZ3 = (TextView) findViewById(R.id.textView9);

		// drawView = new DrawingView(this);
		// drawView.setBackgroundColor(Color.WHITE);
		// setContentView(drawView);

		mDrawingView = new DrawingView(this, outputZ3);

		RelativeLayout mDrawingPad = (RelativeLayout) findViewById(R.id.view_drawing_pad);

		mDrawingPad.addView(mDrawingView);
	}

	@Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(sensorType),
				sensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		super.onStop();
		sensorManager.unregisterListener(this,
				sensorManager.getDefaultSensor(sensorType));
	}

	private float[] gravity = new float[3];
	private float[] motion = new float[3];
	private double ratio;
	private double mAngle;
	private int counter = 0;
	private double px = 600;
	private double py = 600;
	private long lastTime = -1;
	private double lastSpeedX = 0;
	private double lastSpeedY = 0;
	final int sensorType = Sensor.TYPE_LINEAR_ACCELERATION;

	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			switch (event.sensor.getType()) {
			case sensorType:
				outputX.setText("x:" + Float.toString(event.values[0]));
				outputY.setText("y:" + Float.toString(event.values[1]));
				outputZ.setText("z:" + Float.toString(event.values[2]));

				long currentTime = event.timestamp;
				if (lastTime != -1) {
					double dt = (currentTime - lastTime) * 1E-9;
					if (lastSpeedX != 0) {
						outputY3.setText(Double.toString(dt));
						double dx = lastSpeedX * dt + event.values[0] * dt * dt
								/ 2;
						double dy = lastSpeedY * dt + event.values[1] * dt * dt
								/ 2;
						double th = 3E-4;
						 if (!(-th < dx && dx < +th))
						px -= dx*10000;
						 if (!(-th < dy && dy < +th))
						py += dy*10000;
						outputX2.setText(Double.toString(px));
						outputY2.setText(Double.toString(py));
						if (mDrawingView != null) {
							mDrawingView.drawMousePixel(px, py);
						}
					}
					lastSpeedX = event.values[0] * dt;
					lastSpeedY = event.values[1] * dt;
				}

				lastTime = currentTime;
				/*
				 * outputX.setText("x:" + Float.toString(event.values[0]));
				 * outputY.setText("y:" + Float.toString(event.values[1]));
				 * outputZ.setText("z:" + Float.toString(event.values[2]));
				 * 
				 * final double alpha = 0.8;
				 * 
				 * // Isolate the force of gravity with the low-pass filter.
				 * gravity[0] = alpha * gravity[0] + (1 - alpha) *
				 * event.values[0]; gravity[1] = alpha * gravity[1] + (1 -
				 * alpha) * event.values[1]; gravity[2] = alpha * gravity[2] +
				 * (1 - alpha) * event.values[2];
				 * 
				 * outputX2.setText("x:" + Double.toString(gravity[0]));
				 * outputY2.setText("y:" + Double.toString(gravity[1]));
				 * outputZ2.setText("z:" + Double.toString(gravity[2]));
				 * 
				 * 
				 * double linear_acceleration[] = new double[3]; // Remove the
				 * gravity contribution with the high-pass filter.
				 * linear_acceleration[0] = event.values[0] - gravity[0];
				 * linear_acceleration[1] = event.values[1] - gravity[1];
				 * linear_acceleration[2] = event.values[2] - gravity[2];
				 * 
				 * outputX3.setText("x:" +
				 * Double.toString(linear_acceleration[0]));
				 * outputY3.setText("y:" +
				 * Double.toString(linear_acceleration[1]));
				 * outputZ3.setText("z:" +
				 * Double.toString(linear_acceleration[2]));
				 */

				/*
				 * for (int i = 0; i < 3; i++) { gravity[i] = (float) (0.1 *
				 * event.values[i] + 0.9 * gravity[i]); motion[i] =
				 * event.values[i] - gravity[i]; } ratio = gravity[1] /
				 * SensorManager.GRAVITY_EARTH; if (ratio > 1.0) ratio = 1.0; if
				 * (ratio < -1.0) ratio = -1.0; mAngle =
				 * Math.toDegrees(Math.acos(ratio)); if (gravity[2] < 0) {
				 * mAngle = -mAngle; } px += motion[0]; py += motion[1]; if
				 * (counter++ % 10 == 0) { String msg = String
				 * .format("Raw values\nX: %8.4f\nY: %8.4f\nZ: %8.4f\n" +
				 * "Gravity\nX: %8.4f\nY: %8.4f\nZ: %8.4f\n" +
				 * "Motion\nX: %8.4f\nY: %8.4f\nZ: %8.4f\nAngle: %8.1f",
				 * event.values[0], event.values[1], event.values[2],
				 * gravity[0], gravity[1], gravity[2], motion[0], motion[1],
				 * motion[2], mAngle); outputX.setText(msg);
				 * outputX.invalidate(); counter = 1;
				 * 
				 * outputX2.setText(Float.toString(px));
				 * outputY2.setText(Float.toString(py)); }
				 */

			}
		}
	}

	/*
	 * public void onSensorChanged(SensorEvent event) { // In this example,
	 * alpha is calculated as t / (t + dT), // where t is the low-pass filter's
	 * time-constant and // dT is the event delivery rate.
	 * 
	 * final double alpha = 0.8;
	 * 
	 * double gravity[] = new double[3]; // Isolate the force of gravity with
	 * the low-pass filter. gravity[0] = alpha * gravity[0] + (1 - alpha) *
	 * event.values[0]; gravity[1] = alpha * gravity[1] + (1 - alpha) *
	 * event.values[1]; gravity[2] = alpha * gravity[2] + (1 - alpha) *
	 * event.values[2];
	 * 
	 * double linear_acceleration[] = new double[3]; // Remove the gravity
	 * contribution with the high-pass filter. linear_acceleration[0] =
	 * event.values[0] - gravity[0]; linear_acceleration[1] = event.values[1] -
	 * gravity[1]; linear_acceleration[2] = event.values[2] - gravity[2]; int k
	 * = 1; }
	 */

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
