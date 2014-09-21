package com.ssynhtn.mypagertabs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class MyUtilities {
	private static final String TAG = MyUtilities.createTag(MyUtilities.class);
	
	private static final String DATE_FORMAT = "yyyy MM dd hh mm ss";
	private static final SimpleDateFormat sFormat = new SimpleDateFormat(DATE_FORMAT);
	
	public static String createTag(Class<?> clazz){
		return clazz.getSimpleName();
	}
	
	public static Date parseDate(String dateStr){
		try{
			return sFormat.parse(dateStr);
		}catch(ParseException e){
			Log.d(TAG, "bad dateStr: " + dateStr);
			return new Date();
		}
	}
	
	public static String getSimpleDateString(Date date){
		return sFormat.format(date);
	}
}
