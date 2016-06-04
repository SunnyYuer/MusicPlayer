package com.yuer.MusicPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuer.MusicPlayer.R;
import com.yuer.MusicPlayer.file.MyComprator;
import com.yuer.MusicPlayer.file.MyFileFilter;
import com.yuer.MusicPlayer.lrc.LrcService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MusicsList extends Activity 
implements OnItemClickListener,OnClickListener,OnItemLongClickListener
{
	private ListView lv;
	private File[] musics;
	private int window = 1;
	private Button btn_all;
	private Button btn_fod;
	private SharedPreferences sharedPre;
	static String newfodname = "";
	private String focusfod = "";
	private int focusitem = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lists);
		
		btn_all = (Button)findViewById(R.id.btn_all);
		btn_fod = (Button)findViewById(R.id.btn_fod);
		btn_all.setOnClickListener(this);
		btn_fod.setOnClickListener(this);
		lv = (ListView)findViewById(R.id.listView);
		lv.setOnItemLongClickListener(this);
		registerForContextMenu(lv);
		
		sharedPre = getSharedPreferences("fodname", PreferenceActivity.MODE_WORLD_WRITEABLE);
		
		showWindow();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btn_all:
			if(window!=1)
			{
				window = 1;
				btn_all.setBackgroundColor(Color.parseColor("#FFA100"));
				btn_fod.setBackgroundColor(Color.parseColor("#352F30"));
				showWindow();
			}
			break;
		case R.id.btn_fod:
			if(window!=2)
			{
				window = 2;
				btn_fod.setBackgroundColor(Color.parseColor("#FFA100"));
				btn_all.setBackgroundColor(Color.parseColor("#352F30"));
				showWindow();
			}
			break;
		}
	}
	
	public void showWindow()
	{
		if(window==1)
		{
			list();
		}
		if(window==2)
		{
			folder();
		}
	}
	
	public void list()
	{
		String[] name = new String[100];
		int n = sharedPre.getInt("fodnum", 0);
		for(int i=0;i<n;i++)
		{
			name[i] = sharedPre.getString("name"+i, "");
		}
		int k = 0;
		for(int i=0;i<n;i++)
		{
			File f = new File(name[i]);
			File[] music = f.listFiles(new MyFileFilter());
			k = k+music.length;
		}
		musics = new File[k];
		k = 0;
		for(int i=0;i<n;i++)
		{
			File f = new File(name[i]);
			File[] music = f.listFiles(new MyFileFilter());
			for(int j=0;j<music.length;j++)
			{
				musics[k] = music[j];
				k++;
			}
		}
		Arrays.sort(musics,new MyComprator());
		/*
		for(int i=0;i<musics.length;i++)
		{
			System.out.println(musics[i].getName());
		}*/
		
		SimpleAdapter adpter = new SimpleAdapter(this, getData(), R.layout.music, 
				new String[]{"icon","fname"}, 
				new int[]{R.id.icon, R.id.music});
		lv.setAdapter(adpter);
		lv.setOnItemClickListener(this);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0;i<musics.length;i++)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("icon",R.drawable.album);
			map.put("fname", musics[i].getName().replaceAll(".mp3", ""));
			list.add(map);
		}
		return list;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(window==1)
		{
			PlayMusic.ind = arg2;
			PlayMusic.press = true;
			//show("开始播放");
			this.finish();
			//this.onBackPressed();  //这个也可以退回去
		}
		if(window==2 && arg2==0)
		{
			Intent i = new Intent(this,AddMusicFod.class);
	    	startActivity(i);
		}
	}
	
	public void show(CharSequence s)
	{
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	public void folder()
	{
		SimpleAdapter adpter = new SimpleAdapter(this, getData2(), R.layout.music, 
				new String[]{"icon","fname"}, 
				new int[]{R.id.icon, R.id.music});
		lv.setAdapter(adpter);
	}

	private List<Map<String, Object>> getData2() {
		int fodnum;
		String[] name = new String[100];
		fodnum = sharedPre.getInt("fodnum", 0);
		for(int i=0;i<fodnum;i++)
		{
			name[i] = sharedPre.getString("name"+i, "");
		}
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("icon",R.drawable.add);
		map.put("fname", "添加文件夹");
		list.add(map);
		for(int i=0;i<fodnum;i++)
		{
			map = new HashMap<String, Object>();
			map.put("icon",R.drawable.fod);
			map.put("fname", name[i]);
			list.add(map);
		}
		return list;
	}
	
	@Override
	protected void onRestart() {
		LrcService.lrc1.setVisibility(View.INVISIBLE);
		LrcService.lrc2.setVisibility(View.INVISIBLE);
		int n = sharedPre.getInt("fodnum", 0);
		int flag = 0;
		String name="";
		for(int i=0;i<n;i++)
		{  //先查找出有没有相同的文件夹路径
			name = sharedPre.getString("name"+i, "");
			if(name.equals(newfodname))
			{
				flag++;
				break;
			}
		}
		if(flag==0 && !"".equals(newfodname))
		{
			Editor editor = sharedPre.edit();
			editor.putInt("fodnum", n+1);
			editor.putString("name"+n, newfodname);
			editor.commit();
		}
		newfodname = "";
		super.onRestart();
		showWindow();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if(window==2)
		{
			focusitem = arg2;  //选中的文件夹项
			arg2--;
			if(arg2>=0) focusfod = sharedPre.getString("name"+arg2, "");
		}
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if(window==2 && focusitem!=0)
		{
			menu.setHeaderTitle(focusfod);
			menu.add(0, 0, 1, "删除");
			focusfod = "";
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case 0:
			removeFod();
			showWindow();
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	public void removeFod()
	{
		int n = sharedPre.getInt("fodnum", 0);
		String[] name = new String[100];
		for(int i=0;i<n;i++)
		{
			name[i] = sharedPre.getString("name"+i, "");
		}
		Editor editor = sharedPre.edit();
		focusitem--;
		for(int i=focusitem;i<n-1;i++)
		{
			editor.putString("name"+i, name[i+1]);
		}
		n--;
		editor.remove("name"+n);
		editor.putInt("fodnum", n);
		editor.commit();
	}
}
