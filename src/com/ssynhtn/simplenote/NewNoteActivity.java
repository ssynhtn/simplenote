package com.ssynhtn.simplenote;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ssynhtn.simplenote.data.NoteContract.NoteEntry;
import com.ssynhtn.simplenote.model.NoteItem;
import com.ssynhtn.simplenote.util.MyUtilities;

public class NewNoteActivity extends ColorActionBarActivity {
	private static final String TAG = MyUtilities.createTag(NewNoteActivity.class);
	
	public static final String RESULT_NOTE_ITEM = "result_note_item";
	public static final int REQUEST_CODE_NEW_NOTE = 1;
	
	// if intent has this integer extra and value not 0, then the recycle property of new item 
	// is seen as true
	public static final String EXTRA_RECYLE = "extra_recycle";
	
	
	// now this activity supports editing old notes, these two refer to that
	public static final String EDIT_NOTE = "edit_note";
	public static final String EDIT_NOTE_ID = "edit_note_id";
	private boolean editNote;
	private long noteId;
	// when saving note, if oldNote and oldTitle are not changed, then don't save at all
	private String oldTitle;
	private String oldNote;
	
	private EditText mEditText;
	private EditText mTitleEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarColor(getResources().getColor(R.color.light_green));
		setContentView(R.layout.activity_new_note);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mEditText = (EditText) findViewById(R.id.new_note_edittext);
		mTitleEditText = (EditText) findViewById(R.id.new_note_title_edittext);
//		final Button noteButton = (Button) findViewById(R.id.new_note_button);
		
//		noteButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				saveNoteAsResult();
//				finish();
//			}
//		});
//		
//		Button cancelButton = (Button) findViewById(R.id.cancel_note_button);
//		cancelButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});
		
		handleIntent(getIntent());
	}
	
	// if this new note activity is started by implicit intent, such as
	// by sharing, then extract data from intent
	private void handleIntent(Intent intent){
		Log.d(TAG, "handling intent: " + intent);
		String action = intent.getAction();
		Log.d(TAG, "action is: " + action);
		String mimeType = intent.getType();
		Log.d(TAG, "mime type is: " + mimeType);
		if(Intent.ACTION_SEND.equals(action) && "text/plain".equals(mimeType)){
			Log.d(TAG, "creating note from shared stuff");
			if(intent.hasExtra(Intent.EXTRA_TEXT)){
				String text = intent.getStringExtra(Intent.EXTRA_TEXT);
				mEditText.setText(text);
				Log.d(TAG, "text is " + text);
			}
			
			if(intent.hasExtra(Intent.EXTRA_SUBJECT)){
				mTitleEditText.setText(intent.getStringExtra(Intent.EXTRA_SUBJECT));
			}
		}else if(intent.hasExtra(EDIT_NOTE) && intent.getBooleanExtra(EDIT_NOTE, false)){
			// edit old note
			Log.d(TAG, "edit old note");
			noteId = intent.getLongExtra(EDIT_NOTE_ID, -1);
			if(noteId == -1){
				throw new IllegalArgumentException("can't edit note with no id");
			}
			oldNote = intent.getStringExtra(Intent.EXTRA_TEXT);
			oldTitle = intent.getStringExtra(Intent.EXTRA_SUBJECT);
			editNote = true;
			
			mEditText.setText(oldNote);
			mTitleEditText.setText(oldTitle);
		} else {
			Log.d(TAG, "creating new note from scratch");
		}
		
	}
	
	private void saveNoteAsResult(){
		Log.d(TAG, "saving result..");
		String title = mTitleEditText.getText().toString();
		String text = mEditText.getText().toString();
		if(text.equals("") && title.equals("")){	// both title and note are empty
			if(editNote){	// old note now edited to empty
				Toast.makeText(this, "deleting empty note", Toast.LENGTH_SHORT).show();
				deleteNote(noteId);
			}else{	// new note, but empty
				Toast.makeText(this, "Can't save empty note!", Toast.LENGTH_SHORT).show();
			}
		} else if(editNote){	// edit note
			if(title.equals(oldTitle) && text.equals(oldNote)){
				// no meaningful editing done
				Toast.makeText(this, "No Edit done", Toast.LENGTH_SHORT).show();
			}else{
				updateNote(noteId, title, text);
			}
		} else {
			NoteItem item = new NoteItem(title, text);
			
			// if EXTRA_RECYCLE is set to not 0, then deemed as recycle = true
			Intent intent = getIntent();
			if(intent.hasExtra(EXTRA_RECYLE) && intent.getIntExtra(EXTRA_RECYLE, 0) != 0){
				item.setRecycle(true);
			}
			addNewNoteItem(item);
		}
			
			
	}
	
	private void deleteNote(long noteId){
		AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {
		};
		
		handler.startDelete(0, null, NoteEntry.buildSingleNoteUri(noteId), null, null);
	}
	
	private void updateNote(long noteId, String title, String note){
		ContentValues values = new ContentValues(2);
		values.put(NoteEntry.COLUMN_NOTE, note);
		values.put(NoteEntry.COLUMN_TITLE, title);
		
		AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {
		};
		handler.startUpdate(0, null, NoteEntry.buildSingleNoteUri(noteId), values, null, null);
	}
	
	private void addNewNoteItem(NoteItem item){		
		ContentValues values = new ContentValues();
		values.put(NoteEntry.COLUMN_TITLE, item.getTitle());
		values.put(NoteEntry.COLUMN_NOTE, item.getNote());
		values.put(NoteEntry.COLUMN_DATE, item.getDate().getTime());
		
		int recycle = item.isRecycle() ? 1 : 0;
		values.put(NoteEntry.COLUMN_RECYCLE, recycle);
		
		
//		resolver.insert(NoteEntry.CONTENT_URI, values);
		// test using asyncqueryhandler
		AsyncQueryHandler handler = new AsyncQueryHandler(getContentResolver()) {};
		handler.startInsert(0, null, NoteEntry.CONTENT_URI, values);
		
	}
	
	// let back button as save too!
	// But! when pressing up button, the MainActivity seems to be newly created!!
	// all notes back to default array list!
	@Override
	public void onBackPressed() {
		saveNoteAsResult();
		super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == android.R.id.home){
			// if the 'UP' button is pressed
			saveNoteAsResult();
//			return super.onOptionsItemSelected(item);
			
			// this is equivalent to 
			// NavUtils.navigateUpTo(this, this.getParentActivityIntent(this));
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}  else if(id == R.id.action_cancel_note){
			Toast.makeText(this, "Editing canceled", Toast.LENGTH_SHORT).show();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
