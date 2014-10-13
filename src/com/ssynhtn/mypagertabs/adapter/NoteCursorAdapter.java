package com.ssynhtn.mypagertabs.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssynhtn.mypagertabs.R;
import com.ssynhtn.mypagertabs.data.NoteContract.NoteEntry;
import com.ssynhtn.mypagertabs.util.MyUtilities;


public class NoteCursorAdapter extends CursorAdapter {

	private Context context;
	public NoteCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		String note = MyUtilities.getThreeLineNote(cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_NOTE)));
		String title = MyUtilities.getOneLineTitle(cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_TITLE)));
		Long date = cursor.getLong(cursor.getColumnIndex(NoteEntry.COLUMN_DATE));
		
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.titleView.setText(title);
		holder.noteView.setText(note);
		holder.dateView.setText(MyUtilities.makePrettyTime(context, date));

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.single_note_item_view, parent, false);
		ViewHolder holder = new ViewHolder(view);
		view.setTag(holder);
		return view;
	}
	
	public static class ViewHolder {
		
		public TextView titleView;
		public TextView noteView;
		public TextView dateView;
		
		public ViewHolder(View view){
			noteView = (TextView) view.findViewById(R.id.note_textview);
			titleView = (TextView) view.findViewById(R.id.note_title_textview);
			dateView = (TextView) view.findViewById(R.id.note_date_textview);
		}
	}

}
