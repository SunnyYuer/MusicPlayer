package com.yuer.MusicPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuer.MusicPlayer.R;
import com.yuer.MusicPlayer.lrc.LrcService;
import com.yuer.MusicPlayer.myclass.MyComprator;
import com.yuer.MusicPlayer.myclass.MyFolderFilter;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class AddMusicFod extends Activity implements OnItemClickListener{

	private ListView lv;
	private List<Map<String, Object>> data;
	private SimpleAdapter adpter;
	private ArrayList<Integer> listind;//存储上级目录的index
	private ArrayList<Integer> listtop;//存储上级目录的屏幕位置
	private File[] folders;
	private String path;
	private String pathori;  //sdcard目录
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder);
		
		path = "/sdcard";
		pathori = path;
		
		lv = (ListView)findViewById(R.id.fodlist);
		lv.setOnItemClickListener(this);
		
		data = getData();
		adpter = new SimpleAdapter(this, data, R.layout.music, 
				new String[]{"icon","fname"}, 
				new int[]{R.id.icon, R.id.music});
		lv.setAdapter(adpter);
		
		listind = new ArrayList<Integer>();
		listtop = new ArrayList<Integer>();
	}
	
	public void update()
	{
		data.clear();
		data.addAll(getData());
		adpter.notifyDataSetChanged();
	}
	
	private List<Map<String, Object>> getData() {
		File file = new File(path);
		folders = file.listFiles(new MyFolderFilter());
		Arrays.sort(folders,new MyComprator());
		folders = sort(folders);
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0;i<folders.length;i++)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("icon", getIcon(folders[i]));
			map.put("fname", folders[i].getName());
			list.add(map);
		}
		return list;
	}
	
	public File[] sort(File[] files)
	{
		int i,j=0;
		File[] fs = files.clone();
		for(i=0;i<files.length;i++)
		{
			if(files[i].isDirectory())
			{
				fs[j] = files[i];
				j++;
			}
		}
		for(i=0;i<files.length;i++)
		{
			if(files[i].isFile())
			{
				fs[j] = files[i];
				j++;
			}
		}
		for(i=0;i<files.length;i++)
		{
			if(!files[i].isFile() && !files[i].isDirectory())
			{
				fs[j] = files[i];
				j++;
			}
		}
		return fs;
	}
	
	public int getIcon(File file)
	{
		if(file.isFile())
		{
			return R.drawable.album;
		}
		if(file.isDirectory())
		{
			return R.drawable.fod;
		}
		return R.drawable.album;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int i, long arg3)
	{
		File file = new File(path+"/"+folders[i].getName());
		if(file.isDirectory())
		{
			int index = lv.getFirstVisiblePosition();
			View v = lv.getChildAt(0);
			int top = (v == null) ? 0 : v.getTop();
			listind.add(index);//记住位置
			listtop.add(top);
			
			path = path+"/"+folders[i].getName();
			update();
			
			lv.setSelectionFromTop(0, 0);
		}
	}
	
	public void back()
	{
		if(pathori.equals(path))
		{
			return;
		}
		String[] s = path.split("/");
		path = "";
		for(int i=1;i<s.length-1;i++)
		{
			path = path+"/"+s[i];
		}
		if("".equals(path))
		{
			path = "/";
		}
		update();
		
		int index = listind.size()-1;
		lv.setSelectionFromTop(listind.get(index), listtop.get(index));
		listind.remove(index);
		listtop.remove(index);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// 监听返回按键被触发的时候产生的事件
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			back();
		}
		return false;
	}
	
	public void submit(View v)
	{
		MusicList.newfodname = path;
		finish();
	}
	
	public void cancel(View v)
	{
		finish();
	}
	
	@Override
	protected void onRestart() {
		LrcService.layout.setVisibility(View.GONE);
		super.onRestart();
	}
}
