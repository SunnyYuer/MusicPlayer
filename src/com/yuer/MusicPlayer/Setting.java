package com.yuer.MusicPlayer;

import com.yuer.MusicPlayer.lrc.LrcService;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

public class Setting extends PreferenceActivity{

	//private CheckBoxPreference showwinlrc;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        
        //showwinlrc = (CheckBoxPreference) findPreference("showwinlrc");
	}
	
	@Override
	protected void onRestart() {
		LrcService.lrc1.setVisibility(View.INVISIBLE);
		LrcService.lrc2.setVisibility(View.INVISIBLE);
		super.onRestart();
	}
}
