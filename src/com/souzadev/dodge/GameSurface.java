package com.souzadev.dodge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

	//************************************* CONSTRUCTOR **************************************
	public GameSurface(Context context) {
		super(context);
		getHolder().addCallback(this);		
	}

	//*************************************** OVERRIDE **************************************
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {		
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.LTGRAY);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	}
}
