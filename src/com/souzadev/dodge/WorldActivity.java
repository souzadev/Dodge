package com.souzadev.dodge;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class WorldActivity extends Activity{
	private SensorManager sensorManager;
	private Sensor sensor;
	
	private RelativeLayout topLayout;
	private RelativeLayout botLayout;
	
	private GameSurface gameSurface;	
	
	//*********************************** OVERRIDE *************************************
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_world);
		
		//Initialize components
		botLayout = (RelativeLayout)findViewById(R.id.main_rLayout_bot);
		topLayout = (RelativeLayout)findViewById(R.id.main_rLayout_top);		
		gameSurface = new GameSurface(this);

		botLayout.addView(gameSurface);
		
		//Initialize services
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.world, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//*************************************** SENSOR LISTENER **********************************
	private SensorEventListener playerMoveListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
}
