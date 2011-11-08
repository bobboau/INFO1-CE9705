package com.bobboau.mandelbrot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MandelbrotActivity extends Activity {	

	
	/** Called when the activity is first created. */
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final LinearLayout layout = (LinearLayout) findViewById(R.id.main_layout);
   
        final Button btn = (Button)findViewById(R.id.fracking_button);

        //programaticly add this, because it's a requirement
        final OutputView image = new OutputView(this);
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        image.setVisibility(View.GONE);
        layout.addView(image);
        
        //setup the click listener
        btn.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		//swap out the button for the image
        		findViewById(R.id.controls_container).setVisibility(View.GONE);
                
                float x_offset = Float.valueOf( ( ( EditText )findViewById( R.id.x_offset_control ) ).getText().toString() );
                float y_offset = Float.valueOf( ( ( EditText )findViewById( R.id.y_offset_control ) ).getText().toString() );
                float zoom = 1.0f/Float.valueOf( ( ( EditText )findViewById( R.id.zoom_control ) ).getText().toString() );
                int iterations = Integer.valueOf( ( ( EditText )findViewById( R.id.iteration_control ) ).getText().toString() );
                int threads = Integer.valueOf( ( ( EditText )findViewById( R.id.threads_control ) ).getText().toString() );
                               
                image.setParams(x_offset, y_offset, zoom, iterations, threads);
                
                image.setVisibility(View.VISIBLE);
            }
        });
        
   }
}