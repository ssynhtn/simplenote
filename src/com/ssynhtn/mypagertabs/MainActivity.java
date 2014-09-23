package com.ssynhtn.mypagertabs;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.ssynhtn.mypagertabs.BaseNoteFragment.OnItemClickCallback;
import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;


public class MainActivity extends ActionBarActivity implements OnPageChangeListener, OnItemClickCallback {
	
	private static final String TAG = MyUtilities.createTag(MainActivity.class);
	
	private static final int NUM_FRAGMENTS = 3;
	
	private ViewPager mViewPager;
	private PagerAdapter mPagerAdapter;
	private PagerSlidingTabStrip mTabs;
	
	private Fragment mNoteFragment = new NoteFragment();
	private Fragment mNotificationNoteFragment = new NotificationNoteFragment();
	private Fragment mRecycleNoteFragment = new RecycleNoteFragment();
	
	private int currentColor;
	private Drawable oldActionBarBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        handleIntent(getIntent());
        
    }

	private void initialSetup() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
        
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        
        // set margin between pages
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);
        
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.slidingtabs);
        mTabs.setViewPager(mViewPager);
        mTabs.setOnPageChangeListener(this);
        
        Resources res = getResources();
        currentColor = res.getColor(R.color.light_blue);
        changeActionBarColor(currentColor);
	}

    private void handleIntent(Intent intent){
    	String action = intent.getAction();
    	if(Intent.ACTION_SEARCH.equals(action)){
    		String query = intent.getStringExtra(SearchManager.QUERY);
    		doSearch(query);
    	}else if(Intent.ACTION_VIEW.equals(action)){
    		Uri data = intent.getData();
    		viewData(data);
    	} else {
    		initialSetup();
    	}
    }
    
    
    // when a specific search suggestion is clicked. data points to uri of a note
    // this should show the details of the note
    private void viewData(Uri data) {
		// TODO Auto-generated method stub
//    	String dataString = data.toString();
//    	doSearch(dataString);
    	Log.d(TAG, "data for note detail: " + data);
    	Intent intent = new Intent(this, NoteDetailActivity.class);
    	intent.setData(data);
    	startActivity(intent);
	}

    // open search result activity to show search result
	private void doSearch(String query) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, SearchResultActivity.class);
		intent.putExtra(SearchManager.QUERY, query);
		startActivity(intent);
		
	}

	@Override
    protected void onDestroy() {
    	Log.d(TAG, "Main destoryed");
    	super.onDestroy();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.d(TAG, "on new intent: " + intent);
    	setIntent(intent);
    	handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onColorClicked(View view){
    	ImageView imageView = (ImageView) view;
    	ColorDrawable colorImage = (ColorDrawable) imageView.getBackground();
    	int color = getColorFromColorDrawable(colorImage);
    	changeActionBarColor(color);
    }
    
    private int getColorFromColorDrawable(ColorDrawable cd){
    	Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
    	Canvas canvas = new Canvas(bitmap);
    	cd.draw(canvas);
    	int color = bitmap.getPixel(0, 0);
    	bitmap.recycle();
    	return color;
    }
    
    private void changeActionBarColor(int color){
    	Resources res = getResources();
    	
    	currentColor = color;
    	
    	mTabs.setIndicatorColor(currentColor);
    	
    	ActionBar actionBar = getSupportActionBar();
    	
    	BitmapDrawable singleColor = getSingleColorDrawable(currentColor);
    	Drawable bottom = res.getDrawable(R.drawable.actionbar_bottom);
    	LayerDrawable background = new LayerDrawable(new Drawable[]{singleColor, bottom});
    	
    	if(oldActionBarBackground == null){
    		actionBar.setBackgroundDrawable(background);    		
    	}else {
    		TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldActionBarBackground, background});
    		actionBar.setBackgroundDrawable(td);
    		td.startTransition(200);
    	}
    	
    	oldActionBarBackground = background;
    	
    }
    
    private BitmapDrawable getSingleColorDrawable(int color){
    	Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
    	bitmap.setPixel(0, 0, color);
    	BitmapDrawable image = new BitmapDrawable(getResources(), bitmap);
    	return image;
    }
    
    public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			if(index == 0){
				return mNoteFragment;
			}else if(index == 1){
				return mNotificationNoteFragment;
			}else if(index == 2){
				return mRecycleNoteFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return NUM_FRAGMENTS;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			int stringId;
			switch(position){
			case 0: stringId = R.string.note_title; break;
			case 1: stringId = R.string.private_note_title; break;
			case 2: stringId = R.string.recycle_note_title; break;
			default: return null;
			}
			
			return getString(stringId);
		}
    	
    }

    // these three methods are for on page change listener
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPageSelected(int index) {
		// if "notes" page, then blue, if "private notes" page, then green
		
		int colorId = R.color.light_blue;
		if(index == 1){
			colorId = R.color.light_green;
		} else if(index == 2){
			colorId = R.color.light_red;
		}
		int color = getResources().getColor(colorId);
		
		changeActionBarColor(color);
		
	}

	// this main activity has a fragment that contains the note list, when one note 
	// is clicked, would call this
	@Override
	public void onItemClick(long id) {
		Intent intent = new Intent(this, NoteDetailActivity.class);
		Uri data = NoteEntry.buildSingleNoteUri(id);
		intent.setData(data);
		startActivity(intent);
		
	}
}
