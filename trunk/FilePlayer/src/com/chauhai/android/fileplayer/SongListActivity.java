package com.chauhai.android.fileplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
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
	private String currentDirectoryPath;
	
	/**
	 * Item list.
	 */
	private ArrayList<String> itemFileName;

	/**
	 * Full path of the items.
	 */
	private ArrayList<String> itemFilePath;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        // Get view objects.
        currentDirectoryTextView = (TextView) findViewById(R.id.currentDirectoryTextView);
        songListView = (ListView) findViewById(R.id.songListView);
        
        // Display the song list.
        displaySongList(rootPath);
        songListView.setOnItemClickListener(this);
    }
    
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
    	itemFilePath = new ArrayList<String>();
    	
    	// Get file list.
    	String[] musicFileExt = {"mp3"};
    	FileNameComparator fileNameComparator = new FileNameComparator();
    	ArrayList<File> files;
    	files = FileUtils.listFiles(dirPath, new DirectoryFilter(), fileNameComparator);
    	files.addAll(FileUtils.listFiles(dirPath, new MusicFileFilter(musicFileExt), fileNameComparator));

    	// Add file to list view.
    	Iterator<File> it = files.iterator();
    	while (it.hasNext()) {
    		File file = it.next();
    		String fileName = file.isDirectory() ? file.getName() + "/" : file.getName();
    		itemFileName.add(fileName);
    		itemFilePath.add(file.getPath());
    	}

    	// Add parent directory.
    	if (!dirPath.equals(rootPath)) {
    		itemFileName.add(0, "../");
    		itemFilePath.add(0, new File(dirPath).getParent());
    	}
    }

    /**
     * Process when a file or directory is clicked.
     */
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		// Get path of the musci file.
		String filePath = itemFilePath.get(position);
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