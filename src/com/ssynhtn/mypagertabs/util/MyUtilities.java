package com.ssynhtn.mypagertabs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
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

	public static BitmapDrawable makeSingleColorDrawable(Context context, int color){
		Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		bitmap.setPixel(0, 0, color);
		BitmapDrawable image = new BitmapDrawable(context.getResources(), bitmap);
		return image;
	}
}
