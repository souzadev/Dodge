package com.souzadev.dodge;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.souzadev.dodge.ColorPickerDialog.OnColorChangedListener;

public class SettingsActivity extends Activity {	
	//Ambient
	private SharedPreferences sharedPrefs;
	//Components
	private Switch mSwitch;
	private TextView mView;
	
	private ColorPickerDialog cpDialog;	
	

	//******************************************** OVERRIDE ****************************************************
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		sharedPrefs = this.getSharedPreferences(getString(R.string.prefs_string_file_MAIN_PREFS) ,Context.MODE_PRIVATE);
				
		initializeComponents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	//********************************************** PRIVATE ******************************************
	private void initializeComponents(){
		//Initialize Switch
		mSwitch = (Switch)findViewById(R.id.settings_switch_keepName);
		mSwitch.setChecked(sharedPrefs.getBoolean(getString(R.string.prefs_boolean_KEEP_LAST_NAME), true));
		
		mSwitch = (Switch)findViewById(R.id.settings_switch_tripp);
		mSwitch.setChecked(sharedPrefs.getBoolean(getString(R.string.prefs_boolean_TRIPP), false));
		
		//Initialize Color Views
		mView = (TextView)findViewById(R.id.settings_textView_playerColor);
		mView.setBackgroundColor(sharedPrefs.getInt(getString(R.string.prefs_int_PLAYER_IN_COLOR), Color.GREEN));
		
		mView = (TextView)findViewById(R.id.settings_textView_npcColor);
		mView.setBackgroundColor(sharedPrefs.getInt(getString(R.string.prefs_int_NPC_IN_COLOR), Color.RED));
	}
	
	private OnColorChangedListener colorListener = new OnColorChangedListener() {		
		@Override
		public void colorChanged(String key, int color) {
			SharedPreferences.Editor editor = sharedPrefs.edit();
			if (key == "player"){
				mView = (TextView)findViewById(R.id.settings_textView_playerColor);
				mView.setBackgroundColor(color);				
				editor.putInt(getString(R.string.prefs_int_PLAYER_IN_COLOR), color);
				editor.commit();
				
			}else{
				mView = (TextView)findViewById(R.id.settings_textView_npcColor);
				mView.setBackgroundColor(color);
				editor.putInt(getString(R.string.prefs_int_NPC_IN_COLOR), color);
				editor.commit();
			}
		}
	};
	
	//*********************************************** PUBLIC *******************************************
	
	public void switchClick(View view){
		mSwitch = (Switch)view;
		Editor editor;
		
		switch (mSwitch.getId()){
		case R.id.settings_switch_keepName:
			editor = sharedPrefs.edit();
			editor.putBoolean(getString(R.string.prefs_boolean_KEEP_LAST_NAME), mSwitch.isChecked());
			editor.commit();
			break;
			
		case R.id.settings_switch_tripp:
			editor = sharedPrefs.edit();
			editor.putBoolean(getString(R.string.prefs_boolean_TRIPP), mSwitch.isChecked());
			editor.commit();
			break;
		}
	}
	
	public void showColorPicker(View view){
		int defColor = Color.BLUE;
		int initColor = Color.BLUE;
		String pickerKey = "";
		
		switch(view.getId()){
			case R.id.settings_textView_playerColor:
				initColor =  sharedPrefs.getInt(getString(R.string.prefs_int_PLAYER_IN_COLOR), Color.GREEN);
				defColor = Color.GREEN;
				
				pickerKey = "player";
				break;
				
			case R.id.settings_textView_npcColor:
				initColor =  sharedPrefs.getInt(getString(R.string.prefs_int_NPC_IN_COLOR), Color.RED);
				defColor = Color.RED;
				
				pickerKey = "npc";
				break;
		}
				
		cpDialog = new ColorPickerDialog(this, colorListener, pickerKey, initColor, defColor);
		cpDialog.show();
	}
}