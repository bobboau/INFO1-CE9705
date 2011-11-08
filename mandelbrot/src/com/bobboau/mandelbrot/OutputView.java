package com.bobboau.mandelbrot;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

/*
 * class for drawing the mandelbrot set to the screen
 */
public class OutputView  extends View{
	Context context;
	
	//how many iterations did it take to determine if it was in the mandelbrot set or not on each pixel
	Queue<OutputRow> rows;
	
	//the thread pool
	DrawerThread drawers[];
		
	Bitmap bmp;
	
	float x_offset = 0;
	float y_offset = 0;
	float zoom = 0;
	int iterations = 0;
	int thread_count = 0;
	
	public OutputView(Context context) {
		super(context);
		this.context = context;
				
		rows = new LinkedList<OutputRow>();
	}
	
	public void setParams( float x, float y, float z, int i, int t){
		x_offset = x;
		y_offset = y;
		zoom = z;
		iterations = i;
		thread_count = t;	
		
		final OutputView the_output_view = this;
		
		drawers = new DrawerThread[thread_count];
		for(int j = 0; j<thread_count; j++){
			Handler handler = new Handler() {
				public void handleMessage(Message message) {
					float row[] = (float[])message.obj;
					
					rows.add(new OutputRow(row, message.arg1));
					
					Log.d("drawing", "recv; row: "+message.arg1+", val: "+row[0]);
					
					the_output_view.invalidate();
				}
			};

			drawers[j] = new DrawerThread(handler, iterations);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//noting to do
		if(rows.isEmpty()){
			return;
		}
		
		int width = Math.min(getWidth(), getHeight());
		
		int x_offset = (getWidth() - width)/2;
		int y_offset = (getHeight() - width)/2;
		
		//the first row
		OutputRow row = rows.remove();
		
		if(row.row.length != width){
			return;//error out
		}
		
		//for every pixel in the row
		for(int j = 0; j<width; j++){
			//draw the pixel
			int d = (int)(255*row.row[j]);
			bmp.setPixel(j, row.idx, Color.rgb(d, d, d));
		}
		
		//Actually draw it
		canvas.drawBitmap(bmp, x_offset, y_offset, null);
		
		//we still have more to draw, but refresh the screen first
		if(!rows.isEmpty()){
			invalidate();
		}
		
	}
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{        
		//we want to draw a square, so get the min
		int d = Math.min(w, h);
        //reset the rows
        rows.clear();
        
        //rebuild the bitmap
        bmp = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        
		super.onSizeChanged(w, h, oldw, oldh);
        
        //kill any running threads
        for(DrawerThread drawer : drawers){
        	if(drawer.isAlive())
        	{
        		drawer.kill();
        		try {
					drawer.join();
				} catch (InterruptedException e) {
					drawer.stop();//just make sure it's dead
				}
        	}
        }
        
        //reset them to the current dimensions
        DrawerThread.resetDrawers(d, d, x_offset, y_offset, zoom);
        
        //start them up (again)
        for(DrawerThread drawer : drawers){
        	drawer.start();
        }
	}

}
