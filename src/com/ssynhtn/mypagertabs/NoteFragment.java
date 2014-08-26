package com.ssynhtn.mypagertabs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NoteFragment extends Fragment {
	
	private static final String TAG = TagUtility.createTag(NoteFragment.class);
		
	private List<NoteItem> notes;
	private OnItemClickCallback mCallback;
	
	private ArrayAdapter<NoteItem> adapter;
	
	public static interface OnItemClickCallback {
		void onItemClick(NoteItem note);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		initNotes();
	}
	
	private void initNotes(){
		notes = new ArrayList<NoteItem>();
		for(int i = 0; i < 5; i ++){
			notes.add(new NoteItem("title " + i, "note text " + i));
		}
				
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_note, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.action_add_note){
			addNote();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try{
			mCallback = (OnItemClickCallback) activity;
		}catch(ClassCastException e){
			Log.d(TAG, "Activity " + activity + " must implement OnItemClickCallback interface!");
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_note, container, false);
		
		ListView listView = (ListView) rootView.findViewById(R.id.listview_notes);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, NOTES);
		adapter = new NoteAdapter(getActivity(), R.layout.single_note_item_view, R.id.note_textview, notes);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				mCallback.onItemClick(notes.get(position));
				
			}
		});
		return rootView;
		
	}
	
	private void addNote(){
		Intent intent = new Intent(getActivity(), NewNoteActivity.class);
		startActivityForResult(intent, NewNoteActivity.REQUEST_CODE_NEW_NOTE);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == NewNoteActivity.REQUEST_CODE_NEW_NOTE && resultCode == Activity.RESULT_OK && data != null){
			NoteItem item = (NoteItem) data.getSerializableExtra(NewNoteActivity.RESULT_NOTE_ITEM);
			addNewNoteItem(item);
			
			Log.d(TAG, "received result, requestcode is " + requestCode + ", resultcode is " + resultCode + " data is " + data);
			
			return;
		}
		
		Log.d(TAG, "no result... requestcode is " + requestCode + ", resultcode is " + resultCode + " data is " + data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void addNewNoteItem(NoteItem item){
		adapter.add(item);
	}
	
	public class NoteAdapter extends ArrayAdapter<NoteItem> {

		public NoteAdapter(Context context, int resource,
				int textViewResourceId, List<NoteItem> objects) {
			super(context, resource, textViewResourceId, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// if the view passed in is null, then create one
			if(convertView == null){
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				convertView = inflater.inflate(R.layout.single_note_item_view, parent, false);
			}
			
			NoteItem item = getItem(position);
			TextView titleView = (TextView) convertView.findViewById(R.id.note_title_textview);
			titleView.setText(item.getTitle());
			TextView noteView = (TextView) convertView.findViewById(R.id.note_textview);
			noteView.setText(item.getNote());
			TextView dateView = (TextView) convertView.findViewById(R.id.note_date_textview);
			// limit date length
			dateView.setText(item.getDate().toString().substring(0, 10));
			
			return convertView;
		}
		
	}

}
