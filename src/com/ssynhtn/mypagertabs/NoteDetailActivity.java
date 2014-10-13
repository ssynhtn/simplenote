package com.ssynhtn.mypagertabs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ssynhtn.mypagertabs.NoteDetailFragment.OnDeleteNoteListener;

public class NoteDetailActivity extends ColorActionBarActivity implements OnDeleteNoteListener{
	
	private static final String TAG = NoteDetailActivity.class.getSimpleName();
	
	public static final String EXTRA_COLOR = "extra_color";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		int color = intent.getIntExtra(EXTRA_COLOR, getResources().getColor(R.color.light_red));
		setActionBarColor(color);
		
		setContentView(R.layout.activity_note_detail);
		if (savedInstanceState == null) {
			NoteDetailFragment fragment = createDetailFragmentFromIntent(intent);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, fragment).commit();
		}
	}

	private NoteDetailFragment createDetailFragmentFromIntent(Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getData() != null){
			return NoteDetailFragment.newInstance(intent.getData(), intent.getBooleanExtra(NoteDetailFragment.EXTRA_NOTE_RECYCLE, false));
		}
//		else if(intent.hasExtra(NoteDetailFragment.EXTRA_NOTE)){
//			String note = intent.getStringExtra(NoteDetailFragment.EXTRA_NOTE);
//			return NoteDetailFragment.newInstance(note, note, note);
//		}
		else {
			Log.w(TAG, "bad intent: " + intent);
			return null;
		}
		
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.note_detail, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public void onDeleteNote() {
		finish();
		
	}
	@Override
	public void onRestoreNote() {
		finish();
		
	}


}
