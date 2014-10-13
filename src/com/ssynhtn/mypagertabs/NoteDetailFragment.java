package com.ssynhtn.mypagertabs;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ssynhtn.mypagertabs.TwoPickersDialogFragment.OnTimeSetCallback;
import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;
import com.ssynhtn.mypagertabs.data.NoteContract.ReminderEntry;


public class NoteDetailFragment extends Fragment implements LoaderCallbacks<Cursor>, OnTimeSetCallback {
	
	public static final String TAG = NoteDetailFragment.class.getSimpleName();
	
//	public static final String EXTRA_NOTE = "extra_note";
//	private static final String EXTRA_TITLE = "extra_title";
//	private static final String EXTRA_DATE = "extra_date";

	private static final String EXTRA_NOTE_URI = "extra_note_entry";
	public static final String EXTRA_NOTE_RECYCLE = "extra_note_recyle";
	
	private static final int REMINDER_LOADER_ID = 1;
	private static final int NOTE_LOADER_ID = 2;
	
	private TextView mNoteView;
	private Uri mNoteItemUri;
	
	private ListView mRemindersListView;
	private CursorAdapter mRemindersAdapter;
	
	// note data to be loaded
	private String mTitle;
	private String mNote;
	private String mDate;
	private boolean mRecyle;

	public static interface OnDeleteNoteListener {
		void onDeleteNote();
		void onRestoreNote();	// just to be simple
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
		
		if(!mRecyle){
			MenuItem restoreItem = menu.findItem(R.id.action_restore);
			restoreItem.setVisible(false);
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
		} else if(id == R.id.action_restore){
			item.setVisible(false);	// note is restored, so no restore is needed
			mRecyle = false;
			restoreNote();
		}
//		else if(id == R.id.action_share){
//			ShareActionProvider provider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//			provider.setShareIntent(makeShareIntent());
//			return false;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	// if the current note is a recycle note, restore it
	private void restoreNote(){
		AsyncQueryHandler handler = new AsyncQueryHandler(getActivity().getContentResolver()) {
			@Override
			protected void onUpdateComplete(int token, Object cookie, int result) {
				Toast.makeText(getActivity(), "note restored", Toast.LENGTH_SHORT).show();
				mListener.onRestoreNote();
			}
		};
		ContentValues values = new ContentValues(1);
		values.put(NoteEntry.COLUMN_RECYCLE, 0);
		handler.startUpdate(0, null, mNoteItemUri, values, null, null);
	}

	private void createReminder(final long timeMillis){
		ContentResolver resolver = getActivity().getContentResolver();
		final AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

		long id = ContentUris.parseId(mNoteItemUri);
		
		ContentValues values = new ContentValues();
		values.put(ReminderEntry.COLUMN_NOTE_ID, id);
		values.put(ReminderEntry.COLUMN_REMINDER_TIME, timeMillis);
		AsyncQueryHandler handler = new AsyncQueryHandler(resolver) {
			@Override
			protected void onInsertComplete(int token, Object cookie, Uri uri) {
				String logText = "created reminder";
				
				// create alarm for this reminder
				Intent intent = new Intent(getActivity(), ReminderReceiver.class);
				// this intent will be delivered to onReceive
				// and it's data will be used to create an intent to open a NoteDetailActivity
				// so use mNoteItemUri, rather than uri(for reminder stuff)
				// if I later want to show reminder stuff on notification
				// I can pass reminder strings as extra in this mNoteItemUri
				intent.setData(mNoteItemUri);
				PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
				alarmManager.set(AlarmManager.RTC_WAKEUP, timeMillis, pi);
				
				Log.d(TAG, logText);
				Toast.makeText(getActivity(), logText, Toast.LENGTH_SHORT).show();
			}
		};
		handler.startInsert(0, null, ReminderEntry.CONTENT_URI, values);
	}
	
	private void createReminder(){
		final long timeMillis = System.currentTimeMillis() + 10 * 1000; 	// 10 seconds later
		createReminder(timeMillis);
		
	}
	
