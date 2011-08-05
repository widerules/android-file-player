package com.chauhai.android.fileplayer;

import java.io.File;
import java.io.IOException;

import com.chauhai.android.fileplayer.util.FileUtils;

/**
 * Manipulate information of music file or directory.
 */
public class MusicFile {

	/**
	 * Full path of the music file.
	 */
	private String musicFilePath;
	
	/**
	 * File name of the music file.
	 */
	private String musicFileName;
	
	/**
	 * This is a directory or not.
	 */
	private boolean isDirectory;
	
	/**
	 * Full path of the lyrics file.
	 */
	private String lyricsFilePath;
	
	public MusicFile(File file) {
		this.isDirectory = file.isDirectory();
		// Set file path.
		this.musicFilePath = file.getPath();
		// Get file name.
		this.musicFileName = file.getName();
		// Set lyrics file path if exists.
		if (!isDirectory) {
			String lyricsFilePath = generateLyricsFilePath();
			if (new File(lyricsFilePath).exists()) {
				this.lyricsFilePath = lyricsFilePath;
			}
		}
	}
	
	public MusicFile(String musicFilePath) {
		this(new File(musicFilePath));
	}

	public String getMusicFileName() {
		return musicFileName;
	}

	public String getMusicFilePath() {
		return musicFilePath;
	}
	
    private String generateLyricsFilePath() {
    	File musicFile = new File(musicFilePath);
    	return musicFile.getParent() + "/" + FileUtils.fileNameWithoutExtension(musicFile) + ".txt";
    }
    
    public boolean lyricsFileExist() {
    	return new File(lyricsFilePath).exists();
    }
    /**
     * Get lyrics from file.
     * @return The lyrics, or NULL if the lyrics file does not exist.
     */
    public String getLyrics() {
    	String lyrics = null;
    	if (lyricsFilePath != null) {
    		try {
				lyrics = FileUtils.fileGetContents(lyricsFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return lyrics;
    }
    
    /**
     * Check if lyrics file exist.
     * @return
     */
    public boolean hasLyricsFile() {
    	return lyricsFilePath != null;
    }

    /**
     * Save lyrics text to file.
     * @param lyrics
     * @throws IOException 
     */
    public void setLyrics(Object lyrics) throws IOException {
    	String lyricsFilePath = this.lyricsFilePath != null ? this.lyricsFilePath : generateLyricsFilePath();
    	FileUtils.filePutContents(lyricsFilePath, lyrics.toString());
    	this.lyricsFilePath = lyricsFilePath;
    }
    
    public boolean isDirectory() {
    	return isDirectory;
    }
}
