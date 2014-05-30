package com.souzadev.dodge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {
	private int bgColor;
	//************************************* CONSTRUCTOR **************************************
	public GameSurface(Context context) {
		super(context);
		getHolder().addCallback(this);
		
		bgColor = Color.parseColor("#D5D5DA"); 
	}

	//*************************************** OVERRIDE **************************************
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(bgColor);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}	
	
	//**************************************** SETTERS **************************************
	public void setBgColor(int bgColor){
		this.bgColor = bgColor;
	}
	
	//*************************************** GETTERS ***************************************
	public int getBgColor(){
		return bgColor;
	}
}
