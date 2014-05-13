package com.souzadev.dodge;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.RelativeLayout;

public class WorldActivity extends Activity{
	//Const
	private static final int MAX_BALLS = 5;
	private static final int PLAYER_RADIUS = 3; //Player ball screen percentage
	private static final int NPC_RADIUS = 5;
	
	//Ambient
	private GameSurface gSurface;	
	private Ball[] balls;
	
	private Thread gameThread;
	
	//Sensors
	private SensorManager sensorManager;
	private Sensor sensor;
	
	//Components
	private RelativeLayout botLayout;
	
	//Calibration
	private Float[][] calibAvrgValues = new Float[3][10]; //Numeros par media de valores a calibrar
	private Float[] calibValues = new Float[3]; // Valores para calibração
	private int calibrationCounter;
	
	//Utility
	private PointF bounds;
	
	//*********************************** OVERRIDE *************************************
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_world);
		
		//Initialize members
		gSurface = new GameSurface(this);
		balls = new Ball[MAX_BALLS];
		
		//Initialize components
		botLayout = (RelativeLayout)findViewById(R.id.main_rLayout_bot);
		botLayout.addView(gSurface);
		
		//Initialize services
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        //Initialize Utility
        calibrationCounter = 0;
        
        
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
	
	@Override
	protected void onPause() {
		super.onPause();
		gameStop();
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		//gameStart();
	};
	
	//*************************************** SENSOR LISTENER **********************************
	private SensorEventListener playerMoveListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			Ball ball = balls[0];
			PointF tempPosition;
			
			Float[] calibratedValues = new Float[3];
			float x, y;
			
			for (int i = 0; i <= 2; i++){
				calibratedValues[i] = event.values[i] - calibValues[i];
			}
			
			x = (ball.getPosition().x - (calibratedValues[0] * ball.getSpeed().x));
			y = (ball.getPosition().y + (calibratedValues[1] * ball.getSpeed().x));
						
			tempPosition = ball.getPosition();
			ball.setPosition(new PointF(x,y));
			
			switch (outOfBounds(ball)){
			case 1:
				//ball.getPosition().x = tempPosition.x;
				ball.setPosition(new PointF(tempPosition.x, ball.getPosition().y));
				break;
			case 2:
				//ball.getPosition().y = tempPosition.y;
				ball.setPosition(new PointF(ball.getPosition().x, tempPosition.y));
				break;
			case 3:
				ball.setPosition(tempPosition);
				break;
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	private SensorEventListener calibrationListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {			
			for (int i = 0; i <= 2; i++){
				calibAvrgValues[i][calibrationCounter] = event.values[i];
			}
			
			if (calibrationCounter == 9){								
				sensorManager.unregisterListener(calibrationListener);
				
				for (int i = 0; i <= 2; i++){
					for (Float element : calibAvrgValues[i]){
						calibValues[i] =+ element;
					}
				}
				
				calibrationCounter = 0;
				sensorManager.registerListener(playerMoveListener, sensor, SensorManager.SENSOR_DELAY_GAME);
			}
			calibrationCounter++;
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}
	};
	
	private int outOfBounds(Ball ball){
		int ret = 0;
		if((ball.getPosition().x + ball.getRadius() > bounds.x) || (ball.getPosition().x - ball.getRadius() < 0)){
			ret += 1;
		}
		
		if((ball.getPosition().y + ball.getRadius() > bounds.y) || (ball.getPosition().y - ball.getRadius() < 0)){
			ret += 2;
		}
		return ret;
	}
	
	public void calibrate(){
		sensorManager.unregisterListener(playerMoveListener);
		sensorManager.unregisterListener(calibrationListener);
		sensorManager.registerListener(calibrationListener, sensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	//******************************************* PRIVATE ****************************************
	private void initBalls(){
		bounds = new PointF(gSurface.getWidth(), gSurface.getHeight());
		
		float hip = (float)Math.sqrt(Math.pow(gSurface.getWidth(), 2) + Math.pow(gSurface.getHeight(), 2));		
		float pRadius = (hip * PLAYER_RADIUS) / 100;
		float npcRadius = (hip * PLAYER_RADIUS) / 100;
		
		balls[0] = new Ball(pRadius, new PointF(100f, 100f), new PointF(5f, 5f), Color.GREEN, Color.BLACK);
		
	}
	
	private void gameStart(){
		calibrate();
		gameThread = new Thread(){
			public void run(){
				try{
					while(true){
						drawSurface();
						Thread.sleep(1000 / 30);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		};
		gameThread.start();
	}
	
	private void gameStop(){
		sensorManager.unregisterListener(playerMoveListener);
		if (gameThread != null){
			gameThread.interrupt();
		}		
	}
	
	private void gameResume(){
		//TODO
	}
	
	private void drawSurface(){
		SurfaceHolder holder = gSurface.getHolder();
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.LTGRAY);
		for(int i = 0; i < MAX_BALLS; i++){
			if (balls[i] != null){
				canvas.drawCircle(balls[i].getPosition().x, balls[i].getPosition().y, balls[i].getRadius(), balls[i].getPaint());
			}
		}
		holder.unlockCanvasAndPost(canvas);
	}
	
	//******************************************* PUBLIC ******************************************
	public void buttonStart(View view){
		initBalls();
		gameStart();
	}
	
	public void buttonStop(View view){
		gameStop();
	}
}
