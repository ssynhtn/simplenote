package com.ssynhtn.mypagertabs;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

public class DatePickerDialogFragment extends DialogFragment implements OnDateSetListener {
	
	private static final String TAG = DatePickerDialogFragment.class.getSimpleName();
	
	private OnDateSetCallback mCallback;
	
	public static interface OnDateSetCallback {
		void onDateSet(int year, int month, int day);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
		return dialog;
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		try{
			mCallback = (OnDateSetCallback) activity;
		}catch(ClassCastException e){
			Log.w(TAG, "must implement " + OnDateSetCallback.class.getSimpleName());
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		mCallback.onDateSet(year, monthOfYear, dayOfMonth);
	}

}
