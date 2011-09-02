package com.chauhai.android.fileplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class SongListActivity extends ListActivity {
	
	private static final String DIR_PATH = "DIR_PATH";
	
	private static final String TAG = "SongListActivity";
	/**
	 * The root directory that the application can access.
	 */
	private String rootPath = Environment.getExternalStorageDirectory() + "/Music";
	
	/**
	 * The view that display the current directory.
	 */
	private TextView currentDirectoryTextView;

	/**
	 * Current directory path.
	 */
	private String currentDirectoryPath = rootPath;
	
	private List<MusicFile> musicFiles;
	
    /**
     * Call PalySongActivity to play a music file.
     * @param context
     * @param filePath Absolute path.
     */
    public static void listSongs(Context context, String dirPath) {
    	Log.d(TAG, "listSongs " + dirPath);
		Intent intent = new Intent(context, SongListActivity.class);
		intent.putExtra(SongListActivity.DIR_PATH, dirPath);
		context.startActivity(intent);
    }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        // Get view objects.
        currentDirectoryTextView = (TextView) findViewById(R.id.currentDirectoryTextView);
        
        // Display the song list.
        Bundle extras = getIntent().getExtras();
        currentDirectoryPath = extras != null ? extras.get(DIR_PATH).toString() : rootPath;
        displaySongList(currentDirectoryPath);
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
    	getMusicFiles(dir);
    	
    	SongListAdapter adapter = new SongListAdapter(this, musicFiles);
    	setListAdapter(adapter);
    }

    /**
     * Get the current directory path.
     * @return
     */
    public String getCurrentDirectoryPath() {
    	return currentDirectoryPath;
    }
    
    /**
     * Get the list of items (files) in specified directory,
     * set them to musicFiles.
     * 
     * @param dirPath
     */
    private void getMusicFiles(String dirPath) {
    	musicFiles = MusicFile.getMusciFiles(dirPath, true);
    	if (musicFiles == null) { // Music directory does not exist.
    		// Change empty message to "Directory does not exist"
    		((TextView) findViewById(android.R.id.empty)).setText(
    				String.format(getString(R.string.format_directory_not_exist), dirPath));
    		musicFiles = new ArrayList<MusicFile>();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.song_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.about:
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}