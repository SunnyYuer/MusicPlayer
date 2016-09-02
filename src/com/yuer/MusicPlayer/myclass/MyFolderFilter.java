package com.yuer.MusicPlayer.myclass;

import java.io.File;
import java.io.FileFilter;

public class MyFolderFilter implements FileFilter{

	@Override
	public boolean accept(File fod) {
		if((fod.isDirectory() && !fod.getName().startsWith("."))||
				fod.getName().endsWith(".mp3"))
		{
			return true;
		}
		return false;
	}

}
