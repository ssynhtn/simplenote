package com.ssynhtn.mypagertabs;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;
import com.ssynhtn.mypagertabs.data.NoteContract.ReminderEntry;


public class NoteDetailFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	public static final String TAG = NoteDetailFragment.class.getSimpleName();
	
//	public static final String EXTRA_NOTE = "extra_note";
//	private static final String EXTRA_TITLE = "extra_title";
//	private static final String EXTRA_DATE = "extra_date";

	private static final String EXTRA_NOTE_URI = "extra_note_entry";
	
	private TextView mNoteView;
	private Uri mNoteItemUri;
	
	// note data to be loaded
	private String mTitle;
	private String mNote;
	private String mDate;

	public static interface OnDeleteNoteListener {
		void onDeleteNote();
	}
	
	private OnDeleteNoteListener mListener;
	
	public NoteDetailFragment() {
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_note_detail, menu);
		
		
		MenuItem item = menu.findItem(R.id.action_share);
		ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
		if(provider == null){
			Log.d(TAG, "provider null!!");
		}else{
			provider.setShareIntent(makeShareIntent());
		}
	}
	
	private Intent makeShareIntent(){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
		intent.putExtra(Intent.EXTRA_TEXT, mNote);
		intent.setType("text/plain");
		return intent;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_delete){
			deleteCurrentNote();
			return true;
		}
		else if(id == R.id.action_reminder){
			addOrRemoveReminder();
			return true;
		}
//		else if(id == R.id.action_share){
//			ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//			provider.setShareIntent(makeShareIntent());
//			return false;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addOrRemoveReminder() {
		// TODO Auto-generated method stub
		ContentResolver resolver = getActivity().getContentResolver();
		
		long id = ContentUris.parseId(mNoteItemUri);
		String selection = ReminderEntry.COLUMN_NOTE_ID + " = " + id;
		Cursor cursor = resolver.query(ReminderEntry.CONTENT_URI, null, selection, null, null);
		if(cursor != null && cursor.getCount() > 0){
			// have to remove all reminders pointing to this note
			int numDeleted = resolver.delete(ReminderEntry.CONTENT_URI, selection, null);
			String logText = "deleted " + numDeleted + " reminders";
			
			Log.d(TAG,logText);
			Toast.makeText(getActivity(), logText, Toast.LENGTH_SHORT).show();
		}else {
			// add a reminder with current time
			Calendar cal = Calendar.getInstance();
			long timeMills = cal.getTimeInMillis();
			ContentValues values = new ContentValues();
			values.put(ReminderEntry.COLUMN_NOTE_ID, id);
			values.put(ReminderEntry.COLUMN_REMINDER_TIME, timeMills);
			Uri res = resolver.insert(ReminderEntry.CONTENT_URI, values);
			String logText = "created reminder, res uri: " + res;
			
			Log.d(TAG, logText);
			Toast.makeText(getActivity(), logText, Toast.LENGTH_SHORT).show();
		}
		
	}

	private void deleteCurrentNote(){
//		String note = getArguments().getString(EXTRA_NOTE);
		ContentResolver resolver = getActivity().getContentResolver();
		
//		String selection = NoteEntry.COLUMN_NOTE + " = ? ";
//		String[] selectionArgs = new String[]{note};
		
		// query the NoteEntry.COLUMN_RECYCLE first to see if this note is in recycle state
		Cursor cursor = resolver.query(mNoteItemUri, null, null, null, null);
		boolean isRecyle = false;
		if(cursor.moveToFirst()){
			int recycle = cursor.getInt(cursor.getColumnIndex(NoteEntry.COLUMN_RECYCLE));
			if(recycle == 1)
				isRecyle = true;
		}
		
		int numDeleted;
		// if in recycle state, then delete, else mark as recycle
		if(isRecyle){
			numDeleted = resolver.delete(mNoteItemUri, null, null);			
		}else {
			ContentValues values = new ContentValues();
			values.put(NoteEntry.COLUMN_RECYCLE, 1);
			numDeleted = resolver.update(mNoteItemUri, values, null, null);
		}
		
		Toast.makeText(getActivity(), "deleted count: " + numDeleted, Toast.LENGTH_SHORT).show();
		mListener.onDeleteNote();
	}
	
	
	
	public static NoteDetailFragment newInstance(Uri noteItemUri){
		Log.d(TAG, "create note detail fragment with data uri: " + noteItemUri);
		Bundle args = new Bundle();
		args.putParcelable(EXTRA_NOTE_URI, noteItemUri);
		
		NoteDetailFragment fragment = new NoteDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
//	public static NoteDetailFragment newInstance(String title, String note, String date){
//		Bundle args = new Bundle();
//		args.putString(EXTRA_NOTE, note);
//		args.putString(EXTRA_TITLE, title);
//		args.putString(EXTRA_DATE, date);
//		
//		NoteDetailFragment fragment = new NoteDetailFragment();
//		fragment.setArguments(args);
//		return fragment;
//	}

	@Override
	public void onAttach(Activity activity) {
		try{
			mListener = (OnDeleteNoteListener) activity;
		}catch(ClassCastException e){
			Log.d(TAG, "Activity " + activity + " should implement OnDeleteNoteListener");
		}
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_note_detail,
				container, false);
		
		mNoteView = (TextView) rootView.findViewById(R.id.note_textview);
//		String note = getArguments().getString(EXTRA_NOTE);
//		noteView.setText(note);
		
		Bundle args = getArguments();
		if(args.containsKey(EXTRA_NOTE_URI)){
			mNoteItemUri = args.getParcelable(EXTRA_NOTE_URI);
			getLoaderManager().initLoader(0, null, this);	// this returns v4 LoaderManager
//			getActivity().getLoaderManager().initLoader(0, null, this);
		}
//		else{
//			String title = args.getString(EXTRA_TITLE);
//			String note = args.getString(EXTRA_NOTE);
//			String date = args.getString(EXTRA_DATE);
//			
//			mNoteView.setText(title + "\n" + note + "\n" + date);
//		}
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.d(TAG, "loading for note item uri: " + mNoteItemUri);
		return new CursorLoader(getActivity(), mNoteItemUri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		// TODO Auto-generated method stub
		if(data != null){
			if(data.moveToFirst()){
				mTitle = data.getString(data.getColumnIndex(NoteEntry.COLUMN_TITLE));
				mNote = data.getString(data.getColumnIndex(NoteEntry.COLUMN_NOTE));
				mDate = data.getString(data.getColumnIndex(NoteEntry.COLUMN_DATE));
				
				mNoteView.setText(mTitle + "\n" + mNote + "\n" + mDate);
				
			} else {
				Log.d(TAG, "data has zero rows");
			}
		}else{
			Log.d(TAG, "no note data!!, data is " + data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}