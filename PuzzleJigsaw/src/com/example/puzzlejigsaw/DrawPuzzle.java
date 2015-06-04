package com.example.puzzlejigsaw;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;



public class DrawPuzzle extends View {
	Bitmap mBitmap;

	// difficulty level
	int diffLevel;

	public DrawPuzzle(Context context) {
		super(context);

	}
	public DrawPuzzle(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	public DrawPuzzle(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);


	}
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);

		
		Drawable d = getResources().getDrawable(R.raw.map);
		d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		d.draw(canvas);

		switch(level()){
		//this is where differences in levels will take action
		case 1:

			invalidate(); // forces redraw, idk if it's needed though
			break;
		case 2:

			invalidate();
			break;

		case 3:

			invalidate();
			break;

		}
	}

	public int level(){
		//getting the selected difficulty level
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
//		diffLevel = prefs.getInt("diff_level", 1);
		
		diffLevel = Integer.parseInt(prefs.getString("difficultySlide", "1"));
		
		return diffLevel;
	}
}
