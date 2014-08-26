package com.ssynhtn.mypagertabs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;

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
		final String SQL_CREATE_NOTE_TABLE  = "CREATE TABLE " + NoteEntry.TABLE_NAME + "("
				+ NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ NoteEntry.COLUMN_TITLE + " TEXT, "
				+ NoteEntry.COLUMN_NOTE + " TEXT, "
				+ NoteEntry.COLUMN_DATE + " TEXT"
				+ ");";
		
		db.execSQL(SQL_CREATE_NOTE_TABLE);
		Log.d(TAG, "database created, sql is: " + SQL_CREATE_NOTE_TABLE);
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE " + NoteEntry.TABLE_NAME + " IF EXISTS;");
		onCreate(db);
		
	}
	
	

}
