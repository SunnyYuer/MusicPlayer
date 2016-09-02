package com.yuer.MusicPlayer.myclass;

import java.io.File;
import java.io.FileFilter;

public class MyFileFilter implements FileFilter{

	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		if(file.getName().endsWith(".mp3"))
		{
			return true;
		}
		return false;
	}

}
