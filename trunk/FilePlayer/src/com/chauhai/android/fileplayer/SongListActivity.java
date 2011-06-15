package com.chauhai.android.fileplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.chauhai.android.fileplayer.util.DirectoryFilter;
import com.chauhai.android.fileplayer.util.FileNameComparator;
import com.chauhai.android.fileplayer.util.FileUtils;
import com.chauhai.android.fileplayer.util.MusicFileFilter;

public class SongListActivity extends Activity implements OnItemClickListener {
	/**
	 * The root directory that the application can access.
	 */
	private String rootPath = Environment.getExternalStorageDirectory() + "/Music";
	
	/**
	 * The view that display the current directory.
	 */
	private TextView currentDirectoryTextView;

	/**
	 * The view that display the song list.
	 */
	private ListView songListView;
	
	/**
	 * Current directory path.
	 */
	private String currentDirectoryPath = rootPath;
	
	/**
	 * Item list.
	 */
	private ArrayList<String> itemFileName;

	private ArrayList<MusicFile> musicFiles;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        // Get view objects.
        currentDirectoryTextView = (TextView) findViewById(R.id.currentDirectoryTextView);
        songListView = (ListView) findViewById(R.id.songListView);
        
        // Display the song list.
        displaySongList(currentDirectoryPath);
        songListView.setOnItemClickListener(this);
    }
    
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//      super.onConfigurationChanged(newConfig);
//      setContentView(R.layout.song_list);
//    }

    /**
     * Get all the music file in the specified directory, and display it into the list. 
     * @param dir
     */
    private void displaySongList(String dir) {
    	// Display the current directory path.
    	currentDirectoryPath = dir;
    	currentDirectoryTextView.setText(dir);
    	
    	// Get the file list.
    	getItemList(dir);
    	
    	// Display into the songListView
//    	for (MusicFile musicFile : musicFiles) {
//    		TableRow tableRow = new TableRow(this);
//    		ImageView imageView = new ImageView(this);
//    		int imageResource;
//    		if (musicFile.isDirectory()) {
//    			imageResource = R.drawable.ic_folder;
//    		} else if (musicFile.lyricsFileExist()) {
//    			imageResource = R.drawable.ic_lyrics;
//    		} else {
//    			imageResource = R.drawable.ic_music;
//    		}
//    		imageView.setImageResource(imageResource);
//    		tableRow.addView(imageView);
//    		TextView textView = new TextView(this);
//    		textView.setText(musicFile.getMusicFileName());
//    		tableRow.addView(textView);
//    		tableRow.setOnClickListener(this);
//    		songListView.addView(tableRow);
//    	}
    	ArrayAdapter<String> itemList = new ArrayAdapter<String>(this,
    			    android.R.layout.simple_list_item_1,
    			    itemFileName);
    	songListView.setAdapter(itemList);
    }

    /**
     * Get the list of items (files) in specified directory.
     * @param dirPath
     */
    private void getItemList(String dirPath) {
    	itemFileName = new ArrayList<String>();
    	musicFiles = new ArrayList<MusicFile>();
    	
    	// Get file list.
    	String[] musicFileExt = {"mp3", "wma"};
    	FileNameComparator fileNameComparator = new FileNameComparator();
    	ArrayList<File> files;
    	files = FileUtils.listFiles(dirPath, new DirectoryFilter(), fileNameComparator);
    	files.addAll(FileUtils.listFiles(dirPath, new MusicFileFilter(musicFileExt), fileNameComparator));

    	// Add file to list view.
    	Iterator<File> it = files.iterator();
    	while (it.hasNext()) {
    		File file = it.next();
    		addItemIntoList(file);
    	}

    	// Add parent directory.
    	if (!dirPath.equals(rootPath)) {
    		addItemIntoList(0, new File(new File(dirPath).getParent()));
    	}
    }

    private void addItemIntoList(File file) {
    	addItemIntoList(-1, file);
    }
    
    /**
     * Add a file into listview data.
     * @param index Ignore if -1.
     * @param file
     */
    private void addItemIntoList(int index, File file) {
    	MusicFile musicFile = new MusicFile(file);
    	String itemName;
    	if (musicFile.isDirectory()) {
    		itemName = "D";
    	} else if (musicFile.lyricsFileExist()) {
    		itemName = "L";
    	} else {
    		itemName = "M";
    	}
    	itemName = itemName + " " + musicFile.getMusicFileName();
    	if (index == -1) {
    		itemFileName.add(itemName);
    		musicFiles.add(musicFile);
    	} else {
    		itemFileName.add(index, itemName);
    		musicFiles.add(index, musicFile);
    	}
    }
    
    /**
     * Process when a file or directory is clicked.
     */
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		// Get path of the music file.
		String filePath = musicFiles.get(position).getMusicFilePath();
		File file = new File(filePath);
		
		if (file.isDirectory()) {
			// Open directory.
			if (file.canRead()) {
				displaySongList(filePath);
			}
		} else {
			// Play music file.
			if (file.canRead()) {
				Intent intent = new Intent(this, PlaySongActivity.class);
				intent.putExtra(PlaySongActivity.FILE_PATH, file.getPath());
				startActivity(intent);
			}
		}
	}
}