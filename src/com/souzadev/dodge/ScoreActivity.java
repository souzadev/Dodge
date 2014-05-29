package com.souzadev.dodge;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ScoreActivity extends Activity {	
	//Core
	private Intent intent;
	private String name;
	private long time;
	private int position;
	
	private SharedPreferences sharedPrefs;
	
	//Components
	private TextView textView;
	private EditText editText;
	private Button button;
	
	//List
	private ListView listView;
	private ArrayAdapter<String> listArrayAdapter;
	
	//Time
	private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS");
	
	//******************************* OVERRIDE ***************************************
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		
		sharedPrefs = this.getSharedPreferences(getString(R.string.prefs_string_file_MAIN_PREFS) ,Context.MODE_PRIVATE);
		
		//Get time from intent
		intent = getIntent();
		time = intent.getLongExtra(MainActivity.EXTRA_TIME, 0);
		
		//Initialize list and components
		initializeComponents();
		if (listArrayAdapter.getCount() < 1 ){
			resetScore();
		}
		
		position = checkPosition(time);
		if (position < 10){			
			editText.setVisibility(View.VISIBLE);
			button.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.score, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.score_action_reset) {
			resetScore();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.finish();
	}
	
	//************************************* PRIVATE ****************************************
	
	private void initializeComponents(){
		
		textView = (TextView)findViewById(R.id.score_textView_time);
		textView.setText(sdf.format(time));
		if (time > 0){
			textView.setVisibility(View.VISIBLE);
		}
		editText = (EditText)findViewById(R.id.score_editText_name);
		if(sharedPrefs.getBoolean(getString(R.string.prefs_boolean_KEEP_LAST_NAME), true)){
			editText.setText(sharedPrefs.getString(getString(R.string.prefs_string_LAST_NAME), ""));
			editText.selectAll();
		}
		
		button = (Button)findViewById(R.id.score_button_save);
		
		listView = (ListView)findViewById(R.id.score_listView_score);
		listArrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_listitens, readFile());
		listView.setAdapter(listArrayAdapter);
	}
	
	private void writeFile(){
		try{									
			int count = listArrayAdapter.getCount() - 1;
			if (count > 9) count = 9;
			
			OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("hsc", Context.MODE_PRIVATE));
			for(int i = 0; i <= count; i++){
				writer.append(listArrayAdapter.getItem(i) + "\n");
			}
			writer.close();
		}catch(Exception ex){			
			ex.printStackTrace();
		}
	}
	
	private ArrayList<String> readFile(){
		ArrayList<String> ret = new ArrayList<String>();
		
		try{
			InputStream input = openFileInput("hsc");
			if(input != null){
				InputStreamReader reader = new InputStreamReader(input);
				BufferedReader buffReader = new BufferedReader(reader);
				String line;
				while((line = buffReader.readLine()) != null){
					ret.add(line);
				}
				reader.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return ret;
	}
	
	private void addScore(long time, String name){
		String score = sdf.format(time) + " - " + name;
		listArrayAdapter.insert(score, position);
		listArrayAdapter.notifyDataSetChanged();
		writeFile();
		
		//Save last name
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(getString(R.string.prefs_string_LAST_NAME), name);
		editor.commit();
	}
	
	private int checkPosition(long time){
		int position = listArrayAdapter.getCount();
		long listTime;
				
		try{
			for (int i = 0; i < listArrayAdapter.getCount(); i++){
				listTime = Long.parseLong(listArrayAdapter.getItem(i).substring(0, 9).replaceAll("[:.]", ""));
				if(time > listTime){
					position = i;
					break;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		if (time == 0){
			position = 10;
		}
		return position;
	}
	
	private void resetScore(){
		//TODO Add to strings file
		listArrayAdapter.clear();
		listArrayAdapter.add("01:00.000 - Master");
		listArrayAdapter.add("00:50.000 - Expert");
		listArrayAdapter.add("00:40.000 - Advanced");
		listArrayAdapter.add("00:30.000 - Intermediate");
		listArrayAdapter.add("00:20.000 - Beginner");
		listArrayAdapter.notifyDataSetChanged();
		writeFile();
	}
	
	//***************************************** PUBLICS ***************************************************
	public void saveScore(View view){
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		name = editText.getText().toString();
		addScore(time, name);

		editText.setVisibility(View.GONE);
		button.setVisibility(View.GONE);
	}
}
