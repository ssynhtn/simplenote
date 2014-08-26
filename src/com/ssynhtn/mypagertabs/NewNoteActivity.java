package com.ssynhtn.mypagertabs;

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

public class NewNoteActivity extends ActionBarActivity {
	private static final String TAG = TagUtility.createTag(NewNoteActivity.class);
	
	public static final String RESULT_NOTE_ITEM = "result_note_item";
	public static final int REQUEST_CODE_NEW_NOTE = 1;
	
	private EditText mEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_note);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mEditText = (EditText) findViewById(R.id.new_note_edittext);
		final Button noteButton = (Button) findViewById(R.id.new_note_button);
		
		noteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				saveNoteAsResult();
				finish();
			}
		});
	}
	
	private void saveNoteAsResult(){
		Log.d(TAG, "saving result..");
		String text = mEditText.getText().toString();
		NoteItem item = new NoteItem(text, text);
		
		Intent intent = new Intent();
		intent.putExtra(RESULT_NOTE_ITEM, item);
		setResult(RESULT_OK, intent);
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
