package com.example.puzzlejigsaw;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


// timer is working correctly
// restart refreshes the activity
// give up takes the user to stat without the score added

// when done with the puzzle, should immediately be taken
// to stat with the score passed as bundle and put into database


public class Play extends Activity implements OnTouchListener{

	final static int STAT_CODE = 1;
	final static int TOP_LINK = 0;
	final static int LEFT_LINK = 1;
	final static int BOT_LINK = 2;
	final static int RIGHT_LINK = 3;
	final static int TOLERANCE = 30;


	float xInit = - 1;
	float yInit = -1;
	public static int difficultyLevel; //2,3,4
	int displayWidth;
	int pieceWidth;
	int padding = 18;

	private Button timerValue;
	private Button giveUp;
	private Button restart;

	private long startTime = 0L;

	private Handler customHandler = new Handler();

	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;

	int secs, mins, milliseconds;

	int pieces;

	boolean[][] adjCheckList;
	private TextView[] textViewArr;
	private int boxLength;
	private TextView[] imgArr;
	private float xpos = 0;
	private float ypos = 0;
	RelativeLayout mLayout;
	TextView[] circleArr;
	
	static Bitmap bMap;

	ArrayList<Bitmap> bitmapsArray;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);

		timerValue = (Button) findViewById(R.id.timer);
		mLayout = (RelativeLayout) findViewById(R.id.relative_layout);

		Bundle b = getIntent().getExtras();
		String difLvl = b.getString("LEVEL");
		difficultyLevel = Integer.parseInt(difLvl) + 1;
		Log.d("level is: ",""+difficultyLevel);


		pieces = difficultyLevel * difficultyLevel;
		imgArr = new TextView[pieces];
		int bitWidth = -1;
		int bitHeight = -1;

		adjCheckList = new boolean[pieces][4];

		initAdjCheckList();

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		int w = size.x;
		int hh = size.y;
		int ww= w;

		
		w = w + w/3;
		
		
		bitWidth = w;
		bitHeight = w;
		Log.i("mylog","w: " + w);


		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = true;
//		Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.raw.map, options); 
	
		// randomizes the puzzle selection
				Random ran = new Random();
				int r = ran.nextInt(3);
				if(r == 0){
					bMap = BitmapFactory.decodeResource(getResources(), R.raw.map);
				}else if(r == 1){
					bMap = BitmapFactory.decodeResource(getResources(), R.raw.baymax_small);
				}else{
					bMap = BitmapFactory.decodeResource(getResources(), R.raw.terrapin);
				}
				
		// was: R.drawable.map but gave me an error so I changed to 
		//the path where map.png was stored 

		int rows, cols, chunkHeight, chunkWidth;
	

		if(bMap == null)
			Log.i("Error", "MainActivity-------->>>");

		Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, bitHeight, bitWidth, true);
		
		

		rows = cols = difficultyLevel;
		chunkHeight = bMapScaled.getHeight() / rows;
		chunkWidth = bMapScaled.getWidth() / cols;
		pieceWidth = chunkWidth;
		boxLength = chunkWidth - chunkWidth/3 + 4;

		// changed to final
		bitmapsArray = new ArrayList<Bitmap>(pieces);

	
		int yCoord = 0;
		for(int x = 0; x < rows; x++){
			int xCoord = 0;

			for(int y = 0; y < cols; y++){
				bitmapsArray.add(Bitmap.createBitmap(bMapScaled, xCoord, yCoord, chunkWidth, chunkHeight));
				xCoord += chunkWidth;
			}
			yCoord += chunkHeight;
		}

	
		textViewArr = new TextView[pieces];

		
		int boxX = 0;
		int boxY = 0;
		int padding = 18;
		
		//adding text views to the array

		for(int j =0; j< pieces; j++){

			TextView tv = new TextView(this);
			tv.setId(j);
			tv.setBackgroundResource(R.drawable.box);
			tv.setWidth(boxLength);
			tv.setHeight(boxLength);
			tv.setX(boxX + padding);
			tv.setY(boxY);
			textViewArr[j] = tv;
			mLayout.addView(tv);

			if(j % difficultyLevel == difficultyLevel -1){
				boxX = 0;
				boxY += boxLength;
			}else{
				boxX += boxLength;
			}
		}


		boxX = 0;
		boxY = 0;
		for(int i = 0; i < pieces; i++){			
			//making the pieces
			@SuppressWarnings("deprecation")
			BitmapDrawable bd = new BitmapDrawable(bitmapsArray.get(i));
			
			imgArr[i] = new TextView(this);
			imgArr[i].setBackground(bd); 
			imgArr[i].setId(i);
			imgArr[i].setWidth(boxLength - padding + 13);
			imgArr[i].setHeight(boxLength - padding + 13);
			imgArr[i].setOnTouchListener(this);	
			
			if(i % difficultyLevel == difficultyLevel -1){
				boxX = 0;
				boxY += boxLength;
			}else{
				boxX += boxLength;
			}
			
		}



		int xpos = 0;
		int ypos = 0;
		int j = 0;

		for(int i = pieces - 1; i >= 0; i--){
			imgArr[i].setX(xpos + 20);
			imgArr[i].setY(ypos + 3);

			mLayout.addView(imgArr[i]);
			if(j++ % difficultyLevel == difficultyLevel -1){
				xpos = 0;
				ypos += boxLength;
			}else{
				xpos += boxLength;
			}
		}


		TextView[] temp = new TextView[imgArr.length];
		int jj = 0;
		for(int i = pieces - 1; i >= 0; i--){
			temp[jj++] = imgArr[i];
		}

		for(int i = 0; i < pieces; i++){
			imgArr[i] = temp[i];
		}
		
		circleArr = new TextView[4];
		circleArr[0] = (TextView) findViewById(R.id.testing1);
		circleArr[1] = (TextView) findViewById(R.id.testing2);
		circleArr[2] = (TextView) findViewById(R.id.testing);
		circleArr[3] = (TextView) findViewById(R.id.testing3);

		
