package com.chauhai.android.fileplayer.util;

import java.io.File;
import java.util.Comparator;

public class FileNameComparator implements Comparator<File> {

	private boolean ignoreCase = true;
	
	public FileNameComparator() {
		this(true);
	}

	public FileNameComparator(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	@Override
	public int compare(File object1, File object2) {
		return ignoreCase ?
				object1.getName().compareToIgnoreCase(object2.getName()) :
				object1.getName().compareTo(object2.getName());
	}

}
