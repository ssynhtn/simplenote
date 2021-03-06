package com.ssynhtn.simplenote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
//		Toast.makeText(context, "received intent: " + intent, Toast.LENGTH_SHORT).show();
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setTicker("reminder for note!");
		builder.setContentTitle(intent.getStringExtra(Intent.EXTRA_SUBJECT));
		builder.setContentText(intent.getStringExtra(Intent.EXTRA_TEXT));
		builder.setSmallIcon(R.drawable.ic_action_alarms);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
		builder.setDefaults(Notification.DEFAULT_ALL);
		builder.setAutoCancel(true);
		// create intent to open NoteDetailActivity for this note
		Intent noteIntent = new Intent(context, NoteDetailActivity.class);
		noteIntent.setData(intent.getData());
		builder.setContentIntent(PendingIntent.getActivity(context, 0, noteIntent, 0));
		Notification notification = builder.build();
		
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(0, notification);
	}

}
