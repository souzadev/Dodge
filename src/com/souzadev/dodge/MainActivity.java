package com.souzadev.dodge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {	
	public final static String EXTRA_TYPE = "com.souzadev.dodge.TYPE";
	public final static String EXTRA_TIME = "com.souzadev.dodge.TIME";
	//******************************** OVERRIDE ************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {        
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_action_settings){
        	showSettings(null);
        }
        return super.onOptionsItemSelected(item);
    }
    
    //******************************** PUBLIC *****************************************
    public void playGame(View view){
    	Intent intent = new Intent(this, WorldActivity.class);
    	startActivity(intent);
    }
    
    public void showSettings(View view){
    	Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
    }
    
    public void showScore(View view){
    	Intent intent = new Intent(this, ScoreActivity.class);
    	startActivity(intent);
    }
    
    public void exitApp(View view){
    	this.finish();
    }    
}
