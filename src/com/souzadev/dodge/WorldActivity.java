package com.souzadev.dodge;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Chronometer;
import android.widget.RelativeLayout;

public class WorldActivity extends Activity{
	//Ambient
	private Thread gameThread;
	private GameSurface gSurface;	
	private Ball[] balls;	
	private int maxBalls;
	
	SharedPreferences sharedPrefs;
	//Player
	private int playerRadius;
	private PointF playerSpeed;
	private int playerInColor;
	private int playerExtColor;
	//NPC
	private int npcRadius;
	private int npcInColor;
	private int npcExtColor;
	private float acc;
	
	//Sensors
	private SensorManager sensorManager;
	private Sensor sensor;
	
	//Components
	private RelativeLayout botLayout;
	
	//Calibration
	private Float[][] calibAvrgValues = new Float[3][10]; //Numeros par media de valores a calibrar
	private Float[] calibValues = new Float[3]; // Valores para calibração
	private int calibrationCounter;
	
	//Chronometer
	private Chronometer chrono;
	private long startTime;
	private long endTime;
	private long deltaTime;
	
	//Utility
	private PointF bounds;
	
	//Animation
	ObjectAnimator objAnimator;
	
	//*********************************** OVERRIDE *************************************
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_world);
		
		//Initialize ambient
		sharedPrefs = this.getSharedPreferences(getString(R.string.prefs_string_file_MAIN_PREFS) ,Context.MODE_PRIVATE);
		
		
		maxBalls = 5;
		playerRadius = sharedPrefs.getInt(getString(R.string.prefs_int_PLAYER_RADIUS), 3); //Player ball screen percentage
		playerSpeed = new PointF(sharedPrefs.getInt(getString(R.string.prefs_int_PLAYER_SPEED), 5), sharedPrefs.getInt(getString(R.string.prefs_int_PLAYER_SPEED), 5));
		playerInColor = sharedPrefs.getInt(getString(R.string.prefs_int_PLAYER_IN_COLOR), Color.GREEN);
		playerExtColor = sharedPrefs.getInt(getString(R.string.prefs_int_PLAYER_EXT_COLOR), Color.BLACK);
		
		npcRadius = sharedPrefs.getInt(getString(R.string.prefs_int_NPC_RADIUS), 8); //Non player ball screen percentage
		npcInColor = sharedPrefs.getInt(getString(R.string.prefs_int_NPC_IN_COLOR), Color.RED);
		npcExtColor = sharedPrefs.getInt(getString(R.string.prefs_int_NPC_EXT_COLOR), Color.BLACK);
		acc = 0.01f; //Acceleration
		
		gSurface = new GameSurface(this);
		
		//SMALL SCREEN TESTS
