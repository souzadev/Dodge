package com.souzadev.dodge;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public class Ball {
	private float radius;
	private PointF position;
	private PointF speed;
	
	private int inColor;
	private int extColor;
	private Paint paint;
	
	//********************************************* CONSTRUCTOR ************************************************
	public Ball(){		
		this.radius = 10f;		
		this.position = new PointF(20f, 20f);
		this.speed = new PointF(0, 0);
		
		this.inColor = Color.GRAY;
		this.extColor = Color.BLACK;
		paint = new Paint();
		fixShader();
	}

	public Ball(float radius, PointF position, PointF speed, int inColor, int extColor){
		
		this.radius = radius;		
		this.position = position;
		this.speed = speed;
		
		this.inColor = inColor;
		this.extColor = extColor;
		paint = new Paint();
		fixShader();
	}
	
	//********************************************** PRIVATES **************************************************
	public void fixShader(){
		paint.setShader(new RadialGradient(position.x, position.y, radius * 1.4f, inColor, extColor, Shader.TileMode.CLAMP));
	}
	
	//********************************************* SETTERS ****************************************************
	public void setRadius(float radius){
		this.radius = radius;
		fixShader();
	}
	
	public void setPosition(PointF position){
		this.position = position;
		fixShader();
	}
	
	public void setSpeed(PointF speed){
		this.speed = speed;
	}
	
	public void setColor(int inColor, int extCollor){
		this.inColor = inColor;
		this.extColor = extCollor;
		fixShader();
	}
	
	public void setInColor(int inColor){
		this.inColor = inColor;
		fixShader();
	}
	
	public void setExtColor(int extColor){
		this.extColor = extColor;
		fixShader();
	}
	
	public void setPaint(Paint paint){
		this.paint = paint;
	}
	
	
	//********************************************** GETTERS ***************************************
	public float getRadius(){
		return radius;
	}
	
	public PointF getPosition(){
		return position;
	}
	
	public PointF getSpeed(){
		return speed;
	}
	
	public int getInColor(){
		return inColor;
	}
	
	public int getExtColor(){
		return extColor;		
	}
	
	public Paint getPaint(){
		return paint;
	}
}
