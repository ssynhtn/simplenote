package com.ssynhtn.mypagertabs;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;



public class TimePickerDialogFragment extends DialogFragment implements OnTimeSetListener{
	
	private static final String TAG = TimePickerDialogFragment.class.getSimpleName();
	
	private OnTimeSetCallback mCallback;
	
	public static interface OnTimeSetCallback {
		void onTimeSet(int hour, int minute);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		
		TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, true);
		return dialog;
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		mCallback.onTimeSet(hourOfDay, minute);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try{
			mCallback = (OnTimeSetCallback) activity;
		}catch(ClassCastException e){
			Log.w(TAG, "activity " + activity + " should implement interface OnTimeSetCallback");
		}
	}
}