//		float circleX = ww/10 + 20 ;
//		float circleY = hh - hh/3 - 30 ;
//		int length =  90;
//		for(int i = 0; i < 4; i++){
//			
//			
//			TextView t = new TextView(this);
//			t.setBackgroundResource(R.layout.empty_circle);
//			t.setX(circleX);
//			t.setY(circleY);
//
//			circleArr[i] = t;
//			mLayout.addView(t);
//			circleX += length; 
//		}



		// timer starts when play activity is launched
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);

		// when giving up, taken to stat activity 
		giveUp = (Button) findViewById(R.id.give_up);

		giveUp.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				timeSwapBuff += timeInMilliseconds;
				customHandler.removeCallbacks(updateTimerThread);

				// when giving up, taken to stat activity

				Intent intent = new Intent(Play.this, Stat.class);

				startActivityForResult(intent, STAT_CODE);

			}
		});

		// restarts the activity
		restart = (Button) findViewById(R.id.restart);
		restart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				
				for(Bitmap b : bitmapsArray){
					b.recycle();
					b = null;
				}
				System.gc();
				
				recreate();
			}
		});

	}


	private boolean solved(){

		for(int i = 0; i < pieces;i++){
			if(imgArr[i].getId() != i){
				return false;
			}
		}

		return true;
	}
	
	private int getArrayPosition(int id){

		int ans = 0;
		for(int i = 0; i < pieces;i++){
			if(imgArr[i].getId() == id){
				return i;
			}
		}

		return ans;
	}

	private void initAdjCheckList() {

		int row = 1;
		int maxCol = difficultyLevel;
		int maxRow = difficultyLevel;


		// check boundary and set them true
		for (int id = 0; id < pieces; id++){

			if(id == 0){
				adjCheckList[id][LEFT_LINK] = true;
			}

			if(id < maxCol){  
				adjCheckList[id][TOP_LINK] = true;
			}

			if(id == maxCol*row){
				adjCheckList[id][LEFT_LINK] = true;
				row++;
			}

			if(id == maxCol*row - 1){
				adjCheckList[id][RIGHT_LINK] = true;
			}

			if(row == maxRow){
				adjCheckList[id][BOT_LINK] = true;
			}

		}

	}

	// timer runnable
	private Runnable updateTimerThread = new Runnable() {

		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeSwapBuff + timeInMilliseconds;

			secs = (int) (updatedTime / 1000);
			mins = secs / 60;
			secs = secs % 60;
			milliseconds = (int) (updatedTime % 1000);
			timerValue.setText("" + mins + ":"
					+ String.format("%02d", secs) + ":"
					+ String.format("%03d", milliseconds));
			customHandler.postDelayed(this, 0);
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	@Override
	public void onPause() {
		super.onPause();  // Always call the superclass method first

		bMap.recycle(); // to prevent out of memory error
		
		timeSwapBuff += timeInMilliseconds;

		// pause BGM
		MusicManager.pause();
		// timer is paused
		customHandler.removeCallbacks(updateTimerThread);
		Log.d("ON PAUSE-----------", "paused");
	}

	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first

		//start BGM
		MusicManager.start(this, MusicManager.MUSIC_MENU);

		// starts the timer again
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
		Log.d("ON RESUME-----------", "resumed");
	}
	
	@Override
	protected void onDestroy() {
		for(Bitmap b : bitmapsArray){
			b.recycle();
			b = null;
		}
		System.gc();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == STAT_CODE) {
			finish();
		}
	}

	
	private void checkProgress(){
		float correct = 0;
		for(int i = 0; i < pieces; i++){
			if(imgArr[i].getId() == i){
				correct++;
			}
		}
		
		for(int i = 0; i < 4; i++){
			circleArr[i].setBackgroundResource(R.layout.empty_circle);
		}

		float perc =((float)(correct/pieces)) * 100;
		Log.i("myLog","perc: " + perc);

		if(correct != 0){
			if(perc < 25){
				
			}else if(perc >= 25 && perc < 50){
				for(int i = 0; i < 1; i++){
					circleArr[i].setBackgroundResource(R.layout.circle);
				}
			}else if(perc >= 50 && perc < 75){
				for(int i = 0; i < 2; i++){
					circleArr[i].setBackgroundResource(R.layout.circle);
				}
			}else if(perc >= 75 && perc < 100){
				for(int i = 0; i < 3; i++){
					circleArr[i].setBackgroundResource(R.layout.circle);
				}
			}else{
				for(int i = 0; i < 4; i++){
					circleArr[i].setBackgroundResource(R.layout.circle);
				}
			}
		}
	}

	

	@Override
	public boolean onTouch(View v, MotionEvent event) {



		switch (event.getAction()){

		case MotionEvent.ACTION_DOWN:

			xInit = event.getX();
			yInit = event.getY();
			xpos = v.getX();
			ypos = v.getY();


			break;
		case MotionEvent.ACTION_UP:

			int limit = padding + (boxLength*difficultyLevel);
			
			//keep pieces on the screen
			if(v.getX() + v.getWidth()/2 < padding || //left
					v.getX() + v.getWidth()/2 > limit || //right
					v.getY() + v.getWidth()/2 > (limit - padding) || //bottom
					v.getY() + v.getWidth()/2 < 0){ //top
				v.setX(xpos);
				v.setY(ypos);
			}else{

				int yTarget = (int) (v.getY() + v.getWidth()/2 - padding)/boxLength;
				int xTarget = (int) (v.getX() + v.getWidth()/2 - padding)/boxLength;
				int target = yTarget * difficultyLevel + xTarget;
				
				TextView tmp = imgArr[target];
				Log.i("mylog","switching view.id: " + v.getId() + " at position: " + getArrayPosition(v.getId()));
				Log.i("mylog","with view.id: " + tmp.getId() + " at position: " + getArrayPosition(tmp.getId()));
				
				v.setX(tmp.getX());
				v.setY(tmp.getY());
				
				tmp.setX(xpos);
				tmp.setY(ypos);
				
				imgArr[getArrayPosition(v.getId())] = tmp;
				imgArr[target] = (TextView) v;	
				
				checkProgress();
				
				if(solved()){
					completed();
				}
				
				
				
			}
			






			//			//checkPieces(v);
			//			boolean a = checkComplete();
			//			
			//			Log.i("AA", "puzzle complete: " + a);
			//			if(a){
			//				completed(v);
			//			}

			break;
		case MotionEvent.ACTION_MOVE:

			float imgX =v.getX();
			float imgY = v.getY();
			float x = event.getX();
			float y = event.getY();     
			v.bringToFront();


			v.setX(imgX + (x - xInit));
			v.setY(imgY + (y - yInit));



			break;
		}


		return true;
	}


	private boolean checkComplete() {
		for(boolean[] pieces: adjCheckList){

			for(boolean n: pieces){
				if (n == false){
					return false;
				}
			}

		}

		return true;

	}

	private void checkPieces(View v){

		int currLeft,currTop,currRight,currBot, currId;

		currLeft = (int) v.getX();
		currTop = (int) v.getY();
		currRight = currLeft + v.getWidth();
		currBot = currTop + v.getHeight();

		currId = v.getId();

		int L, R, T, B;

		T = currId - difficultyLevel;
		B = currId + difficultyLevel;
		L = currId - 1;
		R = currId + 1;

		/* top pieces is not negative id so must exist so need to check bot y-cord 
		 * with top y-cord of current piece.
		 * 
		 * if they are with in the TOLERANCE then set TOP pieces BOT_LINK = true
		 * and CUR piece TOP_LINK = true in adjCheckList
		 * 
		 */

		if(T >= 0){

			View Tview = mLayout.findViewById(T);

			int T_bot = (int) (Tview.getY() + Tview.getHeight());

			if(T_bot > currTop - TOLERANCE && T_bot < currTop + TOLERANCE){
				adjCheckList[currId][TOP_LINK] = true;
				adjCheckList[T][BOT_LINK] = true;
			}

		}

		if(B < pieces){

			View Bview = mLayout.findViewById(B);

			int B_Top = (int) Bview.getY();

			if(B_Top > currBot - TOLERANCE && B_Top < currBot + TOLERANCE){

				adjCheckList[currId][BOT_LINK] = true;
				adjCheckList[B][TOP_LINK] = true;

			}
		}

		if(L >= 0 && L % difficultyLevel != difficultyLevel-1){
			View Lview = mLayout.findViewById(L);

			int L_Right = (int) (Lview.getX() + Lview.getWidth());

			if(L_Right > currLeft - TOLERANCE && L_Right < currLeft + TOLERANCE){

				adjCheckList[currId][LEFT_LINK] = true;
				adjCheckList[L][RIGHT_LINK] = true;

			}

		}

		if(R < pieces && R % difficultyLevel != 0){
			View Rview = mLayout.findViewById(R);

			int R_Left = (int) Rview.getX();

			if(R_Left > currRight - TOLERANCE && R_Left < currRight + TOLERANCE){

				adjCheckList[currId][LEFT_LINK] = true;
				adjCheckList[R][RIGHT_LINK] = true;

			}

		}


		/*
		 *    T
		 * L  C  R
		 *    B
		 *    
		 *    piece id with respect to the center one
		 *    if less than 0 or greater than pieces then they dont count
		 *    T = C.id - diff lvl
		 *    B = C.id + difficulty level
		 *    L = C.id - 1
		 *    R = C.id + 1
		 *    
		 *    get an 2d boolean array to represent linkage so we know when the puzzle 
		 *    is finished
		 *    
		 *    linkage
		 *    
		 *    0
		 *  1 C 3
		 *    2
		 */




	}

	private void completed(){
		
		
		
		// addds a check mark the after about a second,
				// goes to the stat class

				for(Bitmap b : bitmapsArray){
					b.recycle();
					b = null;
				}
				System.gc();

				customHandler.removeCallbacks(updateTimerThread);

				Timer t = new Timer();
				t.schedule(new TimerTask() {
					public void run() {

						milliseconds = (int) (updatedTime % 1000);
						String timeRecord = "" + mins + ":"
								+ String.format("%02d", secs) + ":"
								+ String.format("%03d", milliseconds);
						

						Bundle b = new Bundle();
						b.putString("TIME", timeRecord);
						Intent intent = new Intent(Play.this, Stat.class);
						intent.putExtras(b);
						startActivityForResult(intent, STAT_CODE);
					}
				}, 1500);



	}


}
