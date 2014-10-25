package com.ssynhtn.simplenote;

import java.sql.Date;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.AsyncQueryHandler;
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

import com.ssynhtn.simplenote.TwoPickersDialogFragment.OnTimeSetCallback;
import com.ssynhtn.simplenote.adapter.ReminderCursorAdapter;
import com.ssynhtn.simplenote.data.NoteContract.NoteEntry;
import com.ssynhtn.simplenote.data.NoteContract.ReminderEntry;
import com.ssynhtn.simplenote.util.MyUtilities;


public class NoteDetailFragment extends Fragment implements LoaderCallbacks<Cursor>, OnTimeSetCallback {
	
	public static final String TAG = NoteDetailFragment.class.getSimpleName();

	private static final String EXTRA_NOTE_URI = "extra_note_entry";
	public static final String EXTRA_NOTE_RECYCLE = "extra_note_recyle";
	
	private static final int REMINDER_LOADER_ID = 1;
	private static final int NOTE_LOADER_ID = 2;
	
	private TextView mTitleView;
	private TextView mNoteView;
	private Uri mNoteItemUri;
	
	private ListView mRemindersListView;
	private CursorAdapter mRemindersAdapter;
	
	// note data to be loaded
	private String mTitle;
	private String mNote;
	private boolean mRecyle;
	
	// if this note has reminder
	private boolean mHasReminder;
	private long mReminderDate; // reminder in millisenconds 
	
	// use a single query handler for all reminder related database operations
	private AsyncQueryHandler mReminderQueryHandler;
	
	// can't initialize this query handler at construction time, because 
	// getActivity requires at least onAttach being called
	// so defer the creation of this handler
	private AsyncQueryHandler getReminderQueryHandler(){
		if(mReminderQueryHandler == null){
			mReminderQueryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
				protected void onInsertComplete(int token, Object cookie, Uri uri) {			
					Long timeMillis = (Long) cookie;
					
					AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
					// create alarm for this reminder
					Intent intent = new Intent(getActivity(), ReminderReceiver.class);
					intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
					intent.putExtra(Intent.EXTRA_TEXT, mNote);
					// this intent will be delivered to onReceive
					// and it's data will be used to create an intent to open a NoteDetailActivity
					// so use mNoteItemUri, rather than uri(for reminder stuff)
					// if I later want to show reminder stuff on notification
					// I can pass reminder strings as extra in this mNoteItemUri
					intent.setData(mNoteItemUri);
					PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
					am.set(AlarmManager.RTC_WAKEUP, timeMillis, pi);
					
					Toast.makeText(getActivity(), "created reminder", Toast.LENGTH_SHORT).show();
				}
				protected void onUpdateComplete(int token, Object cookie, int result) {
					Long timeMillis = (Long) cookie;
					
					AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(getActivity(), ReminderReceiver.class);
					intent.setData(mNoteItemUri);
					PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
					am.cancel(pi);
					am.set(AlarmManager.RTC_WAKEUP, timeMillis, pi);
					
					Toast.makeText(getActivity(), "Reminder updated", Toast.LENGTH_SHORT).show();
				}
				protected void onDeleteComplete(int token, Object cookie, int result) {
					mHasReminder = false;
					getActivity().invalidateOptionsMenu();
					
					AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
					Intent intent = new Intent(getActivity(), ReminderReceiver.class);
					intent.setData(mNoteItemUri);
					PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
					am.cancel(pi);
					
					Toast.makeText(getActivity(), "Reminder removed", Toast.LENGTH_SHORT).show();
				}
			};
		}
		
