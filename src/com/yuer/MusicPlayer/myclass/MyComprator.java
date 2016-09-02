package com.yuer.MusicPlayer.myclass;

import java.io.File;
import java.util.Comparator;

public class MyComprator implements Comparator<File>{

	@Override
	public int compare(File f1, File f2) {
		// TODO Auto-generated method stub
		return f1.compareTo(f2);
	}

}