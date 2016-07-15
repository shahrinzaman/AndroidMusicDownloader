package com.abony.free.musicdownloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basic.musicplayer.AndroidBuildingMusicPlayerActivity;

public class RootFragmentController extends Fragment implements OnClickListener {

	EditText editText;
	Button button;
	ListView listView, listViewLeft;
	LeftRightSlider leftRightSlider;
	Fragment mFrag;
	SampleItemLeft sampleItemLeft;
	SampleAdapterLeft sampleAdapterLeft;
	Context leftRightSliderContext;
	SampleAdapter adapter;
	List<SampleItemLeft> listLeft;
	AndroidFileDownloader androidFileDownloader;
	AndroidBuildingMusicPlayerActivity androidBuildingMusicPlayerActivity;
	AlertDialog alertDialog;
	MediaPlayer player;
	int lastpos = -1;
	AsyncTask<Void, Void, String> taskAsyncPlayer;
	Handler handlerPlayer;

	AsyncTask<String, String, List<SampleItem>> downloadFileFromURL;

	public RootFragmentController(
			String str,
			LeftRightSlider leftRightSlider,
			AlertDialog alertDialog,
			AndroidBuildingMusicPlayerActivity androidBuildingMusicPlayerActivity) {
		this.leftRightSlider = leftRightSlider;
		leftRightSliderContext = leftRightSlider.getApplicationContext();
		this.mFrag = leftRightSlider.mFrag;
		this.androidBuildingMusicPlayerActivity = androidBuildingMusicPlayerActivity;
		this.alertDialog = alertDialog;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		leftRightSlider.setSupportProgressBarIndeterminateVisibility(false);

		adapter = new SampleAdapter(getActivity());

		editText = (EditText) getActivity().findViewById(R.id.inputSearch);
		InputMethodManager imm = (InputMethodManager) leftRightSlider
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

		// editText.setInputType(InputType.TYPE_NULL);

		// editText.setOnTouchListener(new View.OnTouchListener() {
		//
		// public boolean onTouch(View v, MotionEvent event) {
		//
		//
		// editText.setInputType(InputType.TYPE_CLASS_TEXT);
		// editText.onTouchEvent(event); // call native handler
		//
		//
		//
		// return true; // consume touch even
		// }
		//
		//
		// });

		button = (Button) getActivity().findViewById(R.id.inputButton);
		button.setOnClickListener(this);

		listViewLeft = (ListView) mFrag.getActivity().findViewById(
				R.id.listViewleft);

		listView = (ListView) this.getActivity().findViewById(R.id.listView);
		listView.setAdapter(adapter);
		listView.setClickable(true);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(leftRightSliderContext, "long clicked",
				// Toast.LENGTH_SHORT).show();
				return true;
			}

		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				SampleItemLeft sampleItemLeft = new SampleItemLeft(adapter
						.getItem(position).title_song, adapter
						.getItem(position).link_file1, adapter
						.getItem(position).iconRes, "In Queue...", 0);
				// leftRightSlider.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				// WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				// alertDialog.show();

				if (sampleAdapterLeft == null) {
					sampleAdapterLeft = new SampleAdapterLeft(
							leftRightSliderContext);
					listLeft = new ArrayList<RootFragmentController.SampleItemLeft>();

					sampleAdapterLeft.add(sampleItemLeft);
					listLeft.add(sampleItemLeft);

					listViewLeft.setAdapter(sampleAdapterLeft);
					androidFileDownloader = new AndroidFileDownloader(
							leftRightSliderContext, sampleAdapterLeft, listLeft);
					// androidFileDownloader.addToList(sampleItemLeft);
					androidFileDownloader.execute("");

					listViewLeft
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(
										AdapterView<?> parentIN, View viewIN,
										int positionIN, long idIN) {
									listLeft.remove(positionIN);
									sampleAdapterLeft.remove(sampleAdapterLeft
											.getItem(positionIN));
									sampleAdapterLeft.notifyDataSetChanged();
									Toast.makeText(leftRightSliderContext,
											"Item Removed.", Toast.LENGTH_SHORT)
											.show();

								}

							});

