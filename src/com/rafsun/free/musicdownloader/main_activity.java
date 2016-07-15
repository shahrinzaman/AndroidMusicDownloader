package com.rafsun.free.musicdownloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class main_activity extends ActionBarActivity {

	private static final String PREFS_NAME = "FreeMusic";
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences preferences = getSharedPreferences(PREFS_NAME,
				MODE_PRIVATE);
		boolean helpExits = preferences.getBoolean("exits", false);

		if (!helpExits) {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("exits", true);
			editor.commit();
			intent = new Intent(this, HelpActivity.class);
			startActivity(intent);

		} else {

			intent = new Intent(this, LeftRightSlider.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
}
