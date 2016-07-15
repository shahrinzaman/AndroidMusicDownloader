package com.basic.musicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;
import android.util.Log;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH = new String(Environment.getExternalStorageDirectory() + "/FreeMusic/");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	
	// Constructor
	public SongsManager(){
		
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList(){
		
		
		//File outputFile1 = new File(MEDIA_PATH);
		Log.d("songmanager", "Before creating file");
//		if(outputFile1.mkdirs()||outputFile1.isDirectory()){
		File home = new File(MEDIA_PATH);
			if(!home.isDirectory()) home.mkdirs();
		Log.d("songmanager", "after creating file");
		
		Log.d("songmanager", "outside creating file");
		
		if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
				song.put("songPath", file.getPath());
				
				// Adding each song to SongList
				if(!songsList.contains(song)){
				Log.d("Songmanager",song.get("songTitle"));
				songsList.add(song);
				}
			}
			
			Log.d("Songmanager",""+songsList.size());
			return songsList;
		}
		else{
			Log.d("Songmanager",""+"null returned");
			return null;
		}
			
		
		// return songs list array
		
	}
	
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}




/*
// this method creates a list of file extensions that will be searched
//
public static ArrayList<String> createAudioExtensionsList()
{
	ArrayList<String> extensions = new ArrayList<String>();

	extensions.add(".mp3");
	extensions.add(".ogg");
	extensions.add(".flac");
	extensions.add(".wav");
	extensions.add(".3gp");

	return extensions;
}

// simple method to get a file extension (does not work with complex extensions like .tar.gz)
//
public static String getFileExtension(String fileName)
{ 
	String result = fileName.substring(fileName.lastIndexOf(".")); 
	return result;
}

// recursively scans a folder for all files containing one of the file extensions
//
public static LinkedList<File> getFilesInFolder(File rootDir, ArrayList<String> extensions)
{
	LinkedList<File> results = new LinkedList<File>();
	File[] files = rootDir.listFiles();

	for(File file : files)
	{
		if(file.isDirectory())
			results.addAll(getFilesInFolder(file, extensions));
		else
		{
			for(String s : extensions)
			{
				String ext = getFileExtension(file.getName());
				if(s.equalsIgnoreCase(ext))
					results.add(file);
			}
		}
	}	
	return results;
}

*/