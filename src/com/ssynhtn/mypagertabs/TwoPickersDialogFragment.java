package com.ssynhtn.mypagertabs;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;



/**
 * 
 * this dialog fragment shows a date picker and a time picker
 * this is used to be create a new reminder
 *
 */
public class TwoPickersDialogFragment extends DialogFragment {
	
	public static final String ARG_DATE = "arg_date";

	public static interface OnTimeSetCallback {
		void onTimeSet(int year, int  month, int day, int hour, int minute);
	}
	
	private OnTimeSetCallback mCallback;
	
	public static TwoPickersDialogFragment newInstance(Fragment fragment, Date date){
		TwoPickersDialogFragment twoPickers = new TwoPickersDialogFragment();
		twoPickers.setTargetFragment(fragment, 0);
		
		Bundle args = new Bundle();
		if(date != null){
			args.putLong(ARG_DATE, date.getTime());
		}
		twoPickers.setArguments(args);
		return twoPickers;
	}
	
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// cast fragment to callback
		mCallback = (OnTimeSetCallback) getTargetFragment();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View rootView = inflater.inflate(R.layout.two_pickers, null);
		
		final TimePicker timePicker = (TimePicker) rootView.findViewById(R.id.time_picker);
		final DatePicker datePicker = (DatePicker) rootView.findViewById(R.id.date_picker);
		
		timePicker.setIs24HourView(true);
		
		long timeMillis;
		Bundle args = getArguments();
		if(args.containsKey(ARG_DATE)){
			builder.setTitle("Change time for reminder");
			timeMillis = args.getLong(ARG_DATE);
		}else {
			builder.setTitle("Set time for new reminder");
			timeMillis = System.currentTimeMillis() + 60 * 60 * 1000;	// 1 hour later
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMillis);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);
		
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		datePicker.init(year, month, day, null);
		
		builder.setView(rootView);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				int year = datePicker.getYear();
				int month = datePicker.getMonth();
				int day = datePicker.getDayOfMonth();
				
				int hour = timePicker.getCurrentHour();
				int minute = timePicker.getCurrentMinute();
				
				mCallback.onTimeSet(year, month, day, hour, minute);
			}
		});
		
		builder.setNegativeButton("Cancel", null);
		
		return builder.create();
		
	}
}
