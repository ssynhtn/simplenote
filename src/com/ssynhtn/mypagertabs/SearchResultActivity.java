package com.ssynhtn.mypagertabs;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;

public class SearchResultActivity extends ListActivity implements LoaderCallbacks<Cursor>{

	private String mQuery;
	private SimpleCursorAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// list activity don't need this
//		setContentView(R.layout.activity_search_result);
		
		mQuery = getIntent().getStringExtra(SearchManager.QUERY);
		String[] from = {NoteEntry.COLUMN_TITLE, NoteEntry.COLUMN_NOTE, NoteEntry.COLUMN_DATE}; 
		int[] to = {R.id.note_title_textview, R.id.note_textview, R.id.note_date_textview};
		mAdapter = new SimpleCursorAdapter(this, R.layout.single_note_item_view, null, from, to, 0);
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
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
