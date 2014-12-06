package com.ssynhtn.simplenote;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ssynhtn.simplenote.adapter.NoteCursorAdapter;
import com.ssynhtn.simplenote.adapter.NoteCursorAdapter.ViewHolder;

public abstract class BaseNoteFragment extends Fragment implements LoaderCallbacks<Cursor>{

	private static final String TAG = BaseNoteFragment.class.getSimpleName();



	protected ListView listView;
	protected CursorAdapter adapter;

	protected OnItemClickCallback mCallback;

	public static interface OnItemClickCallback {
		// id the note item id
		void onItemClick(long id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	// onActivityCreated is called after onCreateView, so adapter is created before initLoader
	// so adapter.swapCursor() won't be called on null
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try{
			mCallback = (OnItemClickCallback) activity;
		}catch(ClassCastException e){
			Log.w(TAG, "Activity " + activity + " must implement OnItemClickCallback interface!");
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_note, container, false);

		listView = (ListView) rootView.findViewById(R.id.listview_notes);

//		String[] from = new String[]{NoteEntry.COLUMN_TITLE, NoteEntry.COLUMN_NOTE, NoteEntry.COLUMN_DATE};
//		int[] to = {R.id.note_title_textview, R.id.note_textview, R.id.note_date_textview};

		Log.d(TAG, "creating adapter and set list adapter to it");
//		adapter = new SimpleCursorAdapter(getActivity(), R.layout.single_note_item_view, null, from, to, 0);
		// switch to note fragment
		adapter = new NoteCursorAdapter(getActivity(), null, 0);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				mCallback.onItemClick(id);

			}
		});

		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(makeMultiChoiceModeListener());

		return rootView;
	}

	// override this to providea different multichoice mode listener
	protected MultiChoiceModeListener makeMultiChoiceModeListener(){
		return new MultiChoiceModeListener() {

			private int numSelected;

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				numSelected = 0;
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.note_fragment_action_mode, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				int id = item.getItemId();
				if(id == R.id.action_copy){
					copySelectedItems();
					mode.finish();
					return true;
				} else if(id == R.id.action_delete){
					deleteSelectedItems();
					mode.finish();
					return true;
				}
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int arg1, long arg2,
					boolean checked) {
				numSelected += checked ? 1 : -1;
				mode.setTitle(numSelected + " selected notes");

			}

			// copy into a single ClipData.Item
			private void copySelectedItems(){
				SparseBooleanArray list = listView.getCheckedItemPositions();
				int size = list.size();
				StringBuilder sb = new StringBuilder();

				for(int i = 0; i < size; i++){
					int key = list.keyAt(i);
					boolean checked = list.valueAt(i);
					if(checked){
						View itemView = listView.getChildAt(key);
						ViewHolder holder = (ViewHolder) itemView.getTag();
						String text = holder.noteView.getText().toString();
						String title = holder.titleView.getText().toString();

						sb.append(title).append("\n").append(text).append("\n");
					}
				}

				String res = sb.toString();
				Log.d(TAG, "copying " + res + " into clip board");
				Toast.makeText(getActivity(), "Data copied to clipboard.", Toast.LENGTH_SHORT).show();
				copyStringToClipboard(res);
			}


		};
	}
	
	private void copyStringToClipboard(String text){
		ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
		
		ClipData data = ClipData.newPlainText(null, text);
		manager.setPrimaryClip(data);
	}

	private void deleteSelectedItems(){
		int numDeleted = 0;

		// this was based on title, note and date to delete items, now 
		// I want to use ids
		long[] checkedIds = listView.getCheckedItemIds();
		for(int i = 0; i < checkedIds.length; i++){
			Log.d(TAG, "checked id: " + checkedIds[i]);
		}
		
		for(int i = 0; i < checkedIds.length; i++){
			long id = checkedIds[i];
			putToRecycleOrPermanentDelete(id);
		}
		
		numDeleted = checkedIds.length;
//		SparseBooleanArray list = listView.getCheckedItemPositions();
//		for(int i = 0; i < list.size(); i ++){
//			boolean checked = list.valueAt(i);
//			if(checked){
//				int index = list.keyAt(i);
//				View itemView = listView.getChildAt(index);
//				TextView titleView = (TextView ) itemView.findViewById(R.id.note_title_textview);
//				TextView noteView = (TextView) itemView.findViewById(R.id.note_textview);
//				TextView dateView = (TextView) itemView.findViewById(R.id.note_date_textview);
//
//				String title = titleView.getText().toString();
//				String note = noteView.getText().toString();
//				String date = dateView.getText().toString();
//				
//				int deleted = putToRecycleOrPermanentDelete(title, note, date);
//				if(deleted > 0){
//					numDeleted += deleted;
//				}
//			}
//		}

		Toast.makeText(getActivity(), "Deleted: " + numDeleted, Toast.LENGTH_SHORT).show();
	}
	
//	protected abstract int putToRecycleOrPermanentDelete(String title, String note, String date);
	protected abstract int putToRecycleOrPermanentDelete(long id);

}
