/* GraphicsView.java - Class for main drawing surface and listeners.
 * 
 * Copyright © 2011 Kyle Paulsen
 * Please see the file COPYING in this
 * distribution for license terms.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package com.kylepaulsen.openAndroidGame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/**
 * 
 * @author Kyle
 * This class is the primary graphics class that is 
 * responsible for all drawing routines. It takes the MainActivity
 * as its context.
 * 
 * I guess it will eventually listen for input.
 */
public class GraphicsView extends SurfaceView implements Callback {

	//Create a buffer bitmap so that static things can be redrawn fast and easy.
	private Bitmap cBuffer;
	//Create a current frame canvas for animated changes in the graphics.
	private Canvas cFrame;
	//A reference to the surface stuff.
	private SurfaceHolder graphicsHolder;
	//Game loop and thread.
	private GameProgram prog;
	//save context
	private Context context;
	
	private Paint p;
	
	private Player player;
	private PlayerAnimated playerAm;
	
	//temp
	private int x = 0;
	
	public GraphicsView(Context context) {
		super(context);
		this.context = context;
		
		prog = new GameProgram(this, context);
		
		graphicsHolder = this.getHolder();
		graphicsHolder.addCallback(this);
		
		Paint p = new Paint();
		p.setStyle(Paint.Style.FILL);
		p.setColor(Color.WHITE);
		this.p = p;
		
		cFrame = new Canvas();
		//cBuffer = Bitmap.createBitmap(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, Bitmap.Config.ARGB_8888);
        //cFrame.setBitmap(cBuffer);
        //cFrame.drawRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, p);
		
		player = new Player(BitmapFactory.decodeResource(getResources(), 
				R.drawable.mario), 50,50);
		playerAm = new PlayerAnimated(BitmapFactory.decodeResource(getResources(), 
				R.drawable.walk),
				10,50,
				30,47,
				5,5);
		
		
		//keeps the screen on while playing the game.
		this.setKeepScreenOn(true);

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		prog.setRunning(true);
		prog.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		prog.setRunning(false);
		while(prog.isAlive()){
			try{
				prog.join();
				
			}catch(InterruptedException e){}
		}
		this.setKeepScreenOn(false);
	}
	
	//Here is the main drawing method. 
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		//draw the current frame buffer.
		//not sure what or how I'm going to use a buffer for yet. Maybe backgrounds?
		//canvas.drawBitmap(cBuffer, 0, 0, null);	
		
		canvas.drawColor(0xFF000000);
		prog.draw(canvas);
		
		//Clear canvas. Remember that surfaceviews do not clean
		//the canvas on each call to this method.
		//canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(), p);
		
		//draw to frame and buffer.
		//cFrame.drawRect(x, 0, x+50, 50, p);
		
		//draw directly to canvas
		p.setColor(Color.RED);
		canvas.drawRect(x, 0, x+50, 50, p);
		x++;	
		
	      
		//draw many Tiles, for testing purpose

//	      for (int i =0; i<10; ++i){
//	    	  for (int j=0; j<8; ++j){
//	    		Tile tile = new Tile(BitmapFactory.decodeResource(getResources(), 
//	    				R.drawable.grass), 50*i, 50*j);
//	    		tile.draw(canvas);  
//	    	  }	    		  
//	      }

		//player.draw(canvas);
		player.moveWithCollisionDetection();
		//player.UpdateLocation();
		player.draw(canvas);
		
		playerAm.update(System.currentTimeMillis());
		playerAm.draw(canvas);

	}
	
	//buffer getter
	public Canvas getBuffer(){
		return this.cFrame;
	}
	
	//buffer setter.
	public void setBuffer(Canvas b){
		this.cFrame = b;
	}


	/*FUNCTIONS BELOW ARE TEMP, MAY BE ALTERED LATER*/
	
	// touch input handler, just for test
	   @Override
	   public boolean onTouchEvent(MotionEvent event) {
	      float currentX = event.getX();
	      float currentY = event.getY();
	      
	      if (player.getSpeedX()>0 && currentX<player.getX()) {
	    	  player.setSpeedX (- player.getSpeedX());
	      } 
	      if (player.getSpeedX()<0 && currentX>player.getX()+ player.getWidth()) {
	    	  player.setSpeedX(- player.getSpeedX());
	      } 
	      if (player.getSpeedY()>0 && currentY<player.getY()) {
	    	  player.setSpeedY (- player.getSpeedY());
	      } 
	      if (player.getSpeedY()<0 && currentY>player.getY()+player.getHeight()) {
	    	  player.setSpeedY (- player.getSpeedY());
	      } 

	      return true;  // Event handled
	   }
	   
		// Called back when the view is first created or its size changes.
	   @Override
	   public void onSizeChanged(int w, int h, int oldW, int oldH) {
	      // Set the movement bounds for the ball
	      player.xMax = w-1;
	      player.yMax = h-1;
	   }
	   
}
	

