package com.chauhai.android.fileplayer.playsong;

import java.io.File;

import com.chauhai.android.fileplayer.util.MusicFile;

import android.content.Context;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * View to display lyrics file (html, txt) in PlaySongActivity.
 *
 * @author umbalaconmeogia
 *
 */
public class LyricsView extends LinearLayout{

  public LyricsView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void displayLyrics(MusicFile musicFile) {
    // Clear the sub-view that display the lyrics (text or html).
    this.removeAllViews();
    if (musicFile.hasLyricsFile()) {
      // Create new sub-view to display the lyrics.
      View subView = null;
      String lyricsFileType = musicFile.getLyricsFileType();
      if (lyricsFileType == MusicFile.FILE_TYPE_HTML) {
        subView = displayLyricsInWebView(musicFile);
      } else if (lyricsFileType == MusicFile.FILE_TYPE_TEXT) {
        subView = displayLyricsInTextView(musicFile);
      }
      // Add sub view.
      if (subView != null) {
        LayoutParams layoutParams = new LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        this.addView(subView, layoutParams);
      }
    }
  }

  /**
   * Display lyrics in a TextView.
   * @param musicFile
   * @return the TextView object.
   */
  private View displayLyricsInTextView(MusicFile musicFile) {
    TextView textView = new TextView(this.getContext());
    textView.setText(musicFile.getLyrics());
    // Allow scrolling.
    textView.setMovementMethod(new ScrollingMovementMethod());
    return textView;
  }

  /**
   * Display lyrics in a WebView.
   * @param musicFile
   * @return the WebView object.
   */
  private View displayLyricsInWebView(MusicFile musicFile) {
    Uri uri = Uri.fromFile(new File(musicFile.getLyricsFilePath()));
    WebView webView = new WebView(this.getContext());
    webView.loadUrl(uri.toString());
    return webView;
  }
}
