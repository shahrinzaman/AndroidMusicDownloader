package com.abony.free.musicdownloader;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Toast;

import com.basic.musicplayer.AndroidBuildingMusicPlayerActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class LeftRightSlider extends BaseActivity {



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setSupportProgressBarIndeterminateVisibility(false);
		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		setContentView(R.layout.content_frame);

//		FrameLayout fl = (FrameLayout) findViewById(android.R.id.custom);
//		 fl.addView(findViewById(R.layout.dialoglayout));

		 AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getApplicationContext());
		 //dialogBuilder.setView(findViewById(R.layout.dialoglayout));
		 dialogBuilder.setTitle("Test dialog");
		 dialogBuilder.setMessage("GO GO GO ONNNNNNN");
		 AlertDialog alertDialog = dialogBuilder.create();
		 //dialogBuilder.create();
//		 registerForContextMenu(findViewById(R.id.listView).);
		AndroidBuildingMusicPlayerActivity androidBuildingMusicPlayerActivity = new AndroidBuildingMusicPlayerActivity("right");
		getSupportFragmentManager()
		.beginTransaction()

		.replace(R.id.content_frame, new RootFragmentController("MainMENU" ,this,alertDialog,androidBuildingMusicPlayerActivity ))
		.commit();

		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_two,androidBuildingMusicPlayerActivity )
		.commit();
	}



	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("One");
        menu.add("Two");
        menu.add("Three");
        menu.add("Four");
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        //Note how this callback is using the fully-qualified class name
        Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

}
