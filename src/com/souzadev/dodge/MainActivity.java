package com.souzadev.dodge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class MainActivity extends Activity {	
	public final static String EXTRA_TYPE = "com.souzadev.dodge.TYPE";
	public final static String EXTRA_TIME = "com.souzadev.dodge.TIME";
	
	//ADS
	private AdView adView;
	private static final String AD_UNIT_ID = "ca-app-pub-5104897685750315/3844846189";
	private static final String DEVICE_ID = "AE279B9C2F7AE55662682438B86E1C98";
	
	//******************************** OVERRIDE ************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        initAds();
        
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
    
    @Override
    protected void onDestroy() {    	
    	if (adView != null){
    		adView.destroy();
    	}
    	super.onDestroy();
    };
    
    //******************************** PRIVATE *************************************
    private void initAds(){
    	//CREATE AD
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);
        
        //ADD VIEW TO LAYOUT
        LinearLayout layout = (LinearLayout)findViewById(R.id.main_linearLayout_mainMenu);
        layout.addView(adView);
        
        //CREATE AD REQUEST
        AdRequest adRequest = new AdRequest.Builder()
        	.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        	.addTestDevice(DEVICE_ID)
        	.build();
        adView.loadAd(adRequest);
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
