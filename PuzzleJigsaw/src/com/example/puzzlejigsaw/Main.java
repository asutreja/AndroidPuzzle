package com.example.puzzlejigsaw;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Main extends Activity {

	SharedPreferences prefs;
	private boolean continueMusic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}

	public void goStat(View v){
		continueMusic = true;
		Intent intent = new Intent(this,Stat.class);
		startActivity(intent);
	}

	public void goSetting(View v){


		continueMusic = true;
		String a = prefs.getString("difficultySlide", "1");
		Log.i("DD", a);


		Intent intent = new Intent(this,Setting.class);
		startActivity(intent);
	}

	public void goPlay(View v){
		String lvl = prefs.getString("difficultySlide", "1");
		continueMusic = true;
		Intent intent = new Intent(this,Play.class);
		Bundle b = new Bundle();
		b.putString("LEVEL", lvl);
		intent.putExtras(b);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!continueMusic) {
			MusicManager.pause();
		}
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		continueMusic = false;
		MusicManager.start(this, MusicManager.MUSIC_MENU);
	}

}
