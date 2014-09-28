package com.ssynhtn.mypagertabs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;
import com.ssynhtn.mypagertabs.data.NoteContract.ReminderEntry;
import com.ssynhtn.mypagertabs.data.NoteContract.ReminderNoteEntry;

public class NoteDbHelper extends SQLiteOpenHelper {
	private static final String TAG = NoteDbHelper.class.getSimpleName();
	
	private static final String DATABASE_NAME = "notes.db";
	
	private static final int DATABASE_VERSION = 1;

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
				+ NoteEntry._ID + " INTEGER PRIMARY KEY, "
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
		
		final String SQL_CREATE_REMINDER_TABLE = "CREATE TABLE " + ReminderEntry.TABLE_NAME + "("
				+ ReminderEntry._ID + " INTEGER PRIMARY KEY, "
				+ ReminderEntry.COLUMN_NOTE_ID + " INTEGER NOT NULL, "
				+ ReminderEntry.COLUMN_REMINDER_TIME + " TEXT, "
				+ "FOREIGN KEY(" + ReminderEntry.COLUMN_NOTE_ID + ")" + " REFERENCES "
				+ NoteEntry.TABLE_NAME + "(" + NoteEntry._ID + ") ON DELETE CASCADE"
				+ ");";
		
		final String SQL_CREATE_NOTE_WITH_REMINDER_VIEW = "CREATE VIEW " + ReminderNoteEntry.VIEW_NAME + " AS "
				+ " SELECT DISTINCT "
				+ NoteEntry.TABLE_NAME + "." + NoteEntry._ID + ", "
				+ NoteEntry.TABLE_NAME + "." + NoteEntry.COLUMN_TITLE + ", "
				+ NoteEntry.TABLE_NAME + "." + NoteEntry.COLUMN_NOTE + ", "
				+ NoteEntry.TABLE_NAME + "." + NoteEntry.COLUMN_DATE + ", "
				+ NoteEntry.TABLE_NAME + "." + NoteEntry.COLUMN_RECYCLE
				+ " FROM " + NoteEntry.TABLE_NAME + " INNER JOIN " + ReminderEntry.TABLE_NAME
				+ " ON " + NoteEntry.TABLE_NAME + "." + NoteEntry._ID + " = " + ReminderEntry.TABLE_NAME + "." + ReminderEntry.COLUMN_NOTE_ID + ";";
	
		
		db.execSQL(SQL_CREATE_NOTE_TABLE);
		db.execSQL(SQL_CREATE_TRIGGER_TO_UPDATE_SEARCH_SUGGEST_ID);
		db.execSQL(SQL_CREATE_REMINDER_TABLE);
		db.execSQL(SQL_CREATE_NOTE_WITH_REMINDER_VIEW);
		
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
		// first drop reminder and then note table
		db.execSQL("DROP TABLE IF EXISTS " + ReminderEntry.TABLE_NAME + ";");
		db.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME + ";");
		db.execSQL("DROP VIEW IF EXISTS " + ReminderNoteEntry.VIEW_NAME + ";");
		onCreate(db);
		
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		if(!db.isReadOnly()){
			// in sqlite you have to specify this to make foreign key happen;
			// and this has to be called each time the db is opened, it's for this session only
			final String SQL_PRAGMA_FOREIGN_KEY = "PRAGMA foreign_keys = ON;";
			db.execSQL(SQL_PRAGMA_FOREIGN_KEY);
		}
	}
	
	

}
