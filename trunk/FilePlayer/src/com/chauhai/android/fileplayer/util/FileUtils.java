package com.chauhai.android.fileplayer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtils {

	/**
	 * Get all sub directories and files in specified directory, sort by name.
	 * @param dirPath
	 * @param fileFilter If NULL, then sub files are not get.
	 * @param fileComparator Used to sort the file. May be null.
	 * @return If the directory does not exist, return null.
	 *         Else return ArrayList of file.
	 */
	public static ArrayList<File> listFiles(String dirPath,
			FileFilter fileFilter,
			Comparator<File> fileComparator) {

		// Current directory.
		File dir = new File(dirPath);
		// Return null if it is not a directory.
		if (!dir.exists() || !dir.isDirectory()) {
			return null;
		}

		List<File> files;
		// Get files
		files = Arrays.asList(dir.listFiles(fileFilter));
		if (fileComparator != null) {
			Collections.sort(files, fileComparator);
		}
		// Merge.
		return new ArrayList<File>(files);
	}
	
	/**
	 * Return the file extension (without dot).
	 * @param file
	 * @return
	 */
	public static String fileExtension(File file) {
		String fileExt = null;
		String fileName = file.getName();
		int dotPosition = fileName.lastIndexOf(".");
		if (dotPosition > 0) {
			fileExt = fileName.substring(dotPosition + 1, fileName.length());
		}
		return fileExt;
	}
	
	/**
	 * Get the file name without extension.
	 * @param file
	 * @return
	 */
	public static String fileNameWithoutExtension(File file) {
		String fileName = file.getName();
		String bareName = fileName;
		int dotPosition = fileName.lastIndexOf(".");
		if (dotPosition > 0) {
			bareName = fileName.substring(0, dotPosition);
		}
		return bareName;
	}
	
	public static String fileGetContents(String filePath) throws IOException {
		String content = null;
		StringBuilder text = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line;
		while ((line = br.readLine()) != null) {
			text.append(line);
			text.append("\n");
		}
		content = text.toString();
		return content;
	}
	
	public static void filePutContents(String filePath, String content) throws IOException {
		FileWriter fileWriter = new FileWriter(filePath);
		fileWriter.write(content);
		fileWriter.flush();
		fileWriter.close();
	}
}
