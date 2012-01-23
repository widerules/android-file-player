package com.chauhai.android.fileplayer.playsong;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chauhai.android.fileplayer.R;
import com.chauhai.android.fileplayer.R.drawable;
import com.chauhai.android.fileplayer.R.id;
import com.chauhai.android.fileplayer.R.layout;
import com.chauhai.android.fileplayer.R.string;
import com.chauhai.android.fileplayer.util.FileUtils;
import com.chauhai.android.fileplayer.util.MusicFile;

public class PlaySongActivity extends Activity implements OnClickListener, OnCompletionListener {

  private static final String TAG = "PlaySongActivity";

  private static final String PARAM_FILE_PATH = "FILE_PATH";

  private static final int PROGRESS_BAR_UPDATE_TIME = 1000; // 1 second.

  /**
   * Do not repeat playing song.
   */
  private static final int REPEAT_OFF = 0;

  /**
   * Repeat playing current song.
   */
  private static final int REPEAT_ONE = 1;


  /**
   * Repeat playing all songs.
   */
  private static final int REPEAT_ALL = 2;

  /**
   * Music, lyrics file information.
   */
  private MusicFile musicFile;

  private TextView songInfoTextView;
  private LyricsView songLyricsView;
  private EditText songLyricsEditText;

  private ImageButton songEditLyricsButton;
  private ImageButton songSaveLyricsButton;
  private ImageButton songSearchLyricsButton;
  private ImageButton songRepeatButton;

  /**
   * The ProgressBar view.
   */
  private ProgressBar progressBar;

  /**
   * Handler to run updating of progress bar.
   */
  private Handler handler = new Handler();

  /**
   * The thread that update the progress bar.
   */
  private Runnable updateProgressBar;

  /**
   * Display MediaPlayer current position.
   */
  private TextView progressText;

  private MediaPlayer mediaPlayer;

  /**
   * Music file's duration.
   */
  private int mediaPlayerDuration;

  /**
   * Repeat status.
   */
  private int repeat;

  /**
   * Call PalySongActivity to play a music file.
   * @param context
   * @param filePath Absolute path.
   */
  public static void playSong(Context context, String filePath) {
    Log.d(TAG, "playSong " + filePath);
    Intent intent = new Intent(context, PlaySongActivity.class);
    intent.putExtra(PlaySongActivity.PARAM_FILE_PATH, filePath);
    context.startActivity(intent);
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.play_song);

    // Initiate MediaPlayer
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setOnCompletionListener(this);

    getViewObjects();
    setRepeat(REPEAT_OFF);

    // Play the music file.
    String filePath = getIntent().getExtras().get(PARAM_FILE_PATH).toString();
    musicFile = new MusicFile(filePath);
    playFile(musicFile.getMusicFilePath());

