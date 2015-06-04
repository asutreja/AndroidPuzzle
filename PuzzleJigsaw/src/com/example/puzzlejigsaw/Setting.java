package com.example.puzzlejigsaw;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public class Setting extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
	}

	class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.difficulty_setting);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MusicManager.pause();
	}


	@Override
	protected void onResume() {
		super.onResume();
		MusicManager.start(this, MusicManager.MUSIC_MENU);
	}


}