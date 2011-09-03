package com.chauhai.android.fileplayer;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SongListAdapter extends ArrayAdapter<MusicFile> {

	private static final String TAG = "SongListAdapter";
	
	private SongListActivity songListActivity;
	
	private List<MusicFile> musicFiles;
	
	public SongListAdapter(SongListActivity songListActivity, List<MusicFile> musicFiles) {
		super(songListActivity, R.layout.song_list_row, musicFiles);
		this.songListActivity = songListActivity;
		this.musicFiles = musicFiles;
	}
	
	/**
	 * static to save the reference to the outer class and to avoid access to
	 * any members of the containing class
	 */
	static class ViewHolder {
		protected ImageView icon;
		protected TextView name;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// Prepare view (list item object).
		View view = null;
		if (convertView == null) {
			// Create new view object.
			LayoutInflater inflater = songListActivity.getLayoutInflater();
			view = inflater.inflate(R.layout.song_list_row, null);
			// Set ViewHolder to this list element.
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			view.setOnClickListener(new OnClickListener() {
				// Goto PlaySong screen
				@Override
				public void onClick(View v) {
					MusicFile musicFile = (MusicFile) viewHolder.name.getTag();
					if (musicFile.isDirectory()) {
						// Browse sub directory.
						songListActivity.displaySongList(musicFile.getMusicFilePath());
					} else {
						// Play a song.
						PlaySongActivity.playSong(songListActivity, musicFile.getMusicFilePath());
					}
				}
			});
			// Set tag for view.
			view.setTag(viewHolder); // Tag ViewHolder to this list item.
		} else {
			// Reuse the existed object.
			view = convertView;
		}
		
		// Set view content.
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.name.setTag(musicFiles.get(position)); // Tag MusciFile to the name.
		// Set music file name.
		viewHolder.name.setText(musicFiles.get(position).getMusicFileName());
		// Set icon.
		viewHolder.icon.setImageResource(getImageResource(musicFiles.get(position)));
		
		return view;
	}

	/**
	 * Get the image resource depends on the musicFile status.
	 * @param musicFile
	 * @return
	 */
	private int getImageResource(MusicFile musicFile) {
		if (musicFile.isDirectory()) {
			return R.drawable.ic_folder;
		} else {
			return musicFile.hasLyricsFile() ? R.drawable.ic_lyrics : R.drawable.ic_music;
		}
	}
}
