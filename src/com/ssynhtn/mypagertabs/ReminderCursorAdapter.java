package com.ssynhtn.mypagertabs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ssynhtn.mypagertabs.data.NoteContract.ReminderEntry;

public class ReminderCursorAdapter extends CursorAdapter{

	public ReminderCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		String reminder = cursor.getString(cursor.getColumnIndex(ReminderEntry.COLUMN_REMINDER_TIME));
		final long id = cursor.getLong(cursor.getColumnIndex(ReminderEntry._ID));
		
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.reminderView.setText(reminder);
		holder.reminderButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				removeReminder(context, id);
			}
		});
		
	}
	
	// id: reminder id
	private void removeReminder(Context context, long id){
		// remove reminder(notification)
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Uri reminderUri = ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, id);
		Intent intent = new Intent(context, ReminderReceiver.class);
		intent.setData(reminderUri);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		alarmManager.cancel(pi);
		
		// reminder riminder row from database
		AsyncQueryHandler handler = new AsyncQueryHandler(context.getContentResolver()) {};
		handler.startDelete(0, null, ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, id), null, null);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		
		View view = inflater.inflate(R.layout.single_reminder_item_view, parent, false);
		view.setTag(new ViewHolder(view));
		return view;
		
	}
	
	private static class ViewHolder {
		public final TextView reminderView;
		public final Button reminderButton;
		
		public ViewHolder(View view){
			reminderView = (TextView) view.findViewById(R.id.reminder_textview);
			reminderButton = (Button) view.findViewById(R.id.button_delete_reminder);
		}
	}

}
