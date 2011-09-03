package com.chauhai.android.fileplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class SongListActivity extends ListActivity {
	
	/**
	 * Key to save the last opened directory.
	 */
	private static final String PREF_CURRENT_DIRECTORY_PATH = "CURRENT_DIRECTORY_PATH";
	
	private static final String TAG = "SongListActivity";

	/**
	 * The root directory that the application can access.
	 */
	private String rootPath = Environment.getExternalStorageDirectory().toString();
	
	/**
	 * The view that display the current directory.
	 */
	private TextView currentDirectoryTextView;

	/**
	 * Current directory path.
	 */
	private String currentDirectoryPath = rootPath;
	
	private List<MusicFile> musicFiles;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);

        // Get view objects.
        currentDirectoryTextView = (TextView) findViewById(R.id.currentDirectoryTextView);
        
        // Display the song list.
        getDirectoryToOpen();
        displaySongList(currentDirectoryPath);
    }
    
    /**
     * Save the currentDirectoryPath as the last opened directory.
     */
    private void setLastOpenedDirectory()
    {
    	Log.d(TAG, "Set last opened directory to " + currentDirectoryPath);
    	SharedPreferences settings = getPreferences(0);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putString(PREF_CURRENT_DIRECTORY_PATH, currentDirectoryPath);
    	editor.commit();
    }

    /**
     * Get the last opened directory from the shared preferences.
     * @return
     */
    private String getLastOpenedDirectory()
    {
//    	if (true) return rootPath + "/Music/album";
    	String lastOpenedDir = getPreferences(0).getString(PREF_CURRENT_DIRECTORY_PATH, rootPath);
    	Log.d(TAG, "getLastOpenedDirectory = " + lastOpenedDir);
    	return lastOpenedDir;
    }
    
    /**
     * Get the directory to open.
     * First, check if the parameter is passed via intent. If not, use the
     * last opened directory or the rootPath if not specified.
     * If the specified directory does not exist, then use its parent directory.
     */
    private void getDirectoryToOpen()
    {
    	Log.d(TAG, "getDirectoryToOpen");
    	String currentDirectory = getLastOpenedDirectory();
    	// If the specified directory does not exist (for example,
    	// the lastOpenDirectory is deleted), check its parents.
        File currentDir = new File(currentDirectory);
        while (currentDir != null) {
        	if (currentDir.exists() && currentDir.isDirectory()) {
        		break;
        	}
        	Log.w(TAG, "Don't find directory " + currentDir.getPath() + ". Check its parent.");
        	currentDir = currentDir.getParentFile();
        }
    	// If not specified, then use rootPath.
        currentDirectoryPath = currentDir != null ? currentDir.getAbsolutePath() : rootPath;
        Log.i(TAG, "Directory to open: " + currentDirectoryPath);
    }
    
    /**
     * Get all the music file in the specified directory, and display it into the list. 
     * @param dir
     */
    protected void displaySongList(String dir) {
    	// Display the current directory path.
    	currentDirectoryPath = dir;
    	currentDirectoryTextView.setText(dir);
    	
    	// Get the file list.
    	getMusicFiles(dir);
    	
    	SongListAdapter adapter = new SongListAdapter(this, musicFiles);
    	setListAdapter(adapter);

    	// Set the last opened directory to currentDirectoryPath.
    	setLastOpenedDirectory();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	// Do not go to the parent directory if reach the rootPath
        	// (so it will be closed by the default BACK button processing).
        	if (!currentDirectoryPath.equals(rootPath)) {
            	// Open the parent directory.
        		Log.d(TAG, "Goto the parent directory");
	        	File parentDir = new File(currentDirectoryPath).getParentFile();
	        	if (parentDir != null) {
	        		// Open new SongListActivity
	        		displaySongList(parentDir.getAbsolutePath());
	                return true;
	        	}
        	}
        }
        return super.onKeyDown(keyCode, event);
    }
    
    /**
     * Create menu when user clicks the MENU button the first time.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.song_list, menu);
        return true;
    }
    
    /**
     * Handle menu when the user click MENU button.
     */
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