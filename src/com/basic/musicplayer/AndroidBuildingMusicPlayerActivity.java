package com.basic.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abony.free.musicdownloader.R;

public class AndroidBuildingMusicPlayerActivity extends Fragment implements
		OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private MediaPlayer mp;
//	private WifiLock wifiLock;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0;
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList;
	String title;

	public AndroidBuildingMusicPlayerActivity(String str) {
		title = str;
		songsList = new ArrayList<HashMap<String, String>>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(com.rafsun.free.musicdownloader.R.layout.player,container,false);
//		return inflater.inflate(R.layout.player, container, false);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// All player buttons
		btnPlay = (ImageButton) getActivity().findViewById(R.id.btnPlay);
		btnForward = (ImageButton) getActivity().findViewById(R.id.btnForward);
		btnBackward = (ImageButton) getActivity()
				.findViewById(R.id.btnBackward);
		btnNext = (ImageButton) getActivity().findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) getActivity()
				.findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) getActivity()
				.findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) getActivity().findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) getActivity().findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) getActivity().findViewById(
				R.id.songProgressBar);
		songTitleLabel = (TextView) getActivity().findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) getActivity().findViewById(
				R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) getActivity().findViewById(
				R.id.songTotalDurationLabel);

		// Mediaplayer
		mp = new MediaPlayer();

//		wifiLock = ((WifiManager) getSherlockActivity().getSystemService(Context.WIFI_SERVICE))
//			    .createWifiLock(WifiManager.WIFI_MODE_FULL, "rafsun_lock");
//
//		wifiLock.acquire();

		songManager = new SongsManager();
		utils = new Utilities();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important

		// Getting all songs list
		if(songManager.getPlayList() != null){

		songsList = songManager.getPlayList();
		Log.d("AndroidBIG","size in BIG "+songsList.size());

		addAllListener();
		// By default play first song
		playSong(0);
		mp.pause();
		btnPlay.setImageResource(R.drawable.btn_play);
		}
		/**
		 * Play button click event plays a song and changes button to pause
		 * image pauses a song and changes button to play image
		 * */


	}

	private void addAllListener() {
		// TODO Auto-generated method stub
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check for already playing
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				} else {
					// Resume song
					if (mp != null) {
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
//					else if(songsList!= null){
//						playSong(0);
//					}
				}

			}
		});

		/**
		 * Forward button click event Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if (currentPosition + seekForwardTime <= mp.getDuration()) {
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				} else {
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});

		/**
		 * Backward button click event Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if (currentPosition - seekBackwardTime >= 0) {
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				} else {
					// backward to starting position
					mp.seekTo(0);
				}

			}
		});

		/**
		 * Next button click event Plays next song by taking currentSongIndex +
		 * 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if (currentSongIndex < (songsList.size() - 1)) {
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				} else {
					// play first song
					playSong(0);
					currentSongIndex = 0;
				}

			}
		});

		/**
		 * Back button click event Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (currentSongIndex > 0) {
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				} else {
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}

			}
		});

		/**
		 * Button Click event for Repeat button Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isRepeat) {
					isRepeat = false;
					Toast.makeText(getActivity().getApplicationContext(),
							"Repeat is OFF", Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				} else {
					// make repeat to true
					isRepeat = true;
					Toast.makeText(getActivity().getApplicationContext(),
							"Repeat is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isShuffle = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}
			}
		});

		/**
		 * Button Click event for Shuffle button Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isShuffle) {
					isShuffle = false;
					Toast.makeText(getActivity().getApplicationContext(),
							"Shuffle is OFF", Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				} else {
					// make repeat to true
					isShuffle = true;
					Toast.makeText(getActivity().getApplicationContext(),
							"Shuffle is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isRepeat = false;
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}
			}
		});

		/**
		 * Button Click event for Play list click event Launches list activity
		 * which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Intent i = new Intent(getActivity(),
//						PlayListActivity.class);
//				startActivityForResult(i, 100);
			}
		});
	}

	/**
	 * Receiving song index from playlist view and play the song
	 * */
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == 100) {
//			currentSongIndex = data.getExtras().getInt("songIndex");
//			// play selected song
//			playSong(currentSongIndex);
//		}
//
//	}

	/**
	 * Function to play a song
	 *
	 * @param songIndex
	 *            - index of song
	 * */
	public void playSong(int songIndex) {
		// Play song
		try {
			//mHandler.removeCallbacks(mUpdateTimeTask);

			mp.reset();

			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();

//			mp.setWakeMode(getSherlockActivity(), PowerManager.FULL_WAKE_LOCK);
			mp.start();
//			mp.setScreenOnWhilePlaying(true);
//			WakeLock mWakeLock = ((PowerManager) getSherlockActivity().getSystemService(Context.POWER_SERVICE))
//				    .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
//			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
			songTitleLabel.setText(songTitle);

			// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);

			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);

			// Updating progress bar
			updateProgressBar();
		} catch (IllegalArgumentException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (IllegalStateException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	public Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			try {
				long totalDuration =  mp.getDuration();

				long currentDuration = mp.getCurrentPosition();

				// Displaying Total Duration time
				songTotalDurationLabel.setText(""
						+ utils.milliSecondsToTimer(totalDuration));
				// Displaying time completed playing
				songCurrentDurationLabel.setText(""
						+ utils.milliSecondsToTimer(currentDuration));

				// Updating progress bar
				int progress = (int) (utils.getProgressPercentage(
						currentDuration, totalDuration));
				// Log.d("Progress", ""+progress);
				songProgressBar.setProgress(progress);

				// Running this thread after 100 milliseconds
				mHandler.postDelayed(this, 100);

			} catch (Exception ex) {
				//mp.reset();
				//currentSongIndex = 0;
				//Log.d("DURATION", ex.getMessage());
			}
		}
	};

	/**
	 *
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);

		// forward or backward to certain seconds
		mp.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}

	/**
	 * On Song Playing completed if repeat is ON play same song again if shuffle
	 * is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {

		// check for repeat is ON or OFF
		if (isRepeat) {
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if (isShuffle) {
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else {
			// no repeat or shuffle ON - play next song
			if (currentSongIndex < (songsList.size() - 1)) {
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			} else {
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}

//	@Override
//	public void onStop() {
//		// TODO Auto-generated method stub
//		super.onStop();
//		mp.reset();
//	}



	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {


			//mp.reset();
//			if(mp != null && mp.isPlaying()) mp.stop();


			mp.release();
//			wifiLock.release();

		} catch (Exception e) {
			Log.d("On RELEASE", e.getMessage());
		}
	}
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		try {
//			mHandler.removeCallbacks(mUpdateTimeTask);
//
//			//mp.reset();
//			if(mp != null && mp.isPlaying()) mp.stop();
//			mp.release();
//		} catch (Exception e) {
//			Log.d("On RELEASE", e.getMessage());
//		}
//	}

	public void updateList() {
		songManager  = new SongsManager();
		songsList = songManager.getPlayList();
		Log.d("AndroidBIG","size in BIG "+songsList.size());

		// By default play first song
		playSong(0);
		mp.pause();
		btnPlay.setImageResource(R.drawable.btn_play);


	}

}
