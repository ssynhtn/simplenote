package com.ssynhtn.mypagertabs.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class NoteContract {
	
	public static final String CONTENT_AUTHORITY = "com.ssynhtn.mypagetabs";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	public static final String PATH_NOTE = "note";
	
	public static class NoteEntry implements BaseColumns {
		public static final String TABLE_NAME = "note";
		
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_NOTE = "note";
		public static final String COLUMN_DATE = "date";
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
	}
	
	public static Uri buildSingleNoteUri(long id){
		return ContentUris.withAppendedId(NoteEntry.CONTENT_URI, id);
	}

}
