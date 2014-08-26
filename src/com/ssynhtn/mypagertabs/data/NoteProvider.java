package com.ssynhtn.mypagertabs.data;

import org.apache.http.client.utils.URIUtils;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.suitebuilder.annotation.SmallTest;

public class NoteProvider extends ContentProvider {
	
	public static final int NOTE = 0;
	public static final int SINGLE_NOTE = 1;
	
	private static UriMatcher sUriMatcher = initUriMatcher();
	
	private NoteDbHelper mHelper;

	public static UriMatcher initUriMatcher(){
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTE, NOTE);
		matcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.PATH_NOTE + "/#", SINGLE_NOTE);
		return matcher;
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		int code = sUriMatcher.match(uri);
		switch(code){
		case NOTE: return NoteEntry.CONTENT_TYPE;
		case SINGLE_NOTE: return NoteEntry.CONTENT_TYPE_ITEM;
		default: break;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int code = sUriMatcher.match(uri);
		
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Uri res = null;
		switch(code){
		case NOTE: {
			long id = db.insert(NoteEntry.TABLE_NAME, null, values);
			if(id == -1){
				throw new UnsupportedOperationException("can't insert uri: " + uri);
			}

			res = NoteContract.buildSingleNoteUri(id);
			break;
		}
		
		default: throw new UnsupportedOperationException("bad uri: " + uri);
		}
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
		
		Cursor res = null;
		int code = sUriMatcher.match(uri);
		switch(code){
		case NOTE: {
			res = db.query(NoteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, order);
			break;
		}
		case SINGLE_NOTE: {
			long noteId = ContentUris.parseId(uri);
			selection = NoteEntry._ID + " = ";
			selectionArgs = new String[]{Long.toString(noteId)};
			res = db.query(NoteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, order);
			break;
		}
		default: break;
		}
		
		res.setNotificationUri(getContext().getContentResolver(), uri);
		return res;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
