package com.ssynhtn.mypagertabs.data;

import android.app.SearchManager;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class NoteContract {
	
	public static final String CONTENT_AUTHORITY = "com.ssynhtn.mypagertabs.noteprovider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	public static final String PATH_NOTE = "note";
	
	// reminder path
	public static final String PATH_REMINDER = "reminder";
	public static final String PATH_REMINDER_NOTE = "reminder_note";
	
	public static class NoteEntry implements BaseColumns {
		public static final String TABLE_NAME = "note";
		
		//trigger name to copy _id to search_suggest_id
		public static final String TRIGGER_UPDATE_SEARCH_SUGGEST_ID = "trigger_update_search_suggest_id";
		
		// for search suggestions
		// title and note columns are renamed to column names from searchmanager
		public static final String COLUMN_SEARCH_SGGESTION_ID = SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID; 
		public static final String COLUMN_TITLE = SearchManager.SUGGEST_COLUMN_TEXT_1;
		public static final String COLUMN_NOTE = SearchManager.SUGGEST_COLUMN_TEXT_2;
		
		public static final String COLUMN_DATE = "date";
		public static final String COLUMN_RECYCLE = "recycle";
		
		public static final String COLUMN_REMINDER_TIME = "reminder";
		
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE).build();
		public static final Uri SEARCH_SUGGEST_CONTENT_URI = CONTENT_URI.buildUpon().appendPath(SearchManager.SUGGEST_URI_PATH_QUERY).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
		
		
		public static Uri buildSingleNoteUri(long id){
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
	
	public static class ReminderEntry implements BaseColumns {
		public static final String TABLE_NAME = "reminder";
		
		public static final String COLUMN_REMINDER_TIME = "reminder_time";
		public static final String COLUMN_NOTE_ID = "note_id";
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REMINDER).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_REMINDER;
		
		public static Uri buildSingleReminderUri(long id){
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
	
	// note with reminder view
	public static class ReminderNoteEntry {
		public static final String VIEW_NAME = "reminder_note";
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REMINDER_NOTE).build();
	}
	

}
