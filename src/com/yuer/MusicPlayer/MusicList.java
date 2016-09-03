package com.yuer.MusicPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuer.MusicPlayer.lrc.LrcService;
import com.yuer.MusicPlayer.myclass.ListViewAdapter;
import com.yuer.MusicPlayer.myclass.MyComprator;
import com.yuer.MusicPlayer.myclass.MyFileFilter;
import com.yuer.MusicPlayer.myclass.ViewPagerAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MusicList extends Activity
implements OnItemClickListener,OnClickListener,OnItemLongClickListener
{
	private Button btnPla;
	private Button btnFod;
	private ViewPager viewPager;
	private static ViewPagerAdapter adapter;
	private List<View> lists = new ArrayList<View>();
	private View view1;
	private View view2;
	private ListView listmusic;
	private ListView listfolder;
	private static SimpleAdapter adpter1;
	private static ListViewAdapter adpter2;
	private static List<Map<String, Object>> data1;
	private static List<Map<String, Object>> data2;
	private static File[] musics;
	private static SharedPreferences sharedPre;
	static String newfodname = "";
	private String focusfod = "";
	private int focusitem = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		sharedPre = getSharedPreferences("fodname", PreferenceActivity.MODE_WORLD_WRITEABLE);
		
		btnPla = (Button)findViewById(R.id.btnPla);
		btnFod = (Button)findViewById(R.id.btnFod);
		btnPla.setOnClickListener(this);
		btnFod.setOnClickListener(this);
		viewPager = (ViewPager)findViewById(R.id.viewpager);
		
		view1 = getLayoutInflater().inflate(R.layout.listmusic, null);
		view2 = getLayoutInflater().inflate(R.layout.listfolder, null);
		listmusic = (ListView)view1.findViewById(R.id.listMusic);
		listfolder = (ListView)view2.findViewById(R.id.listFolder);
		listmusic.setOnItemClickListener(this);
		listfolder.setOnItemClickListener(this);
		listfolder.setOnItemLongClickListener(this);
		registerForContextMenu(listfolder);
		data1 = getData1();
		adpter1 = new SimpleAdapter(this, data1, R.layout.music, 
				new String[]{"icon","fname"}, 
				new int[]{R.id.icon, R.id.music});
		listmusic.setAdapter(adpter1);
		data2 = getData2();
		adpter2 = new ListViewAdapter(this, data2);
		listfolder.setAdapter(adpter2);
		
		lists.add(view1);
        lists.add(view2);
        adapter = new ViewPagerAdapter(lists);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int i)
            {//页面跳转完后得到调用
                if(i==0)
                {
                	btnPla.setTextColor(Color.parseColor("#FFA100"));
        			btnFod.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(i==1)
                {
                	btnFod.setTextColor(Color.parseColor("#FFA100"));
        			btnPla.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }
            
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            	//页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到
            }
            
            @Override
            public void onPageScrollStateChanged(int arg0) {
            	//arg0=1的时辰默示正在滑动，arg0=2的时辰默示滑动完毕了，arg0=0的时辰默示什么都没做
            }
        });
	}
	
	private static List<Map<String, Object>> getData1() {
		String[] name = new String[100];
		int n = sharedPre.getInt("fodnum", 0);
		int m=0;
		for(int i=0;i<n;i++)
		{
			if(sharedPre.getInt("check"+i, 0)==1)
			{
				name[m] = sharedPre.getString("name"+i, "");
				m++;
			}
		}
		int k = 0;
		for(int i=0;i<m;i++)
		{
			File f = new File(name[i]);
			File[] music = f.listFiles(new MyFileFilter());
			k = k+music.length;
		}
		musics = new File[k];
		k = 0;
		for(int i=0;i<m;i++)
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
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i=0;i<musics.length;i++)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("icon", R.drawable.album);
			map.put("fname", musics[i].getName().replaceAll(".mp3", ""));
			list.add(map);
		}
		return list;
	}

	private static List<Map<String, Object>> getData2() {
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
			map.put("icon", R.drawable.fod);
			map.put("fname", name[i]);
			list.add(map);
		}
		return list;
	}
	
	public static void update()
	{
		data1.clear();
		data1.addAll(getData1());
		adpter1.notifyDataSetChanged();
		data2.clear();
		data2.addAll(getData2());
		adpter2.notifyDataSetChanged();
		adapter.notifyDataSetChanged();
	}
	
	public void show(CharSequence s)
	{
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		LrcService.layout.setVisibility(View.GONE);
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
		if(flag==0 && !"".equals(newfodname) && n!=100)
		{
			Editor editor = sharedPre.edit();
			editor.putInt("fodnum", n+1);
			editor.putInt("check"+n, 1);
			adpter2.setcheck(n+1, true);
			editor.putString("name"+n, newfodname);
			editor.commit();
			update();
		}
		newfodname = "";
		if(n==100) show("目录太多！");
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btnPla:
			if(viewPager.getCurrentItem()!=0)
			{
				viewPager.setCurrentItem(0);
			}
			break;
		case R.id.btnFod:
			if(viewPager.getCurrentItem()!=1)
			{
				viewPager.setCurrentItem(1);
			}
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int item, long arg3) {
		if(viewPager.getCurrentItem()==0)
		{
			MusicPlayer.ind = item;
			MusicPlayer.press = true;
			//show("开始播放");
			this.finish();
			//this.onBackPressed();  //这个也可以退回去
		}
		if(viewPager.getCurrentItem()==1 && item==0)
		{
			Intent i = new Intent(this,AddMusicFod.class);
	    	startActivity(i);
		}
		if(viewPager.getCurrentItem()==1 && item!=0)
		{
			adpter2.setcheck(item, !adpter2.getcheck(item));
			Editor editor = sharedPre.edit();
			editor.putInt("check"+(item-1), adpter2.getcheck(item)?1:0);
			editor.commit();
			update();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		focusitem = arg2;  //选中的文件夹项
		arg2--;
		if(arg2>=0) focusfod = sharedPre.getString("name"+arg2, "");
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		if(focusitem!=0)
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
			update();
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	public void removeFod()
	{
		int n = sharedPre.getInt("fodnum", 0);
		String[] name = new String[100];
		int[] ch = new int[100];
		for(int i=0;i<n;i++)
		{
			ch[i] = sharedPre.getInt("check"+i, 0);
			name[i] = sharedPre.getString("name"+i, "");
		}
		Editor editor = sharedPre.edit();
		focusitem--;
		for(int i=focusitem;i<n-1;i++)
		{
			editor.putInt("check"+i, ch[i+1]);
			editor.putString("name"+i, name[i+1]);
		}
		n--;
		editor.remove("check"+n);
		editor.remove("name"+n);
		editor.putInt("fodnum", n);
		editor.commit();
	}
}
