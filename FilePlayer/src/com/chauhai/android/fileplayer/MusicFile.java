package com.chauhai.android.fileplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.chauhai.android.fileplayer.util.DirectoryFilter;
import com.chauhai.android.fileplayer.util.FileNameComparator;
import com.chauhai.android.fileplayer.util.FileUtils;
import com.chauhai.android.fileplayer.util.MusicFileFilter;

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
    
    /**
     * Get the list of items (files) in specified directory,
     * set them to musicFiles.
     * 
     * @param dirPath
     * @param getSubdirectory If true, then get sub directories names, else only files are get.
     * @return If dirPath is invalid, then return null, else return list of files.
     */
    public static List<MusicFile> getMusciFiles(String dirPath, boolean getSubdirectory) {
    	// Return null if dirPath is invalid.
    	if (!(new File(dirPath)).exists()) {
    		return null;
    	}
    	
    	// Get file list.
//    	String[] musicFileExt = {"mp3", "wma"};
    	String[] musicFileExt = {"mp3"};
    	FileNameComparator fileNameComparator = new FileNameComparator();
    	ArrayList<File> files = new ArrayList<File>();
    	// Get sub directories.
    	if (getSubdirectory) {
    		files.addAll(FileUtils.listFiles(dirPath, new DirectoryFilter(), fileNameComparator));
    	}
    	// Get files.
    	files.addAll(FileUtils.listFiles(dirPath, new MusicFileFilter(musicFileExt), fileNameComparator));

    	// Add files to list.
    	List<MusicFile> musicFiles = new ArrayList<MusicFile>();
    	for (Iterator<File> it = files.iterator(); it.hasNext(); ) {
    		addMusicFileToList(musicFiles, it.next());
    	}
    	
    	return musicFiles;
    }

    /**
     * Append music file to the end of musicFiles.
     * @param file
     */
    private static void addMusicFileToList(List<MusicFile> musicFiles, File file) {
    	addMusicFileToList(musicFiles, -1, file);
    }
    
    /**
     * Add a file into musicFiles at specified position.
     * @param index Position. If is -1, then append to the end of list.
     * @param file
     */
    private static void addMusicFileToList(List<MusicFile> musicFiles, int index, File file) {
    	MusicFile musicFile = new MusicFile(file);
    	if (index == -1) {
    		musicFiles.add(musicFile);
    	} else {
    		musicFiles.add(index, musicFile);
    	}
    }
}
