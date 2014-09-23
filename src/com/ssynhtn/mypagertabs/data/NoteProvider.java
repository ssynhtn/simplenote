package com.ssynhtn.mypagertabs.data;

import java.util.Arrays;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ssynhtn.mypagertabs.MyUtilities;
import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;
import com.ssynhtn.mypagertabs.data.NoteContract.NoteJoinReminder;
import com.ssynhtn.mypagertabs.data.NoteContract.ReminderEntry;

public class NoteProvider extends ContentProvider {

	public static final String TAG = MyUtilities.createTag(NoteProvider.class);

	public static final int NOTES = 0;
	public static final int SINGLE_NOTE = 1;
	public static final int SEARCH_SUGGEST = 2;
	// code for reminder table
	public static final int REMINDERS = 3;
	public static final int SINGLE_REMINDER = 4;
	// code for note join reminder
	public static final int NOTE_JOIN_REMINDER = 5;

	private static UriMatcher sUriMatcher = initUriMatcher();

	private NoteDbHelper mHelper;

	public static UriMatcher initUriMatcher(){
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTE, NOTES);
		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTE + "/#", SINGLE_NOTE);
		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTE + "/" + SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTE + "/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

		// uri matches for reminder table
		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_REMINDER, REMINDERS);
		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_REMINDER + "/#", SINGLE_REMINDER);
		// uri matches for note join reminder
		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTE_JOIN_REMINDER, NOTE_JOIN_REMINDER);
		return matcher;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int code = sUriMatcher.match(uri);	
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int numDeleted;

		switch(code){
		case SINGLE_NOTE: {
			long id = ContentUris.parseId(uri);
			String mySelection = NoteEntry._ID + " = " + id;
			if(selection != null){
				mySelection = mySelection + " and " + selection;
			}

			numDeleted = db.delete(NoteContract.NoteEntry.TABLE_NAME, mySelection, selectionArgs);
			break;
		}
		case NOTES: {
			numDeleted = db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);
			break;
		}

		case SINGLE_REMINDER: {
			long id = ContentUris.parseId(uri);
			String mySelection = ReminderEntry._ID + " = " + id;
			if(selection != null){
				mySelection = mySelection + " AND " + selection;
			}

			numDeleted = db.delete(ReminderEntry.TABLE_NAME, mySelection, selectionArgs);
			break;
		}

		case REMINDERS: {
			numDeleted = db.delete(ReminderEntry.TABLE_NAME, selection, selectionArgs);
			break;
		}

		default: numDeleted = 0; break;
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return numDeleted;
	}

	@Override
	public String getType(Uri uri) {
		int code = sUriMatcher.match(uri);
		switch(code){
		case NOTES: return NoteEntry.CONTENT_TYPE;
		case SINGLE_NOTE: return NoteEntry.CONTENT_TYPE_ITEM;
		case SEARCH_SUGGEST: return NoteEntry.CONTENT_TYPE;	// search suggest kinds of return a list
		case REMINDERS: return ReminderEntry.CONTENT_TYPE;
		case SINGLE_REMINDER: return ReminderEntry.CONTENT_TYPE_ITEM;
		default: throw new IllegalArgumentException("Unknown uri's type: " + uri);	// no match
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int code = sUriMatcher.match(uri);

		SQLiteDatabase db = mHelper.getWritableDatabase();
		Uri res = null;
		switch(code){
		case NOTES: {
			long id = db.insert(NoteEntry.TABLE_NAME, null, values);
			if(id == -1){
				throw new IllegalArgumentException("can't insert uri: " + uri + " with values: " + values);
			}

			res = NoteEntry.buildSingleNoteUri(id);
			break;
		}

		case REMINDERS: {
			long id = db.insert(ReminderEntry.TABLE_NAME, null, values);
			if(id == -1){
				throw new IllegalArgumentException("Unable to insert values: " + values + " into uri: " + uri);
			}

			res = ReminderEntry.buildSingleReminderUri(id);
			break;
		}

		default: throw new IllegalArgumentException("Unknown type of uri: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return res;
	}

	@Override
	public boolean onCreate() {
		mHelper = new NoteDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String order) {

		SQLiteDatabase db = mHelper.getReadableDatabase();

		Log.d(TAG, "query for uri: " + uri);

		Cursor res = null;
		int code = sUriMatcher.match(uri);
		switch(code){
		case NOTES: {
			Log.d(TAG, "query is for note list");
			res = db.query(NoteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, order);
			break;
		}
		case SINGLE_NOTE: {
			long noteId = ContentUris.parseId(uri);
			Log.d(TAG, "query for single note with id " + noteId);

			String mySelection = NoteEntry._ID + " = " + noteId;
			if(selection != null){
				mySelection = mySelection + " AND " + selection;
			}
			res = db.query(NoteEntry.TABLE_NAME, projection, mySelection, selectionArgs, null, null, order);
			break;
		}
		case SEARCH_SUGGEST: {
			//			String mySelection = NoteEntry.COLUMN_NOTE + " like ?";
			// currently not handling anything but returning a null cursor
			// show the args, if args are with spaces..
			Log.d(TAG, "selection args: " + Arrays.asList(selectionArgs));

			String query = selectionArgs[0].trim();
			// show nothing for empty search query
			if(TextUtils.isEmpty(query)){
				return null;
			}

			String mySelection = NoteEntry.COLUMN_TITLE + " like ? OR " + NoteEntry.COLUMN_NOTE + " like ?";
			String[] mySelectionArgs = {"%" + query + "%", "%" + query + "%"};
			res = db.query(NoteEntry.TABLE_NAME, projection, mySelection, mySelectionArgs, null, null, null);
			break;
		}

		case REMINDERS: {
			res = db.query(ReminderEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, order);
			break;
		}
		case SINGLE_REMINDER: {
			long id = ContentUris.parseId(uri);

			String mySelection = ReminderEntry._ID + " = " + id;
			if(selection != null){
				mySelection = mySelection + " AND " + selection;
			}

			res = db.query(ReminderEntry.TABLE_NAME, projection, mySelection, selectionArgs, null, null, order);
			break;
		}
		case NOTE_JOIN_REMINDER: {
			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(NoteJoinReminder.TABLE_NAME);
			builder.setProjectionMap(NoteJoinReminder.COLUMN_MAP);
			// default group by ReminderEntry.COLUMN_NOTE_ID "note_id" to force result cursor containing only 
			// one row for each note
			res = builder.query(db, projection, selection, selectionArgs, ReminderEntry.COLUMN_NOTE_ID, null, order);
			break;
		}
		default: {
			Log.d(TAG, "some how no type match!");
			break;

		}
		}

		ContentResolver resolver = getContext().getContentResolver();
		Log.d(TAG, "resolver: " + resolver);
		Log.d(TAG, "uri: " + uri);
		Log.d(TAG, "res cursor: " + res);
		res.setNotificationUri(getContext().getContentResolver(), uri);
		return res;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int code = sUriMatcher.match(uri);
		SQLiteDatabase db = mHelper.getWritableDatabase();

		int numModified = 0;

		switch(code){
		case NOTES: {
			numModified = db.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);
			break;
		}

		case SINGLE_NOTE: {
			long id = ContentUris.parseId(uri);
			String mySelection = NoteEntry._ID + " = " + id;
			if(selection != null){
				mySelection = NoteEntry._ID + " = " + id + " AND " + selection;
			}

			numModified = db.update(NoteEntry.TABLE_NAME, values, mySelection, selectionArgs);
			break;
		}

		case REMINDERS: {
			numModified = db.update(ReminderEntry.TABLE_NAME, values, selection, selectionArgs);
			break;
		}
		case SINGLE_REMINDER: {
			long id = ContentUris.parseId(uri);

			String mySelection = ReminderEntry._ID + " = " + id;
			if(selection != null){
				mySelection = mySelection + " AND " + selection;
			}

			numModified = db.update(ReminderEntry.TABLE_NAME, values, mySelection, selectionArgs);
			break;
		}
		default: {
			Log.d(TAG, "unexpected uri: " + uri);
			throw new IllegalArgumentException("unexpected uri: " + uri);
		}
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return numModified;
	}

}
