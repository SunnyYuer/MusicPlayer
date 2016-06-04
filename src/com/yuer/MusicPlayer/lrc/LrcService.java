package com.yuer.MusicPlayer.lrc;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
 
public class LrcService extends Service
{
	public static WindowManager winm;
	public static TextView lrc1;
	public static TextView lrc2;
	public static WindowManager.LayoutParams params1;
	public static WindowManager.LayoutParams params2;
	private int statusBarHeight; //桌面状态栏的高度
	private float startX;  //歌词起始位置
    private float startY;
    public static String lrcnext;  //下次播放歌词
    private static int line=1;  //正在播放的行数
    private int realL;  //适配不同大小机型的歌词间距
 
    public LrcService() {
    }
 
    @Override
    public void onCreate() {
        super.onCreate();
        winm = (WindowManager) getApplicationContext().getSystemService(
                WINDOW_SERVICE);
        statusBarHeight = getStatusBarHeight();
        realL = winm.getDefaultDisplay().getHeight()/30;
        //System.out.println(statusBarHeight);
        showWindow();
    }
    
    public WindowManager.LayoutParams getParams()
    {
    	WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    	params.type = LayoutParams.TYPE_SYSTEM_ALERT
                | LayoutParams.TYPE_SYSTEM_OVERLAY;// 设置窗口类型为系统级
    	params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;// 设置窗口焦点
 
    	params.width = WindowManager.LayoutParams.FILL_PARENT;
    	params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    	params.alpha = 80;
 
    	params.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
    	params.x = 0;
    	params.format = PixelFormat.RGBA_8888;  //设置背景为透明
    	return params;
    }
 
    // 显示浮动窗口
    public void showWindow() {
    	params1 = getParams();
    	params1.y = 0;
    	//winm.getDefaultDisplay().getHeight()获取屏幕高度
        
        lrc1 = new TextView(this);
        lrc1.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				float x = event.getRawX();  //当前触碰位置
		        float y = event.getRawY()-statusBarHeight;
		 
		        switch (event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            startX = event.getX();
		            startY = event.getY();
		            break;
		        case MotionEvent.ACTION_MOVE:
		            //Log.w(TAG, "x::" + startX + ",y::" + startY);
		            //Log.w(TAG, "rawx::" + x + ",rawy::" + y);
		        case MotionEvent.ACTION_UP:
		            updatePosition(x - startX, y - startY);
		            break;
		        }
		        return true;
			}
		});
        lrc1.setTextSize(15);
        lrc1.setTextColor(Color.parseColor("#FFA100"));
        lrc1.setText("");
        TextPaint tp = lrc1.getPaint();
        tp.setFakeBoldText(true);
        lrc1.setVisibility(View.INVISIBLE);
		winm.addView(lrc1, params1);
		
		params2 = getParams();
		params2.y = realL;
		
		lrc2 = new TextView(this);
        lrc2.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				float x = event.getRawX();
		        float y = event.getRawY()-statusBarHeight-realL;
		 
		        switch (event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            startX = event.getX();
		            startY = event.getY();
		            break;
		        case MotionEvent.ACTION_MOVE:
		            //Log.w(TAG, "x::" + startX + ",y::" + startY);
		            //Log.w(TAG, "rawx::" + x + ",rawy::" + y);
		        case MotionEvent.ACTION_UP:
		            updatePosition(x - startX, y - startY);
		            break;
		        }
		        return true;
			}
		});
        lrc2.setTextSize(15);
        lrc2.setTextColor(Color.WHITE);
        lrc2.setText(lrcnext);
        lrc2.setGravity(Gravity.RIGHT);
        TextPaint tp2 = lrc2.getPaint();
        tp2.setFakeBoldText(true);
        lrc2.setVisibility(View.INVISIBLE);
		winm.addView(lrc2, params2);
    }
    
    public static void newlrc(String lrc)
    {
    	lrcnext = lrc;
    	if(lrc2!=null&&line==1)
    	{
    		winm.removeView(lrc2);
    		lrc2.setText(lrc);
    		lrc2.setTextColor(Color.WHITE);
    		winm.addView(lrc2, params2);
    		winm.removeView(lrc1);
    		lrc1.setTextColor(Color.parseColor("#FFA100"));
    		winm.addView(lrc1, params1);
    		line = 2;
    		return;
    	}
    	if(lrc1!=null&&line==2)
    	{
    		winm.removeView(lrc1);
    		lrc1.setText(lrc);
    		lrc1.setTextColor(Color.WHITE);
    		winm.addView(lrc1, params1);
    		winm.removeView(lrc2);
    		lrc2.setTextColor(Color.parseColor("#FFA100"));
    		winm.addView(lrc2, params2);
    		line = 1;
    		return;
    	}
    }
    
    // 更新浮动窗口位置参数
    private void updatePosition(float x, float y) {
        // View的当前位置
        params1.x = (int) x;
        params1.y = (int) y;
        params2.x = (int) x;
        params2.y = (int) y + realL;
        winm.updateViewLayout(lrc1, params1);
        winm.updateViewLayout(lrc2, params2);
    }
    
    // 获得状态栏高度
    private int getStatusBarHeight() {
    	int result = 0;
	    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	      result = getResources().getDimensionPixelSize(resourceId);
	    }
	    return result;
    }
 
    // service退出时关闭浮动窗口
    @Override
    public void onDestroy() {
        if (lrc1 != null) {
        	winm.removeView(lrc1);
        }
        if (lrc2 != null) {
        	winm.removeView(lrc2);
        }
        super.onDestroy();
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
}