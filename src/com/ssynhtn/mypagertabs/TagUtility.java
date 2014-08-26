package com.ssynhtn.mypagertabs;

public class TagUtility {
	
	public static String createTag(Class<?> clazz){
		return "mypagetabs" + clazz.getSimpleName();
	}
}
