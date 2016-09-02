package com.yuer.MusicPlayer.myclass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuer.MusicPlayer.MusicList;
import com.yuer.MusicPlayer.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter{

	private List<Map<String, Object>> data;
	private Context context;
	private static HashMap<Integer, Boolean> select;
	private SharedPreferences sharedPre;
	
	public ListViewAdapter(Context context, List<Map<String, Object>> data)
	{
		sharedPre = context.getSharedPreferences("fodname", PreferenceActivity.MODE_WORLD_WRITEABLE);
		this.data = data;
		this.context = context;
		select = new HashMap<Integer, Boolean>();
		for (int i = 0; i < 100; i++)
		{
			select.put(i, false);
        }
		for (int i = 0; i < sharedPre.getInt("fodnum", 0); i++)
		{
			select.put(i+1, sharedPre.getInt("check"+i, 0)==1);
        }
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder
	{
		ImageView iv;
		CheckBox cb;
        TextView tv;
    }

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2)
	{//视图刷新  对即将显示却未显示的一行刷新
		ViewHolder holder;
        if (convertView == null)
        {
        	LayoutInflater inflater = LayoutInflater.from(context);
    		convertView = inflater.inflate(R.layout.directory, null);
        	holder = new ViewHolder();
            holder.iv = (ImageView) convertView.findViewById(R.id.fodicon);
            holder.cb = (CheckBox) convertView.findViewById(R.id.check);
            holder.tv = (TextView) convertView.findViewById(R.id.fodname);
            convertView.setTag(holder);
        }
        else
        {// 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iv.setImageResource((Integer)data.get(position).get("icon"));
        holder.cb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setcheck(position,!select.get(position));
				
				Editor editor = sharedPre.edit();
				editor.putInt("check"+(position-1), select.get(position)?1:0);
				editor.commit();
				MusicList.update();
			}
		});
        //System.out.println(position);
        if(position==0) holder.cb.setVisibility(View.GONE);
        else
        {
        	holder.cb.setChecked(select.get(position));
        	holder.cb.setVisibility(View.VISIBLE);
        }
        holder.tv.setText((String)data.get(position).get("fname"));
		return convertView;
	}
	
	public void setcheck(int position,boolean b)
	{
		select.put(position, b);
	}
	
	public boolean getcheck(int position)
	{
		return select.get(position);
	}
}
