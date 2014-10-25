package com.ssynhtn.simplenote;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.ssynhtn.simplenote.util.MyUtilities;

public class ColorActionBarActivity extends ActionBarActivity {
	
	protected void setActionBarColor(int color){
    	ActionBar actionBar = getSupportActionBar();
    	
    	BitmapDrawable singleColor = MyUtilities.makeSingleColorDrawable(this, color);
    	actionBar.setBackgroundDrawable(singleColor);   
    	
	}
}
