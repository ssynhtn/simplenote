package com.ssynhtn.mypagertabs;

import java.io.Serializable;
import java.util.Date;

import com.ssynhtn.mypagertabs.util.MyUtilities;

public class NoteItem implements Serializable {
	private static final long serialVersionUID = 2123259915902965935L;
	private String note;
	private Date date;
	private String title;
	private boolean isRecycle;

	public NoteItem(String title, String note){
		this.title = title;
		this.note = note;
		date = new Date();
		isRecycle = false;
	}
	
	public NoteItem(String title, String note, String dateStr){
		this.title = title;
		this.note = note;
		this.date = MyUtilities.parseDate(dateStr);
		isRecycle = false;
	}
	
	public NoteItem(String title, String note, String dateStr, boolean isRecycle){
		this(title, note, dateStr);
		this.isRecycle = isRecycle;
	}
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isRecycle() {
		return isRecycle;
	}

	public void setRecycle(boolean isRecycle) {
		this.isRecycle = isRecycle;
	}

}
