package com.ssynhtn.simplenote;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.ssynhtn.simplenote.adapter.NoteCursorAdapter;
import com.ssynhtn.simplenote.data.NoteContract.NoteEntry;
import com.ssynhtn.simplenote.util.MyUtilities;

public class SearchResultActivity extends ListActivity implements LoaderCallbacks<Cursor>{

	private String mQuery;
	private CursorAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarColor(getResources().getColor(R.color.light_red));
		// list activity don't need this
//		setContentView(R.layout.activity_search_result);
		
		mQuery = getIntent().getStringExtra(SearchManager.QUERY);
//		String[] from = {NoteEntry.COLUMN_TITLE, NoteEntry.COLUMN_NOTE, NoteEntry.COLUMN_DATE}; 
//		int[] to = {R.id.note_title_textview, R.id.note_textview, R.id.note_date_textview};
		mAdapter = new NoteCursorAdapter(this, null, 0);
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}
	
	private void setActionBarColor(int color){
    	ActionBar actionBar = getActionBar();
    	
    	BitmapDrawable singleColor = MyUtilities.makeSingleColorDrawable(this, color);
    	actionBar.setBackgroundDrawable(singleColor);   
    	
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.search_result, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, NoteDetailActivity.class);
		intent.setData(NoteEntry.buildSingleNoteUri(id));
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
//		String selection = NoteEntry.COLUMN_TITLE + " like ?";
//		String[] selectionArgs = {mQuery + "%"};
		
		// utilize the search suggest query case
		String[] selectionArgs = {mQuery};
		return new CursorLoader(this, NoteEntry.SEARCH_SUGGEST_CONTENT_URI, null, null, selectionArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(data);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(null);
		
	}
}
