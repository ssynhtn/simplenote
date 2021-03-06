package com.ssynhtn.simplenote;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ssynhtn.simplenote.data.NoteContract.NoteEntry;
import com.ssynhtn.simplenote.util.MyUtilities;

public class RecycleNoteFragment extends BaseNoteFragment {

	public static final String TAG = MyUtilities.createTag(RecycleNoteFragment.class);

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
		intent.putExtra(NewNoteActivity.EXTRA_RECYLE, 1);
		startActivity(intent);
		
	}

	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String order = NoteEntry.COLUMN_DATE + " desc";
		String[] projection = null;
		// only displays notes with "recycle" to be 1
		String selection = NoteEntry.COLUMN_RECYCLE + " = ? ";
		String[] selectionArgs = new String[]{Integer.toString(1)};
		return new CursorLoader(getActivity(), NoteEntry.CONTENT_URI, projection, selection, selectionArgs, order);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		Log.d(TAG, "cursor loading finished, about to swap cursor of adapter");
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
		
	}
	

	@Override
	protected int putToRecycleOrPermanentDelete(long id) {
		ContentResolver resolver = getActivity().getContentResolver();

		Uri uriWithId = NoteEntry.buildSingleNoteUri(id);
		int deleted = resolver.delete(uriWithId, null, null);

		return deleted;
	}

	
}
