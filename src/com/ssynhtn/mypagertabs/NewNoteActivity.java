package com.ssynhtn.mypagertabs;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;

public class NewNoteActivity extends ActionBarActivity {
	private static final String TAG = MyUtilities.createTag(NewNoteActivity.class);
	
	public static final String RESULT_NOTE_ITEM = "result_note_item";
	public static final int REQUEST_CODE_NEW_NOTE = 1;
	
	// if intent has this integer extra and value not 0, then the recycle property of new item 
	// is seen as true
	public static final String EXTRA_RECYLE = "extra_recycle";
	
	private EditText mEditText;
	private EditText mTitleEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_note);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mEditText = (EditText) findViewById(R.id.new_note_edittext);
		mTitleEditText = (EditText) findViewById(R.id.new_note_title_edittext);
		final Button noteButton = (Button) findViewById(R.id.new_note_button);
		
		noteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				saveNoteAsResult();
				finish();
			}
		});
		
		Button cancelButton = (Button) findViewById(R.id.cancel_note_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
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
			if(intent.hasExtra(Intent.EXTRA_TEXT)){
				String text = intent.getStringExtra(Intent.EXTRA_TEXT);
				mEditText.setText(text);
				Log.d(TAG, "text is " + text);
			}
			
			if(intent.hasExtra(Intent.EXTRA_SUBJECT)){
				mTitleEditText.setText(intent.getStringExtra(Intent.EXTRA_SUBJECT));
			}
		}else {
			Log.d(TAG, "bad intent!");
		}
		
	}
	
	private void saveNoteAsResult(){
		Log.d(TAG, "saving result..");
		String title = mTitleEditText.getText().toString();
		String text = mEditText.getText().toString();
		if(text.equals("") && title.equals("")){
			Toast.makeText(this, "Can't save empty note!", Toast.LENGTH_SHORT).show();
			return;
		}
		NoteItem item = new NoteItem(title, text);
		
		// if EXTRA_RECYCLE is set to not 0, then deemed as recycle = true
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_RECYLE) && intent.getIntExtra(EXTRA_RECYLE, 0) != 0){
			item.setRecycle(true);
		}
		addNewNoteItem(item);
	}
	
	private void addNewNoteItem(NoteItem item){
		ContentResolver resolver = getContentResolver();
		
		ContentValues values = new ContentValues();
		values.put(NoteEntry.COLUMN_TITLE, item.getTitle());
		values.put(NoteEntry.COLUMN_NOTE, item.getNote());
		values.put(NoteEntry.COLUMN_DATE, MyUtilities.getSimpleDateString(item.getDate()));
		
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
		if (id == R.id.action_settings) {
			return true;
		} 
		else if(id == android.R.id.home){
			// if the 'UP' button is pressed
			saveNoteAsResult();
//			return super.onOptionsItemSelected(item);
			
			// this is equivalent to 
			// NavUtils.navigateUpTo(this, this.getParentActivityIntent(this));
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
