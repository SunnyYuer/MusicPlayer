package com.yuer.MusicPlayer.lrc;

public class Jiexilrc {

	private String[] lyric;//全部歌词
	
	public Jiexilrc(String[] lyr)
	{
		lyric = lyr;
	}
	
	public int getLine(int jindu,int qline)
	{
		int index=0;//定位歌词的行数
		for(index=qline;index<lyric.length;index++)
		{
			if(lyric[index].length()>=15)  //一行至少要15长度才是歌词
			{
				//System.out.println(lyric[index]);
				int i = lyric[index].indexOf(",");  //找到歌词前面的[100,200]中的','
				if(i==-1)
				{
					continue;
				}
				String j = lyric[index].substring(1, i);
				//System.out.println(j);
				try
				{
					if(jindu < Integer.parseInt(j)) break;
				}
				catch(NumberFormatException e)
				{
				}
			}
		}
		return index;
	}
	
	public String fortmat(String lrc)
	{
		int i=0,t=0;
		String s="";
		for(i=0;i<lrc.length();i++)
		{
			if(lrc.charAt(i)=='>')
			{
				t=1;
				continue;
			}
			if(lrc.charAt(i)=='<')
			{
				t=0;
				continue;
			}
			if(t==1) s = s+lrc.charAt(i);
			//System.out.println((int)lrc.charAt(i));
		}
		return s;
	}
}