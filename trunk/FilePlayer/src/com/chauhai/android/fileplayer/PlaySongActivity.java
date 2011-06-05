package com.chauhai.android.fileplayer;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chauhai.android.fileplayer.util.FileUtils;

public class PlaySongActivity extends Activity implements OnClickListener {
	
	public static final String FILE_PATH = "FILE_PATH";
	
	/**
	 * Path to the music file.
	 */
	private String filePath;
	
	/**
	 * Path to the lyrics file.
	 */
	private String lyricsFilePath;
	
	private TextView songInfoTextView;
	private TextView songLyricsTextView;
	private EditText songLyricsEditText;

	private ImageButton songEditLyricsButton;
	private ImageButton songSaveLyricsButton;
	private ImageButton songSearchLyricsButton;
	
    private MediaPlayer mediaPlayer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_song);

        getViewObjects();
        
        
    	playFile(getIntent().getExtras().get(FILE_PATH).toString());
    }
    
    private void playFile(String filePath) {
        // Get file path from parameters.
    	this.filePath = getIntent().getExtras().get(FILE_PATH).toString();
        // Play music file
        try {
			mediaPlayer.setDataSource(filePath);
	        mediaPlayer.prepare();
	        mediaPlayer.start();

	        // Display info
	    	displaySongInfo();
	    	// Display lyrics
	    	displayLyrics();
	    	// Set buttons.
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void displaySongInfo() {
    	// Display info
        songInfoTextView.setText(filePath);
    }
    
    private void displayLyrics() {
    	songLyricsTextView.setText("");
    	lyricsFilePath = lyricsFileName();
    	if (new File(lyricsFilePath).exists()) {
    		try {
				songLyricsTextView.setText(FileUtils.fileGetContents(lyricsFilePath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    private String lyricsFileName() {
    	File musicFile = new File(filePath);
    	return musicFile.getParent() + "/" + FileUtils.fileNameWithoutExtension(musicFile) + ".txt";
    }

    private void getViewObjects() {
        // Get view objects.
        songInfoTextView = (TextView) findViewById(R.id.songInfoTextView);
        songLyricsTextView = (TextView) findViewById(R.id.songLyricsTextView);
        songLyricsTextView.setMovementMethod(new ScrollingMovementMethod());
        songLyricsEditText = (EditText) findViewById(R.id.songLyricsEditText);
        mediaPlayer = new MediaPlayer();

        songEditLyricsButton = (ImageButton) findViewById(R.id.songEditLyricsButton);
        songEditLyricsButton.setOnClickListener(this);

        songSaveLyricsButton = (ImageButton) findViewById(R.id.songSaveLyricsButton);
        songSaveLyricsButton.setOnClickListener(this);
        
        songSearchLyricsButton = (ImageButton) findViewById(R.id.songSearchLyricsButton);
        songSearchLyricsButton.setOnClickListener(this);

    }
    
    /**
     * Stop the media player when stopping.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
        	mediaPlayer.release();
        	mediaPlayer = null;
        }

    }

	@Override
	public void onClick(View v) {
		if (v == songEditLyricsButton) {
			startEditLyrics();
		} else if (v == songSaveLyricsButton) {
			saveLyrics();
		} else if (v == songSearchLyricsButton) {
			// Open web search for the file name.
			openURL("http://www.google.com/m?q=" +
					URLEncoder.encode(FileUtils.fileNameWithoutExtension(new File(filePath))));
		}
	}

	/**
	 * Allow user to edit lyrics.
	 */
	private void startEditLyrics() {
		// Copy text to edit lyrics.
		songLyricsEditText.setText(songLyricsTextView.getText());
		// Hide view lyrics and edit button.
		songEditLyricsButton.setVisibility(View.GONE);
		songLyricsTextView.setVisibility(View.GONE);
		// Show edit lyrics and save button.
		songSaveLyricsButton.setVisibility(View.VISIBLE);
		songLyricsEditText.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Save lyrics.
	 */
	private void saveLyrics() {
		// Copy text to edit lyrics.
		songLyricsTextView.setText(songLyricsEditText.getText());
		// Save lyrics to file.
		try {
			FileUtils.filePutContents(lyricsFilePath, songLyricsEditText.getText().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Hide edit lyrics and save button.
		songSaveLyricsButton.setVisibility(View.GONE);
		songLyricsEditText.setVisibility(View.GONE);
		// Show view lyrics and edit button.
		songEditLyricsButton.setVisibility(View.VISIBLE);
		songLyricsTextView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Open a url by web browser.
	 * @param url
	 */
	private void openURL(String url) {
		try {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		} catch(Exception e) {
		}
	}
}
