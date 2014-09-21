package com.ssynhtn.mypagertabs;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;

public abstract class BaseNoteFragment extends Fragment implements LoaderCallbacks<Cursor>{

	private static final String TAG = BaseNoteFragment.class.getSimpleName();



	protected ListView listView;
	protected SimpleCursorAdapter adapter;

	protected OnItemClickCallback mCallback;

	public static interface OnItemClickCallback {
		void onItemClick(NoteItem note);
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
			Log.d(TAG, "Activity " + activity + " must implement OnItemClickCallback interface!");
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_note, container, false);

		listView = (ListView) rootView.findViewById(R.id.listview_notes);

		String[] from = new String[]{NoteEntry.COLUMN_TITLE, NoteEntry.COLUMN_NOTE, NoteEntry.COLUMN_DATE};
		int[] to = {R.id.note_title_textview, R.id.note_textview, R.id.note_date_textview};

		Log.d(TAG, "creating adapter and set list adapter to it");
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.single_note_item_view, null, from, to, 0);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				Cursor cursor = adapter.getCursor();
				if(cursor.moveToPosition(position)){
					String title = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_TITLE));
					String note = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NOTE));
					String date = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_DATE));
					NoteItem item = new NoteItem(title, note, date);
					mCallback.onItemClick(item);
				}else {
					Log.d(TAG, "strange...");
				}

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

			private void copySelectedItems(){
				SparseBooleanArray list = listView.getCheckedItemPositions();
				int size = list.size();
				StringBuilder sb = new StringBuilder();

				for(int i = 0; i < size; i++){
					int key = list.keyAt(i);
					boolean checked = list.valueAt(i);
					if(checked){
						View itemView = listView.getChildAt(key);
						TextView textView = (TextView) itemView.findViewById(R.id.note_textview);
						String text = textView.getText().toString();

						sb.append(text + "|");
					}
				}

				String res = sb.toString();
				Toast.makeText((Activity) mCallback, res, Toast.LENGTH_SHORT).show();
			}


		};
	}

	private void deleteSelectedItems(){
		int numDeleted = 0;

		SparseBooleanArray list = listView.getCheckedItemPositions();
		for(int i = 0; i < list.size(); i ++){
			boolean checked = list.valueAt(i);
			if(checked){
				int index = list.keyAt(i);
				View itemView = listView.getChildAt(index);
				TextView titleView = (TextView ) itemView.findViewById(R.id.note_title_textview);
				TextView noteView = (TextView) itemView.findViewById(R.id.note_textview);
				TextView dateView = (TextView) itemView.findViewById(R.id.note_date_textview);

				String title = titleView.getText().toString();
				String note = noteView.getText().toString();
				String date = dateView.getText().toString();
				
				int deleted = putToRecycleOrPermanentDelete(title, note, date);
				if(deleted > 0){
					numDeleted += deleted;
				}
			}
		}

		Toast.makeText(getActivity(), "Deleted: " + numDeleted, Toast.LENGTH_SHORT).show();
	}
	
	protected abstract int putToRecycleOrPermanentDelete(String title, String note, String date);

}