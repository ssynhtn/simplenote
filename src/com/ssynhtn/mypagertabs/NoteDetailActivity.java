package com.ssynhtn.mypagertabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoteDetailActivity extends ActionBarActivity {
	
	public static final String EXTRA_NOTE = "extra_note";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_detail);
		if (savedInstanceState == null && getIntent().hasExtra(EXTRA_NOTE)) {
			String note = getIntent().getStringExtra(EXTRA_NOTE);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, NoteDetailFragment.newInstance(note)).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note_detail, menu);
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
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class NoteDetailFragment extends Fragment {

		public NoteDetailFragment() {
		}
		
		public static NoteDetailFragment newInstance(String note){
			Bundle args = new Bundle();
			args.putString(EXTRA_NOTE, note);
			
			NoteDetailFragment fragment = new NoteDetailFragment();
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_note_detail,
					container, false);
			
			TextView noteView = (TextView) rootView.findViewById(R.id.note_textview);
			String note = getArguments().getString(EXTRA_NOTE);
			noteView.setText(note);
			return rootView;
		}
	}
}
