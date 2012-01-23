package com.chauhai.android.fileplayer.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Manipulate information of music file or directory.
 */
public class MusicFile {

  // Accepted file extendsion.
  public static final String FILE_EXT_HTML = "html";
  public static final String FILE_EXT_HTM = "htm";
  public static final String FILE_EXT_TXT = "txt";

  /**
   * Accepted lyrics file types.
   * Lyrics file is searched in this order.
   */
  public static final String[] ACCEPT_LYRICS_FILE_EXT = {
    FILE_EXT_HTML,
    FILE_EXT_HTM,
    FILE_EXT_TXT
  };

  // Accepted file types: HTML and text.
  public static final String FILE_TYPE_HTML = "html";
  public static final String FILE_TYPE_TEXT = "text";

  /**
   * Map file extension to file type.
   */
  public static Map<String, String> fileTypeOfExt = new HashMap<String, String>();
  static {
    fileTypeOfExt.put(FILE_EXT_HTML, FILE_TYPE_HTML);
    fileTypeOfExt.put(FILE_EXT_HTM, FILE_TYPE_HTML);
    fileTypeOfExt.put(FILE_EXT_TXT, FILE_TYPE_TEXT);
  }

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

  private String lyricsFileType;

  public MusicFile(File file) {
    this.isDirectory = file.isDirectory();
    // Set file path.
    this.musicFilePath = file.getPath();
    // Get file name.
    this.musicFileName = file.getName();
    // Set lyrics file path if exists.
    if (!isDirectory) {
      findLyricsFilePath();
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

  /**
   * @return FILE_TYPE_XXX
   */
  public String getLyricsFileType() {
    return lyricsFileType;
  }

  public String getLyricsFilePath() {
    return lyricsFilePath;
  }

  /**
   * Check if the lyrics file exits.
   * If it exists, then set <code>lyricsFilePath</code>, <code>lyricsFileType</code>.
   */
  private void findLyricsFilePath() {
    File musicFile = new File(musicFilePath);
    // File basename (include dot before the extension.
    String basePath = musicFile.getParent() + "/" + FileUtils.fileNameWithoutExtension(musicFile) + ".";

    checkExt: // Check each extension pattern to find one exists.
    for (String extPattern: ACCEPT_LYRICS_FILE_EXT) {
      // Get lower case and upper case of extension.
      String[] exts = new String[] {extPattern, extPattern.toUpperCase()};
      for (String ext: exts) {
        String filePath = basePath + ext;
        if ((new File(filePath)).exists()) {
          // Find the lyrics file.
          lyricsFilePath = filePath;
          lyricsFileType = fileTypeOfExt.get(extPattern);
          break checkExt;
        }
      }
    }
  }

  /**
   * Generate text lyrics file path.
   * (file that has the same name with the music file but with the .txt extension).
   * @return
   */
  private String generateTextLyricsFilePath() {
    File musicFile = new File(musicFilePath);
    return musicFile.getParent() + "/" + FileUtils.fileNameWithoutExtension(musicFile) + ".txt";
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
    String lyricsFilePath = this.lyricsFilePath != null ? this.lyricsFilePath : generateTextLyricsFilePath();
    FileUtils.filePutContents(lyricsFilePath, lyrics.toString());
    findLyricsFilePath();
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
    String[] musicFileExt = {"mp3", "ogg"};
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