    // Update the progress bar.
    startUpdateProgressBar();
  }

  /**
   * Play a music file.
   * @param filePath The path to the music file.
   */
  private void playFile(String filePath) {
    // Play music file
    try {
      mediaPlayer.reset();
      mediaPlayer.setDataSource(filePath);
      mediaPlayer.prepare();
      mediaPlayerDuration = mediaPlayer.getDuration();
      mediaPlayer.start();

      // Display info
      displaySongInfo();
      // Display lyrics
      displayLyrics();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void displaySongInfo() {
    // Display info
    songInfoTextView.setText(musicFile.getMusicFilePath());
  }

  private void displayLyrics() {
    songLyricsView.displayLyrics(musicFile);
  }

  /**
   * Get view objects from the layout, set onClickListener.
   */
  private void getViewObjects() {
    // Get view objects.
    songInfoTextView = (TextView) findViewById(R.id.songInfoTextView);
    songLyricsView = (LyricsView) findViewById(R.id.songLyricsView);
    songLyricsEditText = (EditText) findViewById(R.id.songLyricsEditText);

    songEditLyricsButton = (ImageButton) findViewById(R.id.songEditLyricsButton);
    songEditLyricsButton.setOnClickListener(this);

    songSaveLyricsButton = (ImageButton) findViewById(R.id.songSaveLyricsButton);
    songSaveLyricsButton.setOnClickListener(this);

    songSearchLyricsButton = (ImageButton) findViewById(R.id.songSearchLyricsButton);
    songSearchLyricsButton.setOnClickListener(this);

    songRepeatButton = (ImageButton) findViewById(R.id.songRepeatButton);
    songRepeatButton.setOnClickListener(this);

    progressBar = (ProgressBar) findViewById(R.id.progressBar);
    progressText = (TextView) findViewById(R.id.progressText);
  }

  /**
   * Stop the media player when stopping.
   */
  @Override
  protected void onDestroy() {
    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
    if (updateProgressBar != null) {
      handler.removeCallbacks(updateProgressBar);
    }
    super.onDestroy();
  }

  @Override
  public void onClick(View v) {
    if (v == songEditLyricsButton) {
      startEditLyrics();
    } else if (v == songSaveLyricsButton) {
      saveLyrics();
    } else if (v == songRepeatButton) {
      setRepeat((repeat + 1) % 3); // Change to next repeat status.
    } else if (v == songSearchLyricsButton) {
      // Open web search for the file name.
      openURL("http://www.google.com/m?q=" +
          URLEncoder.encode(FileUtils.fileNameWithoutExtension(
              new File(musicFile.getMusicFilePath())) +
              " " + getString(R.string.search_lyrics)));
    }
  }

  /**
   * Allow user to edit lyrics.
   */
  private void startEditLyrics() {
    // Copy text to edit lyrics.
    songLyricsEditText.setText(musicFile.getLyrics());
    // Hide view lyrics and edit button.
    songEditLyricsButton.setVisibility(View.GONE);
    songLyricsView.setVisibility(View.GONE);
    // Show edit lyrics and save button.
    songSaveLyricsButton.setVisibility(View.VISIBLE);
    songLyricsEditText.setVisibility(View.VISIBLE);
  }

  /**
   * Save lyrics.
   */
  private void saveLyrics() {
    // Save lyrics to file.
    try {
      musicFile.setLyrics(songLyricsEditText.getText().toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Redisplay the lyrics.
    songLyricsView.displayLyrics(musicFile);
    // Hide edit lyrics and save button.
    songSaveLyricsButton.setVisibility(View.GONE);
    songLyricsEditText.setVisibility(View.GONE);
    // Show view lyrics and edit button.
    songEditLyricsButton.setVisibility(View.VISIBLE);
    songLyricsView.setVisibility(View.VISIBLE);
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
      e.printStackTrace();
    }
  }

  /**
   * Set repeat status.
   * This will update the icon of the songRepeatButton.
   * @param repeat REPEAT_XXX
   */
  private void setRepeat(int repeat) {
    Log.d(TAG, "setRepeat(" + repeat + ")");
    this.repeat = repeat;
    // Update button.
    int iconId;
    switch (repeat) {
    case REPEAT_ONE:
      iconId = R.drawable.ic_menu_repeat_one;
      break;
    case REPEAT_ALL:
      iconId = R.drawable.ic_menu_repeat_all;
      break;
    default:
      iconId = R.drawable.ic_menu_repeat_off;
      break;
    }
    songRepeatButton.setImageResource(iconId);
  }

  /**
   * Process when MediaPlayer complete playing.
   */
  @Override
  public void onCompletion(MediaPlayer mediaPlayer) {
    Log.d(TAG, "onCompletion");
    switch (repeat) {
    case REPEAT_ONE:
      // Repeat this song.
      playFile(musicFile.getMusicFilePath());
      break;
    case REPEAT_ALL:
      // Play the next song.
      playFile(getNextFilePath());
      break;
    default:
      // Do nothing.
      break;
    }
  }

  /**
   * Get the next file to play.
   * If the current file is the last file in the directory,
   * then the first one is get. Else the next file is get.
   * @return
   */
  public String getNextFilePath() {
    // Get list of files in the same folder.
    List<MusicFile> musicFiles = MusicFile.getMusciFiles(
        new File(musicFile.getMusicFilePath()).getParent(), false);
    // File the current file.
    Iterator<MusicFile> it = musicFiles.iterator();
    while (it.hasNext()) {
      if (musicFile.getMusicFilePath().equals(it.next().getMusicFilePath())) {
        break;
      }
    }
    // Get next file of the first file if current file is the last one.
    MusicFile nextFile = it.hasNext() ? it.next() : musicFiles.get(0);
    return nextFile.getMusicFilePath();
  }

  /**
   * Setup for updating the progress bar.
   */
  private void startUpdateProgressBar() {
    updateProgressBar = new Runnable() {
      public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int progress = (int) (100f * currentPosition / mediaPlayerDuration);
        // Update the progress bar.
        progressBar.setProgress(progress);
        progressText.setText(formatDuration(currentPosition) + " / " + formatDuration(mediaPlayerDuration));
        // Run update again.
        handler.postDelayed(this, PROGRESS_BAR_UPDATE_TIME);
      }
    };
    handler.post(updateProgressBar);
  }

  /**
   * Format a duration in millisecond to mm:ss
   * @param milliSecond
   * @return
   */
  private String formatDuration(int milliSecond) {
    int second = milliSecond / 1000;
    int minute = second / 60;
    second = second % 60;
    return String.format("%02d:%02d", minute, second);
  }
}
