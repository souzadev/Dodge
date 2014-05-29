package com.souzadev.dodge;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

public class SettingsActivity extends Activity {	
	//Ambient
	private SharedPreferences sharedPrefs;
	//Components
	private Switch mSwitch;
	

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
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}
	
	//********************************************** PRIVATE ******************************************
	private void initializeComponents(){
		//Initialize Switch
		mSwitch = (Switch)findViewById(R.id.settings_switch_keepName);
		mSwitch.setChecked(sharedPrefs.getBoolean(getString(R.string.prefs_boolean_KEEP_LAST_NAME), true));
		
		mSwitch = (Switch)findViewById(R.id.settings_switch_tripp);
		mSwitch.setChecked(sharedPrefs.getBoolean(getString(R.string.prefs_boolean_TRIPP), false));			 
	}
	
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
}