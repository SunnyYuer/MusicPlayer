package com.yuer.MusicPlayer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import com.yuer.MusicPlayer.R;
import com.yuer.MusicPlayer.file.MyComprator;
import com.yuer.MusicPlayer.file.MyFileFilter;
import com.yuer.MusicPlayer.lrc.Jiexilrc;
import com.yuer.MusicPlayer.lrc.KrcText;
import com.yuer.MusicPlayer.lrc.LrcService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MusicPlayer extends Activity
implements OnClickListener,OnSeekBarChangeListener{

	private MediaPlayer player;
	private File[] musics;
	private int num=0;//歌曲数量
	private int index = 0;//歌曲下标
	private int total;//播放总时长
	public static int ind = 0;//点击的歌曲
	private TextView musicName;
	private ImageButton btnPlay;
	public static boolean press = false;//是否点击歌曲列表
	private boolean play=false;//是否在播放
	private SeekBar seekBar;
	private TextView jindu,zong;//时间
	private ImageButton next,pre;
	private int mode=1;//歌曲切换模式
	private ImageButton btnMode;
	private int nowpro=0;//人为滑动滑块的进度
	private boolean proman=false;//是否人为滑动
	private boolean fugai=false;//是否是被歌曲列表覆盖
	private SharedPreferences sharedPre;//共享的xml文件
	private TextView lrc1;//歌词前
	private TextView lrc2;
	private TextView lrc3;//歌词中
	private TextView lrc4;
	private TextView lrc5;//歌词后
	private String[] lyric;//全部歌词
	private int lrcline = 0;//歌词所在行
	private Jiexilrc jiexi;
	private boolean showlrc=true;//是否有歌词显示
	private long preTime = 0;
	private long secondTime;
	private Intent serviceIntent;//桌面歌词服务
	private boolean showwinlrc;//是否显示桌面歌词
	private boolean sett=false;//是否进入了设置
	
	private void initUI(){
		musicName = (TextView)findViewById(R.id.music_name);
		btnPlay = (ImageButton)findViewById(R.id.btn_play);
		seekBar = (SeekBar)findViewById(R.id.seekBar);
		jindu = (TextView)findViewById(R.id.jindu);
		zong = (TextView)findViewById(R.id.zong);
		next = (ImageButton)findViewById(R.id.btn_next);
		pre = (ImageButton)findViewById(R.id.btn_pre);
		lrc1 = (TextView)findViewById(R.id.lrc1);
		lrc2 = (TextView)findViewById(R.id.lrc2);
		lrc3 = (TextView)findViewById(R.id.lrc3);
		lrc4 = (TextView)findViewById(R.id.lrc4);
		lrc5 = (TextView)findViewById(R.id.lrc5);
		btnMode = (ImageButton)findViewById(R.id.btn_mode);
		setmode();
	}
	
	private void setmode()
	{
		sharedPre = getSharedPreferences("fodname", PreferenceActivity.MODE_WORLD_WRITEABLE);
		int n = sharedPre.getInt("mode", 0);
		if(n==0)
		{
			Editor editor = sharedPre.edit();
			editor.putInt("mode", 1);
			editor.commit();
		}
		else mode=n;
		if(mode==1) btnMode.setImageResource(R.drawable.shunxu_dark);
		if(mode==2) btnMode.setImageResource(R.drawable.shuffle_dark);
		if(mode==3) btnMode.setImageResource(R.drawable.repeat_dark);
	}
	
	public void setshowwinlrc()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		showwinlrc = prefs.getBoolean("showwinlrc", false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playmusic);
		
		registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		//使用广播接受者监听home键
		
		initUI();
		setshowwinlrc();
		filesIni();
		readFiles();
		
		player = new MediaPlayer();
		if(num>0) jiazai();
		
		btnPlay.setOnClickListener(this);
		next.setOnClickListener(this);
		pre.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(this);
		btnMode.setOnClickListener(this);
		
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0)
			{
				jiazai();
				player.start();
			}
		});
		
		serviceIntent = new Intent(this,LrcService.class);
		startService(serviceIntent);//启动桌面歌词服务，默认不显示
	}
	
	public void filesIni()
	{
		int n = sharedPre.getInt("fodnum", 0);
		if(n==0)
		{
			Editor editor = sharedPre.edit();
			editor.putInt("fodnum", 0);
			editor.commit();
		}
	}
	
	public void readFiles()
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
		num = musics.length;
		/*
		for(int i=0;i<num;i++)
		{
			System.out.println(musics[i].getName());
		}*/
	}
	
	public void initial()
	{
		player.reset();
		h.removeCallbacks(r);
		btnPlay.setImageResource(R.drawable.play_dark);
		play=false;
		jindu.setText(getTime(0));
		zong.setText(getTime(0));
		seekBar.setProgress(0);
		seekBar.setMax(1000);
		getlrc("");
		musicName.setText("无音乐");
		index=0;
	}
	
	public void jiazai()
	{
		try {
			player.reset();
			player.setDataSource(musics[index].getAbsolutePath());
			player.prepare();
			
			musicName.setText(musics[index].getName().replaceAll(".mp3", ""));
			total = player.getDuration();//获取播放总时长
			seekBar.setMax(total);
			zong.setText(getTime(total));
			getlrc(musics[index].getName().replaceAll(".mp3", ".krc"));
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getlrc(String name)
	{
		KrcText lrc = new KrcText(name);
		lrc1.setText("");
		lrc2.setText("");
		lrc4.setText("");
		lrc5.setText("");
		try {
			String l = lrc.getLrc();
			lyric = l.split("\n");
			lrc3.setText("");
			showlrc=true;
			jiexi = new Jiexilrc(lyric);
			setlrc();
		} catch (IOException e) {
			lrc3.setText("无歌词");
			showlrc=false;
			setwinlrc(lrc4.getText().toString());
		}
		lrcline = 0;
	}
	
	public void setlrc()
	{
		if(!showlrc) return;
		int line = jiexi.getLine(player.getCurrentPosition(),lrcline);
		if(line!=lrcline)
		{
			if(line>=3) lrc1.setText(jiexi.fortmat(lyric[line-3]));
			else lrc1.setText("");
			if(line>=2) lrc2.setText(jiexi.fortmat(lyric[line-2]));
			else lrc2.setText("");
			if(line>=1) lrc3.setText(jiexi.fortmat(lyric[line-1]));
			else lrc3.setText("");
			if(line<lyric.length) lrc4.setText(jiexi.fortmat(lyric[line]));
			else lrc4.setText("");
			setwinlrc(lrc4.getText().toString());
			if(line<lyric.length-1) lrc5.setText(jiexi.fortmat(lyric[line+1]));
			else lrc5.setText("");
			lrcline = line;
		}
	}
	
	public void setwinlrc(String lrc)
	{  //桌面歌词
		LrcService.newlrc(lrc);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_play:
			play = !play;
			if(play)
			{
				if(num==0)
				{
					show("请添加音乐");
					play = !play;
					break;
				}
				player.start();
				h.post(r);
				//show("开始播放");
				btnPlay.setImageResource(R.drawable.pause_dark);
			}
			else
			{
				player.pause();
				h.removeCallbacks(r);
				//show("暂停");
				btnPlay.setImageResource(R.drawable.play_dark);
			}
			break;
		case R.id.btn_pre:
			if(num==0)
			{
				show("请添加音乐");
				break;
			}
			if(mode==1)
			{
				index = index-2;
			}
		case R.id.btn_next:
			if(num==0)
			{
				show("请添加音乐");
				break;
			}
			if(!play)
			{
				player.start();
				h.post(r);
				btnPlay.setImageResource(R.drawable.pause_dark);
				play = !play;
			}
			changeMusic();
			break;
		case R.id.btn_mode:
			if(mode==1)
			{
				mode = 2;
				btnMode.setImageResource(R.drawable.shuffle_dark);
				//show("随机播放");
				break;
			}
			if(mode==2)
			{
				mode = 3;
				btnMode.setImageResource(R.drawable.repeat_dark);
				//show("单曲循环");
				break;
			}
			if(mode==3)
			{
				mode = 1;
				btnMode.setImageResource(R.drawable.shunxu_dark);
				//show("顺序播放");
				break;
			}
		}
		
	}
	
	public void show(CharSequence s)
	{
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
	
	Handler h = new Handler();
	Runnable r = new Runnable() {
		
		@Override
		public void run() {
			h.postDelayed(r, 100);//间隔100ms再次执行
			if(!proman) seekBar.setProgress(player.getCurrentPosition());
			setlrc();
			if(seekBar.getProgress()+300>total && seekBar.getProgress()<total)
			{  //用于自动切换歌曲
				changeMusic();
			}
		}
	};

	@Override
	public void onProgressChanged(SeekBar arg0, int now, boolean isFromUser) {
		if(isFromUser)
		{
			if(total-now<1000) now = total-1000;
			nowpro = now;
			proman = true;
			//System.out.println(now);
		}
		if(num==0) now = 0;
		jindu.setText(getTime(now));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		if(num!=0)
		{
			player.seekTo(nowpro);
			lrcline = 0;
			setlrc();
		}
		proman = false;
	}
	
	public String getTime(int time)
	{
		String text="";
		time = time/1000;
		int fen = time/60;
		int miao = time%60;
		if(fen<10)
		{
			text = text + "0";
		}
		text = text + fen;
		text = text + ":";
		if(miao<10)
		{
			text = text + "0";
		}
		text = text + miao;
		return text;
	}
	
	public void changeMusic()
	{
		//模式一  顺序上下一曲
		//模式二  随机下一曲
		//模式三  循环播放
		player.seekTo(total);  //设置进度为最后，会触发播放完成事件
		seekBar.setProgress(0);
		if(mode==1)
		{
			index++;
			if(index>=num)
			{
				index = 0;
			}
			if(index<0)
			{
				index = num-1;
			}
		}
		if(mode==2)
		{
			Random r = new Random();
			index=r.nextInt(num);
		}
	}
	
	public void lists(View v)
	{
		fugai = true;
		Intent i = new Intent(this,MusicsList.class);
		startActivity(i);
	}

    @Override
	protected void onRestart() {
    	if(fugai)  //从歌曲列表回到主界面
    	{
	    	readFiles();
	    	if(press)  //点击了歌曲
	    	{
				index = ind;
				if(!play)
				{
					btnPlay.setImageResource(R.drawable.pause_dark);
					jiazai();
					player.start();
					h.post(r);
					play = !play;
				}
				else player.seekTo(total);
				seekBar.setProgress(0);
				press = false;
	    	}
	    	else
	    	{
	    		if(num==0) initial();
	    		if(musicName.getText().equals("无音乐") && num>0) jiazai();
	    	}
	    	fugai = false;
    	}
    	else
    	{
    		if(play)  //打电话完了之后自动继续播放
    		{
    			player.start();
				h.post(r);
				btnPlay.setImageResource(R.drawable.pause_dark);
    		}
    	}
    	LrcService.lrc1.setVisibility(View.INVISIBLE);
		LrcService.lrc2.setVisibility(View.INVISIBLE);
    	if(sett)
    	{  //更新showwinlrc
    		setshowwinlrc();
    		sett = false;
    	}
    	super.onRestart();
	}
    
    @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
    	// 监听返回按键被触发的时候产生的事件
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			secondTime = System.currentTimeMillis();//获取第二次操作退出时间
			if(secondTime - preTime > 2000)
			{
				show("再按一次退出");
				preTime = secondTime;
			}
			else
			{
				h.removeCallbacks(r);
				player.release();
				savemode();
				stopService(serviceIntent);
				System.exit(0);
				return super.onKeyUp(keyCode, event);
			}
		}
		return false;
	}
    
    public void savemode()
	{
		Editor editor = sharedPre.edit();
		editor.putInt("mode", mode);
		editor.commit();
	}
    
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                	//表示按了home键,程序到了后台
                	if(showwinlrc)
        			{
        				LrcService.lrc1.setVisibility(View.VISIBLE);
        				LrcService.lrc2.setVisibility(View.VISIBLE);
        			}
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
                	//表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.action_settings:
			sett = true;
			Intent i = new Intent(this,Setting.class);
	    	startActivity(i);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
}
