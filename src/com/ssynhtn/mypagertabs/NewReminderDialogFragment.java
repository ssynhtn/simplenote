package com.ssynhtn.mypagertabs;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ssynhtn.mypagertabs.DatePickerDialogFragment.OnDateSetCallback;
import com.ssynhtn.mypagertabs.TimePickerDialogFragment.OnTimeSetCallback;


public class NewReminderDialogFragment extends DialogFragment implements OnDateSetCallback, OnTimeSetCallback{
	
	private static final String TAG = NewReminderDialogFragment.class.getSimpleName();

	private boolean mReminderAdded = false;
	
	// the time for the reminder;
	private Calendar mCal;
	
	private OnNewReminderListener mListener;
	public static interface OnNewReminderListener {
		void onNewReminder(Calendar cal);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		initDefaultTime();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Set New Reminders");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int what) {
				// TODO Auto-generated method stub
				mListener.onNewReminder(mCal);
			}
		});
		builder.setNegativeButton("Cancel", null);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.reminder_dialog, null);
		Button datePickerButton = (Button) view.findViewById(R.id.button_date_picker);
		datePickerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDatePickerDialog();
			}
		});
		
		Button timePickerButton = (Button) view.findViewById(R.id.button_time_picker);
		
		builder.setView(view);
		
		return builder.create();
	}
	
	private void initDefaultTime(){
		mCal = Calendar.getInstance();
		long timeMillis = mCal.getTimeInMillis() + 60 * 60 * 1000;	// one hour later;
		mCal.setTimeInMillis(timeMillis);
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		try{
			mListener = (OnNewReminderListener) activity;
		}catch(ClassCastException e){
			Log.w(TAG, "should not happen");
		}
	}
	
	private void showDatePickerDialog(){
		FragmentManager fm = getFragmentManager();
		DatePickerDialogFragment fragment = new DatePickerDialogFragment();
		fragment.show(fm, null);
	}
	
	private void addOrRemoveReminder(){
		if(mReminderAdded){
			mReminderAdded = false;
			Toast.makeText(getActivity(), "Reminder is now removed", Toast.LENGTH_SHORT).show();
		}else {
			mReminderAdded = true;
			Toast.makeText(getActivity(), "Reminder is now added", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onTimeSet(int hour, int minute) {
		// TODO Auto-generated method stub
		mCal.set(Calendar.HOUR_OF_DAY, hour);
		mCal.set(Calendar.MINUTE, minute);
	}

	@Override
	public void onDateSet(int year, int month, int day) {
		// TODO Auto-generated method stub
		mCal.set(Calendar.YEAR, year);
		mCal.set(Calendar.MONDAY, month);
		mCal.set(Calendar.DAY_OF_MONTH, day);
	}
}