	private void addOrRemoveReminder() {
		// TODO Auto-generated method stub
		ContentResolver resolver = getActivity().getContentResolver();
		
		AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		
		long id = ContentUris.parseId(mNoteItemUri);
		String selection = ReminderEntry.COLUMN_NOTE_ID + " = " + id;
		Cursor cursor = resolver.query(ReminderEntry.CONTENT_URI, null, selection, null, null);
		if(cursor != null && cursor.getCount() > 0){
			// before removing all the reminders, get their ids and remove the real remiders(notifications) first
			int idIndex = cursor.getColumnIndex(ReminderEntry._ID);
			while(cursor.moveToNext()){
				long reminderId = cursor.getLong(idIndex);
				Uri reminderUri = ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, reminderId);
				Intent intent = new Intent(getActivity(), ReminderReceiver.class);
				intent.setData(reminderUri);
				PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
				alarmManager.cancel(pi);				
			}
			
			// have to remove all reminders pointing to this note
			int numDeleted = resolver.delete(ReminderEntry.CONTENT_URI, selection, null);
			String logText = "deleted " + numDeleted + " reminders";
			
			Log.d(TAG, logText);
			Toast.makeText(getActivity(), logText, Toast.LENGTH_SHORT).show();
		}else {
			createReminder();
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
	
	
	
	public static NoteDetailFragment newInstance(Uri noteItemUri, boolean recycle){
		Log.d(TAG, "create note detail fragment with data uri: " + noteItemUri);
		Bundle args = new Bundle();
		args.putParcelable(EXTRA_NOTE_URI, noteItemUri);
		args.putBoolean(EXTRA_NOTE_RECYCLE, recycle);
		
		NoteDetailFragment fragment = new NoteDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	private void showNewReminderDialog(){
		// now don't use NewReminderDialog, but TwoPickersDialog to directly show two pickers 
		// in one dialog
//		DialogFragment fragment = new NewReminderDialogFragment();
//		FragmentManager fm = getFragmentManager();
//		fragment.show(fm, null);
		
		TwoPickersDialogFragment fragment = TwoPickersDialogFragment.newInstance(this);
		FragmentManager fm = getFragmentManager();
		fragment.show(fm, null);
	}

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
		
		mNoteView = (TextView) rootView.findViewById(R.id.note_text);
		mRemindersListView = (ListView) rootView.findViewById(R.id.list_reminders);
		
		String[] from = {ReminderEntry.COLUMN_REMINDER_TIME};
		int[] to = {R.id.reminder_textview};
//		mRemindersAdapter = new SimpleCursorAdapter(getActivity(), R.layout.single_reminder_item_view, null, from, to, 0);
		mRemindersAdapter = new ReminderCursorAdapter(getActivity(), null, 0);
		mRemindersListView.setAdapter(mRemindersAdapter);

		Button createReminderButton = (Button) rootView.findViewById(R.id.button_add_reminder);
		createReminderButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showNewReminderDialog();
				
			}
		});
		Bundle args = getArguments();
		if(args.containsKey(EXTRA_NOTE_URI)){
			mNoteItemUri = args.getParcelable(EXTRA_NOTE_URI);
			getLoaderManager().initLoader(NOTE_LOADER_ID, null, this);	// this returns v4 LoaderManager
			
			getLoaderManager().initLoader(REMINDER_LOADER_ID, null, this);
		}
		
		if(args.containsKey(EXTRA_NOTE_RECYCLE)){
			mRecyle = args.getBoolean(EXTRA_NOTE_RECYCLE);
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
		switch(id){
		case NOTE_LOADER_ID: {
			Log.d(TAG, "loading for note item uri: " + mNoteItemUri);
			return new CursorLoader(getActivity(), mNoteItemUri, null, null, null, null);			
		}
		
		case REMINDER_LOADER_ID: {
			long noteId = ContentUris.parseId(mNoteItemUri);
			String selection = ReminderEntry.COLUMN_NOTE_ID + " = " + noteId;
			return new CursorLoader(getActivity(), ReminderEntry.CONTENT_URI, null, selection, null, null);
		}
		default: {
			throw new IllegalArgumentException("unexpected id: " + id);
		}
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		
		long id = loader.getId();
		if(id == NOTE_LOADER_ID){
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
		}else if(id == REMINDER_LOADER_ID){
			mRemindersAdapter.swapCursor(data);
		}else{
			throw new IllegalArgumentException("unexpected loader id: " + id);
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		long id = loader.getId();
		if(id == NOTE_LOADER_ID){
			mNoteView.setText("loader reset");
		}else if(id == REMINDER_LOADER_ID){
			mRemindersAdapter.swapCursor(null);
		}
	}


	@Override
	public void onTimeSet(int year, int month, int day, int hour, int minute) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute);
		createReminder(cal.getTimeInMillis());
	}
	
	
}