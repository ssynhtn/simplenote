package com.ssynhtn.mypagertabs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;

public class NoteDbHelper extends SQLiteOpenHelper {
	private static final String TAG = NoteDbHelper.class.getSimpleName();
	
	private static final String DATABASE_NAME = "notes.db";
	
	// add trigger and search_suggest_id column
	private static final int OLD_DATABASE_VERSION = 3;
	private static final int DATABASE_VERSION = 4;

	public NoteDbHelper(Context context,
			int version) {
		super(context, DATABASE_NAME, null, version);
		// TODO Auto-generated constructor stub
	}
	
	public NoteDbHelper(Context context){
		this(context, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQLite doesn't have a boolean date type, integer 0 for false, others for true
		final String SQL_CREATE_NOTE_TABLE  = "CREATE TABLE " + NoteEntry.TABLE_NAME + "("
				+ NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ NoteEntry.COLUMN_TITLE + " TEXT, "
				+ NoteEntry.COLUMN_NOTE + " TEXT, "
				+ NoteEntry.COLUMN_DATE + " TEXT, "
				+ NoteEntry.COLUMN_RECYCLE + " INTEGER, "
				+ NoteEntry.COLUMN_SEARCH_SGGESTION_ID + " INTEGER"
				+ ");";
		
		final String SQL_CREATE_TRIGGER_TO_UPDATE_SEARCH_SUGGEST_ID = "CREATE TRIGGER "
				+ NoteEntry.TRIGGER_UPDATE_SEARCH_SUGGEST_ID
				+ " AFTER INSERT ON "
				+ NoteEntry.TABLE_NAME
				+ " FOR EACH ROW BEGIN "
				+ "UPDATE " + NoteEntry.TABLE_NAME + " SET " + NoteEntry.COLUMN_SEARCH_SGGESTION_ID
				+ " = " + NoteEntry._ID + " WHERE "
				+ NoteEntry._ID + " = new." + NoteEntry._ID + "; END;";
		
		db.execSQL(SQL_CREATE_NOTE_TABLE);
		db.execSQL(SQL_CREATE_TRIGGER_TO_UPDATE_SEARCH_SUGGEST_ID);
		Log.d(TAG, "database created, sql is: " + SQL_CREATE_NOTE_TABLE);
		Log.d(TAG, "created trigger: " + SQL_CREATE_TRIGGER_TO_UPDATE_SEARCH_SUGGEST_ID);
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		// TODO Auto-generated method stub
//		if(newVersion == DATABASE_VERSION && oldVersion == OLD_DATABASE_VERSION){
////			String alterSql = "alter table " + NoteEntry.TABLE_NAME + " add column recycle integer;";
////			String setDefaultValueSql = "update " + NoteEntry.TABLE_NAME + " set recycle = 0;";
////			db.execSQL(alterSql);
////			db.execSQL(setDefaultValueSql);
//			
//			String updateNullSql = "update " + NoteEntry.TABLE_NAME + " set " + NoteEntry.COLUMN_RECYCLE + " = 0 where " + NoteEntry.COLUMN_RECYCLE + " is null";
//			db.execSQL(updateNullSql);			
//		}else{
//			Log.d(TAG, "unexpected upgrade from old version: " + oldVersion + " to new vesion: " + newVersion);
//		}
		
		// trigger is automatically dropped with the note table
		db.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME + ";");
		onCreate(db);
		
	}
	
	

}
