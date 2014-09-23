package com.ssynhtn.mypagertabs;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;
import com.ssynhtn.mypagertabs.data.NoteContract.NoteJoinReminder;
import com.ssynhtn.mypagertabs.data.NoteContract.ReminderEntry;

public class NotificationNoteFragment extends BaseNoteFragment{
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.fragment_note, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
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
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		String selection = NoteEntry.COLUMN_RECYCLE + " = 0";
		String order = NoteEntry.COLUMN_DATE + " DESC";
		return new CursorLoader(getActivity(), NoteJoinReminder.CONTENT_URI, null, selection, null, order);

	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		// TODO Auto-generated method stub
		adapter.swapCursor(data);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
	}

//	@Override
//	protected int putToRecycleOrPermanentDelete(String title, String note,
//			String date) {
//		ContentResolver resolver = getActivity().getContentResolver();
//		ContentValues values = new ContentValues();
//		values.put(NoteEntry.COLUMN_RECYCLE, 1);
//		
//		String selection = NoteEntry.COLUMN_TITLE + " = ? and " + NoteEntry.COLUMN_NOTE + " = ? and " + NoteEntry.COLUMN_DATE + " = ? ";
//		String[] selectionArgs = new String[]{title, note, date};
//		int numToRecycle = resolver.update(NoteEntry.CONTENT_URI, values, selection, selectionArgs);
//		return numToRecycle;
//	}

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
