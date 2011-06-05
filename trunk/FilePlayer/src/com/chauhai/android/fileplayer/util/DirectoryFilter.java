package com.chauhai.android.fileplayer.util;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter for directory.
 */
public class DirectoryFilter implements FileFilter {
	@Override
	public boolean accept(File pathname) {
		return pathname.isDirectory();
	}
}