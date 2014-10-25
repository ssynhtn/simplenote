package com.ssynhtn.simplenote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ssynhtn.simplenote.data.NoteContract.NoteEntry;
import com.ssynhtn.simplenote.util.MyUtilities;

public class NoteFragment extends BaseNoteFragment {
	
	private static final String TAG = MyUtilities.createTag(NoteFragment.class);

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_note, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_add_note){
			addNote();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addNote(){
		Intent intent = new Intent(getActivity(), NewNoteActivity.class);
		startActivity(intent);
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String order = NoteEntry.COLUMN_DATE + " desc";
		
		String[] projection = null;
//		String selection = NoteEntry.COLUMN_RECYCLE + " is NULL or " + NoteEntry.COLUMN_RECYCLE + " = ? ";
		String selection = NoteEntry.COLUMN_RECYCLE + " = ? ";
		String[] selectionArgs = new String[]{Integer.toString(0)};
//		String selection = null;
//		String[] selectionArgs = null;
		return new CursorLoader(getActivity(), NoteEntry.CONTENT_URI, projection, selection, selectionArgs, order);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
		
	}


	@Override
	protected int putToRecycleOrPermanentDelete(long id) {
		// TODO Auto-generated method stub
		ContentResolver resolver = getActivity().getContentResolver();
		ContentValues values = new ContentValues(1);
		values.put(NoteEntry.COLUMN_RECYCLE, 1);
		
		Uri uriWithId = NoteEntry.buildSingleNoteUri(id);
		int numUpdated = resolver.update(uriWithId, values, null, null);
		return numUpdated;
	}

}