					// androidFileDownloader.addToList(sampleItemLeft);
					// if(!androidFileDownloader.isAlive())

				}

				else {

					sampleAdapterLeft.add(sampleItemLeft);
					listLeft.add(sampleItemLeft);

					sampleAdapterLeft.notifyDataSetChanged();
					// androidFileDownloader.addToList(sampleItemLeft);

					if (androidFileDownloader.getStatus().compareTo(
							Status.FINISHED) == 0) {
						// androidFileDownloader.cancel(true);
						androidFileDownloader = new AndroidFileDownloader(
								leftRightSliderContext, sampleAdapterLeft,
								listLeft);
						androidFileDownloader.execute("");
					}
				}
				// Toast.makeText( getActivity().getApplicationContext(),
				// position + "==" + parent.getAdapter().getItem(position)
				// .toString(), Toast.LENGTH_SHORT).show();

			}

		});

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == button.getId()) {

			InputMethodManager imm = (InputMethodManager) leftRightSlider
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

			String songToBeSEARCHED = editText.getText().toString()
					.replaceAll(" ", "%20");

			if (isNetworkAvailable()) {
				if (songToBeSEARCHED.equals(""))
					Toast.makeText(leftRightSliderContext,
							"Please Input To Search.", Toast.LENGTH_SHORT)
							.show();
				else {

					setlastposition(-1);
					killPlayer();
					downloadFileFromURL = new DownloadFileFromURL(
							getActivity(), listView, adapter, leftRightSlider);
					downloadFileFromURL.execute(songToBeSEARCHED);
				}
			} else {
				Toast.makeText(leftRightSliderContext,
						"Network is unavailable", Toast.LENGTH_LONG).show();
			}

		}

	}

	private boolean isNetworkAvailable() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) leftRightSlider
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;

	}

	public class SampleItem {
		public String title_song;
		public String title_song_info;
		public int iconRes;
		public String link_file1;

		public SampleItem(String title_song, int iconRes,
				String title_song_info, String link_file1) {
			this.title_song = title_song;
			this.iconRes = iconRes;
			this.title_song_info = title_song_info;
			this.link_file1 = link_file1;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return link_file1;
		}
	}

	static class ImageViewHolder {

		public ImageView image;
		public TextView title;
		public TextView title_info;
		public boolean isPlaying;
	}

	class SampleAdapter extends ArrayAdapter<SampleItem> {

		int play_icon_id = android.R.drawable.ic_media_play;
		int pause_icon_id = android.R.drawable.ic_media_pause;

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(final int position, View convertView,
				final ViewGroup parent) {

			final ImageViewHolder imageViewHolder;
			final View conView = convertView;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row, null);
				imageViewHolder = new ImageViewHolder();

				imageViewHolder.image = (ImageView) convertView
						.findViewById(R.id.row_icon);
				imageViewHolder.title = (TextView) convertView
						.findViewById(R.id.row_title);
				imageViewHolder.title_info = (TextView) convertView
						.findViewById(R.id.row_info);
				imageViewHolder.isPlaying = false;
				convertView.setTag(imageViewHolder);
			} else {
				imageViewHolder = (ImageViewHolder) convertView.getTag();
			}

			final ImageView icon = imageViewHolder.image;

			if (position == getlastposition()) {
				icon.setImageResource(pause_icon_id);
				// imageViewHolder.isPlaying = false;
			} else {

				icon.setImageResource(play_icon_id);
				// imageViewHolder.isPlaying = true;
			}

			// *****************************************************

			icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					handlerPlayer = new Handler();

					taskAsyncPlayer = new AsyncTask<Void, Void, String>() {

						protected void onPreExecute() {
							handlerPlayer.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (getlastposition() != position) {
										setlastposition(position);
										Toast.makeText(leftRightSliderContext,
												"Buffering n Playing...",
												Toast.LENGTH_LONG).show();
										notifyDataSetChanged();

									} else {
										setlastposition(-1);
										Toast.makeText(leftRightSliderContext,
												"Stopping Song.",
												Toast.LENGTH_LONG).show();
										notifyDataSetChanged();
									}

								}
							});

						};

						@Override
						protected String doInBackground(Void... params) {
							try {

								String link = adapter.getItem(position).link_file1;
								if (getlastposition() == -1) {
									killPlayer();
									return "success";
								} else {
									killPlayer();
									player = new MediaPlayer();

									player.setDataSource(link);
									player.prepare();
									player.start();

									return "success";
								}

							} catch (Exception mpex) {

								return mpex.toString();

							}

						}

						protected void onPostExecute(String result) {
							if (!result.equals("success")) {
								Toast.makeText(leftRightSliderContext,
										"Link dead,Couldn't be played.",
										Toast.LENGTH_LONG).show();

								setlastposition(-1);
								notifyDataSetChanged();

							}

						};

					};

					taskAsyncPlayer.execute();
				}
			});

			TextView title = imageViewHolder.title;
			title.setText(getItem(position).title_song);
			TextView title_info = imageViewHolder.title_info;

			title_info.setText(getItem(position).title_song_info);

			return convertView;
		}

	}

	@Override
	public void onDestroy() {
		killPlayer();
		super.onDestroy();
	}

	private int getlastposition() {

		return lastpos;
	}

	private void setlastposition(int posLast) {
		lastpos = posLast;
	}

	private void killPlayer() {
		try {
			if (player != null)
				player.release();
		} catch (Exception exx) {

		}

	}

	class DownloadFileFromURL extends
			AsyncTask<String, String, List<SampleItem>> {

		private String link = "http://mp3skull.com/mp3/";
		private FragmentActivity activity;
		ListView listView;
		private int number_OF_RESULT = 30;
		SampleAdapter sampleAdapter;
		LeftRightSlider leftRightSlider;
		List<SampleItem> list = new ArrayList<SampleItem>();
		int mProgress = 0;

		public DownloadFileFromURL(FragmentActivity activity,
				ListView listView, SampleAdapter adapter2,
				LeftRightSlider leftRightSlider) {

			this.leftRightSlider = leftRightSlider;

		}

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			adapter.clear();
			leftRightSlider.setSupportProgressBarIndeterminateVisibility(true);

			leftRightSlider.setSupportProgress(0);

		}

		/**
		 * Downloading file in background thread
		 * */
		@Override
		protected List<SampleItem> doInBackground(String... f_url) {

			try {
				String url = link + f_url[0] + ".html";

				Connection conn = Jsoup.connect(url).timeout(10000);

				Document document = conn.get();
				if (document.getElementById("content") != null) {

					for (int i = 1; i < number_OF_RESULT + 1; i++) {

						mProgress += i;

						// Normalize our progress along the progress bar's scale

						int progress = (Window.PROGRESS_END - Window.PROGRESS_START)
								/ 100 * mProgress;

						publishProgress(Integer.toString(progress));
						String name_file1 = "";
						String link_file = "";
						String info_file = "";

						info_file = document.getElementsByClass("show" + i)
								.get(0).child(0).text();
						// status info,size,bitrate
						String name_file = document
								.getElementsByClass("show" + i).get(0).child(1)
								.children().get(0).text();
						name_file1 = name_file;
						// song name

						link_file = document.getElementsByClass("show" + i)
								.get(0).getElementsByAttribute("href").get(0)
								.absUrl("href");

						String link_file1 = StringEscapeUtils
								.unescapeJava(link_file);

						SampleItem item = new SampleItem(name_file1,
								android.R.drawable.ic_media_play, info_file,
								link_file1);
						list.add(item);

					}

					if (!list.isEmpty()) {

						return list;
					}
				}

			}

			catch (Exception e) {
				return null;
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			leftRightSlider.setSupportProgress(Integer.parseInt(values[0]));
		}

		/**
		 * Updating progress bar
		 * */

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(List<SampleItem> list) {

			if (list != null) {
				for (SampleItem item : list)
					adapter.add(item);
				adapter.notifyDataSetChanged();

			}

			else {
				Toast.makeText(leftRightSliderContext, "No Results",
						Toast.LENGTH_SHORT).show();
			}
			leftRightSlider.setSupportProgressBarIndeterminateVisibility(false);
		}

	}

	public class SampleItemLeft {
		public String tag;
		public int iconRes;
		public int progress_val;
		public String download_stat;
		public String link_file1;

		public SampleItemLeft(String tag, String link_file1, int iconRes,
				String download_stat, int progress_val) {
			this.tag = tag;
			this.iconRes = iconRes;
			this.link_file1 = link_file1;
			this.download_stat = download_stat;
			this.progress_val = progress_val;
		}
	}

	class SampleAdapterLeft extends ArrayAdapter<SampleItemLeft> {

		public SampleAdapterLeft(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.rowleft, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView
					.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			TextView download_stat = (TextView) convertView
					.findViewById(R.id.download_stat);
			download_stat.setText(getItem(position).download_stat);

			return convertView;
		}

	}

	class AndroidFileDownloader extends AsyncTask<String, String, String> {

		private static final int DOWNLOAD_BUFFER_SIZE = 4096;

		SampleAdapterLeft sampleAdapterLeft;
		List<SampleItemLeft> listLeftAndro;
		ProgressBar progressBar;
		TextView songname, song_progressText;
		String fileName;
		File outputFile;

		public AndroidFileDownloader(Context leftRightSliderContext,
				SampleAdapterLeft sampleAdapterLeft,
				List<SampleItemLeft> listLeft) {

			this.sampleAdapterLeft = sampleAdapterLeft;

			Log.d("Before", "Adapter:"
					+ sampleAdapterLeft.getItem(0).link_file1);
			listLeftAndro = listLeft;

			progressBar = (ProgressBar) leftRightSlider
					.findViewById(R.id.song_progressbar);

			songname = (TextView) leftRightSlider.findViewById(R.id.song_name);
			song_progressText = (TextView) leftRightSlider
					.findViewById(R.id.song_progressText);
		}

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			songname.setText(sampleAdapterLeft.getItem(0).tag);
			song_progressText.setText("Connecting...");
			sampleAdapterLeft.remove(sampleAdapterLeft.getItem(0));
			sampleAdapterLeft.notifyDataSetChanged();
			// showDialog(progress_bar_type);
		}

		/**
		 * Downloading file in background thread
		 * */
		@Override
		protected String doInBackground(String... f_url) {
			String url1 = listLeftAndro.get(0).link_file1;
			// get the filename
			int lastSlash = url1.lastIndexOf('.');
			fileName = "file.bin";
			if (lastSlash >= 0) {
				fileName = listLeftAndro.get(0).tag + url1.substring(lastSlash);
			}
			if (fileName.equals("")) {
				fileName = "file.mp3";
			}
			File outputFile1 = new File(
					Environment.getExternalStorageDirectory() + "/FreeMusic/");
			if (outputFile1.mkdirs() || outputFile1.isDirectory())
				outputFile = new File(outputFile1, fileName);
			else
				return "Unable to Create or find File";

			try {
				URL url = new URL(url1);
				HttpURLConnection conection = (HttpURLConnection) url
						.openConnection();
				conection.setConnectTimeout(10000);

				conection.connect();

				// getting file length
				int lenghtOfFile = conection.getContentLength();

				// if (lenghtOfFile < (DOWNLOAD_BUFFER_SIZE * 20))
				// throw new Exception("File too short");

				// input stream to read file - with 8k buffer
				BufferedInputStream input = new BufferedInputStream(
						url.openStream());

				// Output stream to write file
				OutputStream output = new FileOutputStream(outputFile);

				BufferedOutputStream outStream = new BufferedOutputStream(
						output, DOWNLOAD_BUFFER_SIZE);
				byte data[] = new byte[DOWNLOAD_BUFFER_SIZE];

				int bytesRead = 0, totalRead = 0;
				String progress = "";
				while ((bytesRead = input.read(data, 0, data.length)) >= 0) {
					// if(isCancelled()) {
					//
					// outStream.close();
					// throw new Exception("Canceled by User");
					// }
					outStream.write(data, 0, bytesRead);

					// update progress bar
					totalRead += bytesRead;
					progress = "" + (int) ((totalRead * 100) / lenghtOfFile);
					if (Integer.valueOf(progress) > 0)
						publishProgress(progress);

				}

				// flushing output
				output.flush();
				// closing streams
				output.close();
				input.close();

				conection.disconnect();

				return "done";
			} catch (Exception e) {

				outputFile.delete();
				Log.e("Error: ", e.getMessage());
				return e.getMessage();
			}

		}

		/**
		 * Updating progress bar
		 * */

		@Override
		protected void onProgressUpdate(String... progress) {

			// setting progress percentage
			progressBar.setProgress(Integer.parseInt(progress[0]));

			song_progressText.setText(progress[0] + "%");
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded

			if (file_url.equals("done")) {
				androidBuildingMusicPlayerActivity.updateList();
				Toast.makeText(leftRightSliderContext, "Download Completed",
						Toast.LENGTH_SHORT).show();
				song_progressText.setText("Downloaded.");

				if (listLeftAndro.size() == 1) {
					listLeftAndro.remove(0);
				} else if (listLeftAndro.size() > 1) {
					listLeftAndro.remove(0);
					androidFileDownloader = new AndroidFileDownloader(
							leftRightSliderContext, sampleAdapterLeft,
							listLeftAndro);
					androidFileDownloader.execute("");
				}

			} else {
				song_progressText.setText("Failed.");
				Toast.makeText(leftRightSliderContext, file_url,
						Toast.LENGTH_LONG).show();
				if (listLeftAndro.size() == 1) {
					listLeftAndro.remove(0);
				} else if (listLeftAndro.size() > 1) {
					listLeftAndro.remove(0);
					androidFileDownloader = new AndroidFileDownloader(
							leftRightSliderContext, sampleAdapterLeft,
							listLeftAndro);
					androidFileDownloader.execute("");
				}

			}
			sampleAdapterLeft.notifyDataSetChanged();
		}

	}

}