//		gSurface.setLayoutParams(new LayoutParams(300, 300));
		
		balls = new Ball[maxBalls];
		
		//Initialize components
		botLayout = (RelativeLayout)findViewById(R.id.main_rLayout_bot);
		botLayout.addView(gSurface);
		chrono = (Chronometer)findViewById(R.id.main_chronometer_time);
		
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
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gameStop();
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
	private void makeBalls(){
		bounds = new PointF(gSurface.getWidth(), gSurface.getHeight());
		
		PointF mid = new PointF(gSurface.getWidth() /2, gSurface.getHeight() / 2);
		float hip = (float)Math.sqrt(Math.pow(gSurface.getWidth(), 2) + Math.pow(gSurface.getHeight(), 2));		
		float pRadius = (hip * playerRadius) / 100;
		float npRadius = (hip * npcRadius) / 100;
		
		balls[0] = new Ball(pRadius, mid, playerSpeed, playerInColor, playerExtColor);
		balls[1] = new Ball(npRadius, new PointF(mid.x / 2, mid.y / 2), new PointF(-3f, 3f), npcInColor, npcExtColor);
		balls[2] = new Ball(npRadius, new PointF(mid.x * 1.5f, mid.y / 2), new PointF(2f, -1f), npcInColor, npcExtColor);
		balls[3] = new Ball(npRadius, new PointF(mid.x, mid.y * 1.5f), new PointF(-1f, -3f), npcInColor, npcExtColor);
	}	
	
	//Main game sequence
	private void gameStart(){
		if (balls[0] == null){
			makeBalls();
		}
		
		if (balls[0] != null){
			calibrate();
		}
		
		startTime = System.currentTimeMillis();
		chrono.setBase(SystemClock.elapsedRealtime());
		chrono.start();
		
		if(gameThread != null){
			return;
		}
		
		//Tripp animations
		if(sharedPrefs.getBoolean(getString(R.string.prefs_boolean_TRIPP), false)){
			animateColors();
		}
		
		gameThread = new Thread(){
			public void run(){				
				try{					
					while(true){
						updateGame();
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
			gameThread = null;
		}
		chrono.stop();
	}
	
	private void gameEnd(){
		gameStop();
		
		endTime = System.currentTimeMillis();
		deltaTime = endTime - startTime;
		
		Intent intent = new Intent(this, ScoreActivity.class);
		intent.putExtra(MainActivity.EXTRA_TIME, deltaTime);
		startActivity(intent);
	}

	//Updates
	private void updateGame(){
		//Update movement
		for (int i = 1; i < maxBalls; i++){
			if (balls[i] != null){
				updatePosition(balls[i]);
				updateSpeed(balls[i]);
			}			
		}
		
		//Detect collisions
		if (balls[0] != null){
			if(detectCollisions()){
				ballCollision();
			}
		}
	}
	
	private void updatePosition(Ball ball){
		ball.getPosition().x += ball.getSpeed().x;
		if((ball.getPosition().x + ball.getRadius() > bounds.x) || (ball.getPosition().x - ball.getRadius() < 0)){
			ball.getSpeed().x *= (-1);
		}
		
		ball.getPosition().y += ball.getSpeed().y;
		if((ball.getPosition().y + ball.getRadius() > bounds.y) || (ball.getPosition().y - ball.getRadius() < 0)){
			ball.getSpeed().y *= (-1);
		}
		ball.fixShader();
	}
	
	private void updateSpeed(Ball ball){
		if (ball.getSpeed().x > 0){
			ball.getSpeed().x += acc;
		}else{
			ball.getSpeed().x -= acc;
		}

		if (ball.getSpeed().y > 0){
			ball.getSpeed().y += acc;
		}else{
			ball.getSpeed().y -= acc;
		}
	}
	
	//Collisions
	private boolean detectCollisions(){
		float smallRadius = balls[0].getRadius();
		float bigRadius = balls[0].getRadius();
		if (balls[1].getRadius() > bigRadius){
			bigRadius = balls[1].getRadius();			
		}else{
			smallRadius = balls[1].getRadius();
		}
		
		for (int i = 1; i < maxBalls; i++){
			if (balls[i] != null){
				if(getDistance(balls[0], balls[i]) < (bigRadius + smallRadius)){
					return true;
				}
			}
		}
		return false;
	}
	
	private float getDistance(Ball ball0, Ball ball1){
		float dist = (float)Math.sqrt(Math.pow((ball0.getPosition().x - ball1.getPosition().x),2) + Math.pow((ball0.getPosition().y - ball1.getPosition().y),2));		
		return dist;
	}
	
	private void ballCollision(){
		gameEnd();
	}

	//Draw
	private void drawSurface(){
		SurfaceHolder holder = gSurface.getHolder();
		Canvas canvas = holder.lockCanvas();
		if(!sharedPrefs.getBoolean(getString(R.string.prefs_boolean_TRIPP), false)){
			canvas.drawColor(gSurface.getBgColor());
		}
		for(int i = 0; i < maxBalls; i++){
			if (balls[i] != null){
				canvas.drawCircle(balls[i].getPosition().x, balls[i].getPosition().y, balls[i].getRadius(), balls[i].getPaint());
			}
		}
		holder.unlockCanvasAndPost(canvas);
	}
	
	//Animations
	private void animateColors(){
		objAnimator = ObjectAnimator.ofInt(balls[0], "inColor", Color.BLUE, Color.CYAN, Color.GRAY, Color.RED, Color.YELLOW, Color.GREEN);
		objAnimator.setEvaluator(new ArgbEvaluator());
		objAnimator.setRepeatCount(ValueAnimator.INFINITE);
		objAnimator.setRepeatMode(ValueAnimator.REVERSE);
		objAnimator.setDuration(5000);
		objAnimator.start();
		
		objAnimator = ObjectAnimator.ofInt(balls[1], "inColor", Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.YELLOW, Color.RED);
		objAnimator.setEvaluator(new ArgbEvaluator());
		objAnimator.setRepeatCount(ValueAnimator.INFINITE);
		objAnimator.setRepeatMode(ValueAnimator.REVERSE);
		objAnimator.setDuration(4000);
		objAnimator.start();
		
		objAnimator = ObjectAnimator.ofInt(balls[2], "inColor", Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.YELLOW, Color.RED);
		objAnimator.setEvaluator(new ArgbEvaluator());
		objAnimator.setRepeatCount(ValueAnimator.INFINITE);
		objAnimator.setRepeatMode(ValueAnimator.REVERSE);
		objAnimator.setDuration(3000);
		objAnimator.start();
		
		objAnimator = ObjectAnimator.ofInt(balls[3], "inColor", Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.YELLOW, Color.RED);
		objAnimator.setEvaluator(new ArgbEvaluator());
		objAnimator.setRepeatCount(ValueAnimator.INFINITE);
		objAnimator.setRepeatMode(ValueAnimator.REVERSE);
		objAnimator.setDuration(2000);
		objAnimator.start();
	}
	
	
	//******************************************* PUBLIC ******************************************
	public void buttonStart(View view){
		gameStart();
	}
	
	public void buttonStop(View view){
		gameStop();
	}
}
