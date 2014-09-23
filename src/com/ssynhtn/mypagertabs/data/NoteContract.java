package com.ssynhtn.mypagertabs.data;

import java.util.HashMap;
import java.util.Map;

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

	// note join reminder path
	public static final String PATH_NOTE_JOIN_REMINDER = "note_join_reminder";
	
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
	
	public static class NoteJoinReminder {
		// join table
		public static final String TABLE_NAME = NoteEntry.TABLE_NAME + " INNER JOIN " + ReminderEntry.TABLE_NAME + " ON "
				+ NoteEntry.TABLE_NAME + "." + NoteEntry._ID + " = "
				+ ReminderEntry.TABLE_NAME + "." + ReminderEntry.COLUMN_NOTE_ID;
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE_JOIN_REMINDER).build();
		
		public static final Map<String, String> COLUMN_MAP = initColumnMap();
		
		private static final Map<String, String> initColumnMap(){
			Map<String, String> map = new HashMap<String, String>();
			map.put(ReminderEntry._ID, ReminderEntry.TABLE_NAME + "." + ReminderEntry._ID);
			map.put(ReminderEntry.COLUMN_REMINDER_TIME, ReminderEntry.TABLE_NAME + "." + ReminderEntry.COLUMN_REMINDER_TIME);
			map.put(ReminderEntry.COLUMN_NOTE_ID, ReminderEntry.TABLE_NAME + "." + ReminderEntry.COLUMN_NOTE_ID);
			map.put(NoteEntry.COLUMN_TITLE, NoteEntry.TABLE_NAME + "." + NoteEntry.COLUMN_TITLE);
			map.put(NoteEntry.COLUMN_NOTE, NoteEntry.TABLE_NAME + "." + NoteEntry.COLUMN_NOTE);
			map.put(NoteEntry.COLUMN_DATE, NoteEntry.TABLE_NAME + "." + NoteEntry.COLUMN_DATE);
			map.put(NoteEntry.COLUMN_RECYCLE, NoteEntry.TABLE_NAME + "." + NoteEntry.COLUMN_RECYCLE);
			
			return map;
		}
	}

}
