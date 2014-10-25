package com.ssynhtn.simplenote.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateUtils;
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
	
	public static String makePrettyTime(Context context, long timeMillis){
		return DateUtils.formatDateTime(context, timeMillis, DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_SHOW_TIME);
	}

	public static BitmapDrawable makeSingleColorDrawable(Context context, int color){
		Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		bitmap.setPixel(0, 0, color);
		BitmapDrawable image = new BitmapDrawable(context.getResources(), bitmap);
		return image;
	}
	
	public static String getOneLineTitle(String title){
		title = title.trim();
		if(title.contains("\n")){
			String[] parts = title.split("\n");
			title = parts[0];
		}
		
		if(title.length() > 20){
			title = title.substring(0, 20);
		}
		
		return title;
	}
	
	public static String getThreeLineNote(String note){
		note = note.trim();
		if(note.contains("\n")){
			String[] parts = note.split("\n");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < 3 && i < parts.length; i++){
				if(i > 0)
					sb.append("\n");
				sb.append(parts[i]);
			}
			note = sb.toString();
		}
		
		if(note.length() > 60){
			note = note.substring(0, 60);
		}
		return note;
	}
}
