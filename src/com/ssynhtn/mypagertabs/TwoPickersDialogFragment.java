package com.ssynhtn.mypagertabs;

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



/**
 * 
 * this dialog fragment shows a date picker and a time picker
 * this is used to be create a new reminder
 *
 */
public class TwoPickersDialogFragment extends DialogFragment {

	public static interface OnTimeSetCallback {
		void onTimeSet(int year, int  month, int day, int hour, int minute);
	}
	
	private OnTimeSetCallback mCallback;
	
	public static TwoPickersDialogFragment newInstance(Fragment fragment){
		TwoPickersDialogFragment twoPickers = new TwoPickersDialogFragment();
		twoPickers.setTargetFragment(fragment, 0);
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
