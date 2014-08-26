package com.ssynhtn.mypagertabs;

import java.io.Serializable;
import java.util.Date;

public class NoteItem implements Serializable {
	private static final long serialVersionUID = 2123259915902965935L;
	private String note;
	private Date date;
	private String title;

	public NoteItem(String title, String note){
		this.title = title;
		this.note = note;
		date = new Date();
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
}