		return mReminderQueryHandler;
	}

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
		
		if(mRecyle){
			MenuItem restoreItem = menu.findItem(R.id.action_restore);
			restoreItem.setVisible(true);
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
		
		MenuItem addReminderItem = menu.findItem(R.id.action_add_reminder);
		MenuItem dropDownItem = menu.findItem(R.id.action_reminder_drop_down);
		
		addReminderItem.setVisible(!mHasReminder);
		dropDownItem.setVisible(mHasReminder);
		
		if(mHasReminder){
			String reminderDateString = MyUtilities.makePrettyTime(getActivity(), mReminderDate);
			MenuItem modifyReminderItem = menu.findItem(R.id.action_modify_reminder);
			modifyReminderItem.setTitle(reminderDateString);
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
		} else if(id == R.id.action_edit_note){
			Intent intent = new Intent(getActivity(), NewNoteActivity.class);
			intent.putExtra(NewNoteActivity.EDIT_NOTE, true);
			intent.putExtra(NewNoteActivity.EDIT_NOTE_ID, ContentUris.parseId(mNoteItemUri));
			intent.putExtra(Intent.EXTRA_TEXT, mNote);
			intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
			startActivity(intent);
			return true;
		} else if(id == R.id.action_add_reminder){
			showNewReminderDialog(null);
			return true;
		} else if(id == R.id.action_modify_reminder){
			showNewReminderDialog(new Date(mReminderDate));
			return true;
		} else if(id == R.id.action_remove_reminder){
			deleteReminder();
			getActivity().invalidateOptionsMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	// removes reminder from database and update states
	private void deleteReminder(){
		long noteId = ContentUris.parseId(mNoteItemUri);
		String selection = ReminderEntry.COLUMN_NOTE_ID + " = ?";
		String[] selectionArgs = {String.valueOf(noteId)};
		getReminderQueryHandler().startDelete(0, null, ReminderEntry.CONTENT_URI, selection, selectionArgs);
		
	}
	

	private void createReminder(final long timeMillis){
		mHasReminder = true;
		mReminderDate = timeMillis;

		long id = ContentUris.parseId(mNoteItemUri);
		
		ContentValues values = new ContentValues();
		values.put(ReminderEntry.COLUMN_NOTE_ID, id);
		values.put(ReminderEntry.COLUMN_REMINDER_TIME, timeMillis);
		
		getReminderQueryHandler().startInsert(0, timeMillis, ReminderEntry.CONTENT_URI, values);
	}
	
	private void updateReminder(final long timeMillis){
		mReminderDate = timeMillis;		
		
		ContentValues values = new ContentValues(1);
		values.put(ReminderEntry.COLUMN_REMINDER_TIME, timeMillis);
		String selection = ReminderEntry.COLUMN_NOTE_ID + " = ?";
		long noteId = ContentUris.parseId(mNoteItemUri);
		String[] selectionArgs = {String.valueOf(noteId)};
		getReminderQueryHandler().startUpdate(0, timeMillis, ReminderEntry.CONTENT_URI, values, selection, selectionArgs);
	}
	
	

	private static final int TOKEN_RESTORE_NOTE = 0;
	private static final int TOKEN_RECYCLE_NOTE = 1;
	
	private AsyncQueryHandler mNoteQueryHandler;
	
	private AsyncQueryHandler getNoteQueryHandler(){
		if(mNoteQueryHandler == null){
			mNoteQueryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
				protected void onDeleteComplete(int token, Object cookie, int result) {
					Toast.makeText(getActivity(), "delete count: " + result, Toast.LENGTH_SHORT).show();
					mListener.onDeleteNote();
				}
				
				protected void onUpdateComplete(int token, Object cookie, int result) {
					if(token == TOKEN_RESTORE_NOTE){
						Toast.makeText(getActivity(), "note restored", Toast.LENGTH_SHORT).show();
						mListener.onRestoreNote();				
					} else if(token == TOKEN_RECYCLE_NOTE){
						Toast.makeText(getActivity(), "delete count: " + result, Toast.LENGTH_SHORT).show();
						mListener.onDeleteNote();
					} else {
						throw new RuntimeException("Unexpected token");
					}
				}
			};
		}
		return mNoteQueryHandler;
	}
	
	// if the current note is a recycle note, restore it
	private void restoreNote(){
		ContentValues values = new ContentValues(1);
		values.put(NoteEntry.COLUMN_RECYCLE, 0);
		getNoteQueryHandler().startUpdate(TOKEN_RESTORE_NOTE, null, mNoteItemUri, values, null, null);
	}
	
	private void deleteCurrentNote(){
		// if in recycle state, then delete, else mark as recycle
		if(mRecyle){
			getNoteQueryHandler().startDelete(0, null, mNoteItemUri, null, null);
		}else {
			ContentValues values = new ContentValues();
			values.put(NoteEntry.COLUMN_RECYCLE, 1);
			getNoteQueryHandler().startUpdate(TOKEN_RECYCLE_NOTE, null, mNoteItemUri, values, null, null);
		}
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
	
	private void showNewReminderDialog(Date date){
		// now don't use NewReminderDialog, but TwoPickersDialog to directly show two pickers 
		// in one dialog
//		DialogFragment fragment = new NewReminderDialogFragment();
//		FragmentManager fm = getFragmentManager();
//		fragment.show(fm, null);
		
		TwoPickersDialogFragment fragment = TwoPickersDialogFragment.newInstance(this, date);
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
		mTitleView = (TextView) rootView.findViewById(R.id.note_title);
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
				showNewReminderDialog(null);
				
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
					mRecyle = data.getInt(data.getColumnIndex(NoteEntry.COLUMN_RECYCLE)) == 1;
					
					mTitleView.setText(mTitle);
					mNoteView.setText(mNote);
					
				} else {
					Log.d(TAG, "data has zero rows");
				}
			}else{
				Log.d(TAG, "no note data!!, data is " + data);
			}
		}else if(id == REMINDER_LOADER_ID){
			if(data != null && data.moveToFirst()){
				mHasReminder = true;
				mReminderDate = data.getLong(data.getColumnIndex(ReminderEntry.COLUMN_REMINDER_TIME));
				getActivity().invalidateOptionsMenu();
			}
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
		
		if(mHasReminder){
			updateReminder(cal.getTimeInMillis());
		}else {
			// creates reminder in android system, insert into database and update state
			createReminder(cal.getTimeInMillis());	
		}
		
		// both kinds needs to update menu
		getActivity().invalidateOptionsMenu();
	}
	
	
}