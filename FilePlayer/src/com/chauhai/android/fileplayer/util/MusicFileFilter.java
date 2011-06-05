package com.chauhai.android.fileplayer.util;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter for music file.
 */
public class MusicFileFilter implements FileFilter {

	public String[] acceptFileExt;

	/**
	 * @param acceptFileExt For example {'mp3', 'ogg'}
	 */
	public MusicFileFilter(String[] acceptFileExt) {
		this.acceptFileExt = new String[acceptFileExt.length];
		for (int i = 0; i < acceptFileExt.length; i++) {
			this.acceptFileExt[i] = acceptFileExt[i].toLowerCase();
		}
	}
	
	@Override
	public boolean accept(File pathname) {
		if (!pathname.isFile()) {
			return false;
		}
		String fileName = pathname.getName().toLowerCase();
		int dotPosition = fileName.lastIndexOf(".");
		if (dotPosition > 0) {
			String fileExtension = fileName.substring(dotPosition + 1, fileName.length());
			for (String ext : acceptFileExt) {
				if (ext.equals(fileExtension)) {
					return true;
				}
			}
		}
		return false;
	}
}